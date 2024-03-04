package IDS_DirectQueue;

import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class DirectConsumerThread extends Thread {

    private String queueName;
    private String queueKey;
    private String threadName;

    public DirectConsumerThread(String queueName, String queueKey, String threadName) {
        this.queueName = queueName;
        this.queueKey = queueKey;
        this.threadName = threadName;
    }

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("my-topic-exchange", BuiltinExchangeType.TOPIC, true);

            channel.queueDeclare();
            channel.queueDeclare(queueName, true, false, false, null);

            channel.queueBind(queueName, "my-topic-exchange", queueKey);

            channel.confirmSelect();
            System.out.println(queueName + " has started listening for logs...");

            //Response messages
            String confirmMessage = "Login credentials received from Employee Device ID: ";
            String denyMessage = "Login credentials incorrect from Employee Device ID: ";

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String log = new String(delivery.getBody(), "UTF-8");
                System.out.println(threadName + " Received <" + log + ">");
                //According to the log, the consumer sends a response to the appropriate employee device
                if (log.contains("SUCCESSFUL")){
                    channel.basicPublish("my-topic-exchange", "response.Server", null, confirmMessage.getBytes(StandardCharsets.UTF_8));
                }else{
                    channel.basicPublish("my-topic-exchange", "response.Server", null, denyMessage.getBytes(StandardCharsets.UTF_8));
                }
            };

            while (true) {
                Thread.sleep(100);
                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
