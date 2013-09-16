package mixedserver.protocol;

/**
 * 模块描述
 * 
 * @author zhangxha
 * 
 */
public class ModuleConfiguration {
	private String name;

	private String interfaceClass;

	private String implClass;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public String getImplClass() {
		return implClass;
	}

	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}
}
