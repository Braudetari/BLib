package server;

import common.User;
import ocsf.server.ConnectionToClient;

/**
 * Connection To Client Info for more Authentication and Client Tracking
 */
public class ConnectionToClientInfo {
	private ConnectionToClient client;
	private final String clientIp;
	private final String clientName;
	private String sessionId;
	private User clientUser;
	private ClientConnectionStatus clientStatus;
	
	public static enum ClientConnectionStatus{Disconnected, Connected};
	
	/**
	 * Constructor
	 * @param client
	 * @param clientName
	 * @param clientUser
	 */
	ConnectionToClientInfo(ConnectionToClient client, String clientName, User clientUser){
		this.client = client;
		this.clientName = clientName;
		clientIp = client.getInetAddress().getHostAddress();
		this.clientUser = clientUser;
		clientStatus = ClientConnectionStatus.Connected;
	}
	
	public String getName() {
		return this.clientName;
	}
	
	public String getIp() {
		return this.clientIp;
	}
	
	public ConnectionToClient getClient() {
		return this.client;
	}
	
	/**
	 * Client equals if they share the same hostname and Ipaddress
	 * @param client
	 * @param clientName
	 * @return
	 */
	public boolean equals(ConnectionToClient client, String clientName) {
		if(this.clientName.equals(clientName)
				&& this.clientIp.equals(client.getInetAddress().getHostAddress())) {
			return true;
		}
		return false;
	}
	
	public ClientConnectionStatus getStatus() {
		return this.clientStatus;
	}
	
	public void setStatus(ClientConnectionStatus status) {
		this.clientStatus = status;
	}
	
	public void setClient(ConnectionToClient client) {
		this.client = client;
	}
	
	public void setUser(User user) {
		this.clientUser = user;
	}
	
	public User getUser() {
		return this.clientUser;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String toString() {
		return "" + this.clientName + " " + this.clientIp;
	}
}
