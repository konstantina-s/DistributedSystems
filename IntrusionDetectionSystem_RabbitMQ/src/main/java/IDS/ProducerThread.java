package IDS;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class ProducerThread extends Thread {

    String log;
    int ID;

    public ProducerThread(String log, int ID) {
        this.log = log;
        this.ID = ID;
    }

    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            // Logs to be sent
            for (int failedLogCount=0, i = 0; i < 10; i++) {
                String log = this.log;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                // Filtering the logs according to the state of the login
                if (log.equals("Login SUCCESSFUL")) {
                    log =  dtf.format(now) + " Employee Device ID: " + ID + "  status: <" + log + ">";
                    channel.basicPublish("e1", "i" + i % 10, null, log.getBytes());
                    System.out.println(log);
                    break;
                } else {
                    //Count the number of failed log per thread
                    failedLogCount++;

                    //If the number of failed logs reaches 5, reject the log, and do not requeue them
                    // Stop the employee from sending logs
                    if(failedLogCount>4){
                        System.out.println(dtf.format(now)  + "  Rejected: " + log + "  Employee Device ID: " + ID + "  status occurred " + failedLogCount + " times");
                        System.out.println( dtf.format(now) + "  Further messages sent from Employee Device: " + ID + " shall not be processed! ");
                        channel.basicReject(i, false);
                        Thread.currentThread().stop();
                        break;
                    }
                    log =  dtf.format(now) + " Employee Device ID: " + ID + "  status: <" + log + ">" + "  status occurred " + failedLogCount + " times";
                    channel.basicPublish("e2", "i" + i % 10, null, log.getBytes());
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
