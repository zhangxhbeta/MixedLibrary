package mixedserver.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加密工具
 * 
 * @author 佚名
 * 
 */
public class EncrpytionTool {

	private static final String KEY = "78165B0FE3319E7E5918968671877DB8F42C48BCC10430FA";

	private static final byte[] BYTE_I_V = {};

	private static final Logger logger = LoggerFactory.getLogger(EncrpytionTool.class);

	/**
	 * 对字符串src进行Base64(3DES(src))加密
	 * 
	 * @param src
	 * @return
	 */
	public static String encryptByBase64_3DES(String src) {
		String s = "";

		try {
			s = EncryptionHelper.encryptText(src, KEY, BYTE_I_V);
		} catch (Exception e) {
			logger.error("", e);
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
			s = EncryptionHelper.dencryptText(dest, KEY, BYTE_I_V);
		} catch (Exception e) {
			logger.error("", e);
		}
		return s;
	}
}
