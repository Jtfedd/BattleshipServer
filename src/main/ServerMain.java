package main;

import java.io.IOException;

import core.Server;

public class ServerMain {
	private static final int PORT = 17397;

	public static void main(String[] args) throws IOException {
		System.out.println("Server Starting");
		
		Server s = new Server(PORT);
		s.runServer();
		
		System.out.println("Server Exiting");
	}

}
