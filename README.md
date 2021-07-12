# TCPChatServer

TCPChatServer is a Java Class to be used as a server able to broadcast messages from one client to others connected.
You can connect to the server using a simple telnet command.

## Installation

1. Clone repo from GIT
3. Compile using provided pom.xml
4. Run tests and enjoy

## Usage

```Java
import it.example.ChatServer;

//init some array of integers
int port=10000;
ChatServer server = new ChatServer(port);
server.execute();
```
