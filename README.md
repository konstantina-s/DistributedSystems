# DistributedSystems
Project for the course Distributed systems using RabbitMQ to make an intrusion detection system.
* The producers create messages (logs) of successful and unsuccessful login attempts.
* Randomly, one producer is compromised and failed attempts are generated to the consumer.
* After 5 consecutive failed attempts, that consumer is blocked from loging in until given clearance.
