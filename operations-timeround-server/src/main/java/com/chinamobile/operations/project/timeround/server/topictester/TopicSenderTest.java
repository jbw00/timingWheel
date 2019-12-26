package com.chinamobile.operations.project.timeround.server.topictester;

import com.alibaba.fastjson.JSON;
import com.chinamobile.operations.project.seed.kafka.configurer.Provider;
import com.chinamobile.operations.project.timeround.server.redisutil.MSGUtil;
import com.chinamobile.operations.project.timeround.server.timing.template.SMSInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Bowen
 * 发送消息测试
 */
@Slf4j
@Api(tags="测试用api")
@RestController
@RequestMapping("/test")
public class TopicSenderTest {

    @Autowired
    private Provider provider;
    @Autowired
    private MSGUtil msgUtil;
    @Value("${spring.kafka.topic.msg-delay}")
    private String topic;


    @ApiOperation("向topic中发送时间轮消息")
    @PostMapping("/send")
    public void sendTopic(@RequestBody HashMap<Date, SMSInfo> map){
        String msg = JSON.toJSONString(map);
        provider.sender(topic, msg);
    }

    @ApiOperation("时间轮取消发送消息")
    @PostMapping("/cancel")
    public void cancelSend(@RequestBody HashMap<Date, SMSInfo> map){
        msgUtil.discardMSG(map);
    }
}
