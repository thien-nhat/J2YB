package com.database.thiendb.Database;

import java.util.HashMap;

import com.database.thiendb.Table.Column;
import com.database.thiendb.Table.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;

public class Database {
    private String databaseName;
    private HashMap<String, Table> tables;

    public Database(String databaseName) {
        this.databaseName = databaseName;
        this.tables = new HashMap<>();
        Database existingDatabase = createOrLoadDatabaseFromFile(databaseName + ".json");
        if (existingDatabase != null) {
            this.tables = existingDatabase.tables;
        }
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

    // public void createTable(String tableName, Column column) {
    //     Table table = new Table();
    //     table.addColumn(column);
    //     tables.put(tableName, table);
    // }
    public void createTable(String tableName) {
        Table table = new Table();
        tables.put(tableName, table);
    }

    public void dropTable(String tableName) {
        tables.remove(tableName);
    }

    private Database createOrLoadDatabaseFromFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            System.out.println(fileName);
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
        } else {
            try {
                // Nếu tập tin chưa tồn tại, tạo một tập tin mới
                file.createNewFile();
                System.out.println("Created new database file: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
