package loading.storage.exception;

import loading.storage.entity.LoadingStorageResultEnum;

public class LoadingStorageException extends RuntimeException{

	private static final long serialVersionUID = -5577715444374136335L;
	
	private String code;

    public LoadingStorageException(LoadingStorageResultEnum resultEnum) {
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
