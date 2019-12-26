package com.chinamobile.operations.project.timeround.server.timing;

import com.chinamobile.operations.project.seed.redis.util.RedisUtils;
import com.chinamobile.operations.project.seed.util.DateUtils;
import com.chinamobile.operations.project.timeround.server.common.TimingConstants;
import com.chinamobile.operations.project.timeround.server.redisutil.MSGUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bowen
 * 指针
 */
@Component
@Slf4j
public class Pointer {

    @Value("${spring.timing.lockTimeOut}")
    private long lockTimeOut;
    @Value("${spring.timing.pointerStep}")
    private long step;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CarriedOut carriedOut;
    @Autowired
    private MSGUtil msgUtil;


    @Async("TaskExecutor")
    public void startThread() {
        log.info("Pointer round start...");
        round();
    }

    /**
     * 指针旋转
     */
    private void round() {
        try {
            //读取锁
            String readLock = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_FUNCTION, TimingConstants.REDIS_BUILD_READ_LOCK);
            //判断是否有锁
            while (true) {
                if (!redisTemplate.hasKey(readLock)) {
                    //无锁创建读取锁
                    redisUtils.set(readLock, readLock, lockTimeOut);
                    //指针按步长跳动执行任务
                    while (true) {
                        //判断上锁是否成功
                        if (redisTemplate.hasKey(readLock)) {
                            log.debug("=================>pointer run<=================");
                            //成功每隔步长执行一次任务
                            //Date date = new Date();
                            Calendar calendar = Calendar.getInstance();
                            Date date = calendar.getTime();
                            calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) - 1));
                            Date previousDate = calendar.getTime();
                            String msgDate = DateUtils.dateToStr(date, TimingConstants.STRING_DATETIME_FORMAT);
                            String previousMsgDate = DateUtils.dateToStr(previousDate, TimingConstants.STRING_DATETIME_FORMAT);
                            String msgDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, msgDate);
                            if (redisTemplate.hasKey(msgDateKey)) {
                                List<String> msgRange = redisTemplate.opsForList().range(msgDateKey, 0, -1);
                                List<String> list = doTask(msgDateKey, msgRange, previousMsgDate);
                                clean(msgDateKey, list);
                            }
                            Thread.sleep(step);
                        } else {
                            //锁失效或上锁失败则重新抢锁
                            break;
                        }
                    }
                } else {
                    Thread.sleep(step);
                }
            }

        } catch (Exception e) {
            log.error("Pointer round error:", e);
        }
    }


    /**
     * 执行任务
     *
     * @param msgDateKey   执行任务列队时间key
     * @param msgRange     需要执行任务的内容集合
     * @param previousMsgDate 上一分钟
     * @return 是否执行完成
     */
    private List<String> doTask(String msgDateKey, List<String> msgRange, String previousMsgDate) {
        List<String> msgFinished = new ArrayList();
        List<String> list = msgRange.stream().distinct().collect(Collectors.toList());
        for (String msg : list) {
            if (check(msgDateKey, msg, previousMsgDate)) {
                String alreadyDo = todu(msg);
                msgFinished.add(alreadyDo);
            }
        }
        return msgFinished;
    }


    /**
     * 校对任务是否失效
     *
     * @param msgDateKey 执行任务列队时间key
     * @param msg        需要执行任务的内容
     * @return 是否需要执行
     */
    private Boolean check(String msgDateKey, String msg, String previousMsgDate) {
        Boolean needToDo = true;
        String discardDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_DISCARD_LIST);
        String previousDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, previousMsgDate);
        if (redisTemplate.hasKey(discardDateKey)) {
            if (redisTemplate.opsForHash().hasKey(discardDateKey, msgDateKey)) {
                String discardValue = String.valueOf(redisTemplate.opsForHash().get(discardDateKey, msgDateKey));
                Set<String> discardList = msgUtil.disSTS(discardValue);
                //校验
                if (discardList.contains(msg)) {
                    needToDo = false;
                    discardList.remove(msg);
                    String discard = msgUtil.disSTS(discardList);
                    redisTemplate.opsForHash().put(discardDateKey, msgDateKey, discard);
                    redisTemplate.opsForHash().delete(discardDateKey, previousDateKey);
                }
            }
        }
        return needToDo;
    }

    /**
     * 执行任务
     *
     * @param msg 任务内容
     * @return 已执行任务
     */
    private String todu(String msg) {
        String result = null;
        Boolean send = carriedOut.send(msg);
        if (send) {
            result = msg;
        }
        return result;
    }

    /**
     * 清理任务
     *
     * @param msgDateKey  执行任务列队时间key
     * @param msgFinished 已执行任务集合
     * @return 是否清理完成
     */
    private Boolean clean(String msgDateKey, List<String> msgFinished) {
        Boolean cleaned = false;
        try {
            List<String> range = redisTemplate.opsForList().range(msgDateKey, 0, -1);
            range.removeAll(msgFinished);
            if (0 != range.size()) {
                //操作失败队列
                String failedDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_FAILED_LIST);
                redisTemplate.opsForList().rightPushAll(failedDateKey, range);
            }
            redisTemplate.delete(msgDateKey);
            String discardDateKey = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_DISCARD_LIST);
            redisTemplate.opsForHash().delete(discardDateKey, msgDateKey);
            cleaned = true;
        } catch (Exception e) {
            log.error("clean msgFinished failed:", e);
        }
        return cleaned;
    }

}
