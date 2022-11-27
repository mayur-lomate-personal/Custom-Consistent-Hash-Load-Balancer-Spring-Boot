# Consistent Hash based Load Balancing

The current project is an example of consistent hash based load balancer.

# Consistent Hash

Consistent Hashing is a distributed hashing scheme that operates independently of the number of servers or objects in a distributed hash table by assigning them a position on an abstract circle, or hash ring. This allows servers and objects to scale without affecting the overall system.

# Project

The project uses consistent hashing and gets list of server from eureka and load balances them to server by using consistent hashing.

# APIs

GET localhost:9080/hello :- returns hello with port number.
