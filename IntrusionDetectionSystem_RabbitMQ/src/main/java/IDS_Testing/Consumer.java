package IDS_Testing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // Declare a queue named 'hello'
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.exchangeDeclare(QUEUE_NAME,"fanout");

            System.out.println(" [*] Waiting for messages....");

            // Callback to handle incoming messages
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            };


            while (true) {
                // Consume messages from the queue
                Thread.sleep(1000);
                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                });
            }
        }
    }
}
