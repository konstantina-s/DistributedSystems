package IDS_DirectQueue;

import com.rabbitmq.client.*;

public class DirectQueueConsumer {

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare("my-topic-exchange", BuiltinExchangeType.TOPIC, true);
            //Declaring two queues that will sort logs into successful and failed logs
            channel.queueDeclare("Success", true, false, false, null);
            channel.queueDeclare("Fail", true, false, false, null);

            //The successful and failed queueKeys will accordingly sort the incoming logs if they start with the keyword and zero or more words after
            DirectConsumerThread ct1 = new DirectConsumerThread("Success", "success.#", "SUCCESSFUL_LOGIN_THREAD");
            DirectConsumerThread ct2 = new DirectConsumerThread("Fail", "fail.#", "FAILED_LOGIN_THREAD");
            ct1.start();
            ct2.start();
        }
    }
}