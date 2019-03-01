import os
import jinja2
import inflection

from base_generator import BaseGenerator
from base_generator import FieldType


class JavaGenerator(BaseGenerator):
    def __init__(self, schema_path, template_path, out_path='.'):
        super().__init__(
            schema_path=schema_path,
            template_path=os.path.join(template_path, 'java'),
            out_path=os.path.join(out_path, 'java', 'com', 'seedcx', 'marketdata'))

    def generate(self):
        self._generate_enums()
        self._generate_segments()
        self._generate_decoders()

    def _generate_segment(self, segment):
        file_name = inflection.camelize(segment.name) + '.java'
        out_path = os.path.join(self.out_path, 'msg')
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('segment.j2')
        result = t.render(segment=segment, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)

    def _generate_enum(self, enum):
        file_name = inflection.camelize(enum.name) + '.java'
        out_path = os.path.join(self.out_path, 'enums')
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('enum.j2')
        result = t.render(enum=enum, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)
        pass

    def _generate_decoders(self):
        self._generate_abstract_incremental_update_decoder()
        self._generate_incremental_update_decoder()
        self._generate_snapshot_response_decoder()
        self._generate_abstract_snapshot_response_decoder()

    def _generate_incremental_update_decoder(self):
        file_name = 'IncrementalUpdateDecoder.java'
        out_path = self.out_path
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('incremental_update_decoder.j2')

        msg_map = next((mm for mm in self.msg_maps if mm.name == "multicast"))
        result = t.render(messages=msg_map.items, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)
        pass

    def _generate_abstract_incremental_update_decoder(self):
        file_name = 'AbstractIncrementalUpdateDecoder.java'
        out_path = self.out_path
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('abstract_incremental_update_decoder.j2')

        msg_map = next((mm for mm in self.msg_maps if mm.name == "multicast"))
        result = t.render(messages=msg_map.items, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)
        pass

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

    def _generate_abstract_snapshot_response_decoder(self):
        file_name = 'AbstractSnapshotResponseDecoder.java'
        out_path = self.out_path
        os.makedirs(out_path, exist_ok=True)
        file_path = os.path.join(out_path, file_name)

        t = self.j2env.get_template('abstract_snapshot_response_decoder.j2')

        msg_map = next((mm for mm in self.msg_maps if mm.name == "snapshot"))
        result = t.render(messages=msg_map.items, gen=self)

        with open(file_path, 'w') as f:
            f.write(result)
        pass

    @classmethod
    def get_lang_type(cls, field_type):
        tm = {
            FieldType.bool: 'boolean',
            FieldType.uint8: 'int',
            FieldType.uint16: 'int',
            FieldType.uint32: 'long',

            FieldType.instrument_id: 'long',
            FieldType.order_id: 'com.seedcx.marketdata.OrderId',
            FieldType.execution_id: 'com.seedcx.marketdata.ExecutionId',

            FieldType.price: 'long',
            FieldType.order_size: 'long',
            FieldType.sequence_num: 'long',
            FieldType.time_stamp: 'long',

            FieldType.message_type: 'int',
            FieldType.trading_status: 'com.seedcx.marketdata.enums.TradingStatus',
            FieldType.side: 'com.seedcx.marketdata.enums.Side',
            FieldType.snapshot_fail_reason: "com.seedcx.marketdata.enums.SnapshotFailReason",

            FieldType.padding: 'byte[]',
            FieldType.text: 'java.lang.String'
        }
        return tm[field_type]

    @classmethod
    def is_ref_type(cls, field_type):
        tm = {
            FieldType.order_id: True,
            FieldType.execution_id: True,

            FieldType.message_type: True,
            FieldType.trading_status: True,
            FieldType.side: True,
            FieldType.snapshot_fail_reason: True,

            FieldType.padding: True,
            FieldType.text: True
        }
        return tm.get(field_type, False)

    @classmethod
    def reader_for_type(cls, field_type):
        tm = {
            FieldType.bool: 'readBoolean',
            FieldType.uint8: 'readUnsignedByte',
            FieldType.uint16: 'readUnsignedShort',
            FieldType.uint32: 'readUnsignedInt',

            FieldType.instrument_id: 'readInstrumentId',
            FieldType.order_id: 'readOrderId',
            FieldType.execution_id: 'readExecutionId',

            FieldType.price: 'readPrice',
            FieldType.order_size: 'readOrderSize',
            FieldType.sequence_num: 'readSequenceNumber',
            FieldType.time_stamp: 'readTimeStamp',

            FieldType.message_type: 'readUnsignedByte',
            FieldType.trading_status: 'readTradingStatus',
            FieldType.side: 'readSide',
            FieldType.snapshot_fail_reason: 'readSnapshotFailReason',

            FieldType.padding: 'skipBytes',
            FieldType.text: 'readAsciiText'
        }
        return tm[field_type]

    @classmethod
    def writer_for_type(cls, field_type):
        tm = {
            FieldType.bool: 'writeBoolean',
            FieldType.uint8: 'writeUnsignedByte',
            FieldType.uint16: 'writeUnsignedShort',
            FieldType.uint32: 'writeUnsignedInt',

            FieldType.instrument_id: 'writeInstrumentId',
            FieldType.order_id: 'writeOrderId',
            FieldType.execution_id: 'writeExecutionId',

            FieldType.price: 'writePrice',
            FieldType.order_size: 'writeOrderSize',
            FieldType.sequence_num: 'writeSequenceNumber',
            FieldType.time_stamp: 'writeTimeStamp',

            FieldType.message_type: 'writeUnsignedByte',
            FieldType.trading_status: 'writeTradingStatus',
            FieldType.side: 'writeSide',
            FieldType.snapshot_fail_reason: 'writeSnapshotFailReason',

            FieldType.padding: 'writeBytes',
            FieldType.text: 'writeAsciiText'
        }
        return tm[field_type]

    def _generate_enums(self):
        for enum in self.enums:
            self._generate_enum(enum)

    def _generate_segments(self):
        for segment in self.segments:
            self._generate_segment(segment)


