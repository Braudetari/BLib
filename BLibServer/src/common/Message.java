package common;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
/** 
* Handles messages and data between client and server <p>
* Encrypts and decrypts messages with Base64 <p>
*/
public class Message {
	public static String encryptToBase64(String msg){
		Base64.Encoder encoder = Base64.getEncoder();
		return new String(encoder.encode(msg.getBytes()));
	}
	
	public static String decryptFromBase64(String msg) {
		Base64.Decoder decoder = Base64.getDecoder();
		return new String(decoder.decode(msg));
	}
}
