package common;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
/** 
* Handles messages and data between client and server <p>
* Encrypts and decrypts messages with Base64 <p>
*/
public class Message implements Serializable{
	private String request;
	private String sessionId;
	private String msg;
	private boolean encrypted;
	
	public Message() {
		request = null;
		sessionId = null;
		msg = null;
		encrypted = false;
	}
	
	
	public Message(String request, String sessionId, String msg) {
		this.request = request;
		this.sessionId = sessionId;
		this.msg = msg;
		encrypted = false;
	}
	
	public Message(Message message) {
		this.request = message.request;
		this.sessionId = message.sessionId;
		this.msg = message.msg;
		this.encrypted = message.encrypted;
	}
	
	//Getters and Setters
		public String getRequest() {
			return this.request;
		}
		
		public void setRequest(String request) {
			this.request = request;
		}
		
		public String getSessionId() {
			return this.sessionId;
		}
		
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		
		public String getMessage() {
			return this.msg;
		}
		
		public void setMessage(String msg) {
			this.msg = msg;
		}
	
	private static String encryptToBase64(String msg){
		Base64.Encoder encoder = Base64.getEncoder();
		return new String(encoder.encode(msg.getBytes()));
	}
	
	private static String decryptFromBase64(String msg) {
		Base64.Decoder decoder = Base64.getDecoder();
		return new String(decoder.decode(msg));
	}
	
	public String toString() {
		return new String(this.request + " " + this.sessionId + " " + this.msg); 
	}
	
	public static Message encrypt(Message message) {
		if(message.encrypted)
			return message;
		//Implement encryption based on this.sessionId;
		return message;
	}
	
	public static Message decrypt(Message message, String sessionId) {
		if(!message.encrypted)
			return message;
		//Implement decryption based on sessionId
		return message;
	}
	
}
