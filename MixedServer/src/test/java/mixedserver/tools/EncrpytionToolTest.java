package mixedserver.tools;

import junit.framework.TestCase;

/**
 * @author zhangxh
 */
public class EncrpytionToolTest extends TestCase {

  EncrpytionTool tool = new EncrpytionTool();

  public void testEncypytion() {
    String text = ""
        + "皑如山上雪，皎若云间月。\n"
        + "闻君有两意，故来相决绝。\n"
        + "今日斗酒会，明旦沟水头。\n"
        + "躞蹀御沟上，沟水东西流。\n"
        + "凄凄复凄凄，嫁娶不须啼。\n"
        + "愿得一人心，白首不相离。\n"
        + "竹竿何袅袅，鱼尾何簁簁！\n"
        + "男儿重意气，何用钱刀为！";


    // System.out.println("原文");
    // System.out.println(text);
    //
    String result = tool.encryptByBase64_3DES(text);

    // System.out.println("密文");
    // System.out.println(result);

    // String result = "h7hzjsLcS6cct4u8qQ7RymLiIIUWQR6Y5087UiEErGPc3D/t+NtF/5o3qzEV8t2Cfxn/70FtewksX9GEPVydjIBAq9jpxiKw";

    String reverse = tool.dencryptFromBase64_3DES(result);

    System.out.println(reverse);

  }

}