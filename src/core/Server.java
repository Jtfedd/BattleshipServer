package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import auth.AuthenticationResult;
import auth.Authenticator;
import auth.InMemoryAuthenticatorImpl;
import connection.Connection;

public class Server {
	private final int PORT;
	
	private List<Connection> connectionsList;
	
	private Authenticator auth;
	
	public Server(int port) {
		PORT = port;
		
		connectionsList = new ArrayList<>();
		
		auth = new InMemoryAuthenticatorImpl("users.txt");
	}
	
	public void runServer() throws IOException {
		
		ServerSocket serSock = new ServerSocket(PORT);
		System.out.println("Listening on port " + PORT);
		
		while (true) {
			try {
				Socket connSock = serSock.accept();
				Connection conn = new Connection(connSock, this);
				conn.start();
			} catch (IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public void registerConnection(Connection conn) {
		connectionsList.add(conn);
	}
	
	public void removeConnection(Connection conn) {
		connectionsList.remove(conn);
	}
	
	public int numOnline() {
		int count = 0;
		for (Connection c : connectionsList) {
			if (c.authenticated()) count++;
		}
		return count;
	}

	public AuthenticationResult registerNewPlayer(String username, String password) {
		return auth.registerNewPlayer(username, password);
	}

	public AuthenticationResult authenticatePlayer(String username, String password) {
		return auth.authenticatePlayer(username, password);
	}
}
