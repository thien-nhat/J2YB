package com.database.thiendb.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Repository.DatabaseRepository;

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
    public void addTable(String databaseName, String tableName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        database.createTable(tableName);
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


}
