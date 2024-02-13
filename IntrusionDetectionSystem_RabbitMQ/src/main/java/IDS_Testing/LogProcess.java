package IDS_Testing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.ArrayList;

public class LogProcess {
    private final static String QUEUE1_NAME = "success";
    private final static String QUEUE2_NAME = "failure";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(QUEUE1_NAME, "topic");
            channel.exchangeDeclare(QUEUE2_NAME, "topic");

            System.out.println(" The router has started");

            // start a few threads to simulate router interfaces
            for (int i = 0; i < 5; i++) {

                ArrayList<String> bks = new ArrayList<String>();
                bks.add("i" + i);
                bks.add("i" + (i + 5));
                ConsumerThreadTest ric1 = new ConsumerThreadTest("success","Interface " + i, bks, "Thread " + i);
                ConsumerThreadTest ric2 = new ConsumerThreadTest("failure","Interface " + i, bks, "Thread " + (i + 5));

                ric1.start();
                ric2.start();
            }
        }
    }
}
