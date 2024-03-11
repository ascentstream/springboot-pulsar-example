package com.ascentstream.example;

import static com.ascentstream.example.constant.Constants.USER_DEAD_LETTER_TOPIC;
import com.ascentstream.example.bean.Person;
import com.ascentstream.example.puslar.PulsarConsumerListener;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.Schema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.pulsar.annotation.EnablePulsar;
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
     * 注册死信队列
     * @return
     */
    @Bean
    DeadLetterPolicy deadLetterPolicy() {
        return DeadLetterPolicy.builder()
                .maxRedeliverCount(1)
                .deadLetterTopic(USER_DEAD_LETTER_TOPIC)
                .build();
    }

}
