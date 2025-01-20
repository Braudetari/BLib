package common;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
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
	
	public String toString() {
		return new String(this.request + " " + this.sessionId + " " + this.msg); 
	}
	
	public static Message encrypt(Message message) {
		if(message.encrypted)
			return message;
//		try{
//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//			byte[] keyBytes = sha256.digest(message.getSessionId().getBytes(StandardCharsets.UTF_8));
//			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//			ByteArrayOutputStream bs = new ByteArrayOutputStream();
//			CipherOutputStream cos = new CipherOutputStream(bs, cipher);
//			byte[] messageBytes = message.toString().getBytes(StandardCharsets.UTF_8);
//			cos.write(messageBytes);
//			cos.close();
//			Message encryptedMsg = new Message(null, null, messageBytes.toString());
//			encryptedMsg.encrypted = true;
//			return encryptedMsg;
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//			System.err.println("Could not encrypt message");
//			return message;
//		}
		return message;
	}
	
	public static Message decrypt(Message message, String sessionId) {
		if(!message.encrypted)
			return message;
		//Implement decryption based on sessionId
		return message;
	}
	
}
