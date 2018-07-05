package connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import auth.AuthenticationResult;
import core.Server;

public class Connection implements Runnable {
	private static int connectionsCounter = 1;

	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private Socket connSock;

	private Thread t;
	private int connIndex;

	private Server server;

	private boolean authenticated = false;

	public Connection(Socket connSock, Server parentServer) throws IOException {
		inFromClient = new BufferedReader(new InputStreamReader(connSock.getInputStream()));
		outToClient = new DataOutputStream(connSock.getOutputStream());
		this.connSock = connSock;

		server = parentServer;
		server.registerConnection(this);
	}

	public void sendMessage(String message) {
		try {
			outToClient.writeBytes(message + "\n");
		} catch (IOException e) {
			print(e.getMessage());
		}
	}

	private boolean authenticate() throws IOException {
		String auth = inFromClient.readLine();

		String authTokens[] = auth.split(":");
		if (authTokens.length != 2)
			return false;

		String authType = authTokens[0];

		String tokens[] = authTokens[1].split(";");
		if (authTokens.length != 2)
			return false;

		String username = tokens[0];
		String password = tokens[1];

		if (authType.equals("register")) {
			AuthenticationResult result = server.registerNewPlayer(username, password);
			if (result == AuthenticationResult.EXISTS) {
				sendMessage("exists");
				return false;
			}
			return true;
		} else if (authType.equals("auth")) {
			AuthenticationResult result = server.authenticatePlayer(username, password);
			if (result == AuthenticationResult.ERROR) {
				sendMessage("invalid");
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		try {
			if (!authenticate()) {
				disconnect();
				return;
			}

			while (true) {
				String received = inFromClient.readLine();
				print(received);
			}
		} catch (Exception e) {
			disconnect();
			return;
		}
	}

	public void start() {
		if (t == null) {
			this.connIndex = connectionsCounter++;
			System.out.println("Starting connection thread " + connIndex);
			t = new Thread(this, "connection-" + connIndex);
			t.start();
		}
	}

	private void print(String s) {
		System.out.println("Thread " + connIndex + ": " + s);
	}

	public boolean authenticated() {
		return authenticated;
	}

	private void disconnect() {
		print("Disconnecting");
		server.removeConnection(this);
		try {
			connSock.close();
		} catch (IOException e) {
			print("failed to close socket");
		}
	}
}
