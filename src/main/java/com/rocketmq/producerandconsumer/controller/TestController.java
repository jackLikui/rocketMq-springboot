package com.rocketmq.producerandconsumer.controller;

import com.rocketmq.producerandconsumer.event.OrderPaidEvent;
import com.rocketmq.producerandconsumer.event.OrderPaidEvent1;
import com.rocketmq.producerandconsumer.publish.RocketMqEventPublisher;
import com.rocketmq.producerandconsumer.publish.annotation.RocketMqEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @ProjectName: producerandconsumer
 * @Package: com.rocketmq.producerandconsumer.controller
 * @ClassName: TestController
 * @Author: chinasoft.k.li
 * @Description:
 * @Date: 2019/8/1 14:28
 * @Version: 1.0
 */
@RestController
public class TestController {

    @Autowired
    private RocketMqEventPublisher rocketMqEventPublisher;

    @RocketMqEventListener
    public void onOrderPaidEvent(OrderPaidEvent orderPaidEvent){
        System.out.println("orderPaidEvent = [" + orderPaidEvent + "]");
    }

    @RocketMqEventListener
    public void onOrderPaidEvent1(OrderPaidEvent1 orderPaidEvent){
        System.out.println("orderPaidEvent1 = [" + orderPaidEvent + "]");
    }

    @GetMapping("/send")
    public String send(@RequestParam(required = false, defaultValue = "hello spring") String message) {
        rocketMqEventPublisher.publishEvent(new OrderPaidEvent("0",BigDecimal.ZERO));
        rocketMqEventPublisher.publishEvent(new OrderPaidEvent1("1",BigDecimal.ONE));
        return "ok";
    }

}
