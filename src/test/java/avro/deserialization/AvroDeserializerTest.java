package avro.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import avro.utils.TestRecord;

public class AvroDeserializerTest {

    AvroDeserializer<TestRecord> avroDeserializer;

    @BeforeEach
    void setup() {
        avroDeserializer = new AvroDeserializer<>(TestRecord.class);
    }

    @Test
    void avroRecordDeserializes() {
        byte[] bytes = {32, 123, 34, 107, 101, 121, 34, 58, 32, 34, 118, 97, 108, 117, 101, 34, 125};
        TestRecord actual = avroDeserializer.deserialize(null, null, bytes);

        TestRecord expected = new TestRecord("{\"key\": \"value\"}");

        assertEquals(expected, actual);
    }
}
