package com.rocketmq.producerandconsumer.publish.generateClass;

import com.rocketmq.producerandconsumer.publish.SimpleRocketMqListener;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;

@RocketMQMessageListener(topic = "springTopic", consumerGroup = "my-consumer_test-topic-1")
public class DefaultSimpleRocketMqListenerImpl extends SimpleRocketMqListener {
}
