package com.database.thiendb.Repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Repository
public class DatabaseRepository {
    @Autowired
    private Database database;

    public DatabaseRepository(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    // Get database
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

                System.out.println("Data imported from JSON file successfully.");
                return database;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            return null;
        return null;
    }

    // Create database
    public void createDatabaseByName(String databaseName) {
        String fileName = databaseName + ".json";
        File file = new File(databaseName);
        try {
            // Nếu tập tin chưa tồn tại, tạo một tập tin mới
            file.createNewFile();
            System.out.println("Created new database file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lấy bảng
    public Table getTableByDatabase(String databaseName, String tableName) {
        // TODO
        return database.getTable(tableName);
    }

    // Thêm bảng
    public void addTableByDatabase(String databaseName, String tableName) {
        // TODO
        Database database = new Database(databaseName);
        database.createTable(tableName);
        this.save(this.database);
    }

    // Save
    public void save(Database database) {
        System.out.println(database.getDatabaseName());

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

            System.out.println("Data exported to JSON file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
