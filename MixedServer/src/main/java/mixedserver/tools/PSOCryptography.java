package mixedserver.tools;


import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;



public class PSOCryptography {

  private static String CodingType = "UTF-8";
  private static String DigestAlgorithm = "SHA1";
  private static String CryptAlgorithm = "DESede/CBC/PKCS5Padding";
  private static String KeyAlgorithm = "DESede";
  private static String strSeparator = "$";
  private static byte[] defaultIV = {1,2,3,4,5,6,7,8};
  public static String SPID = null;

  public PSOCryptography() {
  }

  static
  {
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * Function Description    Base64����
   * @param b                ��ҪBase64������ַ�����
   * @return                 �������ַ�����
   * @throws Exception
   */
  public static byte[] Base64Encode(byte[] b) throws Exception
  {
    return Base64.encode(b);
  }

  /**
   * Function Description    BASE64������
   * @param b                ��ҪBase64��������ַ�����
   * @return                 ���������ַ�����
   * @throws Exception
   */
  public static byte[] Base64Decode(byte[] b) throws Exception
  {
    return Base64.decode(b);
  }

  /**
   * Function Description    BASE64������
   * @param s                ��ҪBase64��������ַ�
   * @return                 ���������ַ�����
   * @throws Exception
   */
  public static byte[] Base64Decode(String s) throws Exception
  {
    return Base64.decode(s);
  }

  /**
   * Function Description     URL����
   * @param strToBeEncode     ��ҪURL������ַ�
   * @return                  URL�������ַ�
   * @throws Exception
   */
  public static String URLEncode(String strToBeEncode) throws Exception
  {
    return URLEncoder.encode(strToBeEncode);
  }

  /**
   * Function Description      URL������
   * @param strToBeDecode      ��ҪURL��������ַ�
   * @return                   URL���������ַ�
   * @throws Exception
   */
  public static String URLDecode(String strToBeDecode) throws Exception
  {
    return URLDecoder.decode(strToBeDecode);
  }

  /**
   * Function Description    ���16�����ַ����IV
   * @param strIV            16�����ַ�
   * @return                 �ַ�����
   * @throws Exception
   */
  public static byte[] IVGenerator(String strIV) throws Exception
  {
    return Hex.decode(strIV);
  }


  /**
   * Function Description       3DES����
   * @param strTobeEnCrypted    Ҫ���ܵ��ַ�
   * @param strKey              ��Կ
   * @param byteIV              ������
   * @return                    ���ܺ���ַ�
   * @throws Exception
   */
  public static String Encrypt(String strTobeEnCrypted,
                               String strKey, byte[] byteIV) throws Exception
  {
    byte[] input = strTobeEnCrypted.getBytes(CodingType);

    Key k = KeyGenerator(strKey);
    IvParameterSpec IVSpec = (byteIV.length == 0)?IvGenerator(defaultIV):IvGenerator(byteIV);

    Cipher c = Cipher.getInstance(CryptAlgorithm);
    c.init(Cipher.ENCRYPT_MODE,k,IVSpec);
    byte[] output = c.doFinal(input);

    return new String(Base64Encode(output),CodingType);
  }

  /**
   * Function Description       3DES����
   * @param strTobeDeCrypted    Ҫ���ܵ��ַ�
   * @param strKey              ��Կ
   * @param byteIV              ������
   * @return                    ���ܺ���ַ�
   * @throws Exception
   */
  public static String Decrypt(String strTobeDeCrypted,
                              String strKey, byte[] byteIV) throws Exception
  {
    byte[] input = Base64Decode(strTobeDeCrypted);

    Key k = KeyGenerator(strKey);
    IvParameterSpec IVSpec = (byteIV.length == 0)?IvGenerator(defaultIV):IvGenerator(byteIV);

    Cipher c = Cipher.getInstance(CryptAlgorithm);
    c.init(Cipher.DECRYPT_MODE,k,IVSpec);
    byte[] output = c.doFinal(input);

    return new String(output,CodingType);
  }


  /*------------------------------------------
   * PSOCryptography���ڲ�ʹ�õĹ�������
   * Modified On: 2003-11-6
   *
   * Modified On: 2005-04-18
   * Content: ��ӷ���Hash��Encrypt
    -----------------------------------------*/
    // ˵��: ��ɼӽ�����
    private static IvParameterSpec IvGenerator(byte[] b) throws Exception
    {
      IvParameterSpec IV = new IvParameterSpec(b);
      return IV;
    }

    // ˵��: ���16�����ַ������Կ
    private static Key KeyGenerator(String KeyStr) throws Exception
    {
      byte[] input = Hex.decode(KeyStr);

      DESedeKeySpec KeySpec = new DESedeKeySpec(input);
      SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance(KeyAlgorithm);

      return KeyFactory.generateSecret(KeySpec);
    }

    /**
     * ˵��    ��������ַ����Ƿ����ƶ��ָ���
     * ����    String s       �����ַ�
     *          char separator �ָ���
     * ���    boolean        �ǻ��
     */
    private static boolean hasMoreElement(String s, char separator)
    {
      int size = 0;
      for(int i=0; i<s.length(); i++)
      {
        if (s.charAt(i) == separator)
          size++;
      }

      return size>0?true:false;
    }

    /**
     * ˵��:  �����ַ�����ĳ���
     * ����:  String s - Ҫ���㳤�ȵ��ַ�
     * ���:   int ����
     */
    private static int length(String[] s)
    {
      int length = 0;
      for(int i=0; i<s.length; i++)
      {
        if (s[i] != null)
          length += s[i].length();
        else
          length += 0;
      }

      return length;
    }

}