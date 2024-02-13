package IDS;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.ArrayList;

public class LogCollect {

    private final static String QUEUE1_NAME = "q1";
    private final static String QUEUE2_NAME = "q2";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //Declaring two queues that will sort logs into successful and failed logs
            channel.exchangeDeclare(QUEUE1_NAME, "topic");
            channel.exchangeDeclare(QUEUE2_NAME, "topic");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            //Starting multiple queues for successful and unsuccessful login logs
            ArrayList<String> bks;
            for (int i = 0; i < 5; i++) {

                bks = new ArrayList<String>();
                bks.add("i" + i);

                ConsumerThread ct1 = new ConsumerThread("e1", "Interface " +i, bks, "Thread 1");
                ct1.start();
                ConsumerThread ct2 = new ConsumerThread("e2", "Interface " +i, bks, "Thread 2");
                ct2.start();
            }
        }
    }
}
