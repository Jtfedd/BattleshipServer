package auth;

public interface Authenticator {
	
	public AuthenticationResult registerNewPlayer(String username, String password);
	
	public AuthenticationResult authenticatePlayer(String username, String password);
	
}
