package book.utils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

	public static RSAPublicKey getPublicKeyFromBase64(String base64PublicKey) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return (RSAPublicKey) keyFactory.generatePublic(spec);
	}
}
