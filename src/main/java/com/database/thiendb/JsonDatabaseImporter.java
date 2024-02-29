package com.database.thiendb;

import com.database.thiendb.Database.Database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class JsonDatabaseImporter {
    public static Database importFromJson(String fileName) {
        // Tạo đối tượng Gson
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
            return null;
        }
    }
}

