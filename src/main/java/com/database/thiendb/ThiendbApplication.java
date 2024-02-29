package com.database.thiendb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.database.thiendb.Database.Database;
import com.database.thiendb.Table.Column;

@SpringBootApplication
public class ThiendbApplication {

	public static void main(String[] args) {
		// Import
		Database database = createDatabase();
        JsonDatabaseExporter.exportToJson(database);

		
		// SpringApplication.run(ThiendbApplication.class, args);
	}

	private static Database createDatabase() {
		// Code để tạo và thêm dữ liệu vào Database ở đây
		Database database = new Database("schoolManagement");
		System.out.println("hihi");

		// String[] columns = { "id", "name", "age" };

		// database.createTable("students", columns);
		database.createTable("students");
		Column id = new Column("id", "int", false, "");
		Column name = new Column("name", "string", false, "Anonymous");
		Column age = new Column("age", "int", false, "");
		database.getTable("students").addColumn(id);
		database.getTable("students").addColumn(name);
		database.getTable("students").addColumn(age);

		// Thêm một số hàng vào bảng
		Object[] row1 = { 1, "Nhatt", 25 };
		Object[] row2 = { 2, "Thienn", 22 };
		Object[] row3 = { 3, "Thienn", 22 };

		database.getTable("students").insertRow(row1);
		database.getTable("students").insertRow(row2);
		database.getTable("students").insertRow(row3);

		// System.out.println(database.getTable("students").toString());

		// database.getTable("students").deleteRow(0);
		System.out.println(database.getTable("students").toString());
		return database;
	}
}
