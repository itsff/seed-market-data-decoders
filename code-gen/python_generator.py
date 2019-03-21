import os
import jinja2
import inflection

from base_generator import BaseGenerator
from base_generator import FieldType


class PythonGenerator(BaseGenerator):
    def __init__(self, schema_path, template_path, out_path='.'):
        super().__init__(
            schema_path=schema_path,
            template_path=os.path.join(template_path, 'python'),
            out_path=os.path.join(out_path, 'python'))

    def generate(self):
        file_name = 'seed_market_data.py'
        out_path = os.path.join(self.out_path)
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('seed_market_data.j2')

        mcast_msg_map = next((mm for mm in self.msg_maps if mm.name == "multicast"))
        snap_msg_map = next((mm for mm in self.msg_maps if mm.name == "snapshot"))
        result = t.render(
            segments=self.segments,
            enums=self.enums,
            mcast_messages=mcast_msg_map.items,
            snap_messages=snap_msg_map.items,
            gen=self)

        with open(file_path, 'w') as f:
            f.write(result)

    @classmethod
    def get_struct_format(cls, field):
        tm = {
            FieldType.bool: '?',
            FieldType.uint8: 'B',
            FieldType.uint16: 'H',
            FieldType.uint32: 'I',

            FieldType.instrument_id: 'Q',
            FieldType.order_id: '16s',
            FieldType.execution_id: '16s',

            FieldType.price: 'Q',
            FieldType.order_size: 'Q',
            FieldType.sequence_num: 'Q',
            FieldType.time_stamp: 'Q',

            FieldType.message_type: 'B',
            FieldType.trading_status: 'B',
            FieldType.side: 'B',
            FieldType.snapshot_fail_reason: 'B',

            FieldType.padding: 'x',
            FieldType.text: 's'
        }
        if field.is_padding or field.is_text:
            return '{0}{1}'.format(field.size, tm[field.type])
        else:
            return tm[field.type]

    @classmethod
    def get_lang_type(cls, field):
        tm = {
            FieldType.order_id: 'OrderId',
            FieldType.execution_id: 'ExecutionId',

            FieldType.message_type: 'MessageTypeEnum',
            FieldType.trading_status: 'TradingStatusEnum',
            FieldType.side: 'SideEnum',
            FieldType.snapshot_fail_reason: 'SnapshotFailReasonEnum',

            FieldType.text: 'str'
        }
        return tm.get(field.type, None)

