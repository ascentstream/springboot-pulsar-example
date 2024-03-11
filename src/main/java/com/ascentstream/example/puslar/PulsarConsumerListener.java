package com.ascentstream.example.puslar;

import com.ascentstream.example.bean.Person;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.pulsar.annotation.PulsarListener;
import static com.ascentstream.example.constant.Constants.*;
import static java.lang.Thread.sleep;
import org.springframework.stereotype.Component;

@Component
public class PulsarConsumerListener {

    @PulsarListener(
            subscriptionName = "sub-test",
            topics = STRING_TOPIC,
            subscriptionType = SubscriptionType.Shared
    )
    public void stringTopicListener(Message<byte[]>  message) {
        /*
         * 1.topic 生产和消费使用的schema要匹配，否则无法没收到消息
         * 2.生产者如果没有显示的指定发送时的schema，则默认为byte[]
         */
        System.out.println("Received byte message: " + new String(message.getValue()) + ", produce name : " + message.getProducerName());

    }
    @PulsarListener(
            subscriptionName = "sub-test",
            topics = PERSON_TOPIC,
            subscriptionType = SubscriptionType.Shared,
            schemaType = SchemaType.JSON
    )
    public void personTopicListener(Message<Person> message) {
        /**
         * AVRO或JSON类型的schema,需要提前为PulsarClient注册配置其schema信息
         */
        System.out.println("Received object message: " + message.getValue() + ", produce name : " + message.getProducerName());

    }

    @PulsarListener(
            subscriptionName = "sub-delay",
            topics = STRING_DELAY_TOPIC,
            subscriptionType = SubscriptionType.Shared
    )
    public void delayMessageTopicListener(Message<byte[]> message) {
        System.out.println("Received delay message: " + new String(message.getValue()) + " at : " + System.currentTimeMillis() + ", produce name : " + message.getProducerName());
    }

    @PulsarListener(
            subscriptionName = "sub-test",
            topics = UN_ACK_TOPIC,
            subscriptionType = SubscriptionType.Shared,
            properties = {"ackTimeoutMillis=2000"}
    )
    public void unAckTopicListener(Message<byte[]> message) throws InterruptedException {
        /**
         * 如果消息处理逻辑耗时过长超过了ackTimeoutMillis设置的时间活着手动触发unack，会进行重新投递。
         * PulsarListener支持properties参数拓展ConsumerConfigurationData支持所有的配置
         */
        System.out.println("Received byte message: " + new String(message.getValue()) + ", produce name : " + message.getProducerName());
        sleep(5000);
    }


    @PulsarListener(
            subscriptionName = "sub-test",
            topics = BATCH_MESSAGE_TOPIC,
            subscriptionType = SubscriptionType.Shared,
            batch = true
    )
    public void batchTopicListener(Messages<byte[]> messages) {
        /**
         * BatchReceive消费
         */
        System.out.println("Batch size ：" + messages.size());
        messages.forEach((message) -> System.out.println("Received byte message: " + new String(message.getValue()) + ", produce name : " + message.getProducerName()));
    }

}