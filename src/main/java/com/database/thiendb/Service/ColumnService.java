package com.database.thiendb.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Column;
import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Repository.DatabaseRepository;

@Service
public class ColumnService {
    @Autowired
    private DatabaseRepository databaseRepository;

    public ColumnService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }
    public Column getColumn(String databaseName, String tableName, Integer columnId) {
        return null;
    }
    public void addColumn(String databaseName, String tableName, Column column) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.addColumn(column);
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            System.out.println("Database '" + databaseName + "' not found.");
        }

        this.databaseRepository.save(database);
    }
}
