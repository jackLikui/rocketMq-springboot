package com.rocketmq.producerandconsumer.publish;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmq.producerandconsumer.publish.annotation.RocketMqEventListener;
import com.rocketmq.producerandconsumer.publish.generateClass.RocketMqListenerGenerateFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ProjectName: producerandconsumer
 * @Package: com.rocketmq.producerandconsumer.publish
 * @ClassName: ConsumerManager
 * @Author: chinasoft.k.li
 * @Description:
 * @Date: 2019/8/7 11:40
 * @Version: 1.0
 */
@Component
public class RocketMqConsumerManager implements ApplicationContextAware, SmartInitializingSingleton {

    @Autowired
    private AbstractApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private AtomicLong counter = new AtomicLong(0L);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = (AbstractApplicationContext)applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.run();
    }

    public void run(){
        Collection<ApplicationListener<?>> applicationListeners = context.getApplicationListeners();
        for (ApplicationListener listener:applicationListeners) {
            if(listener instanceof ApplicationListenerMethodAdapter){
                ApplicationListenerMethodAdapter applicationListenerMethodAdapter = (ApplicationListenerMethodAdapter) listener;
                try {
                    Object bean = getTargetBean(applicationListenerMethodAdapter);
                    Method listenerMethod = getListenerMethod(applicationListenerMethodAdapter);
                    RocketMqEventListener annotation = listenerMethod.getAnnotation(RocketMqEventListener.class);
                    if(null != annotation){
                        int parameterCount = listenerMethod.getParameterCount();
                        if (parameterCount > 1) {
                            throw new IllegalStateException("Maximum one parameter is allowed for event listener method: " + listenerMethod);
                        }else{
                            String simpleRocketMqListenerName = String.format("%s%s", SimpleRocketMqListener.class.getSimpleName(), this.counter.incrementAndGet());
                            Class<SimpleRocketMqListener> simpleRocketMqListenerClazz = RocketMqListenerGenerateFactory.generate(simpleRocketMqListenerName);
                            SimpleRocketMqListener simpleRocketMqListener = simpleRocketMqListenerClazz.getDeclaredConstructor().newInstance();
                            simpleRocketMqListener.init(listenerMethod,bean,objectMapper);
                            RocketMQMessageListener rocketMQMessageListenerAnno = simpleRocketMqListenerClazz.getAnnotation(RocketMQMessageListener.class);
                            this.setCustomPropertisToListener(rocketMQMessageListenerAnno,annotation,listenerMethod.getParameterTypes()[0].getSimpleName(),listenerMethod.getParameterTypes()[0].getSimpleName());
                            ((GenericApplicationContext)this.context).registerBean(simpleRocketMqListenerName,SimpleRocketMqListener.class
                                    ,()-> simpleRocketMqListener, new BeanDefinitionCustomizer[0]);
//                            startConsumer(bean, listenerMethod);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setCustomPropertisToListener(RocketMQMessageListener rocketMQMessageListenerAnno, RocketMqEventListener annotation, String newTopic, String group) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(rocketMQMessageListenerAnno);
            Field values = invocationHandler.getClass().getDeclaredField("memberValues");
            values.setAccessible(true);
            Map<String, Object> memberValues =(Map<String, Object>) values.get(invocationHandler);
            memberValues.put("selectorType",annotation.selectorType());
            memberValues.put("selectorExpression",annotation.selectorExpression());
            memberValues.put("consumeMode",annotation.consumeMode());
            memberValues.put("messageModel",annotation.messageModel());
            memberValues.put("consumeThreadMax",annotation.consumeThreadMax());
            memberValues.put("consumeTimeout",annotation.consumeTimeout());
            memberValues.put("accessKey",annotation.accessKey());
            memberValues.put("secretKey",annotation.secretKey());
            memberValues.put("enableMsgTrace",annotation.enableMsgTrace());
            memberValues.put("customizedTraceTopic",annotation.customizedTraceTopic());
            memberValues.put("nameServer",annotation.nameServer());
            memberValues.put("accessChannel",annotation.accessChannel());
            memberValues.put("topic", newTopic);
            memberValues.put("consumerGroup",group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeTopicAndGroup(RocketMQMessageListener annotation,String newTopic,String group) {
        try {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field values = invocationHandler.getClass().getDeclaredField("memberValues");
            values.setAccessible(true);
            Map<String, Object> memberValues =(Map<String, Object>) values.get(invocationHandler);
            memberValues.put("topic", newTopic);
            memberValues.put("consumerGroup",group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startConsumer(Object bean, Method listenerMethod) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_4"+listenerMethod.getParameterTypes()[0].getSimpleName());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setNamesrvAddr("localhost:9876");
        consumer.subscribe(listenerMethod.getParameterTypes()[0].getSimpleName(), "*");
        consumer.registerMessageListener((final List<MessageExt> msgs,
                                          final ConsumeConcurrentlyContext context)->{
            try {
                Object paramer = this.doConvertMessage(msgs.get(0),listenerMethod.getParameterTypes()[0]);
                listenerMethod.invoke(bean,paramer);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }

    private Object getTargetBean(ApplicationListenerMethodAdapter applicationListenerMethodAdapter) throws NoSuchFieldException, IllegalAccessException {
        Field beanName = applicationListenerMethodAdapter.getClass().getDeclaredField("beanName");
        beanName.setAccessible(true);
        return context.getBean((String) beanName.get(applicationListenerMethodAdapter));
    }

    private Method getListenerMethod(ApplicationListenerMethodAdapter applicationListenerMethodAdapter) throws NoSuchFieldException, IllegalAccessException {
        Field field = applicationListenerMethodAdapter.getClass().getDeclaredField("method");
        field.setAccessible(true);
        return (Method) field.get(applicationListenerMethodAdapter);
    }

    private Object doConvertMessage(MessageExt messageExt,Class messageType) {
        if (Objects.equals(messageType, MessageExt.class)) {
            return messageExt;
        } else {
            String str = new String(messageExt.getBody(), Charset.forName("utf-8"));
            if (Objects.equals(messageType, String.class)) {
                return str;
            } else {
                try {
                    return this.objectMapper.readValue(str, messageType);
                } catch (Exception var4) {
                    throw new RuntimeException("cannot convert message to " + messageType, var4);
                }
            }
        }
    }

}
