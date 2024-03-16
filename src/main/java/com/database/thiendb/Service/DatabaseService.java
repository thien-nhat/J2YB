package com.database.thiendb.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.Exception.ObjectNotFoundException;
import com.database.thiendb.Repository.DatabaseRepository;

@Service
public class DatabaseService {
    @Autowired
    private DatabaseRepository databaseRepository;

    public DatabaseService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public DatabaseRepository getDatabaseRepository() {
        return databaseRepository;
    }

    public void setDatabaseRepository(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void addDatabase(String databaseName) {
        // TODO
        Database database = databaseRepository.createDatabaseByName(databaseName);
        if (database != null) {
            this.databaseRepository.save(database);
        } else {
            throw new ObjectNotFoundException("Database '" + databaseName + "' has not been created.");
        }
    }
}
