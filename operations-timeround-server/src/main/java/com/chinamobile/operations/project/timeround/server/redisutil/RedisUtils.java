package com.chinamobile.operations.project.timeround.server.redisutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Floki on 2018/4/17.
 */
@Component
public class RedisUtils {
    /**
     * Redis Key 名称的分割字符
     */
    public final static String KEY_SPLIT_CHAR = ":";

    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 构建 Redis Key 名称
     *
     * @param project 项目名称
     * @param module 模块名称
     * @param func 功能名称
     * @param args 自定义
     * @return
     */
    public String buildRedisKey(String project, String module, String func, String...args) {
        StringBuffer key = new StringBuffer(project);
        key.append(KEY_SPLIT_CHAR).append(module).append(KEY_SPLIT_CHAR).append(func);
        for (String arg : args) {
            key.append(KEY_SPLIT_CHAR).append(arg);
        }
        return key.toString();
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    @SuppressWarnings("unchecked")
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    @SuppressWarnings("unchecked")
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 查询指定前缀的KEY
     *
     * */
    public Set<String> getKeys(String  prefix){
    	
    	Set<String> set=redisTemplate.keys(prefix);
    	return set;
    }

    /**
     * 动态切换数据库号
     * @param num 库号
     */
    public void changeDB(Integer num){
        JedisConnectionFactory connectionFactory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
        connectionFactory.setDatabase(num);
    }

    /**
      * @Author LiuQuan
      * @Description 刷新缓存时间
      * @Date 2019/9/10 20:52
      * @Param [key, expireTime]
      * @return boolean
     **/
    @SuppressWarnings("unchecked")
    public boolean expire(final String key, Long expireTime) {
        boolean result = false;
        try {
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取redis指定map里的值
     * @param mapKey
     * @param valueKey
     * @return
     */
    public Object getMapValue(String mapKey, String valueKey){
        return redisTemplate.opsForHash().get(mapKey, valueKey);
    }

    /**
     * 获取redis指定map里全部键值对
     * @param mapKey
     * @return
     */
    public Map getMapAllValues(String mapKey){
        return redisTemplate.opsForHash().entries(mapKey);
    }
}













