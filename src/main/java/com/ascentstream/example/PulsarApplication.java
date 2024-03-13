package com.ascentstream.example;

import static com.ascentstream.example.constant.Constants.USER_DEAD_LETTER_TOPIC;
import com.ascentstream.example.bean.Person;
import com.ascentstream.example.puslar.PulsarConsumerListener;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.BatchReceivePolicy;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.Schema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.pulsar.annotation.EnablePulsar;
import org.springframework.pulsar.core.ConsumerBuilderCustomizer;
import org.springframework.pulsar.core.DefaultSchemaResolver;
import org.springframework.pulsar.core.SchemaResolver;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnablePulsar
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.ascentstream.example"})
public class PulsarApplication {
    public static void main(String[] args) {
        SpringApplication.run(PulsarApplication.class, args);
    }

    @Bean
    public PulsarConsumerListener listener() {
        return new PulsarConsumerListener();
    }
    @Bean
    public SchemaResolver.SchemaResolverCustomizer<DefaultSchemaResolver> schemaResolverCustomizer() {
        //给Person对象注册schema
        return (schemaResolver) -> {
            schemaResolver.addCustomSchemaMapping(Person.class, Schema.JSON(Person.class));
        };
    }

    /**
     * 注册死信队列策略
     * @return
     */
    @Bean
    DeadLetterPolicy deadLetterPolicy() {
        return DeadLetterPolicy.builder()
                .maxRedeliverCount(1)
                .deadLetterTopic(USER_DEAD_LETTER_TOPIC)
                .build();
    }

    /**
     * 注册自定义BatchReceive策略
     * @return
     */
    @Bean
    ConsumerBuilderCustomizer<byte[]> batchReceivePolicyCustomizer() {
        /**
         * 批量接收策略可以限制单个批次中消息的数量和字节数，并且可以指定等待该批次足够消息的超时时间。
         * 只要满足任何一个条件（有足够的消息数，有足够的消息大小，等待超时），就会完成此批接收。
         * 示例： 1.如果设置 maxNumMessages = 10, maxSizeOfMessages = 1MB 并且没有超时，这意味着 Consumer.batchReceive()将一直等待，直到有足够的消息。
         *       2.如果设置maxNumberOfMessages = 0，maxNumBytes = 0，timeout = 100ms，表示Consumer.batchReceive()无论消息是否足够，都会等待100ms。
         * 注意：必须指定消息限制（maxNumMessages，maxNumBytes）或等待超时。 否则，Messages ingest Message 将永远不会结束。
         */
        BatchReceivePolicy batchReceivePolicy = BatchReceivePolicy.builder().
                maxNumBytes(10485760).maxNumMessages(10).timeout(100, TimeUnit.MILLISECONDS).build();
        return consumerBuilder -> consumerBuilder.batchReceivePolicy(batchReceivePolicy);
    }
}
