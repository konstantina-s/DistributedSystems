package IDS_Testing;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProducerThreadTest extends Thread {

    String message;

    public ProducerThreadTest(String message) {
        this.message = message;
    }

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Message to be sent
            for (int i = 0; i < 10; i++) {
                String message = this.message + i;
                if(message.length()==0) {
                    channel.basicPublish("success", "i" + i % 10, null, message.getBytes());
                    System.out.println(" Producer sent '" + message + "'");
                }else{
                    channel.basicPublish("failure", "i" + i % 10, null, message.getBytes());
                    System.out.println(" Producer sent '" + message + "'");
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
