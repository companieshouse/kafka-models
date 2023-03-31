package consumer.serialization;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import consumer.exception.NonRetryableErrorException;
import consumer.utils.TestRecord;

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

    @Test
    void nullPayloadReturnsNull() {
        byte[] result = avroSerializer.serialize(null, null);

        assertArrayEquals(null, result);
    }

    @Test
    void bytesReturnsBytes() {
        byte[] expected = {32, 123, 34};
        byte[] result = avroSerializer.serialize(null, expected);

        assertArrayEquals(expected, result);
    }

    @Test
    void stringReturnsBytes() {
        byte[] expected = {84, 104, 105, 115, 32, 105, 115, 32, 115, 111, 109, 101, 32, 100, 97, 116, 97};
        byte[] result = avroSerializer.serialize(null, "This is some data");
        assertArrayEquals(expected, result);
    }

    @Test
    void avroRecordDoesntDeserializes() {
        TestRecord testRecord = mock(TestRecord.class);
        when(testRecord.getSchema()).thenAnswer(invocation -> { throw new IOException(); });
        assertThrows(NonRetryableErrorException.class, () -> avroSerializer.serialize(null, testRecord));

    }

}
