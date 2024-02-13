package IDS;

import java.util.Random;

public class LogSend {

    public static void main(String[] args) {
        //Generating a random number to represent a random employee device being attacked with brute force
        Random rand = new Random();
        int attackedEmployee = rand.nextInt(100);
        int employeeDeviceID = 0;

        for (int i = 0; i < 100; i++) {
            employeeDeviceID++;

            //The attacked employee sends a 'Failed Login' log
            if (i == attackedEmployee) {
                ProducerThread ric2 = new ProducerThread("!Login FAILED!", employeeDeviceID );
                ric2.start();
            }

            ProducerThread ric1 = new ProducerThread("Login SUCCESSFUL", employeeDeviceID);
            ric1.start();

        }

    }
}


