package com.ruoyi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁注解
 * 用于方法级别的分布式锁控制，防止高并发场景下的资源竞争
 *
 * @author ruoyi
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisLockAnnotation
{
    /**
     * 锁的键，支持 SpEL 表达式
     * 例如：#id、#user.id、'lock:' + #name
     * 如果为空，则使用类名+方法名+参数作为锁的键
     */
    String key() default "";

    /**
     * 锁的前缀
     */
    String prefix() default "lock";

    /**
     * 过期时间
     */
    long expireTime() default 30;

    /**
     * 过期时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 等待时间（获取锁的最大等待时间）
     * 设置为0表示不等待，立即返回
     */
    long waitTime() default 0;

    /**
     * 等待时间单位
     */
    TimeUnit waitUnit() default TimeUnit.MILLISECONDS;

    /**
     * 获取锁失败时的提示消息
     */
    String message() default "系统繁忙，请稍后重试";

    /**
     * 是否在异常时自动释放锁
     * 默认为 true，建议保持默认
     */
    boolean autoUnlock() default true;
}
