package com.chinamobile.operations.project.timeround.server.timing;

import com.alibaba.fastjson.JSON;
import com.chinamobile.operations.project.seed.kafka.configurer.Provider;
import com.chinamobile.operations.project.timeround.server.timing.template.SMSInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 消费内容至kafka
 * @author Bowen
 */
@Component
@Slf4j
public class CarriedOut {

    @Autowired
    private Provider provider;
    @Value("${spring.kafka.topic.sms}")
    private String smsTopic;

    public Boolean send(String msg){
        log.debug("timing end {}<=====================", msg);
        return provider.sender(smsTopic, msg);
    }

    /**
     * 格式化
     * @param msg
     * @return
     */
    private SMSInfo format(String msg){
        SMSInfo smsInfo = JSON.parseObject(msg, SMSInfo.class);
        return smsInfo;
    }

}
