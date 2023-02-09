package consumer.deserialization;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

import consumer.exception.NonRetryableErrorException;

public class AvroDeserializer<T> implements Deserializer<T> {
    
    Class<T> avroClass;

    public AvroDeserializer(Class<T> avroClass) {
        this.avroClass = avroClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<T> reader = new ReflectDatumReader<>(avroClass);
            return reader.read(null, decoder);
        } catch (Exception ex) {
            throw new NonRetryableErrorException(ex);
        }
    }
}