package IDS_Testing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Random;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {

    int EmployeeID;
    Random rand = new Random();
    int attackedEmployee = rand.nextInt(100);
    String message;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // Declare a queue named 'hello'

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.exchangeDeclare(QUEUE_NAME, "fanout");

            // Message to be sent
            for (int i = 0; i < 100; i++) {
                if (i==attackedEmployee){
                    message = "!!Login Failed!! " +i;
                }
                else {
                    message = "*Login Successful* " + i;
                }

                // Publish the message to the queue
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}