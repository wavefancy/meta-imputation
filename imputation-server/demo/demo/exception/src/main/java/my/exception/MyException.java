package my.exception;

import my.entity.ResultEnum;

public class MyException extends RuntimeException{

	private static final long serialVersionUID = -5577715444374136335L;
	
	private String code;

    public MyException(ResultEnum resultEnum) {
    	super(resultEnum.getMsg());
        this.code = resultEnum.getCode();
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
