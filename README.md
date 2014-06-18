#MixedLibrary

// 2014-06-18更新，说明文档已补全

说明文档还没时间弄，不过有两个全面的Demo。项目最初来自于另一个项目[JProxy](https://code.google.com/p/jpoxy/)，但这个项目并没有支持json-rpc2.0规范，也没有批量操作、Spring支持等等，现在可以说是思路类似，但功能完全不同的两个项目了

##功能说明

1. 数据类型支持。所有内置类型；java.util.Date；byte[]；POJO；数组；java.util.List<Class>; java.util.Map<String, Class>，简单层次的范型 Foo<Bar>
2. 全面支持 JSON-RPC 2.0 规范，http://www.jsonrpc.org/specification
3. 支持** 批量(Batch) **操作，多个请求可以一起发送
4. Spring友好的rpc实现类，如果在classpath里面发现有 Spring 容器，并且在 Spring 的配置文件里面有配置 <bean class="mixedserver.protocol.ModuleContext" /> 那么会启用注解支持

##服务端例子

	// 接口
	public interface Echo {
		public String say(String what) throws RPCException;
	}
	
	// 实现
	@Component // 这里用Spring容器来管理，当然你也可以不用Spring
	public class EchoImpl implements Echo {

		@Override
		public String say(String what) throws RPCException {
			return what;
		}

	}
	
	// 配置 module-configuration.xml
	<?xml version="1.0" encoding="UTF-8" ?>
	<modules>
		<module>
			<interface>mixedserver.application.Echo</interface>
		</module>
	</modules>


##客户端代码例子

	Client client = Client.getClient("http://localhost:8080/api");

	// 设定是否解密服务端的消息
	client.setDencryptMessage(false);
	// 设定是否加密服务端的消息
	client.setEncryptMessage(false);
	
	// 打开一个客户端代理
	Echo echo = client.openProxy("echo", Echo.class);
	try {
		String message = "Hello World!";
		echo.say(message);
	} catch (RPCException e) {
		e.printStackTrace();
	} finally {
		// 关闭代理
		client.closeProxy(echo);
	}

##开发路线

1. 在 web.xml 的 json-rpc servlet 参数里，可以指定配置文件，这样可以支持多个接口不同的配置，比如一个不加密，一个加密
2. 支持自定义密钥设置
3. 支持超时设置


##文件说明

MixedServer 是库工程，通过mvn构建出需要的jar包：

	mvn clean package

MixedServerDemo 是服务端演示，运行命令后会在 http://localhost:8080/JSON-RPC 提供json-rpc 2.0服务
	
	mvn jetty:run

MixedServerDemoAndroid 是android演示，需要提前运行 MixedServerDemo，同时需要修改 MixedServerDemoAndroid/src/mixedserver/demo/tools/GlobalTools.java 里面的接口地址

##版本说明

v2.2.3 发布说明

1. 配置文件简化，可以仅配置接口，实现类从Spring容器获取
2. 可以配置一个包下面所有接口， com.somepackage.*，实现依然从 Spring 容器获取
3. 模块配置时，name为可选项，name 直接取接口名字，将首字母变小写
4. 登录接口添加记住用户方法
5. 针对 Apache shiro 的权限检查异常，转换为对应错误码

v2.1.0 发布说明

1. 添加新版的登录接口 AuthenticationManagement
2. 支持简单范型类型，比如 Foo<Bar>，但不支持多层嵌套比如 Foo<Bar<Other>

v2.0.2 发布说明

1. 修改控制台打印日志格式，更加美观

v2.0.1 发布说明

1. 去掉commons-io.jar commons-lang.jar 依赖
2. bug修复：客户端登录以后，往session放置用户名密码错误

v2.0 发布说明

1. 模块实现上支持Spring注解
2. 添加 java.util.Date 的支持，Date会映射到 long 类型的时间戳