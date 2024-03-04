package IDS_DirectQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class DirectProducerThread extends Thread {

    String log;
    int ID;

    public DirectProducerThread(String log, int ID) {
        this.log = log;
        this.ID = ID;
    }

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            //Queue for credential confirmation responses form consumer
            channel.queueDeclare("Response", true, false, false, null);
            channel.queueBind("Response", "my-topic-exchange", "response.*");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String sMessage = new String(delivery.getBody(), "UTF-8");
                System.out.println("Received <" + sMessage + ID + ">");
            };

            // Logs to be sent
            for (int failedLogCount = 0, i = 0; i < 10; i++) {
                String log = this.log;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                // Filtering the logs according to the state of the login
                if (log.equals("Login SUCCESSFUL")) {
                    log = dtf.format(now) + " Employee Device ID: " + ID + "  status: <" + log + ">";

                    //The successful logins are sent to the Success queue
                    channel.basicPublish("my-topic-exchange", "success.login", null, log.getBytes());
                    //The response is received from the consumer
                    channel.basicConsume("Response", true, deliverCallback, consumerTag -> {
                    });
                    System.out.println(log);
                    break;
                } else {
                    //Count the number of failed log per thread
                    failedLogCount++;

                    //If the number of failed logs reaches 5, reject the log, and do not requeue them
                    // Stop the employee from sending logs
                    if (failedLogCount > 4) {
                        System.out.println(dtf.format(now) + "  Rejected: " + log + "  Employee Device ID: " + ID + "  status occurred " + failedLogCount + " times");
                        System.out.println(dtf.format(now) + "  Further messages sent from Employee Device: " + ID + " shall not be processed! ");
                        channel.basicReject(i, false);
                        Thread.currentThread().stop();
                        break;
                    }
                    log = dtf.format(now) + " Employee Device ID: " + ID + "  status: <" + log + ">" + "  status occurred " + failedLogCount + " times";
                    //Failed logs are sent to the Failed queue
                    channel.basicPublish("my-topic-exchange", "fail.login", null, log.getBytes());
                    //The response is received from the consumer
                    channel.basicConsume("Response", true, deliverCallback, consumerTag -> {
                    });
                    System.out.println(log);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
