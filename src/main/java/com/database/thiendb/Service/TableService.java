package com.database.thiendb.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Repository.DatabaseRepository;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

@Service
public class TableService {
    @Autowired
    private DatabaseRepository databaseRepository;
    
    public TableService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public Table getTable(String databaseName, String tableName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        return database.getTable(tableName);
    }

    // Only add table
    public void addTable(String databaseName, String tableName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        database.createTable(tableName);
        this.databaseRepository.save(database);
    }

    // Add table and columns
    public void addTable(String databaseName, String tableName, List<ColumnDefinition> columnDefinitions) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        database.createTable(tableName);
        Table newTable = database.getTable(tableName);
        for (ColumnDefinition columnDefinition : columnDefinitions) {
            String columnName = columnDefinition.getColumnName();
            String dataType = columnDefinition.getColDataType().toString();
            boolean isPrimaryKey = false;
            String defaultValue = "NULL";
            Column column = new Column(columnName, dataType, isPrimaryKey, defaultValue, false);
            newTable.addColumn(column);
        }
        this.databaseRepository.save(database);
    }

    public void updateTableName(String databaseName, String tableName, String newTableName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        database.updateTableName(tableName, newTableName);
        this.databaseRepository.save(database);
    }

    public void deleteTable(String databaseName, String tableName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        database.dropTable(tableName);
        this.databaseRepository.save(database);
    }

    public void addIndexedColumn(String databaseName, String tableName, String columnName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        Table table = database.getTable(tableName);
        table.addIndexedColumn(columnName);
        this.databaseRepository.save(database);
    }
    public Row findIndexedColumnByName(String databaseName, String tableName, String columnName, Object value) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        Table table = database.getTable(tableName);
        return table.findRowByIndexedColumn(columnName, value);
    }
}
