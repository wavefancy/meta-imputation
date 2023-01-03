package loading.storage.entity;

public enum LoadingStorageResultEnum {
    SUCCESS("0", "sueecss"),
    ZOOKEEPER_FAILED("0010001", "Zookeeper发生故障"),
    EXECUTE_FAILED("0010002", "运行故障"),
    GETTINGDATA_FAILED("0010003", "无法获取Zookeeper节点下数据"),
    INPUT_FAILED("0010004", "规则录入失败"),
    REMOVE_FAILED("0010005", "规则删除失败"),
    CHANGESTATUS_FAILED("0010006", "无法改变规则现有状态"),
    CANNOTREMOVE("0010007", "当前状态下无法删除规则"),
    ERRSTATUS("0010011", "规则处于错误状态"),
    
    PLUGINPATH_ERROR("0010008", "插件路径错误"),
    NOPLUGINPATH("0010009", "没有任何解析插件可供使用"),
    PARAMISSING("0010010", "缺少必要的参数"),
    ;
	
	/*信息编码*/
    private String code;
   
    /*提示信息*/
    private String msg;

    LoadingStorageResultEnum(String code, String msg) {
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
