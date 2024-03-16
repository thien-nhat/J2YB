package com.database.thiendb.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Exception.ObjectNotFoundException;
import com.database.thiendb.Repository.DatabaseRepository;

@Service
public class RowService {
    @Autowired
    private DatabaseRepository databaseRepository;

    public RowService(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    // Lấy hàng
    public Row getRow(String databaseName, String tableName, Integer rowId) {
        Database database = databaseRepository.findDatabaseByName(databaseName);
        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                return table.getRow(rowId);
            } else {
                throw new ObjectNotFoundException(
                        "Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            throw new ObjectNotFoundException("Database '" + databaseName + "' not found.");
        }
    }

    // Thêm hàng
    public void addRow(String databaseName, String tableName, Row row) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.addRow(row);
            } else {
                throw new ObjectNotFoundException(
                        "Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            throw new ObjectNotFoundException("Database '" + databaseName + "' not found.");
        }

        this.databaseRepository.save(database);
    }

    // Sửa hàng
    public void updateRow(String databaseName, String tableName, Integer rowId, Row row) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.updateRow(--rowId, row);
            } else {
                throw new ObjectNotFoundException(
                        "Table '" + tableName + "' not found in database '" + databaseName + "'.");
            }
        } else {
            throw new ObjectNotFoundException("Database '" + databaseName + "' not found.");

        }

        this.databaseRepository.save(database);
    }

    // Xóa hàng
    public void deleteRow(String databaseName, String tableName, Integer rowId) {
        // TODO
        Database database = databaseRepository.findDatabaseByName(databaseName);

        if (database != null) {
            Table table = database.getTable(tableName);
            if (table != null) {
                table.deleteRow(--rowId);
            } else {
                throw new ObjectNotFoundException(
                        "Table '" + tableName + "' not found in database '" + databaseName + "'.");

            }
        } else {
            throw new ObjectNotFoundException("Database '" + databaseName + "' not found.");

        }

        this.databaseRepository.save(database);
    }

}
