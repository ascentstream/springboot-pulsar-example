package com.ascentstream.example.schedule;

import static com.ascentstream.example.constant.Constants.*;
import com.ascentstream.example.bean.Person;
import com.ascentstream.example.puslar.PulsarProducer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.pulsar.client.api.BatcherBuilder;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProduceTask {

    @Autowired
    PulsarProducer<byte[]> stringProducer;
    @Autowired
    PulsarProducer<Person> personProducer;

    @Scheduled(fixedRate = 500000,initialDelay = 2000)
    public void send() throws PulsarClientException {

        long currentTimeMillis = System.currentTimeMillis();

        /**
         * 默认schema消息场景消息发送
         */
        String str = "[hello_world_" + currentTimeMillis+"]";
        MessageId id1 = stringProducer.sendMessage(STRING_TOPIC,str.getBytes(StandardCharsets.UTF_8));
        System.out.println("Send string message success, value : " + str + " , messageId : " + id1);

        /**
         * Json schema场景消息发送
         */
        Person person = Person.of().setAge(18).setName("张三").setCity("北京");
        MessageId id2 = personProducer.sendMessage(PERSON_TOPIC,person);
        System.out.println("Send person message success, value : " + person + " , messageId : " + id2);

        /**
         * 延迟消息场景消息发送
         */
        String str1 = "[hello_delay_message_"+ currentTimeMillis+"]";
        MessageId id3 = stringProducer.sendDelayMessage(STRING_DELAY_TOPIC,str1.getBytes(StandardCharsets.UTF_8),30,
                TimeUnit.SECONDS);
        System.out.println("Send delay message success, value : " + str1 + " , messageId : " + id3);

        /**
         * 自定义producer场景消息发送
         */
        Person person1 = Person.of().setAge(20).setName("李四").setCity("上海");
        MessageId id4 = personProducer.sendMessageWithProducerCustomizer(PERSON_TOPIC,person1,5,TimeUnit.SECONDS,
                BatcherBuilder.KEY_BASED);
        System.out.println("Send person message with producer customizer success, value : " + person1 + " , messageId : " + id4);

        /**
         * unack场景消息发送
         */
        String str2 = "[hello_unack_message_"+ currentTimeMillis+"]";
        MessageId id5 = stringProducer.sendMessage(UN_ACK_TOPIC,str2.getBytes(StandardCharsets.UTF_8));
        System.out.println("Send message success, value : " + str2 + " , messageId : " + id5);

        /**
         * batchReceive场景消息发送
         */
        for (int i = 0; i < 100; i++) {
            String str3 = "[hello_batch_message_"+ i +"]";
            stringProducer.sendMessageAsync(BATCH_MESSAGE_TOPIC,str3.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * 死信队列场景消息发送
         */
        for (int i = 0; i < 100; i++) {
            String str4 = "[hello_dead_letter_message_"+ i +"]";
            stringProducer.sendMessageAsync(STRING_TOPIC,str4.getBytes(StandardCharsets.UTF_8));
        }

    }
}
