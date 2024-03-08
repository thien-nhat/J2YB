package com.database.thiendb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.Service.DatabaseService;

@RestController
@RequestMapping(path = "/api")
public class DatabaseController {
    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Get all database

    // Get database

}
