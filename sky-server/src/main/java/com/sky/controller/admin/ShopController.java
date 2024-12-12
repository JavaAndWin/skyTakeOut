package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 商店信息接口
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "商店信息接口")
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String Key = "shop_status";
    /**
     * 更改店铺营业状态
     *
     * @param status
     */
    @PutMapping("/{status}")
    public void updateStatus(@PathVariable Integer status) {
        log.info("更改店铺营业状态:{}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(Key, status);

    }

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
