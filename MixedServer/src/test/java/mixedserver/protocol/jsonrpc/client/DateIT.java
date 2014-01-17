package mixedserver.protocol.jsonrpc.client;

import java.util.Calendar;
import java.util.Date;

import com.saintangelo.application.TestJsonRpc;

public class DateIT extends AbstractedRPC {
	public void testOpenProxy() {
		TestJsonRpc jrpc = client.openProxy("testJsonRpc", TestJsonRpc.class);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date date = jrpc.returnDate(cal.getTime());

		int compare = date.compareTo(cal.getTime());

		assertEquals(0, compare);

	}
}
