package consumer.utils;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;

public class TestRecord extends SpecificRecordBase {

    public TestRecord(String data) {
        this.data = data;
    }

    public TestRecord() {
    }

    public static final Schema SCHEMA$ = new Schema.Parser().parse(
        "{\"type\":\"record\",\"name\":\"TestRecord\",\"namespace\":\"testrecord\",\"doc\":\"Test Schema\",\"fields\":[{\"name\":\"data\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"Test Payload\"}]}");
    public String data;

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public java.lang.Object get(int field) {
        return data;
      }

    public void put(int field, Object value) {
        data = value.toString();
    }
}