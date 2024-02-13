package IDS_Testing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.ArrayList;

public class ConsumerThreadTest extends Thread {

    private String exchangeName;
    private String interfaceName;
    private ArrayList<String> bindingKeys;
    private String threadName;

    public ConsumerThreadTest(String exchangeName, String interfaceName, ArrayList<String> bindingKeys, String threadName) {
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
            channel.exchangeDeclare("router", "topic");

            channel.queueDeclare();
            channel.queueDeclare(interfaceName, false, false, false, null);
            for (String bk : bindingKeys) {
                channel.queueBind(interfaceName, exchangeName, bk);
            }
            System.out.print(interfaceName + " has started listening for messages on" + exchangeName + " using the binding keys: ");
            for (String bk : bindingKeys) {
                System.out.println(bk + " ");
            }

            // Da napravam da broi failed messages!! i ko kje najde, da zatvori vtoro queue za failed
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(threadName + " Received '" + message + "'");
            };

            while (true) {
                // Consume messages from the queue
                Thread.sleep(100);
                channel.basicConsume(interfaceName, true, deliverCallback, consumerTag -> {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
