package com.database.thiendb.DataStructure;

public class Column {
    private String name;
    private String dataType;
    private boolean isPrimaryKey;
    private String defaultValue;

    public Column(String name, String dataType, boolean isPrimaryKey, String defaultValue) {
        this.name = name;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    // Kiểm tra kiểu dữ liệu tương ứng
    public boolean isValidValue(Object value) {
        switch (dataType) {
            case "int":
                return value instanceof Integer;
            case "string":
                return value instanceof String;
            // Thêm các kiểu dữ liệu khác .....
            default:
                return false;
        }
    }

}
