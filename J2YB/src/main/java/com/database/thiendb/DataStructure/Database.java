package com.database.thiendb.DataStructure;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.database.thiendb.Exception.ObjectNotFoundException;

@Component
public class Database {

    private String databaseName;
    private HashMap<String, Table> tables;

    public Database() {
    }
    
    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new HashMap<>();
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    public void addTable(String tableName, Table table) {
        tables.put(tableName, table);
    }

    public void createTable(String tableName) {
        Table table = new Table();
        tables.put(tableName, table);
    }
    public void updateTableName(String tableName, String newTableName) {
        if (tables.containsKey(tableName)) {
            Table table = tables.remove(tableName);
            tables.put(newTableName, table);
        } else {
            throw new ObjectNotFoundException("Table '" + tableName + "' not found in database.");
        }
    }

    public void dropTable(String tableName) {
        tables.remove(tableName);
    }

}
