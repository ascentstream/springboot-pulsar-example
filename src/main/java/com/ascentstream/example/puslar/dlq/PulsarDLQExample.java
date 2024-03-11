package com.ascentstream.example.puslar.dlq;

import static com.ascentstream.example.constant.Constants.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

@Component
public class PulsarDLQExample {

    @PulsarListener(
            subscriptionName = "sub-test",
            topics = STRING_TOPIC,
            subscriptionType = SubscriptionType.Shared,
            deadLetterPolicy = "deadLetterPolicy",
            properties = {"retryEnable=true"},
            batch = true
    )
    public void topicListener(List<Message<byte[]>> messages, Consumer<String> consumer) {
        /**
         * 死信队列功能需要自定义死信队列策略并且开启retry
         */
        int i = 0;
        for (Message<byte[]> message : messages){
            if(i%2==0){
                System.out.println("Received byte message: " + new String(message.getValue())) ;
            } else {
                /**
                 * 模拟消息处理失败后进入死信队列处理逻辑
                 */
                try {
                    System.out.println("Received byte message error, will add to deadLetter topic after 1 second ! " + new String(message.getValue())) ;
                    consumer.reconsumeLater(message,1, TimeUnit.SECONDS);
                } catch (PulsarClientException e) {
                   e.printStackTrace();
                }
            }
          i++;
        }

    }

    @PulsarListener(
            subscriptionName = "dlq-sub",
            topics = USER_DEAD_LETTER_TOPIC,
            subscriptionType = SubscriptionType.Shared
    )
    public void userDlqTopicListener(byte[] str){
        System.out.println("Received message in from DLQ topic  : " + new String(str));
    }



}