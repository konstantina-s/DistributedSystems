package IDS_Testing;

import java.util.Random;

public class SendingLogs {

    public static void main(String[] args) {

        Random rand = new Random();
        int attackedEmployee = rand.nextInt(100);

        for (int i = 0; i < 100; i++) {

            if(i == attackedEmployee) {
                ProducerThreadTest ric2 = new ProducerThreadTest("!Login FAILED!");
                ric2.start();
            }

            ProducerThreadTest ric1 = new ProducerThreadTest("Login SUCCESSFUL");
            ric1.start();

        }

    }
}
