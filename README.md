# Distributed Systems Project - Intrusion Detection System
Project for the course Distributed systems using RabbitMQ to simulate an intrusion detection system.
Student ID 201519.
* The producers create messages (logs) of successful and unsuccessful login attempts.
* Randomly, one producer is compromised and failed attempts are generated to the consumer.
* After 5 consecutive failed attempts, that consumer is blocked from loging in.
* The producers are represented by multiple threads.
* There are more than one consumer threads that listen to the queues for better management and fault tolerance.
