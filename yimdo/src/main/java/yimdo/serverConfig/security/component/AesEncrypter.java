package yimdo.serverConfig.security.component;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class AesEncrypter {

	private String initializaionVector; // 초기화 벡터
	private Key keySpec;			    // 비밀키
	private SecureRandom secureRandom = new SecureRandom();
	
	public AesEncrypter() {

		this.initializaionVector = generateRandomString(16, "0123456789ABCDEF");
		
		byte[] ivByteArr = initializaionVector.getBytes();
		byte[] keyBytes = new byte[16];

		int ivByteArrLen = ivByteArr.length;
		if (ivByteArrLen > keyBytes.length) { ivByteArrLen = keyBytes.length; }
		
		System.arraycopy(ivByteArr, 0, keyBytes, 0, ivByteArrLen);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		
		this.keySpec = secretKeySpec;
	}
	
	/**
	 * 무작위 문자열을 반환하는 메서드.
	 * 
	 * @param length 출력할 문자열 길이
	 * @param characters 랜덤하게 문자열을 구성할 문자들
	 * @return new String
	 */
    private String generateRandomString(int length, String characters) {
    	
    	int charactersLength = characters.length();
        StringBuilder randomStringBuilder = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
        	
            randomStringBuilder.append(characters.charAt(secureRandom.nextInt(charactersLength)));
        }

        return randomStringBuilder.toString();
    }
    
	/**
	 * 무작위 문자열을 반환하는 메서드.
	 * 
	 * @param length 출력할 문자열 길이
	 * @return new String
	 */
    public String generateRandomString(int length) {
    	
    	String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "~`!@#$%^&*";
        String allChars = upper + lower + digits + specialChars;
        
    	return generateRandomString(length, allChars);
    }
    
	public String encrypt(String value) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(initializaionVector.getBytes()));
		
		byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
		
		String encryptedString = new String(Base64.getEncoder().encode(encrypted));
		
		return encryptedString;
	}
	
	public String decrypt(String value) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException {
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(initializaionVector.getBytes()));
		
		byte[] valueByteArr = Base64.getDecoder().decode(value.getBytes());
		
		String decryptedString = new String(cipher.doFinal(valueByteArr), "UTF-8");
		
		return decryptedString;
	}
}
