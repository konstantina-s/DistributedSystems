package IDS;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.util.ArrayList;

public class ConsumerThread extends Thread {

    private String exchangeName;
    private String interfaceName;
    private ArrayList<String> bindingKeys;
    private String threadName;

    public ConsumerThread(String exchangeName, String interfaceName, ArrayList<String> bindingKeys, String threadName) {
        this.exchangeName = exchangeName;
        this.interfaceName = interfaceName;
        this.bindingKeys = bindingKeys;
        this.threadName = threadName;
    }

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(exchangeName, "topic");

            channel.queueDeclare();
            channel.queueDeclare(interfaceName, false, false, false, null);

            for (String bk : bindingKeys) {
                channel.queueBind(interfaceName, exchangeName, bk);
            }

            System.out.println(interfaceName + " has started listening for logs on " + exchangeName);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String log = new String(delivery.getBody(), "UTF-8");
                System.out.println(threadName + " Received <" + log + "> from " + exchangeName);
            };

            while (true) {
                Thread.sleep(100);
                channel.basicConsume(interfaceName, true, deliverCallback, consumerTag -> {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

