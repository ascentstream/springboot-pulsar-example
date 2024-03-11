package com.ascentstream.example.puslar;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.BatcherBuilder;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.stereotype.Component;

@Component
public class PulsarProducer<T> {
    @Autowired
    PulsarTemplate<T> pulsarTemplate;

    //消息发送
    public MessageId sendMessage(String topic, T message) throws PulsarClientException {
        return this.pulsarTemplate.send(topic, message);
    }

    //异步消息发送
    public CompletableFuture<MessageId> sendMessageAsync(String topic, T message) throws PulsarClientException {
        return this.pulsarTemplate.sendAsync(topic, message);
    }

    //延迟消息发送
    public MessageId sendDelayMessage(String topic, T message, long delay, TimeUnit unit) throws PulsarClientException {
        /**
         * MessageCustomizer可以用来来配置消息延迟、在特定时间发送、禁用跨地域复制等。
         */
        return this.pulsarTemplate.newMessage(message).withTopic(topic)
                .withMessageCustomizer(m -> {
                    m.deliverAfter(delay,unit).disableReplication();
                }).send();
    }

    //自定义produce参数发送消息
    public MessageId sendMessageWithProducerCustomizer(String topic, T message, int timeout, TimeUnit unit, BatcherBuilder builder) throws PulsarClientException {
        /**
         * ProducerCustomizer可用于添加超时时间、访问模式、自定义消息路由和拦截器，以及启用或禁用chunk和batch等。
         * ProducerCustomizer的每次初始化都会构建一个新的Producer对象
         */
        return this.pulsarTemplate.newMessage(message).withTopic(topic)
                .withProducerCustomizer(pc->{
                    pc.batcherBuilder(builder).sendTimeout(timeout,unit).enableBatching(true).batchingMaxMessages(10);
                }).send();
    }


}