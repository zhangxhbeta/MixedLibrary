package mixedserver.server;

import mixedserver.protocol.jsonrpc.RPC;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Server server = new Server(8080);
		ServletContextHandler handler = new ServletContextHandler(
				ServletContextHandler.SESSIONS);
		handler.setContextPath("/"); // technically not required, as "/" is the
										// default

		ServletHolder sh = new ServletHolder(new RPC());
		sh.setInitParameter("expose_methods", "true");
		sh.setInitParameter("print_message", "true");
		sh.setInitParameter("persist_class", "true");
		sh.setInitParameter("detailed_errors", "true");
		sh.setInitParameter("gzip_threshold", "10");
		handler.addServlet(sh, "/JSON-RPC");

		server.setHandler(handler);

		// handler.getSessionHandler().getSessionManager().setMaxInactiveInterval(90);
		// handler.addEventListener(new ClientSessionListener());

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
