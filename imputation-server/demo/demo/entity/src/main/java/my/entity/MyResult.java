package my.entity;

public class MyResult<T> {
	/*错误码*/
	private String code;
	
	/*提示信息*/
	private String msg;
	
	/*结果数据*/
	private T data;

	public String getCode() {
		return code;
	}

	public void setCode(String Code) {
		this.code = Code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
