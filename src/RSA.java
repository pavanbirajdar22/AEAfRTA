import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA {
	private KeyPair keyPair;

	public RSA() throws Exception {
		Initialize();
	}

	public void Initialize() throws Exception {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
		keygen.initialize(2048);
		keyPair = keygen.generateKeyPair();
	}

	public byte[] getPublicKey() {
		return keyPair.getPublic().getEncoded();
	}

	public static PublicKey convertBytesToPublicKey(byte[] publicKey) throws Exception {

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public byte[] encrypt(byte[] plaintext, PublicKey key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64.getEncoder().encode(cipher.doFinal(plaintext));
	}

	public byte[] decrypt(byte[] ciphertext) throws Exception {
		PrivateKey key = keyPair.getPrivate();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

		cipher.init(Cipher.DECRYPT_MODE, key);

		return cipher.doFinal(Base64.getDecoder().decode(ciphertext));
	}

}