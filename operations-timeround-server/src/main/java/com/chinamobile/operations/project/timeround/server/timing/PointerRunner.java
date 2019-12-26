package com.chinamobile.operations.project.timeround.server.timing;

import com.chinamobile.operations.project.seed.redis.util.RedisUtils;
import com.chinamobile.operations.project.timeround.server.common.TimingConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Bowen
 * 指针启动器
 */
@Component
@Slf4j
public class PointerRunner implements ApplicationRunner {

    @Autowired
    private Pointer pointer;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("clean redis readLock ...");
        String readLock = redisUtils.buildRedisKey(TimingConstants.REDIS_BUILD_PROJECT, TimingConstants.REDIS_BUILD_FUNCTION, TimingConstants.REDIS_BUILD_READ_LOCK);
        redisUtils.remove(readLock);
        log.info("pointer run start with application ...");
        pointer.startThread();
    }

}
