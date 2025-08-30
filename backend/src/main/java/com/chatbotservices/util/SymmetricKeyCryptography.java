
package com.chatbotservices.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Hex;


public class SymmetricKeyCryptography {

	private static byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
		if (s.length() < length) {
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) {
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");
	}

	public static String encrypt(String secret, String value) {
		try {
			byte[] key = new byte[16];
			key = fixSecret(secret, 16);
			IvParameterSpec iv = new IvParameterSpec(key);
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return new String(Hex.encode(encrypted));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String decrypt(String secret, String encrypted) {
		try {
			byte[] key = fixSecret(secret, 16);
			IvParameterSpec iv = new IvParameterSpec(key);
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			CharSequence cs = encrypted;
			byte[] original = cipher.doFinal(Hex.decode(cs));
			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}