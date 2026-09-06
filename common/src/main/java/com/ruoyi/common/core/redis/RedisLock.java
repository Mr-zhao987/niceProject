package com.ruoyi.common.core.redis;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

/**
 * Redis 分布式锁工具类
 * 基于 SETNX 实现，支持自动续期和 Lua 脚本原子性解锁
 *
 * @author ruoyi
 */
@Component
public class RedisLock
{
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 锁前缀
     */
    private static final String LOCK_PREFIX = "redis_lock:";

    /**
     * 解锁 Lua 脚本，保证原子性
     * 只有当 value 与传入值相等时才删除
     */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    /**
     * 尝试获取锁（不等待）
     *
     * @param key 锁的键
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @return 锁标识（用于解锁），获取失败返回 null
     */
    public String tryLock(String key, long expireTime, TimeUnit timeUnit)
    {
        return tryLock(key, expireTime, timeUnit, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 尝试获取锁（支持等待）
     *
     * @param key 锁的键
     * @param expireTime 过期时间
     * @param timeUnit 时间单位
     * @param waitTime 等待时间
     * @param waitUnit 等待时间单位
     * @return 锁标识（用于解锁），获取失败返回 null
     */
    public String tryLock(String key, long expireTime, TimeUnit timeUnit, long waitTime, TimeUnit waitUnit)
    {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        long expireMillis = timeUnit.toMillis(expireTime);
        long waitMillis = waitUnit.toMillis(waitTime);
        long startMillis = System.currentTimeMillis();

        while (true)
        {
            // 使用 SETNX 原子性加锁
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireMillis, TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(acquired))
            {
                return lockValue;
            }

            // 检查是否超过等待时间
            if (waitMillis > 0 && System.currentTimeMillis() - startMillis < waitMillis)
            {
                try
                {
                    // 短暂休眠后重试
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * 释放锁
     * 使用 Lua 脚本保证原子性，只有锁的持有者才能释放
     *
     * @param key 锁的键
     * @param lockValue 锁标识（加锁时返回的值）
     * @return 是否释放成功
     */
    public boolean unlock(String key, String lockValue)
    {
        if (lockValue == null)
        {
            return false;
        }
        String lockKey = LOCK_PREFIX + key;
        RedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
        return Long.valueOf(1).equals(result);
    }

    /**
     * 释放锁（带重试）
     *
     * @param key 锁的键
     * @param lockValue 锁标识
     * @param retryCount 重试次数
     * @return 是否释放成功
     */
    public boolean unlockWithRetry(String key, String lockValue, int retryCount)
    {
        for (int i = 0; i <= retryCount; i++)
        {
            if (unlock(key, lockValue))
            {
                return true;
            }
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * 检查锁是否被持有
     *
     * @param key 锁的键
     * @return 是否被持有
     */
    public boolean isLocked(String key)
    {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    /**
     * 获取锁的剩余过期时间
     *
     * @param key 锁的键
     * @param timeUnit 时间单位
     * @return 剩余时间，-1表示无过期时间，-2表示锁不存在
     */
    public long getLockTTL(String key, TimeUnit timeUnit)
    {
        String lockKey = LOCK_PREFIX + key;
        return redisTemplate.getExpire(lockKey, timeUnit);
    }

    /**
     * 强制释放锁（不检查持有者，慎用）
     * 仅用于管理员操作或紧急情况
     *
     * @param key 锁的键
     * @return 是否释放成功
     */
    public boolean forceUnlock(String key)
    {
        String lockKey = LOCK_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
    }
}
