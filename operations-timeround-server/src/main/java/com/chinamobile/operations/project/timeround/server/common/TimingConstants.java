package com.chinamobile.operations.project.timeround.server.common;

/**
 * @author Bowen
 * Timing常量
 */
public class TimingConstants {

    /**rediskey构建：文档*/
    public final static String REDIS_BUILD_PROJECT = "timeRound";
    /**rediskey构建：功能*/
    public final static String REDIS_BUILD_FUNCTION = "solt";
    /**rediskey构建：写入锁*/
    public final static String REDIS_BUILD_WRITE_LOCK = "writeLock";
    /**rediskey构建：读取锁*/
    public final static String REDIS_BUILD_READ_LOCK = "readLock";
    /**槽位格式*/
    public static final String STRING_DATETIME_FORMAT = "yyyyMMddHHmm";
    /**废弃列队*/
    public final static String REDIS_BUILD_DISCARD_LIST = "discard";
    /**失败列队*/
    public final static String REDIS_BUILD_FAILED_LIST = "failed";

}
