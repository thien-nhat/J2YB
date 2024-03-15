package com.database.thiendb;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.database.thiendb.Repository.DatabaseRepository;
import com.database.thiendb.Service.TableService;

@SpringBootApplication
public class ThiendbApplication {

	public static void main(String[] args) {	
		DatabaseRepository DatabaseRepository = new DatabaseRepository();

		TableService tableService = new TableService(DatabaseRepository);

        // Test the addIndexedColumn method
        String databaseName = "schoolManagement"; 
        String tableName = "students"; 
        String columnName = "age"; 
		Object value = 23.0; // Chưa fix lỗi integer và double chỗ này
        tableService.addIndexedColumn(databaseName, tableName, columnName);
		System.out.println(tableService.findIndexedColumnByName(databaseName, tableName, columnName, value));

		// Column column = tableService.
		SpringApplication.run(ThiendbApplication.class, args);
	}
}
