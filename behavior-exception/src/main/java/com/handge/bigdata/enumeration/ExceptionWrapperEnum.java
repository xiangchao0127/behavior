package com.handge.bigdata.enumeration;

/**
 * @author liuqian
 * @date 2018/5/28.
 */
public enum ExceptionWrapperEnum {
    //******************************java内置异常（非计划型）******************************
    //IllegalArgumentException	抛出的异常表明向方法传递了一个不合法或不正确的参数。
    IllegalArgumentException("0101001", "参数不合法", "参数不合法"),

    //NullPointerException	当应用程序试图在需要对象的地方使用 null 时，抛出该异常
    NullPointerException("0201003", "空指针", "空指针"),

    //ArithmeticException	当出现异常的运算条件时，抛出此异常。例如，一个整数"除以零"时，抛出此类的一个实例。
    ArithmeticException("0102005", "运算条件异常", "运算条件异常"),

    //OutOfMemoryException 内存不足错误
    OutOfMemoryError("0299018", "内存不足", "内存不足"),

    //ArrayIndexOutOfBoundsException 数组下标越界异常
    ArrayIndexOutOfBoundsException("0202019", "数组下标越界", "数组下标越界"),

    //ClassCastException 当试图将对象强制转换为不是实例的子类时，抛出该异常。
    ClassCastException("0199020", "类型转换错误", "类型转换错误"),

    //FileNotFoundException 文件未找到异常
    FileNotFoundException("0299021", "文件未找到", "文件未找到"),

    //ArrayStoreException 数组存储异常
    ArrayStoreException("0199022", "数组存储异常", "数组存储异常"),

    //ClassNotFoundException 应用程序试图加载类时，找不到相应的类，抛出该异常。
    ClassNotFoundException("0299024", "类加载异常，找不到相应的类", "类加载异常，找不到相应的类"),

    //NumberFormatException 字符串转换为数字异常
    NumberFormatException("0299025", "字符串转数字错误", "字符串转数字错误"),

    //IllegalAccessException 没有访问权限
    IllegalAccessException("0299026", "没有访问权限", "没有访问权限"),

    //SQLException 数据库异常
    SQLException("0299027", "数据库异常", "数据库异常"),

    //EOFException 文件已结束异常
    EOFException("0299028", "文件已结束", "文件已结束"),

    //SecturityException 违背安全原则异常
    SecurityException("0299029", "违背安全原则", "违背安全原则"),

    //NoSuchMethodException 方法未找到异常
    NoSuchMethodException("0299030", "方法未找到", "方法未找到"),

    //InterruptedException	线程被中断异常
    InterruptedException("0299031", "线程被中断", "线程被中断"),

    //IOException IO异常
    IOException("0299036", "IO异常", "IO异常"),

    //SQLException相关
    DB_Refuse_Connection("0103006", "数据库拒绝连接", "数据获取失败"),
    DB_Deadlock("0299015", "数据库死锁", "数据获取失败"),
    DB_Connect_Timeout("0299016", "数据库连接超时", "数据获取失败"),
    DB_Unique_Index_Repeat("0104009", "数据库唯一索引重复", "数据库唯一索引重复"),
    DB_Table_Field_NOT_EXIST("0104010", "数据库表字段不存在", "数据库表字段不存在"),
    DB_Table_Field_Type_NOT_MATCH("0104011", "数据库表字段类型不匹配", "数据库表字段类型不匹配"),
    DB_Table_Field_LENGTH("0104012", "超出数据库表字段长度", "超出数据库表字段长度"),
    Invalid_Column_Name("0299032", "无效列名", "无效列名"),
    Table_Or_View_NOT_Exist("0299033", "表或视图不存在", "表或视图不存在"),
    Can_NOT_Insert_NULL("0299034", "不能插入空值", "不能插入空值"),
    Invalid_Value("0299035", "无效数字", "无效数字"),

    //IOException相关
    Enter_Path_NOT_EXIST("0103007", "输入路径不存在", "输入路径不存在"),
    Output_Path_NOT_EXIST("0103008", "输出路径不存在", "输出路径不存在"),
    Disk_Space_NOT_ENOUGH("0299017", "磁盘空间不足", "磁盘空间不足"),


    Parameter_Enum_NOT_EXIST("0101002", "对应枚举不存在", "对应枚举不存在"),

    Data_Outside_Special_Range("0102004", "超出特定范围", "超出特定范围"),

    DB_Result_IS_NULL("0204013", "数据库查询结果为空", "数据库查询结果为空"),

    Auth_TOKEN_ERROR("0105000", "令牌不正确或已过期", "令牌不正确或已过期"),
    Role_NOT_Power("0105014", "角色不具备该权限", "角色不具备该权限"),
    Auth_NOT_Validate("0105015", "认证用户名不存在", "认证用户名不存在"),
    Auth_Unknow_Error("0105016", "认证时发生未知错误，请联系管理员", "认证时发生未知错误，请联系管理员"),
    Auth_Password_Error("0105017", "账户密码错误", "账户密码错误"),
    Auth_Status_Error("0105018", "账户状态异常", "账户状态异常"),
    Auth_Role_Grade_Error("0105019", "当前角色无访问权限", "当前角色无访问权限"),
    Auth_Match_Error("0105020", "认证信息匹配失败", "认证信息匹配失败"),
    Auth_IP_VALID_HYPER_ERROR("0105021", "不允许使用超级管理员登录", "不允许使用超级管理员登录"),

    Connection_Pool_NOT_Resource("0203023", "连接池没有资源", "连接池没有资源"),
    Parameter_Enum_NOT_Match("0299037", "枚举不匹配", "枚举不匹配"),
    Account_NOT_Exist("0299038", "账号不存在", "账号不存在"),
    Member_Exist_NOT_Delete("0299039","该部门或其下级部门成员不为空，无法删除","该部门或其下级部门成员不为空，无法删除"),
    NOT_NULL("0299040","不能为空","不能为空");


    private String code;
    private String explain1;
    private String explain2;

    ExceptionWrapperEnum(String code, String explain1, String explain2) {
        this.code = code;
        this.explain1 = explain1;
        this.explain2 = explain2;
    }


    public String getCode() {
        return code;
    }

    public String getExplain1() {
        return explain1;
    }

    public String getExplain2() {
        return explain2;
    }
}
