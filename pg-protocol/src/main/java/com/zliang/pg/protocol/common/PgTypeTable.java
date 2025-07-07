package com.zliang.pg.protocol.common;

import java.util.HashMap;
import java.util.Map;

public class PgTypeTable {

    public static final PgType bool = new PgType(16, "bool", 1000, 0, 1, PgType.Type.Base, PgType.Category.Boolean);
    public static final PgType bytea = new PgType(17, "bytea", 1001, 0, -1, PgType.Type.Base, PgType.Category.UserDefined);
    // char是关键字
    public static final PgType chara = new PgType(18, "char", 1002, 0, 1, PgType.Type.Base, PgType.Category.String);
    public static final PgType name = new PgType(19, "name", 1003, 18, 64, PgType.Type.Base, PgType.Category.String);
    public static final PgType int8 = new PgType(20, "int8", 1016, 0, 8, PgType.Type.Base, PgType.Category.Numeric);
    public static final PgType int2 = new PgType(21, "int2", 1005, 0, 2, PgType.Type.Base, PgType.Category.Numeric);
    public static final PgType int2vector = new PgType(22, "int2vector", 1006, 21, -1, PgType.Type.Base, PgType.Category.Array);
    public static final PgType int4 = new PgType(23, "int4", 1007, 0, 4, PgType.Type.Base, PgType.Category.Numeric);
    public static final PgType regproc = new PgType(24, "regproc", 1008, 0, 4, PgType.Type.Base, PgType.Category.Numeric);
    public static final PgType text = new PgType(25, "text", 1009, 0, -1, PgType.Type.Base, PgType.Category.String);
    public static final PgType oid = new PgType(26, "oid", 1028, 0, 4, PgType.Type.Base, PgType.Category.Numeric);

    // 后续可填充其他不常见的类型
    public static final PgType json = new PgType(114, "json", 199, 0, -1, PgType.Type.Base, PgType.Category.UserDefined);
    public static final PgType xml = new PgType(142, "xml", 143, 0, -1, PgType.Type.Base, PgType.Category.UserDefined);
    public static final PgType bpchar = new PgType(1042, "bpchar", 1014, 0, -1, PgType.Type.Base, PgType.Category.String);
    public static final PgType varchar = new PgType(1043, "varchar", 1015, 0, -1, PgType.Type.Base, PgType.Category.String);
    public static final PgType date = new PgType(1082, "date", 1182, 0, 4, PgType.Type.Base, PgType.Category.DateTime);
    public static final PgType time = new PgType(1083, "time", 1183, 0, 8, PgType.Type.Base, PgType.Category.DateTime);
    public static final PgType timestamp = new PgType(1114, "timestamp", 1114, 0, 8, PgType.Type.Base, PgType.Category.DateTime);
    public static final PgType timestamptz = new PgType(1184, "timestamptz", 1184, 0, 8, PgType.Type.Base, PgType.Category.DateTime);
    public static final PgType interval = new PgType(1186, "interval", 1186, 0, 16, PgType.Type.Base, PgType.Category.Timespan);
    public static final PgType numeric = new PgType(1231, "numeric", 1231, 0, -1, PgType.Type.Base, PgType.Category.Numeric);

    // 创建通过变量名分组的映射
    public static final Map<String, PgType> typeMap = new HashMap<>();

    static {
        typeMap.put("bool", bool);
        typeMap.put("bytea", bytea);
        typeMap.put("char", chara);
        typeMap.put("name", name);
        typeMap.put("int8", int8);
        typeMap.put("int2", int2);
        typeMap.put("int2vector", int2vector);
        typeMap.put("int4", int4);
        typeMap.put("regproc", regproc);
        typeMap.put("text", text);
        typeMap.put("oid", oid);
        typeMap.put("json", json);
        typeMap.put("xml", xml);
        typeMap.put("bpchar", bpchar);
        typeMap.put("varchar", varchar);
        typeMap.put("date", date);
        typeMap.put("time", time);
        typeMap.put("timestamp", timestamp);
    }

    // 创建通过OID分组的映射
    public static final Map<Integer, PgType> typeMapByOid = new HashMap<>();

    static {
        for (Map.Entry<String, PgType> entry : typeMap.entrySet()) {
            typeMapByOid.put(entry.getValue().getOid(), entry.getValue());
        }
    }
}