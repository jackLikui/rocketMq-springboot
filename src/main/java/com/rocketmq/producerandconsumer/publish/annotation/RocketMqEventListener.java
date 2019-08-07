package com.rocketmq.producerandconsumer.publish.annotation;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @ProjectName: producerandconsumer
 * @Package: com.rocketmq.producerandconsumer.publish.annotation
 * @ClassName: RocketMqEventListener
 * @Author: chinasoft.k.li
 * @Description:
 * @Date: 2019/8/6 15:58
 * @Version: 1.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EventListener
public @interface RocketMqEventListener {

    SelectorType selectorType() default SelectorType.TAG;

    String selectorExpression() default "*";

    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    MessageModel messageModel() default MessageModel.CLUSTERING;

    int consumeThreadMax() default 64;

    long consumeTimeout() default 30000L;

    String accessKey() default "${rocketmq.consumer.access-key:}";

    String secretKey() default "${rocketmq.consumer.secret-key:}";

    boolean enableMsgTrace() default true;

    String customizedTraceTopic() default "${rocketmq.consumer.customized-trace-topic:}";

    String nameServer() default "${rocketmq.name-server:}";

    String accessChannel() default "${rocketmq.access-channel:}";
}
