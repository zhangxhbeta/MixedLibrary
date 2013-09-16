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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import mixedserver.protocol.RPCException;
import mixedserver.protocol.jsonrpc.client.TransportRegistry.SessionFactory;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transport session straightforwardly implemented in HTTP. As compared to the
 * built-in URLConnectionSession, it allows more control over HTTP transport
 * parameters, for example, proxies and the support for HTTPS.
 * 
 * <p>
 * To use this transport you need to first register it in the TransportRegistry,
 * for example:
 * <p>
 * <code>
 * 		HTTPSession.register(TransportRegistry.i());
 * </code>
 */
public class HTTPSession implements Session {
	private final static Logger logger = LoggerFactory
			.getLogger(HTTPSession.class);

	protected HttpClient client;

	protected URI uri;

	private int soTimeout = 3 * 60 * 1000; // 读超时3分钟

	private int connectionTimeout = 30 * 1000; // 连接超时3秒

	private Hashtable attributes;

	CookieStore cookieStore;

	HttpContext localContext;

	public HTTPSession(URI uri) {
		this.uri = uri;
		attributes = new Hashtable();

		cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();

		// 在本地环境绑定一个本地存储
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	/**
	 * As per JSON-RPC Working Draft
	 * http://json-rpc.org/wd/JSON-RPC-1-1-WD-20060807.html#RequestHeaders
	 */
	static final String JSON_CONTENT_TYPE = "application/json";

	public String sendAndReceive(String message) throws RPCException {

		if (logger.isDebugEnabled()) {
			logger.debug("Sending: " + message.toString());
		}

		String responseString;
		HttpEntity entity;
		try {
			entity = new JSONEntity(message, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RPCException("Unsupported encoding");
		}

		responseString = sendEntity(entity);
		return responseString;
	}

	private String sendEntity(HttpEntity entity) throws RPCException {
		HttpPost request = new HttpPost(this.uri);
		request.setEntity(entity);

		HttpResponse response;
		String responseString = null;
		try {
			Date before = new Date();
			response = http().execute(request, localContext);
			Date after = new Date();

			logger.debug("执行时间" + (after.getTime() - before.getTime()));

			responseString = EntityUtils.toString(response.getEntity());
			responseString = responseString.trim();

		} catch (ClientProtocolException e) {
			throw new RPCException("客户端协议配置错误");
		} catch (IOException e) {
			throw new RPCException("网络连接错误",
					RPCException.ERROR_CODE_NETWORK_ERROR);
		} catch (Exception e) {
			if (e.getClass().getName().equals("NetworkOnMainThreadException")) {
				throw new RPCException("不要在界面线程执行网络程序");
			}

			throw new RPCException("客户端http链接错误:" + e.getMessage());
		}

		return responseString;
	}

	HttpClient http() throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException {
		if (client == null) {
			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params,
					getConnectionTimeout());
			HttpConnectionParams.setSoTimeout(params, getSoTimeout());
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			/*
			 * ClientConnectionManager mgr = new ThreadSafeClientConnManager(
			 * params, registry);
			 */

			ClientConnectionManager mgr = new ThreadSafeClientConnManager(
					params, registry);

			DefaultHttpClient defaultHttpClient = new DefaultHttpClient(mgr,
					params);

			// 添加gzip处理
			defaultHttpClient
					.addRequestInterceptor(new HttpRequestInterceptor() {

						public void process(final HttpRequest request,
								final HttpContext context)
								throws HttpException, IOException {
							if (!request.containsHeader("Accept-Encoding")) {
								request.addHeader("Accept-Encoding", "gzip");
							}
						}

					});

			defaultHttpClient
					.addResponseInterceptor(new HttpResponseInterceptor() {

						public void process(final HttpResponse response,
								final HttpContext context)
								throws HttpException, IOException {
							HttpEntity entity = response.getEntity();
							if (entity != null) {
								Header ceheader = entity.getContentEncoding();
								if (ceheader != null) {
									HeaderElement[] codecs = ceheader
											.getElements();
									for (int i = 0; i < codecs.length; i++) {
										if (codecs[i].getName()
												.equalsIgnoreCase("gzip")) {
											response.setEntity(new GzipDecompressingEntity(
													response.getEntity()));
											return;
										}
									}
								}
							}
						}

					});
			client = defaultHttpClient;
		}
		return client;
	}

	public void close() {
		//
	}

	static class Factory implements SessionFactory {
		public Session newSession(URI uri) {
			return new HTTPSession(uri);
		}
	}

	/**
	 * Register this transport in 'registry'
	 */
	public static void register(TransportRegistry registry) {
		Factory f = new Factory();
		registry.registerTransport("http", f);
		registry.registerTransport("https", f);
	}

	/*
	 * @Override public String login(String username, String password) { try {
	 * 
	 * JSONObject jsonRequest = new JSONObject(); jsonRequest.put("method",
	 * "authority.login"); jsonRequest.put("jsonrpc", "2.0");
	 * 
	 * JSONArray params = new JSONArray(); params.put(username);
	 * params.put(password); jsonRequest.put("params", params);
	 * 
	 * // 读取返回值 JSONObject jsonObj = sendAndReceive(jsonRequest);
	 * 
	 * if (jsonObj.has("error")) { throw new ClientError("登录请求发送失败"); } else {
	 * return jsonObj.getString("result"); } } catch (JSONException e) { throw
	 * new ClientError(e); }
	 * 
	 * }
	 */

	/**
	 * Get the socket operation timeout in milliseconds
	 */
	public int getSoTimeout() {
		return soTimeout;
	}

	/**
	 * Set the socket operation timeout
	 * 
	 * @param soTimeout
	 *            timeout in milliseconds
	 */
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	/**
	 * Get the connection timeout in milliseconds
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * Set the connection timeout
	 * 
	 * @param connectionTimeout
	 *            timeout in milliseconds
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	@Override
	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}

	@Override
	public Object getAttribute(String key) {
		return this.attributes.get(key);
	}

	@Override
	public String getSessionId() {
		List<Cookie> cookies = cookieStore.getCookies();
		for (int i = 0; i < cookies.size(); i++) {
			Cookie c = cookies.get(i);
			if (c.getName().equals(getSessionKey())) {
				return c.getValue();
			}
		}

		return null;
	}

	@Override
	public String getSessionKey() {
		return "JSESSIONID";
	}

	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	@Override
	public void removeAllAttribute() {
		attributes.clear();
	}

	public void setCookie(String name, String value) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(uri.getHost());
		cookie.setPath(uri.getPath());
		cookieStore.addCookie(cookie);
	}

	public class GzipDecompressingEntity extends DecompressingEntity {

		/**
		 * Creates a new {@link GzipDecompressingEntity} which will wrap the
		 * specified {@link HttpEntity}.
		 * 
		 * @param entity
		 *            the non-null {@link HttpEntity} to be wrapped
		 */
		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		@Override
		InputStream decorate(final InputStream wrapped) throws IOException {
			return new GZIPInputStream(wrapped);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Header getContentEncoding() {
			/* This HttpEntityWrapper has dealt with the Content-Encoding. */
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long getContentLength() {
			/* length of ungzipped content is not known */
			return -1;
		}
	}
}
