package avro.utils;

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
    public int attempt;

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public java.lang.Object get(int field) {
        switch (field) {
        case 0: return data;
        case 1: return attempt;
        default: throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
      }

    public void put(int field, java.lang.Object value) {
        switch (field) {
        case 0: data = (java.lang.String)value; break;
        case 1: attempt = (java.lang.Integer)value; break;
        default: throw new org.apache.avro.AvroRuntimeException("Bad index");
        }
    }
}