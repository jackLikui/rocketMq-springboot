package com.rocketmq.producerandconsumer.publish;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: producerandconsumer
 * @Package: com.rocketmq.producerandconsumer.publish
 * @ClassName: RocketMqEventPublisher
 * @Author: chinasoft.k.li
 * @Description:
 * @Date: 2019/8/6 15:44
 * @Version: 1.0
 */
@Service
public class RocketMqEventPublisher implements ApplicationEventPublisher {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void publishEvent(Object o) {
        Class<?> aClass = o.getClass();
        rocketMQTemplate.asyncSend(aClass.getSimpleName(), o, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("sendResult = [" + sendResult + "]");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("throwable = [" + throwable + "]");
            }
        });
    }
}
