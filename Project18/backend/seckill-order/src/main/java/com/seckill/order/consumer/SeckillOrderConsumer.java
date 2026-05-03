package com.seckill.order.consumer;

import com.alibaba.fastjson.JSON;
import com.seckill.common.mq.SeckillOrderMessage;
import com.seckill.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SeckillOrderConsumer {

    private final OrderService orderService;

    @Bean
    public Consumer<SeckillOrderMessage> seckillOrderInput() {
        return message -> {
            log.info("收到秒杀订单消息: {}", JSON.toJSONString(message));
            try {
                orderService.processSeckillOrder(message);
            } catch (Exception e) {
                log.error("处理秒杀订单消息失败: {}", JSON.toJSONString(message), e);
            }
        };
    }
}
