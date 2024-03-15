package com.database.thiendb.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.database.thiendb.DataStructure.Row;
import com.database.thiendb.DataStructure.Table;
import com.database.thiendb.Service.DatabaseService;
import com.database.thiendb.Service.QueryService;
import com.database.thiendb.Service.RowService;
import com.database.thiendb.Service.TableService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;

@RestController
@RequestMapping(path = "/api")
public class QueryController {
    private final DatabaseService databaseService;
    private final TableService tableService;
    private final RowService rowService;
    private final QueryService queryService;

    public QueryController(DatabaseService databaseService, TableService tableService, RowService rowService,
            QueryService queryService) {
        this.databaseService = databaseService;
        this.tableService = tableService;
        this.rowService = rowService;
        this.queryService = queryService;
    }

    @PostMapping("/parse-sql")
    public ResponseEntity<Object> parseDatabaseSQL(@RequestBody String query) {
        if (query.trim().startsWith("CREATE DATABASE")) {
            // Extract the database name from the SQL statement
            String NewDatabaseName = query.substring("CREATE DATABASE".length()).trim().replaceAll(";", "");
            // Call the function to create the database
            System.out.println(NewDatabaseName);
            this.databaseService.addDatabase(NewDatabaseName);
        }
        return ResponseEntity.ok(query);
    }

    @PostMapping("/parse-sql/{databaseName}")
    public ResponseEntity<Object> parseSQL(@RequestBody String query,
            @PathVariable("databaseName") String databaseName) {
        try {
            // Parse câu truy vấn SQL
            Statement statement = CCJSqlParserUtil.parse(query);
            if (statement instanceof CreateTable) {
                // executeCreateTable((CreateTable) statement);
                CreateTable createTableStatement = (CreateTable) statement;
                System.out.println(createTableStatement);
                String tableName = createTableStatement.getTable().getName();
                System.out.println("Creating table: " + tableName);
                List<ColumnDefinition> columnDefinitions = createTableStatement.getColumnDefinitions();

                this.tableService.addTable(databaseName, tableName, columnDefinitions);

            }
            if (statement instanceof CreateIndex) {
                CreateIndex createIndex = (CreateIndex) statement;
                Index index = createIndex.getIndex();
                String indexName = createIndex.getIndex().getName();
                String tableName = createIndex.getTable().getName();
                String columnName = index.getColumnsNames().get(0);
                System.out.println("Index Name: " + indexName);

                tableService.addIndexedColumn(databaseName, tableName, columnName);
            }
            if (statement instanceof Select) {
                Table tableData = queryService.handleSelectStatement(statement, databaseName);
                return ResponseEntity.ok().body(tableData.getRows());
            }
            if (statement instanceof Insert) {
                this.queryService.handleInsertStatement(statement, databaseName);
                return ResponseEntity.ok(query);
            }
            if (statement instanceof Update) {
                Update updateStatement = (Update) statement;

                // Get a list of updated columns and their corresponding values
                List<Column> columns = updateStatement.getColumns();
                List<Expression> expressions = updateStatement.getExpressions();

                // Create an updated list (columns and corresponding values)
                HashMap<String, Expression> updates = new HashMap<>();

                for (int i = 0; i < columns.size(); i++) {
                    Column column = columns.get(i);
                    Expression expression = expressions.get(i);
                    updates.put(column.getColumnName(), expression);
                }
                this.queryService.handleUpdateStatement(databaseName, updateStatement, updates);
                return ResponseEntity.ok(query);
            }

            if (statement instanceof Delete) {
                Delete deleteStatement = (Delete) statement;
                String tableName = deleteStatement.getTable().getName();
                this.tableService.deleteTable(databaseName, tableName);
                return ResponseEntity.ok(query);
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(query);

    }
}
