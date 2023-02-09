package consumer.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TopicErrorInterceptorTest {
    private TopicErrorInterceptor interceptor;

    @BeforeEach
    public void setUp(){
        interceptor = new TopicErrorInterceptor();
    }

    @Test
    void when_correct_topic_is_sent_record_is_unchanged() {
        ProducerRecord<String, Object> record = createTestRecord("topic", "header");
        ProducerRecord<String, Object> newRecord = interceptor.onSend(record);

        assertEquals(newRecord, record);
    }

    @Test
    void when_error_is_nonretryable_topic_is_set_to_invalid() {
        ProducerRecord<String, Object> record = createTestRecord("topic-error", NonRetryableErrorException.class.getName());
        ProducerRecord<String, Object> newRecord = interceptor.onSend(record);

        assertEquals(newRecord.topic(),"topic-invalid");
    }

    @Test
    void when_error_is_retryable_topic_is_unchanged() {
        ProducerRecord<String, Object> record = createTestRecord("topic-error", RetryableErrorException.class.getName());
        ProducerRecord<String, Object> newRecord = interceptor.onSend(record);

        assertEquals(newRecord.topic(),"topic-error");
    }

    private ProducerRecord<String, Object> createTestRecord(String topic, String exceptionName) {
        Object recordObj = new Object();
        RecordHeaders headers = setHeaders(exceptionName);
        return new ProducerRecord<>(topic, 1,1L ,null, recordObj, headers);
    }

    private RecordHeaders setHeaders(String exceptionName) {
        RecordHeaders headers = new RecordHeaders();
        headers.add("kafka_exception-fqcn", exceptionName.getBytes());
        return headers;
    }
}