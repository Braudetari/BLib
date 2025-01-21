package common;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
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
	
	public boolean isEncrypted() {
		return this.encrypted;
	}
	
	/**
	 * Encrypts Message using SHA-256, AES with sessionId
	 * I had to google this, pretty sure we learn this in cryptology later on.
	 * @param message
	 * @return encrypted message (decrypted if fails)
	 */
	public static Message encrypt(Message message) {
		if(message.encrypted)
			return message;
		if(message.sessionId == null)
			return message;
		try {
	        byte[] keyBytes = digestKey(message.getSessionId());
	        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        byte[] iv = new byte[cipher.getBlockSize()];
	        SecureRandom random = new SecureRandom();
	        random.nextBytes(iv);
	        IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        outputStream.write(iv);
	        CipherOutputStream cos = new CipherOutputStream(outputStream, cipher);
	        if(message.getMessage() == null || message.getMessage().equals("") || message.getMessage().equals(" "))
	        	message.setMessage("empty");
	        Message messageSpaced = new Message(message.getRequest(), message.getSessionId(), message.getMessage().replace(" ", "%20"));
	        byte[] plaintextBytes = messageSpaced.toString().getBytes(StandardCharsets.UTF_8);
	        cos.write(plaintextBytes); 
	        cos.close();
	        byte[] encryptedBytes = outputStream.toByteArray();
	        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
	        Message encryptedMsg = new Message("encrypted", null, encryptedBase64);
	        encryptedMsg.encrypted = true;
			return encryptedMsg;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not encrypt Message");
			return message;
		}
	}
	
	/**
	 * Decrypts an Encrypted Message using SHA-256, AES with sessionId
	 * I had to google this, pretty sure we learn this in cryptology later on.
	 * @param message
	 * @param sessionId
	 * @return decrypted message (encrypted if fails)
	 */
	public static Message decrypt(Message message, String sessionId) {
		if(!message.encrypted)
			return message;
		if(sessionId == null) {
			System.err.println("Could not decrypt message with null sessionId");
			return message;
		}
        try {
        	byte[] keyBytes = digestKey(sessionId);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            byte[] encryptedBytes = Base64.getDecoder().decode(message.getMessage());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            System.arraycopy(encryptedBytes, 0, iv, 0, blockSize);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            ByteArrayInputStream inputStream = 
            new ByteArrayInputStream(encryptedBytes, blockSize, encryptedBytes.length - blockSize);
            ByteArrayOutputStream decryptedOutput = new ByteArrayOutputStream();
            CipherInputStream cis = new CipherInputStream(inputStream, cipher);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cis.read(buffer)) != -1) {
                decryptedOutput.write(buffer, 0, bytesRead);
            }
            cis.close();
            String decryptedText = new String(decryptedOutput.toByteArray(), StandardCharsets.UTF_8);
            String[] msgFields = decryptedText.split(" ");
            Message decryptedMsg = new Message(msgFields[0], msgFields[1], msgFields[2].replace("%20", " "));
            decryptedMsg.encrypted = false;
            return decryptedMsg;
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.err.println("Could not decrypt Message, you don't have the correct sessionId.");
        	return null;
        }
	}
	
	private static byte[] digestKey(String sessionId) throws Exception{
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		return sha256.digest(sessionId.getBytes(StandardCharsets.UTF_8));
	}
}
