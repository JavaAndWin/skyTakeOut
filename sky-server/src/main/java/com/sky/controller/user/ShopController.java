package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 商店信息接口
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "商店信息接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String Key = "shop_status";
    /**
     * 获取店铺营业状态
     *
     * @return
     */
    @GetMapping("/status")
    public Result getStatus() {
        int status = (Integer)redisTemplate.opsForValue().get(Key);
        log.info("获取店铺营业状态:{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
