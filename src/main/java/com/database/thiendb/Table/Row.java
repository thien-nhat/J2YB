package com.database.thiendb.Table;

import java.util.Arrays;

public class Row { 
    private Object[] values;

    public Row(Object[] values) {
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Row [values=" + Arrays.toString(values) + "]";
    }
}

