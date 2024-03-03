package com.database.thiendb.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.JsonDatabaseExporter;
import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;

@Service
public class DatabaseService {
    @Autowired
    private Database database;

    public DatabaseService(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return this.database;
    }

    // Lấy all cột

    // Lấy all hàng

    // Lấy bảng
    public Table getTable(String databaseName, String tableName) {
        // TODO
        return database.getTable(tableName);
    }
    // Lấy hàng
    // Lấy cột

    // Thêm database
    public void addDatabase(String databaseName) {
        // TODO
        // this.database = new Database(databaseName);
    }

    // Thêm bảng
    public void addTable(String databaseName, String tableName) {
        // TODO
        Database database = new Database(databaseName);
        database.createTable(tableName);
        JsonDatabaseExporter.exportToJson(this.database);
    }

    // Thêm hàng
    public void addRow(String databaseName, String tableName, Row row) {
        // TODO
        this.database = new Database(databaseName);
        database.getTable(tableName).addRow(row.getValues());
        JsonDatabaseExporter.exportToJson(this.database);
    }

    // Thêm cột
    public void addColumn(String databaseName, String tableName, Column column) {
        // TODO
        Database database = new Database(databaseName);
        database.getTable(tableName).addColumn(column);
        JsonDatabaseExporter.exportToJson(this.database);
    }

    // Sửa hàng
    public void updateRow(String databaseName, String tableName, Integer rowId, Row row) {
        // TODO
        this.database = new Database(databaseName);
        database.getTable(tableName).updateRow(--rowId, row.getValues());

        JsonDatabaseExporter.exportToJson(this.database);
    }
    // Sủa cột

    // Xóa hàng

    // Xóa cột

}
