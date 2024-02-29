package com.database.thiendb;

import com.database.thiendb.Database.Database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;

public class JsonDatabaseExporter {
    public static void exportToJson(Database database) {
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
