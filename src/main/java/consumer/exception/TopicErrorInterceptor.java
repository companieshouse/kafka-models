package consumer.exception;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;

/**
 * Topic Error Interceptor.
 */
public class TopicErrorInterceptor implements ProducerInterceptor<String, Object> {

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        String nextTopic = record.topic().contains("-error") ? getNextErrorTopic(record)
                : record.topic();
        if (nextTopic.contains("-invalid")) {
            return new ProducerRecord<>(nextTopic, record.key(), record.value());
        }

        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception ex) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
    }

    /**
     * Move non retryable errors onto invalid topic.
     * @param record the ProducerRecord intercepted before kafka
     * @return the next error topic for the record that has errored
     */
    private String getNextErrorTopic(ProducerRecord<String, Object> record) {
        Header header1 = record.headers().lastHeader("kafka_exception-fqcn");
        Header header2 = record.headers().lastHeader("kafka_exception-stacktrace");
        return ((header1 != null
                && new String(header1.value()).contains(NonRetryableErrorException.class.getName()))
                || (header2 != null
                && new String(header2.value()).contains(
                NonRetryableErrorException.class.getName())))
                ? record.topic().replace("-error", "-invalid") : record.topic();
    }
}