## Lamport's Mutual Exclusion Algorithm for the Distributed system

This project is an implementation of the Lamport's mutual exclusion algorithm in a distributed system. Here we consider three servers and five clients for demonstration. 

##### Details of the system:
The servers do not have the multi-threading feature. So these are incapable of synchronizing the incoming requests from multiple clients. 
As the servers are incapable of synchronization, the clients synchronize with each other by passing messages between each other (maintaining a queue for each client).
There servers and clients should be run on different machines. However, for resource limitation, we demonstrate by creating multiple instances in the same machine (run in several consoles)

For implementation we use, Java 1.8, Java Socket Programming, and Threading. the program will achieve mutual exclusion over the network or on a local computer using different ports. Note that, the ip's and ports should be ensured to be accessible to each sites.



##### Configuration files:
There are two configuration files. `/config/servers.txt` and `/config/clients.txt`. The number and all other configuration details of servers, as well as clients, should be defined in one of these files, respectively. 
- The format of servers.txt file is : 
`<server_id>,<hostname>,<port>,<path_to_server_root_folder_in_project>`

- The format of clients.txt file is : 
`<client_id>,<hostname>,<port>`

Please note that The parameters of the client should be comma-separated (No spaces before or after the comma). Here, path_to_server_root_folder_in_project contains files where read/write would take place by clients. 

##### Steps:
- The program can be run locally on terminals. 
- Each terminal runs a single server or client.
- To start a server, we need to run the command, `java Server <server_id> <absolute_path_to_servers.txt> <capacity>`. Here, server_id is started from 1 and can be assigned incrementally to each server. In our scenario, the default capacity is 5.
- To start a client, run `java Client <client_id> <absolute_path_to_clients.txt> <absolute_path_to_servers.txt>`. Here, client_id is started from 1 and can be assigned incrementally to each client.
- Note that The servers and clients should all be started within 60 seconds (this is configurable in source code). Otherwise, the program will fail.
- In this implementation, the program will run until terminating it deliberately using `Ctrl+C` or `Cmd+C`.