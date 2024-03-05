package com.database.thiendb.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.Repository.DatabaseRepository;

@Service
public class TableService {
    @Autowired
    private DatabaseRepository databaseRepository;

    public TableService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void addTable(String databaseName, String tableName) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);
        database.createTable(tableName);
        this.databaseRepository.save(database);
    }

}
