package com.database.thiendb.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

@RestController
@RequestMapping(path = "/api")
public class QueryController {
    
    @PostMapping("/parse-sql")
    public String parseSQL(@RequestBody String query) {
        try {
            // Parse câu truy vấn SQL
            Statement statement = CCJSqlParserUtil.parse(query);

            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
                
                // Kiểm tra các cột được chọn
                List<SelectItem> selectItems = plainSelect.getSelectItems();
                if (selectItems.size() == 1 && selectItems.get(0).toString().equals("*")) {
                    // Chọn tất cả các cột

                    System.out.println("Executing getTable()");
                } else {
                    // Trường hợp chọn các cột cụ thể
                    System.out.println("Executing getRow()");
                }
            } 
            if (statement instanceof Insert) {
                System.out.println("Executing addRow()");
            }  
            if (statement instanceof Update) {
                System.out.println("Executing updateRow()");
            }

            if (statement instanceof Delete) {
                System.out.println("Executing deleteRow()");
            }

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return query;
        
    }
}
