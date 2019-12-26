package com.chinamobile.operations.project.timeround.server.timing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinamobile.operations.project.seed.redis.util.RedisUtils;
import com.chinamobile.operations.project.seed.util.DateUtils;
import com.chinamobile.operations.project.timeround.server.common.TimingConstants;
import com.chinamobile.operations.project.timeround.server.timing.template.SMSInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 时间槽位生产者
 *
 * @author Bowen
 */
@Component
@Slf4j
public class Producer {

    @Value("${spring.timing.lockTimeOut}")
    private long lockTimeOut;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Async("TaskExecutor")
    public void startThread(HashMap<Date, SMSInfo> dateMap) {
        log.info("Producer Thread Start...");
        insertToSolt(dateMap);
    }

    /**
     * 插入事件轮槽位
     *
     * @param dateMap
     * @return 判断是否插入成功
     */
    private void insertToSolt(HashMap<Date, SMSInfo> dateMap) {
        try {
            Date date = null;
            //写入锁
            String writeLock = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_FUNCTION, TimingConstants.REDIS_BUILD_WRITE_LOCK);
            Set<Date> dates = dateMap.keySet();
            Iterator<Date> iterator = dates.iterator();
            //创建时间轮槽位key
            while (iterator.hasNext()) {
                date = iterator.next();
            }
            String msgDate = DateUtils.dateToStr(date, TimingConstants.STRING_DATETIME_FORMAT);
            String msgDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, msgDate);
            log.info("use msg date build redis key : {}", msgDateKey);
            //判断是否有锁
            while (true) {
                if (!redisTemplate.hasKey(writeLock)) {
                    redisUtils.set(writeLock, writeLock, lockTimeOut);
                    break;
                } else {
                    Thread.sleep(10000);
                }
            }
            //插入操作list
            redisTemplate.opsForList().rightPush(msgDateKey, JSON.toJSONString(dateMap.get(date)));
            redisUtils.remove(writeLock);
        } catch (Exception e) {
            log.error("insert into wheel failed：", e);
        }
    }


    /**
     * 格式化升级管理提前告警
     *
     * @param msg {Date, String}
     * @return
     */
    public HashMap<Date, SMSInfo> format(Object msg) {
        String jsonString = msg.toString();
        int start = jsonString.indexOf(":");
        int end = jsonString.lastIndexOf("}");
        String time = jsonString.substring(1, start);
        String content = jsonString.substring(start + 1, end);
        JSONObject jsonObject = JSON.parseObject(content);
        SMSInfo smsInfo = JSONObject.toJavaObject(jsonObject, SMSInfo.class);
        Date date = new Date(Long.valueOf(time));
        HashMap<Date, SMSInfo> result = new HashMap<>(1);
        result.put(date, smsInfo);
        return result;
    }

    public static void main(String[] args) {
        String s = "{1576491720000:{\"content\":\"123\",\"sendTime\":\"32\",\"tempNo\":\"12\",\"userId\":[1,2]}}";
        System.out.println(s);
        int start = s.indexOf(":");
        int end = s.lastIndexOf("}");
        String substring = s.substring(start + 1, end);
        String time = s.substring(1, start);
        System.out.println(substring);
        System.out.println(time);
    }
}
