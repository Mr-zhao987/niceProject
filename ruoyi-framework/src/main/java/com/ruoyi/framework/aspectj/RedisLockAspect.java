package com.ruoyi.framework.aspectj;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.ruoyi.common.annotation.RedisLockAnnotation;
import com.ruoyi.common.core.redis.RedisLock;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;

/**
 * 分布式锁切面
 * 处理 @RedisLockAnnotation 注解
 *
 * @author ruoyi
 */
@Aspect
@Component
@Order(-1) // 保证在事务等其他切面之前执行
public class RedisLockAspect
{
    private static final Logger log = LoggerFactory.getLogger(RedisLockAspect.class);

    @Autowired
    private RedisLock redisLock;

    /**
     * 环绕通知，处理分布式锁
     */
    @Around("@annotation(com.ruoyi.common.annotation.RedisLockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLockAnnotation annotation = method.getAnnotation(RedisLockAnnotation.class);

        if (annotation == null)
        {
            return joinPoint.proceed();
        }

        // 生成锁的键
        String lockKey = generateKey(annotation, joinPoint);
        String lockValue = null;

        try
        {
            // 尝试获取锁
            lockValue = redisLock.tryLock(
                    lockKey,
                    annotation.expireTime(),
                    annotation.timeUnit(),
                    annotation.waitTime(),
                    annotation.waitUnit()
            );

            if (lockValue == null)
            {
                log.warn("获取分布式锁失败: key={}, thread={}", lockKey, Thread.currentThread().getName());
                throw new ServiceException(annotation.message());
            }

            log.debug("获取分布式锁成功: key={}, value={}", lockKey, lockValue);

            // 执行目标方法
            return joinPoint.proceed();
        }
        finally
        {
            // 自动释放锁
            if (annotation.autoUnlock() && lockValue != null)
            {
                try
                {
                    boolean released = redisLock.unlock(lockKey, lockValue);
                    if (released)
                    {
                        log.debug("释放分布式锁成功: key={}", lockKey);
                    }
                    else
                    {
                        log.warn("释放分布式锁失败（可能已过期）: key={}", lockKey);
                    }
                }
                catch (Exception e)
                {
                    log.error("释放分布式锁异常: key={}", lockKey, e);
                }
            }
        }
    }

    /**
     * 生成锁的键
     * 支持 SpEL 表达式解析（简化版，支持基本表达式）
     */
    private String generateKey(RedisLockAnnotation annotation, ProceedingJoinPoint joinPoint)
    {
        String prefix = annotation.prefix();
        String key = annotation.key();

        // 如果没有指定 key，使用类名+方法名+参数哈希
        if (StringUtils.isEmpty(key))
        {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = signature.getName();
            Object[] args = joinPoint.getArgs();

            StringBuilder sb = new StringBuilder();
            sb.append(className).append(":").append(methodName);
            if (args != null && args.length > 0)
            {
                for (Object arg : args)
                {
                    sb.append(":").append(arg == null ? "null" : arg.toString());
                }
            }
            return prefix + ":" + sb.toString();
        }

        // 简单的 SpEL 表达式解析（支持 #paramName 形式）
        if (key.startsWith("#"))
        {
            String paramName = key.substring(1);
            Object[] args = joinPoint.getArgs();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();

            if (parameterNames != null && args != null)
            {
                for (int i = 0; i < parameterNames.length; i++)
                {
                    if (parameterNames[i].equals(paramName) && args[i] != null)
                    {
                        return prefix + ":" + args[i].toString();
                    }
                }
            }
        }

        // 支持属性访问 #param.property 形式
        if (key.contains("."))
        {
            String[] parts = key.split("\\.");
            if (parts.length == 2 && parts[0].startsWith("#"))
            {
                String paramName = parts[0].substring(1);
                String propertyName = parts[1];
                Object[] args = joinPoint.getArgs();
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String[] parameterNames = signature.getParameterNames();

                if (parameterNames != null && args != null)
                {
                    for (int i = 0; i < parameterNames.length; i++)
                    {
                        if (parameterNames[i].equals(paramName) && args[i] != null)
                        {
                            try
                            {
                                // 通过反射获取属性值
                                String getterName = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                                Method getter = args[i].getClass().getMethod(getterName);
                                Object value = getter.invoke(args[i]);
                                return prefix + ":" + (value == null ? "null" : value.toString());
                            }
                            catch (Exception e)
                            {
                                log.warn("解析锁键表达式失败: {}", key, e);
                            }
                        }
                    }
                }
            }
        }

        // 直接返回配置的 key
        return prefix + ":" + key;
    }
}
