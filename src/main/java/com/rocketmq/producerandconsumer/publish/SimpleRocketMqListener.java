package com.rocketmq.producerandconsumer.publish;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

@RocketMQMessageListener(topic = "springTopic", consumerGroup = "my-consumer_test-topic-1")
public abstract class SimpleRocketMqListener implements RocketMQListener<MessageExt> {

    private Method method;

    private Object targetBean;

    private ObjectMapper objectMapper;
    public SimpleRocketMqListener() {
    }

    public void init(Method method, Object targetBean, ObjectMapper objectMapper) {
        this.method = method;
        this.targetBean = targetBean;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            Class<?> parameterType = method.getParameterTypes()[0];
            String str = new String(messageExt.getBody(), Charset.forName("utf-8"));
            method.invoke(targetBean,objectMapper.readValue(str,parameterType));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
