/*
 * jabsorb - a Java to JavaScript Advanced Object Request Broker
 * http://www.jabsorb.org
 *
 * Copyright 2007-2009 The jabsorb team
 *
 * based on original code from
 * JSON-RPC-Client, a Java client extension to JSON-RPC-Java
 * (C) Copyright CodeBistro 2007, Sasha Ovsankin <sasha at codebistro dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package mixedserver.protocol.jsonrpc.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import mixedserver.protocol.RPCException;

/**
 * Transport based on URLConnection
 * 
 */
public class URLConnectionSession implements Session {
	URL url;

	/**
	 * Create a URLConnection transport
	 * 
	 * @param url
	 */
	URLConnectionSession(URL url) {
		this.url = url;
	}

	public void close() {
	}

	public String sendAndReceive(String message) throws RPCException {
		try {
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			// As per
			// http://java.sun.com/docs/books/tutorial/networking/urls/readingWriting.html
			Writer request = new OutputStreamWriter(
					connection.getOutputStream());
			request.write(message);
			request.close();
			// TODO the following sequence of reading a string out of output
			// stream is too complicated
			// there must be a simpler way
			StringBuffer builder = new StringBuffer(1024);
			char[] buffer = new char[1024];
			Reader reader = new InputStreamReader(connection.getInputStream());
			while (true) {
				int bytesRead = reader.read(buffer);
				if (bytesRead < 0)
					break;
				builder.append(buffer, 0, bytesRead);
			}
			reader.close();
			return builder.toString();
		} catch (IOException ex) {
			throw new RPCException(ex.getMessage());
		}
	}

	@Override
	public void setAttribute(String key, Object value) {
		// TODO 未实现

	}

	@Override
	public Object getAttribute(String key) {
		// TODO 未实现
		return null;
	}

	@Override
	public String getSessionId() {
		// TODO 未实现
		return null;
	}

	@Override
	public String getSessionKey() {
		// TODO 未实现
		return null;
	}

	@Override
	public void removeAttribute(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAllAttribute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCookie(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCookie(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
