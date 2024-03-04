package IDS_DirectQueue;

import java.util.Random;

public class DirectQueueProducer {

    public static void main(String[] args) {

        Random rand = new Random();
        int attackedEmployee;
        int employeeDeviceID = 0;

        for (int i = 0; i < 100; i++) {
            employeeDeviceID++;
            //Generating a random number to represent a random employee device being attacked with brute force, may be multiple employees
            attackedEmployee = rand.nextInt(100);
            //The attacked employee sends a 'Failed Login' log
            if (i == attackedEmployee) {
                DirectProducerThread dpt2 = new DirectProducerThread("!Login FAILED!", employeeDeviceID);
                dpt2.start();
                continue;
            }
            DirectProducerThread dpt1 = new DirectProducerThread("Login SUCCESSFUL", employeeDeviceID);
            dpt1.start();

        }

    }
}
