package com.chinamobile.operations.project.timeround.server.api;

import com.chinamobile.operations.project.timeround.server.redisutil.MSGUtil;
import com.chinamobile.operations.project.timeround.server.timing.template.SMSInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(tags="外部调用接口")
@RestController
@RequestMapping("/message")
public class MessageControl {

    @Autowired
    private MSGUtil msgUtil;

    @ApiOperation("时间轮取消发送消息")
    @PostMapping("/cancel")
    public void cancelSend(@RequestBody HashMap<Date, SMSInfo> map){
        msgUtil.discardMSG(map);
    }
}
