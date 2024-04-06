package com.database.thiendb.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.database.thiendb.DataStructure.Database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Repository
public class DatabaseRepository {
    @Autowired
    private Database database;

    public DatabaseRepository() {
    }

    public DatabaseRepository(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public List<Database> findAll() {
        List<Database> databases = new ArrayList<>();
        File folder = new File("./"); 
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                Database database = findDatabaseByName(file.getName().replace(".json", ""));
                if (database != null) {
                    databases.add(database);
                }
            }
        }
        return databases;
    }

    // Get database by name
    public Database findDatabaseByName(String databaseName) {
        String fileName = databaseName + ".json";
        File file = new File(fileName);
        if (file.exists()) {
            Gson gson = new GsonBuilder().create();

            try {
                // Mở tập tin JSON để đọc
                Reader reader = new FileReader(fileName);

                // Chuyển đổi JSON thành đối tượng Database và trả về
                Database database = gson.fromJson(reader, Database.class);

                // Đóng tập tin
                reader.close();
                return database;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            return null;
        return database;
    }

    // Create database
    public Database createDatabaseByName(String databaseName) {
        String fileName = databaseName + ".json";
        File file = new File(fileName);
        try {
            // Nếu tập tin chưa tồn tại, tạo một tập tin mới
            file.createNewFile();
            return new Database(databaseName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Save
    public void save(Database database) {
        // Tạo đối tượng Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            // Tạo tên file dựa trên tên của database
            String fileName = database.getDatabaseName() + ".json";

            // Mở tập tin JSON để ghi
            FileWriter writer = new FileWriter(fileName);

            // Chuyển đổi database thành JSON và ghi vào tập tin
            gson.toJson(database, writer);

            // Đóng tập tin
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
