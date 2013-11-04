package mixedserver.protocol.jsonrpc.client;

import com.saintangelo.application.Person;
import com.saintangelo.application.TestJsonRpc;

public class BinaryIT extends AbstractedRPC {
	public void testUploadImage() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		String strImage = "这是用来代替测试的图片的";
		byte[] binaryImage = strImage.getBytes();
		byte[] returnBinaryImage = jrpc.uploadSmallImage(binaryImage);
		String returnStrImage = new String(returnBinaryImage);

		assertEquals(strImage, returnStrImage);

		Byte[] binaryImage2 = byteToByte(binaryImage);

		Byte[] returnBinaryImage2 = jrpc.uploadSmallImageByte(binaryImage2);
		String returnStrImage2 = new String(ByteTobyte(returnBinaryImage2));

		assertEquals(strImage, returnStrImage2);

		Person p = new Person();
		p.setImage(binaryImage);
		p.setImage2(binaryImage2);

		Person returnPerson = jrpc.uploadPersonWithImage(p);

		returnStrImage = new String(returnPerson.getImage());
		returnStrImage2 = new String(ByteTobyte(returnPerson.getImage2()));

		assertEquals(strImage, returnStrImage2);
		assertEquals(strImage, returnStrImage);
	}

	private Byte[] byteToByte(byte[] binaryImage) {
		Byte[] binaryImage2 = new Byte[binaryImage.length];
		for (int i = 0; i < binaryImage2.length; i++) {
			binaryImage2[i] = Byte.valueOf(binaryImage[i]);
		}
		return binaryImage2;
	}

	private byte[] ByteTobyte(Byte[] binaryImage) {
		byte[] binaryImage2 = new byte[binaryImage.length];
		for (int i = 0; i < binaryImage2.length; i++) {
			binaryImage2[i] = binaryImage[i];
		}
		return binaryImage2;
	}
}
