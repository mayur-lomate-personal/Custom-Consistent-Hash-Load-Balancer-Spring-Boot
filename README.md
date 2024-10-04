# Consistent Hash based Load Balancing

This project implements a high-performance load balancer using a custom consistent hashing algorithm within a Spring Boot framework. The load balancer efficiently distributes incoming traffic across multiple backend servers, optimizing resource utilization and enhancing application performance. By leveraging consistent hashing, the load balancer ensures minimal disruption when servers are added or removed, maintaining a balanced load with improved fault tolerance.

Overall, this project demonstrates the effectiveness of consistent hashing in achieving reliable and efficient load distribution, ultimately improving response times and user experience in high-traffic environments.

# Consistent Hash

Consistent Hashing is a distributed hashing scheme that operates independently of the number of servers or objects in a distributed hash table by assigning them a position on an abstract circle, or hash ring. This allows servers and objects to scale without affecting the overall system.

# Project

The project uses consistent hashing and gets list of server from eureka and load balances them to server by using consistent hashing.

# APIs

GET localhost:9080/hello :- returns hello with port number.
