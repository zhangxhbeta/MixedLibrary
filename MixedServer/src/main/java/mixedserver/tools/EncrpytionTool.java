package mixedserver.tools;

/**
 * 加密工具
 * 
 * @author 佚名
 * 
 */
public class EncrpytionTool {

	private static final String KEY = "78165B0FE3319E7E5918968671877DB8F42C48BCC10430FA";

	private static final byte[] BYTE_I_V = {};

	/**
	 * 对字符串src进行Base64(3DES(src))加密
	 * 
	 * @param src
	 * @return
	 */
	public static String encryptByBase64_3DES(String src) {
		String s = "";

		try {
			s = PSOCryptography.Encrypt(src, KEY, BYTE_I_V);
		} catch (Exception e) {
		}
		return s;
	}

	/**
	 * 对通过Base64(3DES(src))加密后的字符串dest解密
	 * 
	 * @param src
	 * @return
	 */
	public static String dencryptFromBase64_3DES(String dest) {
		String s = "";

		try {
			s = PSOCryptography.Decrypt(dest, KEY, BYTE_I_V);
		} catch (Exception e) {
		}
		return s;
	}
}
