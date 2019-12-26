package com.chinamobile.operations.project.timeround.client;

import com.chinamobile.operations.project.timeround.client.vo.SMSInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Bowen
 * timeround外部接口
 */
@FeignClient(name = "operations-timeround-server")
public interface TimeroundClient {

    @PostMapping("/timeround/message/cancel")
    void cancelSend(@RequestBody HashMap<Date, SMSInfo> map);
}
