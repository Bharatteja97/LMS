package utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtility {

    private Workbook workbook;
    private Sheet sheet;

    public ExcelUtility(String excelPath, String sheetName) {
        try {
            FileInputStream fis = new FileInputStream(excelPath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read the Excel file");
        }
    }

    public String getCellData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            Cell cell = row.getCell(colNum);
            if (cell != null) {
                DataFormatter formatter = new DataFormatter();
                return formatter.formatCellValue(cell);
            }
        }
        return "";
    }

    public int getRowCount() {
        return sheet.getLastRowNum();
    }
    
    public int getColCount(int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            return row.getLastCellNum();
        }
        return 0;
    }
}
