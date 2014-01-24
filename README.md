#MixedLibrary

说明文档还没时间弄，不过有两个全面的Demo

##功能说明

1. 数据类型支持。所有内置类型；java.util.Date；byte[]；POJO；数组；java.util.List<Class>; java.util.Map<String, Class>
2. 全面支持 JSON-RPC 2.0 规范，http://www.jsonrpc.org/specification
3. 支持** 批量(Batch) **操作，多个请求可以一起发送
4. Spring友好的rpc实现类，如果在classpath里面发现有 Spring 容器，并且在 Spring 的配置文件里面有配置 <bean class="mixedserver.protocol.ModuleContext" /> 那么会启用注解支持

##文件说明

MixedServer 是库工程，通过mvn构建出需要的jar包：

	mvn clean package

MixedServerDemo 是服务端演示，运行命令后会在 http://localhost:8080/JSON-RPC 提供json-rpc 2.0服务
	
	mvn jetty:run

MixedServerDemoAndroid 是android演示，需要提前运行 MixedServerDemo，同时需要修改 MixedServerDemoAndroid/src/mixedserver/demo/tools/GlobalTools.java 里面的接口地址

##版本说明

当前版本 v2.0，版本说明

1. 模块实现上支持Spring注解
2. 添加 java.util.Date 的支持，Date会映射到 long 类型的时间戳