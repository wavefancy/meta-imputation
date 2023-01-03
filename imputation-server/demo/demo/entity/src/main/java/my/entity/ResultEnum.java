package my.entity;

public enum ResultEnum {
    SUCCESS("0", "sueecss"),
    ZOOKEEPER_FAILURE("0010001", "Zookeeper发生故障"),
    EXECUTE_FAILURE("0010002", "运行故障"),
    NO_DATA("0010003", "没有数据"),
    INSERT_FAILURE("0010004", "插入数据记录失败"),
    DATABASE_FAILURE("0010005", "数据库故障，请联系管理员"),
    HTTPREQUEST_FAILURE("0010006", "HTTP请求失败"),
    HTTPRES_FAILURE("0010006", "HTTP响应错误"),
    
    REGISTER_FAILURE1("0020001", "注册失败，用户名已存在"),
    REGISTER_FAILURE2("0020002", "注册失败，无法新建用户"),
    REGISTER_FAILURE3("0020003", "注册失败，注册邮箱已使用"),
    LOGIN_FAILURE("0020004", "登录失败，您输入的用户名或密码有误，或用户不存在"),
    ACCESS_FAILURE("0020005", "抱歉，您无权访问"),
    USERQUERY_FAILURE("0020006", "抱歉，查询不到您的信息"),
    
    UPLOAD_FILE_FAILURE("0030001", "文件上传失败"),
    CHUNK_EXISTING("0030002", "文件块已存在，不必重复上传"),
    PATH_NOTEXISTING("0030003", "文件存储路径不存在或无法创建"),
    CHUNK_SIZE_ERROR("0030004", "文件块大小计算有误"),
    USER_HAS_NOACCESS("0030005", "抱歉，您没有改文件的访问权限"),
    IDENTIFIER_ERROR("0030006", "抱歉，您提供的文件标识符错误"),
    NOFILE("0030007", "抱歉，文件不存在或已删除"),
    NOACCESS("0030008", "抱歉，您没有删除这个文件的权限"),
    CANNOT_DELETE("0030009", "抱歉，文件删除失败，请联系管理员"),
    FILEID_ERROR("0030010", "抱歉，文件标识符有误"),
    FILE_EXTRACT_INFO_FAILURE("0030011", "抱歉，文件信息提取失败"),
    FILENAME_ERROR("0030011", "抱歉，您的文件名称有误，请核对后重新操作")
    ;
	
	/*信息编码*/
    private String code;
   
    /*提示信息*/
    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
