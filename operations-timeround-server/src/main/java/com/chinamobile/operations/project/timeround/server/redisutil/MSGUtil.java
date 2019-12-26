package com.chinamobile.operations.project.timeround.server.redisutil;

import com.alibaba.fastjson.JSON;
import com.chinamobile.operations.project.seed.redis.util.RedisUtils;
import com.chinamobile.operations.project.seed.util.DateUtils;
import com.chinamobile.operations.project.timeround.server.common.TimingConstants;
import com.chinamobile.operations.project.timeround.server.timing.template.SMSInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Bowen
 * redis内发送信息工具
 */
@Slf4j
@Component
public class MSGUtil {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * redis取消队列--增加消息
     *
     * @return 操作是否成功
     */
    public boolean discardMSG(HashMap<Date, SMSInfo> map) {
        boolean success = false;
        try {
            String discardDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_DISCARD_LIST);
            Date date = null;
            Iterator<Date> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                date = iterator.next();
            }
            int i = date.compareTo(new Date());
            String msg = JSON.toJSONString(map.get(date));
            if (i > 0) {
                String msgDate = DateUtils.dateToStr(date, TimingConstants.STRING_DATETIME_FORMAT);
                String msgDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, msgDate);
                log.info("use msg date build redis key: {}", msgDateKey);
                String disValue = String.valueOf(redisTemplate.opsForHash().get(discardDateKey, msgDateKey));
                Set<String> strings = disSTS(disValue);
                strings.add(msg);
                String value = disSTS(strings);
                log.info("insert into discardDateList value: {}", value);
                redisTemplate.opsForHash().put(discardDateKey, msgDateKey, value);
            }
            success = true;
        } catch (Exception e) {
            log.error("insert into discardDateList error: ", e);
        }
        return success;
    }

    /**
     * redis取消队列--opsHashValue转set
     *
     * @param opsHashValue
     * @return
     */
    public Set<String> disSTS(String opsHashValue) {
        String[] split = opsHashValue.split("\\|");
        Set<String> result = new HashSet<>();
        for (String value : split) {
            if (!StringUtils.isEmpty(value)) {
                result.add(value);
            }
        }
        return result;
    }

    /**
     * redis取消队列--set转opsHashValue
     *
     * @param valueSet
     * @return
     */
    public String disSTS(Set<String> valueSet) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : valueSet) {
            if(!StringUtils.isEmpty(value)) {
                stringBuilder.append(value);
                stringBuilder.append("|");
            }
        }
        return stringBuilder.toString();
    }
}
