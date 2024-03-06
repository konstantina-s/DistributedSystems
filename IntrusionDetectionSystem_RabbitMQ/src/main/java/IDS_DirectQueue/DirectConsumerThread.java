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

    String ID (String log){
        String[] ID = log.split("ID");
        return ID[1].replaceAll("[^0-9]", "");

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
            String confirmMessage = " Login credentials received";
            String denyMessage = " Login credentials incorrect";

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String log = new String(delivery.getBody(), "UTF-8");
                System.out.println(threadName + " Received <" + log + ">");
                //Extracting the Employee ID for the queueKey
                String employeeID = ID(log);
                //According to the log, the consumer sends a response to the appropriate employee device
                if (log.contains("SUCCESSFUL")){
                    channel.basicPublish("my-topic-exchange", "response." + employeeID, null, ("Employee ID: " + employeeID + confirmMessage).getBytes(StandardCharsets.UTF_8));
                }else{
                    channel.basicPublish("my-topic-exchange", "response." + employeeID, null, ("Employee ID: " + employeeID + denyMessage).getBytes(StandardCharsets.UTF_8));
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
