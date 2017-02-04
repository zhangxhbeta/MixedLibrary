package mixedserver.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密工具类
 * 
 * @author zhangxiaohui
 * 
 */
public class EncryptionHelper {

	private static byte[] defaultIV = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * 加密
	 * 
	 * @param plainText
	 * @param strKey
	 * @param byteIV
	 * @return
	 * @throws Exception
	 */
	public static String encryptText(String plainText, String strKey, byte[] byteIV)
			throws Exception {

		byte[] plaintext = plainText.getBytes("utf-8");// input

		byte[] tdesKeyData = hexStringToByteArray(strKey);
		byte[] myIV = byteIV.length == 0 ? defaultIV : byteIV;

		Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(myIV);

		c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
		byte[] cipherText = c3des.doFinal(plaintext);

		String encryptedString = Base64.encodeToString(cipherText, Base64.NO_WRAP);
		return encryptedString;
	}

	/**
	 * 解密
	 * 
	 * @param plainText
	 * @param strKey
	 * @param byteIV
	 * @return
	 * @throws Exception
	 */
	public static String dencryptText(String plainText, String strKey, byte[] byteIV)
			throws Exception {
		byte[] inputData = Base64.decode(plainText, Base64.NO_WRAP);

		byte[] tdesKeyData = hexStringToByteArray(strKey);
		byte[] myIV = byteIV.length == 0 ? defaultIV : byteIV;

		Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(myIV);

		c3des.init(Cipher.DECRYPT_MODE, myKey, ivspec);
		byte[] cipherText = c3des.doFinal(inputData);

		String dencryptedString = new String(cipherText, "utf-8");
		return dencryptedString;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(
					s.charAt(i + 1), 16));
		}
		return data;
	}
}
