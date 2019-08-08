package com.rocketmq.producerandconsumer.publish.generateClass;

import com.itranswarp.compiler.JavaStringCompiler;
import com.rocketmq.producerandconsumer.publish.SimpleRocketMqListener;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

/**
 * @ProjectName: producerandconsumer
 * @Package: com.rocketmq.producerandconsumer.publish.generateClass
 * @ClassName: RocketMqListenerGenerateFactory
 * @Author: chinasoft.k.li
 * @Description:
 * @Date: 2019/8/7 15:18
 * @Version: 1.0
 */
public class RocketMqListenerGenerateFactory {

    public static Class<SimpleRocketMqListener> generate(String javaName) {
        JavaStringCompiler compiler = new JavaStringCompiler();
        Map<String, byte[]> results = null;
        try {
            String defaultSimpleRocketMqListenerImpl = JAVA_SOURCE_CODE.replaceAll("DefaultSimpleRocketMqListenerImpl", javaName);
            results = compiler.compile(javaName+".java", defaultSimpleRocketMqListenerImpl);
            Class<?> clazz = compiler.loadClass("com.rocketmq.producerandconsumer.publish.generateClass."+javaName, results);
            return (Class<SimpleRocketMqListener>) clazz;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String JAVA_SOURCE_CODE = "package com.rocketmq.producerandconsumer.publish.generateClass;\n" +
            "\n" +
            "import com.rocketmq.producerandconsumer.publish.SimpleRocketMqListener;\n" +
            "import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;\n" +
            "\n" +
            "@RocketMQMessageListener(topic = \"springTopic\", consumerGroup = \"my-consumer_test-topic-1\")\n" +
            "public class DefaultSimpleRocketMqListenerImpl extends SimpleRocketMqListener {\n" +
            "}\n"
           ;
}
