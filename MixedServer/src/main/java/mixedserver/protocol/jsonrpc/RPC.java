package mixedserver.protocol.jsonrpc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mixedserver.protocol.ModulProxy;
import mixedserver.protocol.ModuleContext;
import mixedserver.protocol.RPCException;
import mixedserver.protocol.RPCMethod;
import mixedserver.protocol.SessionRelatedModul;
import mixedserver.protocol.jsonrpc.annotate.JpoxyIgnore;
import mixedserver.protocol.jsonrpc.events.JSONRPCEventListener;
import mixedserver.protocol.jsonrpc.events.JSONRPCMessage;
import mixedserver.protocol.jsonrpc.events.JSONRPCMessageEvent;
import mixedserver.tools.EncrpytionTool;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.WordUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thanks to Wes Widner, This class creates a servlet which implements the
 * JSON-RPC 2.0 specification. based on Wes Widner's implemention
 * 
 * @author zhangxh
 * 
 */
@SuppressWarnings("serial")
public class RPC extends HttpServlet {

	protected final static Logger logger = LoggerFactory.getLogger(RPC.class);

	private boolean PERSIST_CLASS = true;

	private boolean EXPOSE_METHODS = false;

	private boolean DETAILED_ERRORS = false;

	private boolean PRINT_MESSAGE = true;

	private int GZIP_THRESHOLD = 140;

	private boolean ENCRYPT_MESSAGE = false;

	private boolean DENCRYPT_MESSAGE = false;

	private HashMap<String, Object> rpcobjects;

	private HashMap<String, RPCMethod> rpcmethods;

	private List<JSONRPCEventListener> listeners = new ArrayList<JSONRPCEventListener>();

	private ServletConfig servletconfig;

	private ServletContext servletcontext;

	private ObjectMapper objectmapper;

	boolean springAvaible = false;

	JpoxyIgnore jpoxyignoreannotation;

	private Pattern pattern = Pattern.compile("^(.*)\\.(.*)\\*$");

	private void processClass(String moduleName, Class<?> interfaceClass,
			Class<?> implementClass) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		int classmodifiers = interfaceClass.getModifiers();
		/*
		 * 必须有接口类和实现类，方法不能是静态方法，不能是私有接口
		 */
		if (!Modifier.isInterface(classmodifiers)) {
			logger.info("Skipping class, not interface");
			return;
		}

		if (Modifier.isStatic(classmodifiers)) {
			logger.info("Skipping class, 静态类");
			return;
		}

		if (implementClass == null && !springAvaible) { // 没有指定实现类
			logger.info("Skipping class, 当前环境必须配置实现类");
			return;
		}

		boolean requireProxy = false;
		try {

			if (implementClass != null
					&& isImplementsInterface(implementClass, ModulProxy.class)) {
				requireProxy = true;
			}

			// 预先实例化实现类（如果 PERSIST_CLASS == true）
			if (PERSIST_CLASS) {

				Object obj = null;

				// 如果没有指定实现类，去spring容器里面找一下实现类
				if (implementClass == null && springAvaible) {
					obj = ModuleContext.getBean(interfaceClass);
					if (obj != null) {
						logger.info("Got class "
								+ interfaceClass.getSimpleName()
								+ " from spring context, use it");

						implementClass = obj.getClass();
					}
				}

				if (implementClass == null) {
					logger.error("无法处理 " + interfaceClass.getName()
							+ " ，找不到对应的实现类，跳过该接口");
					return;
				}

				// 查看 rpcobjects 里面有无记录该实现类
				if (!rpcobjects.containsKey(implementClass.getName())) {

					// 从Spring容器获取
					if (springAvaible) {
						obj = ModuleContext.getBean(implementClass);
						if (obj != null) {
							logger.info("Got " + implementClass.getSimpleName()
									+ " from spring context, use it");
						}
					}

					if (obj == null) {
						obj = implementClass.newInstance();
						if (springAvaible) {
							logger.info(implementClass.getSimpleName()
									+ " not found in spring context, create it");
						}
					}

					rpcobjects.put(implementClass.getName(), obj);
				}

				if (obj != null && implementsRPCEventListener(implementClass)) {
					addMessageListener((JSONRPCEventListener) obj);
				}
			}
		} catch (InstantiationException ie) {
			logger.error("Caught InstantiationException", ie);
			return;
		}

		// 处理 RPC 方法
		Method methods[] = interfaceClass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			int methodmodifiers = methods[i].getModifiers();

			// Auto-prune non-public methods and any methods that return data
			// types we can't serialize
			if (!Modifier.isPublic(methodmodifiers)
					|| (!objectmapper.canSerialize(methods[i].getReturnType())
							&& !methods[i].getReturnType().equals(
									JSONObject.class)
							&& !methods[i].getReturnType().equals(
									JSONArray.class) && !methods[i]
							.getReturnType().isPrimitive())) {
				continue;
			}

			jpoxyignoreannotation = methods[i].getAnnotation(JpoxyIgnore.class);
			if (jpoxyignoreannotation != null && jpoxyignoreannotation.value()) {
				continue;
			}

			String methodsig = generateMethodSignature(moduleName, methods[i]);

			logger.debug("Methodsig:" + methodsig);

			if (methodsig == null) {
				continue;
			}

			if (rpcmethods.containsKey(methodsig)) {
				logger.debug("Skipping duplicate method name: [" + methodsig
						+ "]");
				continue;
			}

			logger.info("Adding method sig: [" + methodsig + "]");

			RPCMethod rpcMethod = RPCMethod.newMethod(methods[i].getName(),
					implementClass.getName(), methods[i]);

			rpcMethod.setProxy(requireProxy); // 设置是否需要代理

			if (isImplementsInterface(implementClass, SessionRelatedModul.class)) {
				rpcMethod.setSessionRelated(true);
			}

			rpcmethods.put(methodsig, rpcMethod);

		}
	}

	/**
	 * This method reads the servlet configuration for a list of classes it
	 * should scan for acceptable Method objects that can be called remotely.
	 * Acceptable methods are methods that do not have a {@link Modifier}
	 * marking them as abstract or interface methods. Static methods are fine.
	 * <P>
	 * Valid methods are gathered into a {@link HashMap} and instances of
	 * non-static classes are created and reused for subsequent RPC calls.
	 * <P>
	 * Class is marked as final to prevent overriding and possible interference
	 * upstream.
	 * 
	 * @see <a href=
	 *      "http://java.sun.com/j2se/1.5.0/docs/api/java/lang/reflect/Method.html"
	 *      >java.lang.reflect.Method</>
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/reflect/Modifier.html">java.lang.reflect.Modifier</a>
	 * @see <a
	 *      href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/HashMap.html">java.util.HashMap</a>
	 * @param config
	 *            ServletConfig passed from container upon initialization
	 */
	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("RPC init!");

		objectmapper = new ObjectMapper();
		objectmapper
				.configure(
						DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);

		servletconfig = config;
		servletcontext = getServletContext();

		try {
			XMLConfiguration modulesConfig = new XMLConfiguration(
					"module-configuration.xml");

			// List<ConfigurationNode> modules =
			// modulesConfig.getRoot().getChildren();
			List<HierarchicalConfiguration> modules = modulesConfig
					.configurationsAt("module");

			if (servletconfig.getInitParameter("expose_methods") != null) {
				EXPOSE_METHODS = servletconfig.getInitParameter(
						"expose_methods").equalsIgnoreCase("true");
			}

			if (servletconfig.getInitParameter("detailed_errors") != null) {
				DETAILED_ERRORS = servletconfig.getInitParameter(
						"detailed_errors").equalsIgnoreCase("true");
			}

			if (servletconfig.getInitParameter("persist_class") != null) {
				PERSIST_CLASS = servletconfig.getInitParameter("persist_class")
						.equalsIgnoreCase("true");
			}

			if (servletconfig.getInitParameter("print_message") != null) {
				PRINT_MESSAGE = servletconfig.getInitParameter("print_message")
						.equalsIgnoreCase("true");
			}

			if (servletconfig.getInitParameter("gzip_threshold") != null) {
				String t = servletconfig.getInitParameter("gzip_threshold");
				try {
					GZIP_THRESHOLD = Integer.parseInt(t);
				} catch (NumberFormatException e) {
					logger.error("gzip_threshold 配置错误，加载默认值 140");
				}
			}

			if (servletconfig.getInitParameter("encrypt_message") != null) {
				ENCRYPT_MESSAGE = servletconfig.getInitParameter(
						"encrypt_message").equalsIgnoreCase("true");
			}

			if (servletconfig.getInitParameter("dencrypt_message") != null) {
				DENCRYPT_MESSAGE = servletconfig.getInitParameter(
						"dencrypt_message").equalsIgnoreCase("true");
			}

			if (modules.isEmpty()) {
				throw new RPCException("No RPC classes specified.");
			}

			rpcmethods = new HashMap<String, RPCMethod>();
			rpcobjects = new HashMap<String, Object>();

			try {
				String contextAware = "org.springframework.context.ApplicationContextAware";
				Class clsContextAware = Class.forName(contextAware);
				springAvaible = true;
				logger.info("Find spring context, use it retrive module beans");
			} catch (java.lang.ClassNotFoundException e) {
				springAvaible = false;
			}

			String moduleName, implementClass, interfaceClass;
			for (int o = 0; o < modules.size(); o++) {
				HierarchicalConfiguration node = modules.get(o);
				moduleName = node.getString("name");
				interfaceClass = node.getString("interface");
				implementClass = node.getString("class");

				if (interfaceClass == null) {
					continue;
				}

				logger.debug("Examining class: " + interfaceClass);

				Matcher matcher = pattern.matcher(interfaceClass);
				boolean matchFound = matcher.find();

				if (matchFound) { // 通配符处理

					// 是否可以处理接口声明为通配符
					boolean multiInterfaceAllSet = implementClass != null
							&& isImplementsInterface(
									Class.forName(implementClass),
									ModulProxy.class) || implementClass == null
							&& springAvaible;

					if (!multiInterfaceAllSet) {
						// 错误的通配符配置
						logger.info("Processing: " + interfaceClass
								+ "，接口配置错误，当前环境下接口不能为通配符");
						continue;
					}

					// 如果可以处理通配符，并且接口指定的确实为通配符
					logger.debug("Looking for classes in package: "
							+ interfaceClass);

					Reflections reflections = new Reflections(
							new ConfigurationBuilder()
									.setUrls(
											ClasspathHelper.forPackage(matcher
													.group(1))).setScanners(
											new SubTypesScanner(false)));

					Set<String> stringSet = reflections.getStore()
							.get(SubTypesScanner.class).keySet();
					for (String key_str : stringSet) {
						String additional = "";
						if (matcher.group(2).length() > 0)
							additional = "." + matcher.group(2);
						if (key_str.matches(matcher.group(1) + additional
								+ ".*")
								&& !key_str.matches(".*\\$.*")) {
							logger.info("Processing: " + key_str);

							// 模块名默认用接口名首字母变小写
							int lastIndex = key_str.lastIndexOf('.');
							String name = key_str.substring(lastIndex + 1);
							moduleName = WordUtils.uncapitalize(name);

							if (implementClass != null)
								processClass(moduleName,
										Class.forName(key_str),
										Class.forName(implementClass));
							else
								processClass(moduleName,
										Class.forName(key_str), null);
						}
					}

				} else {
					// 模块名默认用接口名首字母变小写
					if (moduleName == null) {
						int lastIndex = interfaceClass.lastIndexOf('.');
						String name = interfaceClass.substring(lastIndex + 1);
						moduleName = WordUtils.uncapitalize(name);
					}

					logger.info("Processing: " + interfaceClass);
					if (implementClass != null)
						processClass(moduleName, Class.forName(interfaceClass),
								Class.forName(implementClass));
					else
						processClass(moduleName, Class.forName(interfaceClass),
								null);
				}

			}

			if (EXPOSE_METHODS) {
				Class<?> rpcclass = this.getClass();
				Method infoMethod = rpcclass.getMethod("listrpcmethods",
						(Class<?>[]) null);

				RPCMethod method = RPCMethod.newMethod("listrpcmethods", this
						.getClass().getName(), infoMethod);

				rpcmethods.put("listrpcmethods:0", method);
				rpcobjects.put("listrpcmethods", this);
			}

			if (rpcmethods.isEmpty()) {
				throw new RPCException("No valid RPC methods found.");
			}

			JSONRPCMessage msg = generateMessage(JSONRPCMessage.INIT, null,
					null);
			msg.setServletConfig(servletconfig);
			msg.setServletContext(servletcontext);
			fireMessageEvent(msg);
		} catch (Exception e) {
			Date date = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
			sdf.applyPattern("dd MMM yyyy HH:mm:ss z");

			logger.error("Exception caught at [" + sdf.format(date) + "]");
			logger.error("Stack trace:", e);
			logger.error("End exception code");
		}
	}

	/**
	 * Adds a class that implements the JSONRPCEventListener interface to the
	 * internal list of listeners
	 * 
	 * @param l
	 */
	public synchronized void addMessageListener(JSONRPCEventListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a class that implements the JSONRPCEventListener interface from
	 * the internal list of listeners
	 * 
	 * @param l
	 */
	public synchronized void removeMessageListener(JSONRPCEventListener l) {
		listeners.remove(l);
	}

	/**
	 * Walks through the listeners list, firing the messageRecieved method on
	 * all classes that implement the JSONRPCEventListener
	 * 
	 * @param m
	 */
	private synchronized void fireMessageEvent(JSONRPCMessage m) {
		logger.info("Firing message with code: " + m.getCode());

		JSONRPCMessageEvent me = new JSONRPCMessageEvent(this, m);
		Iterator<JSONRPCEventListener> ilisteners = listeners.iterator();
		while (ilisteners.hasNext()) {
			ilisteners.next().messageReceived(me);
		}
	}

	/**
	 * This method returns true or false depending on whether the supplied class
	 * implements JSONRPCEventListener or not
	 * 
	 * @param c
	 *            Class to test
	 * @return boolean indicating whether supplied class implements
	 *         JSONRPCEventListener or not
	 */
	private boolean implementsRPCEventListener(Class<?> c) {
		return isImplementsInterface(c, JSONRPCEventListener.class);
	}

	private boolean isImplementsInterface(Class<?> c, Class<?> intf) {
		Class<?>[] theInterfaces = c.getInterfaces();
		for (int i = 0; i < theInterfaces.length; i++) {

			String interfaceName = theInterfaces[i].getName();
			logger.debug("Class: " + c.getName() + " implents interface: "
					+ interfaceName);

			if (theInterfaces[i].equals(intf)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method generates a string containing the "signature" of a
	 * {@link java.util.Method} object in the form of methodname:paramcount or
	 * test:3
	 * <p>
	 * The output of this method is used to generate keys for Method objects
	 * stored in a {@link HashMap} for easy retrieval.
	 * 
	 * @param moduleName
	 * 
	 * @param Method
	 *            The Method object you would like to generate a signature for.
	 * @return String Contains the "signature" of a Method object in the form of
	 *         methodname:paramcount or test:3
	 */
	private String generateMethodSignature(String moduleName, Method m)
			throws ClassNotFoundException {
		Class<?> paramclasses[] = m.getParameterTypes();

		String mname = moduleName + "." + m.getName();

		return mname + ":" + paramclasses.length;

		/*
		 * if (paramclasses.length == 0) { return mname + ":0"; } else if
		 * (paramclasses.length == 1) { if (paramclasses[0].getName().matches(
		 * "org.jpoxy.events.JSONRPCMessageEvent")) { return null; } else if
		 * (paramclasses[0].getName().matches("org.json.JSONObject")) { return
		 * mname + ":JSONObject"; } else if
		 * (paramclasses[0].getName().matches("java.util.HashMap")) { return
		 * mname + ":HashMap"; } else if
		 * (paramclasses[0].getName().matches(".+\\..+") &&
		 * !paramclasses[0].getName().matches("java.lang.String") &&
		 * objectmapper.canSerialize(paramclasses[0])) { return mname +
		 * ":Object"; } else { return mname + ":1"; } } else { int parmscount =
		 * 0; for (int j = 0; j < paramclasses.length; j++) { if
		 * (paramclasses[j].getName().equals("java.lang.String") ||
		 * paramclasses[j].isPrimitive()) { parmscount++; } else { return null;
		 * } } return mname + ":" + parmscount; }
		 */
	}

	/**
	 * Method lists available RPC methods loaded from configured classes.
	 * 
	 * @return JSONObject Containing available method information.
	 * @throws JSONException
	 */
	public JSONObject listrpcmethods() {
		logger.info("Listing rpc methods");

		JSONObject result = new JSONObject();
		Iterator<String> iterator = rpcmethods.keySet().iterator();
		try {
			while (iterator.hasNext()) {
				String methodsig = iterator.next();
				RPCMethod rm = rpcmethods.get(methodsig);
				Method m = rm.getMethod();

				int modifiers = m.getModifiers();
				JSONObject methodObj = new JSONObject();
				methodObj.put("name", m.getName());
				methodObj.put("static", Modifier.isStatic(modifiers));
				methodObj.put("class", m.getDeclaringClass().getName());
				methodObj.put("returns", m.getReturnType().getName());
				Class<?> paramclasses[] = m.getParameterTypes();
				for (int i = 0; i < paramclasses.length; i++) {
					methodObj.append("params", paramclasses[i].getName());
				}
				if (!methodObj.has("params")) {
					methodObj.put("params", new JSONArray());
				}

				result.append("method", methodObj);
			}
		} catch (JSONException e) {
			logger.error("", e);
		}

		return result;
	}

	/**
	 * This method attempts to take a Throwable object and turn it into a valid
	 * JSONRPC error.
	 * 
	 * @param t
	 * @throws JSONException
	 */
	private Response handleException(Response response) {
		if (!DETAILED_ERRORS) {
			try {
				response.clearErrorData();
			} catch (JSONException e) {
				logger.error("", e);
			}
		}
		fireMessageEvent(generateMessage(JSONRPCMessage.EXCEPTION, null, null));

		return response;
	}

	/**
	 * Unified method for outputting the internal jsonresponse (error or not).
	 * Method checks for a "debug" parameter and, if it is set to "true", prints
	 * the JSONObject in a more human-readable fashion.
	 * 
	 * @param req
	 * @param res
	 * @throws IOException
	 * @throws JSONException
	 */
	private void writeResponse(HttpServletRequest req, HttpServletResponse res,
			List<Response> rpcResponses, boolean batch) throws IOException {
		String jsonStr = "";

		boolean debug = false;
		if (req.getParameter("debug") != null
				&& req.getParameter("debug").matches("true")) {
			debug = true;
		}

		try {

			if (rpcResponses.size() == 1 && batch == false) {
				Response resp = rpcResponses.get(0);
				jsonStr = debug ? resp.getJSONString(2) : resp.getJSONString();
			} else {
				JSONArray array = new JSONArray();
				StringWriter sw = new StringWriter();
				for (Response response : rpcResponses) {
					array.put(response.getJSON());
				}

				jsonStr = debug ? array.toString(2) : array.toString();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String outputStr = null;
		if (req.getParameter("callback") != null) {
			if (req.getParameter("callback").matches("^\\?")) {
				outputStr = "(" + jsonStr + ")";
			} else if (req.getParameter("callback").matches("^(\\d|\\w|\\.)+$")) {
				outputStr = req.getParameter("callback") + "(" + jsonStr + ")";
			} else {
				outputStr = "{\"error\":\"Invalid callback parameter specified.\"}";
			}
		} else {
			outputStr = jsonStr;
		}

		if (PRINT_MESSAGE) {
			logger.info("<<< Send to client: ");
			logger.info(outputStr);
		}

		// 写入到服务器输出
		outputStr += "\r\n";

		// 加密，如果有需要的话
		if (ENCRYPT_MESSAGE) {
			outputStr = EncrpytionTool.encryptByBase64_3DES(outputStr);
		}

		// 压缩，如果需要的话
		byte[] output = outputStr.getBytes("UTF-8");
		output = gzipToByte(output, res);

		OutputStream os = res.getOutputStream();
		try {
			os.write(output);
		} finally {
			os.close();
		}
	}

	/**
	 * Method to generate a JSONRPCMessage, usually used in connection with a
	 * fireMessageEvent
	 * 
	 * @param code
	 * @param req
	 * @param res
	 * @return JSONRPCMessage
	 * @see fireMessageEvent
	 */
	private JSONRPCMessage generateMessage(int code, HttpServletRequest req,
			HttpServletResponse res) {
		JSONRPCMessage msg = new JSONRPCMessage(code);
		msg.setServletConfig(getServletConfig());
		// msg.setRPCResponse(response);
		return msg;
	}

	/**
	 * Forwards request to doGet method for consistency
	 * 
	 * @see doGet
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req, res);
	}


	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String origin = req.getHeader("Origin");
		String requestHeaders = req.getHeader("Access-Control-Request-Headers");

		// 允许来源
		res.setHeader("Access-Control-Allow-Origin", origin);

		// 允许的http方法
		res.setHeader("Access-Control-Allow-Methods", "POST");

		// 允许提交 Cookies
		res.setHeader("Access-Control-Allow-Credentials", "true");

		// 允许的额外请求头
		if (requestHeaders != null) {
			res.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type");
		}
	}

	/**
	 * Called by servlet container when a request is made.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setContentType("application/json; charset=UTF-8");

		res.setHeader("Access-Control-Allow-Origin", "*");
		// res.addHeader("Access-Control-Allow-Methods", "POST");
		// res.addHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");

		List<Request> rpcRequests = new ArrayList<Request>(1);
		List<Response> rpcResponses = new ArrayList<Response>();

		boolean batch = parseRequest(req, rpcRequests); // 解析 request

		if (rpcRequests == null || rpcRequests.size() == 0) {
			if (EXPOSE_METHODS) {
				Response response = new Response();
				response.setResult(listrpcmethods());
				rpcResponses.add(response);
			} else {
				Response response = new Response(new RPCException(
						"Unspecified JSON-RPC method.",
						RPCException.PREDEFINED_ERROR_METHOD_NOT_FOUND));
				rpcResponses.add(response);
			}
		} else {
			for (Request request : rpcRequests) {

				Response response = getResponseFromRequestId(request);

				try {
					handleRequest(req, request, response);
				} catch (InvocationTargetException ite) {
					// 处理代理里面的异常
					Throwable throwable = ite.getCause();
					if (throwable != null) {

						logger.error("调用 jssonrpc 实现时出现异常，", throwable);

						response = getResponseFromRequestId(request, throwable);
						handleException(response);
					}
				} catch (Exception e) {
					// 处理其他异常
					response = getResponseFromRequestId(request, e);
					handleException(response);
				}

				rpcResponses.add(response);
			}
		}

		// 处理正常
		if (rpcResponses.size() != 0) {
			writeResponse(req, res, rpcResponses, batch);
		} else {
			PrintWriter writer = null;
			try {
				writer = res.getWriter();
				writer.println("{\"error\":\"unrecoverable error\"}");
			} finally {
				if (writer != null)
					writer.close();
			}
		}
	}

	private Response getResponseFromRequestId(Request request) {
		return getResponseFromRequestId(request, null);
	}

	private Response getResponseFromRequestId(Request request, Throwable t) {
		Response response;

		if (t == null) {
			response = new Response();
		} else {
			response = new Response(t);
		}

		if (request.getId() != null) {
			try {
				response.setId(request.getId(), request.idIsNumber());
			} catch (JSONException e) {
				logger.error("", e);
			}
		}
		return response;
	}

	/**
	 * Converts HttpServletRequest POST/GET parameters into a HashMap
	 */
	private HashMap reqParamsToHashMap(HttpServletRequest request) {
		HashMap<String, Object> retmap = new HashMap();

		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();

			if (request.getParameterValues(paramName) != null
					&& request.getParameterValues(paramName).length > 1) {
				String[] paramValue = request.getParameterValues(paramName);
				retmap.put(paramName, paramValue);
			} else {
				String paramValue = request.getParameter(paramName);
				retmap.put(paramName, paramValue);
			}
		}

		retmap.put("request", request);

		return retmap;
	}

	public static String getRawPostData(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = req.getReader();

			String line;
			do {
				line = reader.readLine();
				if (line != null)
					sb.append(line).append("\n");
			} while (line != null);
			reader.close();
		} catch (IOException e) {
			return null;
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param req
	 *            HttpServletRequest given to us from doGet or doPost
	 * @param res
	 *            HttpServletResponse we can output information to, This method
	 *            passes res to the
	 *            {@link #writeResponse(HttpServletRequest, HttpServletResponse)}
	 *            method.
	 * @throws JSONException
	 * @throws IOException
	 * @throws JSONException
	 * @throws RPCException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void handleRequest(HttpServletRequest req, Request request,
			Response response) throws JSONException, JsonParseException,
			JsonMappingException, IOException, RPCException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException {

		// 开始处理方法调用
		String method = request.getMethod();

		int param_count = request.getParamCount();
		String methodsig = method + ":" + param_count;

		logger.debug("Looking for methodsig: " + methodsig);

		Object result = new Object();
		RPCMethod m = null;
		Object methparams[] = null;

		// HashMap<String, ?> paramstohashmap = null;
		// paramstohashmap = reqParamsToHashMap(req);

		// 寻找要调用的那个方法实例
		if (rpcmethods.containsKey(methodsig)) {
			m = rpcmethods.get(methodsig);
			if (param_count > 0) {
				methparams = new Object[param_count];
				Class[] paramtypes = m.getParameterTypes();
				Type[] types = m.getGenericParameterTypes();

				for (int i = 0; i < paramtypes.length; i++) {

					// 解析每个参数
					if (paramtypes[i].getName().matches("float")) {
						methparams[i] = Float.parseFloat(request.getParamAt(i));
					} else if (paramtypes[i].getName().matches("int")) {
						methparams[i] = Integer.parseInt(request.getParamAt(i));
					} else if (paramtypes[i].getName().matches("long")) {
						methparams[i] = Long.parseLong(request.getParamAt(i));
					} else if (paramtypes[i].getName().matches(
							"java.lang.String")) {
						methparams[i] = request.getParamAt(i);
					} else if (paramtypes[i].getName().matches("double")) {
						methparams[i] = Double.parseDouble(request
								.getParamAt(i));
					} else if (paramtypes[i].getName().matches("boolean")) {
						methparams[i] = Boolean.parseBoolean(request
								.getParamAt(i));
					} else if (paramtypes[i].getName().equals("[B")) {
						// byte[]
						byte[] encoded = objectmapper.convertValue(
								request.getParamAt(i), byte[].class);
						methparams[i] = encoded;
					} else if (paramtypes[i].isEnum()) {
						methparams[i] = Enum.valueOf(paramtypes[i],
								request.getParamAt(i));
					} else if (paramtypes[i].getName()
							.matches("java.util.List")) {
						if (types[i] instanceof ParameterizedType) {
							Type[] actualTypes = ((ParameterizedType) types[i])
									.getActualTypeArguments();
							TypeFactory factory = objectmapper.getTypeFactory();
							methparams[i] = objectmapper.readValue(request
									.getParamAt(i), factory
									.constructCollectionType(paramtypes[i],
											(Class<?>) actualTypes[0]));
						} else {
							throw new RPCException(
									"List 必须加泛型",
									RPCException.PREDEFINED_ERROR_INVALID_PARAMS);
						}

					} else if (paramtypes[i].getName().matches("java.util.Map")) {
						if (types[i] instanceof ParameterizedType) {
							Type[] actualTypes = ((ParameterizedType) types[i])
									.getActualTypeArguments();
							TypeFactory factory = objectmapper.getTypeFactory();
							methparams[i] = objectmapper.readValue(request
									.getParamAt(i), factory.constructMapType(
									paramtypes[i], (Class<?>) actualTypes[0],
									(Class<?>) actualTypes[1]));
						} else {
							throw new RPCException(
									"Map 必须加泛型",
									RPCException.PREDEFINED_ERROR_INVALID_PARAMS);
						}
					} else {
						methparams[i] = objectmapper.readValue(
								request.getParamAt(i), paramtypes[i]);
					}
				}
			}
		} else {
			throw new RPCException("JSON-RPC method [" + method + "] with "
					+ param_count + " parameters not found.",
					RPCException.PREDEFINED_ERROR_INVALID_PARAMS);
		}

		logger.debug("Running methodsig: " + methodsig + " param_count:"
				+ param_count);

		// 方法就绪，可以执行

		// 去服务器远程执行
		if (m.isProxy()) {

			ModulProxy mp;
			if (PERSIST_CLASS) {
				mp = (ModulProxy) rpcobjects.get(m.getImplementClass());
			} else {
				Class<?> c = Class.forName(m.getImplementClass());
				mp = (ModulProxy) c.newInstance();
			}

			Date timeBefore = new Date();

			result = mp.invoke(m.getInterfaceClass(), m.getMethodName(),
					m.getParameterTypes(), methparams,
					new JsonRpcSessionAttributes(req.getSession()));

			Date timeAfter = new Date();

			long ms = timeAfter.getTime() - timeBefore.getTime();
			logger.debug("代理用时：" + (ms / 1000.00) + "秒");

		} else {
			result = runMethod(m, param_count, methparams, req);
		}

		// 写返回值到客户端
		if (result == null) {
			response.setResult(JSONObject.NULL);
			return;
		}
		String resultclassname = result.getClass().getName();
		if (resultclassname.equals("[B")) {
			String jsonstr = objectmapper.writeValueAsString(result);
			result = jsonstr;
		} else if (resultclassname.equals("java.util.Date")) {
			String jsonstr = objectmapper.writeValueAsString(result);
			result = jsonstr;
		} else if (resultclassname.matches(".+\\..+")
				&& !resultclassname.matches("^org\\.json\\..+")
				&& !resultclassname.matches("^java\\.lang\\..+")
				&& !result.getClass().isPrimitive()) {
			String jsonstr = objectmapper.writeValueAsString(result);
			if (jsonstr.matches("^\\[.*\\]$")) {
				result = new JSONArray(jsonstr);
			} else if (jsonstr.matches("^\\{.*\\}$")) {
				result = new JSONObject(jsonstr);
			} else {
				throw new RPCException(
						"could not reserialize data returned from method, "
								+ jsonstr,
						RPCException.PREDEFINED_ERROR_INTERNAL_ERROR);
			}
		}

		response.setResult(result);
	}

	/**
	 * 解析请求报文
	 * 
	 * @param req
	 * @param rpcRequests
	 * @return 是否是批量请求
	 */
	private boolean parseRequest(HttpServletRequest req,
			List<Request> rpcRequests) {
		boolean batch = false;

		// 不再支持非Post请求
		if (!req.getMethod().matches("POST")) {
			return batch;
		}

		req = new HttpServletRequestWrapper(req);
		String body = getRawPostData(req);
		if (body == null) {
			return batch;
		}

		// 解密请求
		if (DENCRYPT_MESSAGE) {
			body = EncrpytionTool.dencryptFromBase64_3DES(body);
		}

		if (PRINT_MESSAGE && body != null) {
			logger.info("");
			logger.info(">>> Received from client: ");
			logger.info(body);
		}

		try {
			Object json = new JSONTokener(body).nextValue();
			if (json instanceof JSONObject) {
				rpcRequests.add(new Request(body));
				req = new HttpServletRequestWrapper(req, "".getBytes(),
						new HashMap());
				batch = false;
			} else if (json instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) json;

				for (int i = 0; i < jsonArray.length(); i++) {
					Object item = jsonArray.get(i);
					if (item instanceof JSONObject) {
						Request request = new Request();
						request.parseJSON((JSONObject) item);
						rpcRequests.add(request);
					}
				}

				req = new HttpServletRequestWrapper(req, "".getBytes(),
						new HashMap());
				batch = true;

			} else {
				// 报文非法
				return batch;
			}
		} catch (JSONException e) {
			logger.error("", e);
			return batch;
		}

		return batch;
	}

	public static String ucfirst(String string) {
		String retval;

		retval = string.toUpperCase(Locale.getDefault()).substring(0, 1)
				+ string.substring(1);

		return retval;
	}

	private Object runMethod(RPCMethod method, int param_count,
			Object[] methparams, HttpServletRequest req)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException,
			ClassNotFoundException, JSONException, IOException, RPCException {

		Method m = method.getMethod();

		Object result = new Object();
		Object obj;

		if (PERSIST_CLASS) {
			obj = rpcobjects.get(method.getImplementClass());
		} else {
			Class<?> c = Class.forName(method.getImplementClass());
			obj = c.newInstance();
		}

		if (method.isSessionRelated()) {
			((SessionRelatedModul) obj)
					.setSession(new JsonRpcSessionAttributes(req.getSession()));
		}

		if (param_count > 0) {
			result = (Object) m.invoke(obj, methparams);
		} else {
			result = (Object) m.invoke(obj);
		}

		if (!PERSIST_CLASS) {
			obj = null;
		}

		return result;
	}

	/**
	 * gzip 压缩
	 * 
	 * @param str
	 * @return
	 */
	private byte[] gzipToByte(byte[] str, HttpServletResponse response) {
		if (str == null || str.length == 0) {
			return null;
		}

		if (str.length < GZIP_THRESHOLD * 1024) {
			return str;
		}

		// 执行压缩操作
		response.setHeader("Content-encoding", "gzip");

		byte[] result = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str);
			gzip.flush();
		} catch (IOException e) {
			logger.error("gzip压缩异常:", e);
		} finally {
			try {
				gzip.close();
			} catch (IOException e) {
				logger.error("", e);
			}
			try {
				out.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}

		result = out.toByteArray();

		if (PRINT_MESSAGE) {
			logger.info(String.format("压缩前大小:%d, 压缩后大小:%d, 压缩比率:%f",
					str.length, result.length,
					(float) (str.length - result.length) / str.length));
		}

		return result;
	}
}
