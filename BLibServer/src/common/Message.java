package common;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
* Handles messages and data between client and server
* Encrypts and decrypts messages with SHA256/AES
*/
public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private String request;
	private String sessionId;
	private Object msg;
	private boolean encrypted;
	
	public Message() {
		request = null;
		sessionId = null;
		msg = null;
		encrypted = false;
	}
	
	/**
	 * Message Regular Constructor
	 * @param request
	 * @param sessionId
	 * @param msg
	 */
	public Message(String request, String sessionId, Object msg) {
		this.request = request;
		this.sessionId = sessionId;
		this.msg = msg;
		encrypted = false;
	}
	
	/**
	 * Message Constructor With Message
	 * @param message
	 */
	public Message(Message message) {
		this.request = message.request;
		this.sessionId = message.sessionId;
		this.msg = message.msg;
		this.encrypted = message.encrypted;
	}
	
	////////	GETTERS AND SETTERS	////////
	
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
		
		public Object getMessage() {
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
	 * @return Message encrypted message (decrypted if fails)
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

	        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	        byteStream.write(iv); // Prepend IV for decryption

	        CipherOutputStream cipherOut = new CipherOutputStream(byteStream, cipher);
	        ObjectOutputStream objectOut = new ObjectOutputStream(cipherOut);
	        objectOut.writeObject(message); // Serialize the entire Message object
	        objectOut.flush();
	        objectOut.close();

	        byte[] encryptedBytes = byteStream.toByteArray();
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
	 * @return Message decrypted (encrypted if fails)
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
            byte[] encryptedBytes = Base64.getDecoder().decode(message.getMessage().toString());

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            int blockSize = cipher.getBlockSize();
            byte[] iv = new byte[blockSize];
            System.arraycopy(encryptedBytes, 0, iv, 0, blockSize);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            ByteArrayInputStream byteStream = new ByteArrayInputStream(encryptedBytes, blockSize, encryptedBytes.length - blockSize);
            CipherInputStream cipherIn = new CipherInputStream(byteStream, cipher);
            ObjectInputStream objectIn = new ObjectInputStream(cipherIn);
            Message decryptedMsg = (Message) objectIn.readObject();
            objectIn.close();
            return decryptedMsg;
        }
        catch(Exception e) {
        	e.printStackTrace();
        	System.err.println("Could not decrypt Message, you don't have the correct sessionId.");
        	return null;
        }
	}
	
	/**
	 * Gets a sha256 from SessionId
	 * @param sessionId
	 * @return byte[]
	 * @throws Exception
	 */
	private static byte[] digestKey(String sessionId) throws Exception{
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		return sha256.digest(sessionId.getBytes(StandardCharsets.UTF_8));
	}
}
