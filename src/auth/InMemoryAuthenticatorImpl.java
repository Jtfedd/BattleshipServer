package auth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class InMemoryAuthenticatorImpl implements Authenticator {
	private HashMap<String, String> savedUsers;

	public InMemoryAuthenticatorImpl() {
		savedUsers = new HashMap<>();
	}

	public InMemoryAuthenticatorImpl(String initializationFilename) {
		this();

		File file = new File(initializationFilename);
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String tokens[] = line.split(";");
				savedUsers.put(tokens[0], tokens[1]);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open initialization file: " + initializationFilename);
		}
	}

	@Override
	public AuthenticationResult registerNewPlayer(String username, String password) {
		if (savedUsers.containsKey(username)) {
			return AuthenticationResult.EXISTS;
		}

		savedUsers.put(username, password);
		return AuthenticationResult.SUCCESS;
	}

	@Override
	public AuthenticationResult authenticatePlayer(String username, String password) {
		if (savedUsers.get(username).equals(password)) {
			return AuthenticationResult.SUCCESS;
		}

		return AuthenticationResult.ERROR;
	}
}
