package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.config.RedissonConfig;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供RedisLimiter限流基础服务（通用代码，可以放到不同项目）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 参数key区分不同的限流器
     * 如果根据用户id限流，那么入参就是用户id
     * @param key
     */
    public void doRateLimit(String key){
        //创建一个限流器，名称为 key
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //设置限流器速率，key限流器每1秒允许2个请求
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        //获取一个令牌
        boolean tag = rateLimiter.tryAcquire(1);
        //判断是否成功获取
        if (!tag){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        //成功获取，放行

    }
}
