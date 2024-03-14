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

    // Phương thức để lấy loại dữ liệu cơ bản
    private String getBaseDataType(String dataType) {
        int parenIndex = dataType.indexOf("(");
        if (parenIndex != -1) {
            return dataType.substring(0, parenIndex);
        } else {
            return dataType;
        }
    }

    // Phương thức để lấy độ dài tối đa của kiểu dữ liệu
    private int getMaxLength(String dataType) {
        int parenIndex = dataType.indexOf("(");
        if (parenIndex != -1) {
            String lengthStr = dataType.substring(parenIndex + 1, dataType.length() - 1);
            try {
                return Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu chuỗi không chứa một số hợp lệ
                e.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    // Kiểm tra kiểu dữ liệu tương ứng
    public boolean isValidValue(Object value) {
        switch (getBaseDataType(dataType)) {
            case "int":
                return value instanceof Integer;
            case "string":
                return value instanceof String;
            case "varchar":
                int maxLength = getMaxLength(dataType);
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
