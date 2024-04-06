package com.database.thiendb.DataStructure;

import com.database.thiendb.Utils.SharedFunction;

public class Column {
    private String name;
    private String dataType;
    private boolean isPrimaryKey;
    private boolean isIndex;
    private String defaultValue;
    private boolean isForeignKey;
    private String referencedTableName; // Name of the referenced table
    private String referencedColumnName; // Name of the referenced column
    
    public Column(String name, String dataType, boolean isPrimaryKey, String defaultValue, boolean isIndex) {
        this.name = name;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.defaultValue = defaultValue;
        this.isIndex = isIndex;
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
    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }
    
    public boolean isForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(boolean isForeignKey) {
        this.isForeignKey = isForeignKey;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    // Kiểm tra kiểu dữ liệu tương ứng
    public boolean isValidValue(Object value) {
        switch (SharedFunction.getBaseDataType(dataType)) {
            case "int":
                return value instanceof Integer;
            case "string":
                return value instanceof String;
            case "varchar":
                int maxLength = SharedFunction.getMaxLengthOfDataType(dataType);
                return value instanceof String && ((String) value).length() <= maxLength;
            // Thêm các kiểu dữ liệu khác .....
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "Column [name=" + name + ", dataType=" + dataType + ", isPrimaryKey=" + isPrimaryKey + ", defaultValue="
                + defaultValue + "]";
    }

}
