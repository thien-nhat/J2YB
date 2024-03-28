package com.database.thiendb.Service;

import java.util.List;

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


    public List<Database> getAllDatabases() {
        return databaseRepository.findAll();
    }

    public Database getDatabaseByName(String name) {
        return databaseRepository.findDatabaseByName(name);
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
