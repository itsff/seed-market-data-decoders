import os
import jinja2
import inflection

from base_generator import BaseGenerator
from base_generator import FieldType


class CppGenerator(BaseGenerator):
    def __init__(self, schema_path, template_path, out_path='.'):
        super().__init__(
            schema_path=schema_path,
            template_path=os.path.join(template_path, 'cpp'),
            out_path=os.path.join(out_path, 'cpp', 'seedcx-md'))

    def generate(self):
        self._generate_segments()
        self._generate_enums()
        self._generate_decoders()

    def _generate_segments(self):
        self._generate_segments_file(
            'market_data.hpp',
            os.path.join(self.out_path, 'include', 'seedcx'),
            'segments.hpp.j2')

        self._generate_segments_file(
            'market_data.cpp',
            os.path.join(self.out_path, 'src'),
            'segments.cpp.j2')

    def _generate_segments_file(self, file_name, out_path, template):
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template(template)
        result = t.render(segments=self.segments,
                          enums=self.enums,
                          gen=self)

        with open(file_path, 'w') as f:
            f.write(result)

    def _generate_enums(self):
        self._generate_enums_file(
            'enums.hpp',
            os.path.join(self.out_path, 'include', 'seedcx'),
            'enums.hpp.j2')

        self._generate_enums_file(
            'enums.cpp',
            os.path.join(self.out_path, 'src'),
            'enums.cpp.j2')

    def _generate_enums_file(self, file_name, out_path, template):
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template(template)
        result = t.render(enums=self.enums, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)

    def _generate_decoders(self):
        self._generate_incremental_update_decoder()
        # self._generate_snapshot_response_decoder()
        # self._generate_abstract_snapshot_response_decoder()

    def _generate_incremental_update_decoder(self):
        file_name = 'incremental_update_decoder.hpp'
        out_path = os.path.join(self.out_path, 'include', 'seedcx')
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('incremental_update_decoder.hpp.j2')

        msg_map = next((mm for mm in self.msg_maps if mm.name == "multicast"))
        result = t.render(messages=msg_map.items, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)

    def _generate_snapshot_response_decoder(self):
        file_name = 'SnapshotResponseDecoder.java'
        out_path = self.out_path
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('snapshot_response_decoder.j2')

        msg_map = next((mm for mm in self.msg_maps if mm.name == "snapshot"))
        result = t.render(messages=msg_map.items, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)
        pass

    @classmethod
    def get_lang_type(cls, field_type):
        tm = {
            FieldType.bool: 'bool',
            FieldType.uint8: 'uint8_t',
            FieldType.uint16: 'uint16_t',
            FieldType.uint32: 'uint32_t',

            FieldType.instrument_id: 'instrument_id_t',
            FieldType.order_id: 'order_id_t',
            FieldType.execution_id: 'execution_id_t',

            FieldType.price: 'price_t',
            FieldType.order_size: 'order_size_t',
            FieldType.sequence_num: 'sequence_num_t',
            FieldType.time_stamp: 'time_stamp_t',

            FieldType.message_type: 'uint8_t',
            FieldType.trading_status: 'enums::trading_status',
            FieldType.side: 'enums::side',
            FieldType.snapshot_fail_reason: "enums::snapshot_fail_reason",

            FieldType.padding: 'uint8_t',
            FieldType.text: 'char'
        }
        return tm[field_type]

