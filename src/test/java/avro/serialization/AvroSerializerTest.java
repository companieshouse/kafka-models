package avro.serialization;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import avro.utils.TestRecord;

public class AvroSerializerTest {

    AvroSerializer avroSerializer;

    @BeforeEach
    void setup() {
        avroSerializer = new AvroSerializer();
    }

    @Test
    void avroRecordDeserializes() {
        TestRecord testRecord = new TestRecord("{\"key\": \"value\"}");
        byte[] result = avroSerializer.serialize(null, testRecord);

        byte[] expected = {32, 123, 34, 107, 101, 121, 34, 58, 32, 34, 118, 97, 108, 117, 101, 34, 125};
        assertArrayEquals(expected, result);
    }

}
