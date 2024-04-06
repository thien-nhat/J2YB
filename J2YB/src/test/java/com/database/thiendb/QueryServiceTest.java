package com.database.thiendb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.database.thiendb.DataStructure.Database;
import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Repository.DatabaseRepository;
import com.database.thiendb.Service.QueryService;
import com.database.thiendb.Service.RowService;
import com.database.thiendb.Service.TableService;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

import static org.junit.jupiter.api.Assertions.*;

class QueryServiceTest {
    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private RowService rowService;

    @Test
    void testFindRowByCondition() {
        // Create a sample table
        Table table = new Table();
        table.addColumn("id", "int");
        table.addColumn("name", "varchar(11)");
        table.addColumn("age", "int");

        assertEquals("id", table.getColumns().get(0).getName());
        assertEquals("int", table.getColumns().get(0).getDataType());

        // Add some rows to the table
        Object[] values1 = new Object[] { 1, "John", 25 };
        table.addRow(new Row(values1));
        Object[] values2 = new Object[] { 2, "Jane", 30 };
        table.addRow(new Row(values2));
        Object[] values3 = new Object[] { 3, "Bob", 35 };
        table.addRow(new Row(values3));

        // Create an instance of QueryService
        Database database = new Database("testDatabase");
        databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable

        tableService = new TableService(databaseRepository);
        rowService = new RowService(databaseRepository);

        QueryService queryService = new QueryService(databaseRepository, tableService, rowService);

        // Test finding a row by condition
        Row foundRow = queryService.findRowByCondition(table, "name", "Jane");
        assertNotNull(foundRow);
        assertEquals(2, foundRow.getValues()[0]); // Check the value of the "id" column
        assertEquals("Jane", foundRow.getValues()[1]); // Check the value of the "name" column
        assertEquals(30, foundRow.getValues()[2]); // Check the value of the "age" column

        // Test finding a row that doesn't exist
        Row notFoundRow = queryService.findRowByCondition(table, "name", "Alice");
        assertNull(notFoundRow);
    }

    @Test
    void testHandleAlterStatement() {
        // Create a sample database
        try {

            Database database = new Database("testDatabase");
            databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
            databaseRepository.save(database);

            // Create a sample table
            Table table = new Table();
            table.addColumn("id", "int");
            table.addColumn("name", "varchar(11)");
            table.addColumn("age", "int");
            table.addColumn("person_id", "int");
            database.addTable("testTable", table);
            databaseRepository.save(database);

            // Create an instance of QueryService
            databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
            tableService = new TableService(databaseRepository);
            QueryService queryService = new QueryService(databaseRepository, tableService, rowService);

            // Test handling drop column
            Statement dropColumnStatement;
            dropColumnStatement = CCJSqlParserUtil.parse("ALTER TABLE testTable DROP COLUMN age");

            Table updatedTable1 = queryService.handleAlterStatement(dropColumnStatement, "testDatabase");
            assertNull(updatedTable1.getColumnByName("age"));

            // Test handling add column
            Statement addColumnStatement = CCJSqlParserUtil.parse("ALTER TABLE testTable ADD COLUMN email varchar(50)");
            Table updatedTable2 = queryService.handleAlterStatement(addColumnStatement, "testDatabase");
            assertNotNull(updatedTable2.getColumnByName("email"));
            assertEquals("varchar (50)", updatedTable2.getColumnByName("email").getDataType());

            // Test handling add foreign key
            Statement addForeignKeyStatement = CCJSqlParserUtil.parse(
                    "ALTER TABLE testTable ADD CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES person(id)");
            // Must be get datatype from person table
            Table updatedTable3 = queryService.handleAlterStatement(addForeignKeyStatement, "testDatabase");
            assertNotNull(updatedTable3.getColumnByName("person_id").isForeignKey());
            // assertEquals("person",
            // updatedTable3.getForeignKey("person_id").getReferencedTable());
            // assertEquals("id",
            // updatedTable3.getForeignKey("person_id").getReferencedColumn());
        } catch (JSQLParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    void testHandleCreateTable() {
        // Create a sample database
        Database database = new Database("testDatabase");
        databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
        databaseRepository.save(database);

        // Create a sample create table statement
        String createTableSql = "CREATE TABLE testTable (id INT, name VARCHAR(50), age INT, PRIMARY KEY (id))";
        Statement createTableStatement;
        try {
            createTableStatement = CCJSqlParserUtil.parse(createTableSql);
        } catch (JSQLParserException e) {
            fail("Failed to parse create table statement");
            return;
        }
        tableService = new TableService(databaseRepository);
        QueryService queryService = new QueryService(databaseRepository, tableService, rowService);
        // Call the handleCreateTable method
        Table createdTable = queryService.handleCreateTable(createTableStatement, "testDatabase");

        // Table table = database.getTable("testTable");
        assertEquals("id", createdTable.getColumns().get(0).getName());
        assertEquals("INT", createdTable.getColumns().get(0).getDataType());
        assertEquals("name", createdTable.getColumns().get(1).getName());
        assertEquals("VARCHAR (50)", createdTable.getColumns().get(1).getDataType());
        assertEquals("age", createdTable.getColumns().get(2).getName());
        assertEquals("INT", createdTable.getColumns().get(2).getDataType());
        assertEquals("id", createdTable.getPrimaryKeyColumn().getName());

        // Verify that the table is saved in the database repository
        Database savedDatabase = databaseRepository.findDatabaseByName("testDatabase");
        assertNotNull(savedDatabase);
        assertNotNull(savedDatabase.getTable("testTable"));
        assertEquals(createdTable.toString(), savedDatabase.getTable("testTable").toString());
    }

    @Test
    void testHandleSelectStatement() {
        // Create a sample database
        Database database = new Database("testDatabase");
        databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
        databaseRepository.save(database);

        // Create a sample table
        Table table = new Table();
        table.addColumn("id", "int");
        table.addColumn("name", "varchar(11)");
        table.addColumn("age", "int");
        database.addTable("testTable", table);
        databaseRepository.save(database);

        // Create an instance of QueryService
        databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
        tableService = new TableService(databaseRepository);
        QueryService queryService = new QueryService(databaseRepository, tableService, rowService);

        // Create a sample SELECT statement
        String selectSql = "SELECT * FROM testTable";
        Statement selectStatement;
        try {
            selectStatement = CCJSqlParserUtil.parse(selectSql);
        } catch (JSQLParserException e) {
            fail("Failed to parse SELECT statement");
            return;
        }

        // Call the handleSelectStatement method
        Table resultTable = queryService.handleSelectStatement(selectStatement, "testDatabase");

        // Verify the result
        assertNotNull(resultTable);
        assertEquals(table.toString(), resultTable.toString());
    }

    @Test
    void testHandleInsertStatement() {
        // Create a sample database
        Database database = new Database("testDatabase");
        databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
        databaseRepository.save(database);

        // Create a sample table
        Table table = new Table();
        table.addColumn("id", "int");
        table.addColumn("name", "varchar(11)");
        table.addColumn("age", "int");
        database.addTable("testTable", table);
        databaseRepository.save(database);

        // Create an instance of QueryService
        databaseRepository = new DatabaseRepository(database); // Initialize the databaseRepository variable
        tableService = new TableService(databaseRepository);
        rowService = new RowService(databaseRepository);

        QueryService queryService = new QueryService(databaseRepository, tableService, rowService);

        // Create a sample INSERT statement
        String insertSql = "INSERT INTO testTable (id, name, age) VALUES (1, 'John', 25)";
        Statement insertStatement;
        try {
            insertStatement = CCJSqlParserUtil.parse(insertSql);
        } catch (JSQLParserException e) {
            fail("Failed to parse INSERT statement");
            return;
        }

        // Call the handleInsertStatement method
        queryService.handleInsertStatement(insertStatement, "testDatabase");
        
        // Verify the inserted row
        Database savedDatabase = databaseRepository.findDatabaseByName("testDatabase");
        Table resultTable = savedDatabase.getTable("testTable");
        assertNotNull(resultTable);
        assertEquals(1, resultTable.getRows().size());

        Row insertedRow = resultTable.getRows().get(0);
        assertNotNull(insertedRow);
        assertEquals(1.0, insertedRow.getValues()[0]);
        assertEquals("John", insertedRow.getValues()[1]);
        assertEquals(25.0, insertedRow.getValues()[2]);

    }
}