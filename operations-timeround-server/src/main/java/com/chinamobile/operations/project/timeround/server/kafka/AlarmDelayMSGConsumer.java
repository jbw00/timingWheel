package com.chinamobile.operations.project.timeround.server.kafka;

import com.chinamobile.operations.project.seed.kafka.configurer.BaseConsumer;
import com.chinamobile.operations.project.timeround.server.timing.Producer;
import com.chinamobile.operations.project.timeround.server.timing.template.SMSInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

/**
 * 延迟告警短信消费者
 * @author Bowen
 */
@Component
@Slf4j
public class AlarmDelayMSGConsumer extends BaseConsumer {

    @Autowired
    private Producer producer;

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.msg-delay}")
    protected void consumer(ConsumerRecord<?, ?> consumerRecord, Acknowledgment ack) {
        try {
            Object msg = getMsg(consumerRecord);
            HashMap<Date, SMSInfo> format = producer.format(msg);
            producer.startThread(format);
            ack.acknowledge();
        }catch (Exception e){
            log.error("kafka topic listening failed:", e);
        }
    }

}
