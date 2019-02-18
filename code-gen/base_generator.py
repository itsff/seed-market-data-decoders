import enum
import os
import jinja2
import inflection
import itertools

import xml.etree.ElementTree as ET


def pascal(text):
    return inflection.camelize(text, uppercase_first_letter=True)


def camel(text):
    return inflection.camelize(text, uppercase_first_letter=False)


def snake(text):
    return inflection.underscore(text)


class FieldType(enum.Enum):
    bool = ('bool', 1)

    uint8 = ('uint8', 1)
    uint16 = ('uint16', 2)
    uint32 = ('uint32', 4)

    instrument_id = ('instrument_id', 8)
    order_id = ('order_id', 16)
    execution_id = ('execution_id', 16)

    price = ('price', 8)
    order_size = ('order_size', 8)

    sequence_num = ('sequence_num', 8)
    time_stamp = ('time_stamp', 8)

    message_type = ('message_type', 1)
    trading_status = ('trading_status', 1)
    side = ('side', 1)
    snapshot_fail_reason = ('snapshot_fail_reason', 1)

    padding = ('padding', 0)
    text = ('text', 0)

    def __init__(self, name, wire_size):
        self.type_name = name
        self.wire_size = wire_size


class Field:
    def __init__(self, field_type: FieldType, name: str, size: int):
        self.type = field_type
        self.name = name
        self.size = size

    @property
    def is_padding(self):
        return self.type == FieldType.padding

    @property
    def is_text(self):
        return self.type == FieldType.text


class Segment:
    def __init__(self, name: str, is_msg: bool):
        self.name = name
        self.is_msg = is_msg
        self.all_fields = []

    @property
    def has_fields(self):
        return len(self.all_fields) > 0

    @property
    def fields(self):
        for f in self.all_fields:
            if not f.is_padding:
                yield f


class Enum:
    def __init__(self, name: str):
        self.name = name
        self.items = []


class EnumItem:
    def __init__(self, name: str, value: int):
        self.name = name
        self.value = value


class MessageMap:
    def __init__(self, name: str, enum: Enum):
        self.name = name
        self.enum = enum
        self.items = []


class MessageMapItem:
    def __init__(self, name: str, enum_item: EnumItem):
        self.name = name
        self.enum = enum_item


class BaseGenerator:
    def __init__(self, schema_path, template_path, out_path):
        self.schema_path = schema_path
        self.template_path = template_path
        self.out_path = out_path
        self.j2env = jinja2.Environment(loader=jinja2.FileSystemLoader(self.template_path),
                                        keep_trailing_newline=False,
                                        trim_blocks=True,
                                        lstrip_blocks=True)
        self.j2env.filters['pascal'] = pascal
        self.j2env.filters['camel'] = camel
        self.j2env.filters['snake'] = snake

        os.makedirs(self.out_path, exist_ok=True)
        self._parse_schema()

    def generate(self):
        pass

    def _parse_schema(self):
        tree = ET.parse(self.schema_path)
        root = tree.getroot()

        segments = []

        for seg_elem in root.findall('./segment'):
            seg = Segment(seg_elem.get('name'),
                          is_msg=seg_elem.get('is_msg'))

            padding_count = 0

            for f in seg_elem:
                if f.tag == 'padding':
                    field = Field(name='padding'.format(padding_count),
                                  field_type=FieldType.padding,
                                  size=int(f.get('size')))
                    padding_count += 1
                else:
                    field_type = FieldType[f.get('type')]
                    size = int(f.get('size', default=field_type.wire_size))
                    field = Field(name=f.get('name'),
                                  field_type=field_type,
                                  size=size)
                seg.all_fields.append(field)

            segments.append(seg)

        self.segments = segments

        self.enums = []
        for enum_elem in root.findall('./enum'):
            e = Enum(enum_elem.get('name'))
            for item in enum_elem:
                enum_item = EnumItem(name=item.get('name'),
                                     value=int(item.get('value')))
                e.items.append(enum_item)
            self.enums.append(e)

        self.msg_maps = []
        for msg_map_elem in root.findall('./message_map'):

            enum_name = msg_map_elem.get('enum_name')
            enum = next((e for e in self.enums if e.name == enum_name))
            mm = MessageMap(msg_map_elem.get('name'), enum)
            for item in msg_map_elem:
                ei = item.get("enum")
                enum_item = next((i for i in enum.items if i.name == ei))

                msg_map_item = MessageMapItem(name=item.get('name'), enum_item=enum_item)
                mm.items.append(msg_map_item)
            self.msg_maps.append(mm)
