package com.gopinathrm.sql.excelmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MergeExcelOnHeaders {

    public static void main(String[] args) {
        try {
            // get input excel files
            FileInputStream excellFile1 = new FileInputStream(new File(
                    "D:\\inputExcel1.xls"));
            FileInputStream excellFile2 = new FileInputStream(new File(
                    "D:\\inputExcel2.xls"));

            // Create Workbook instance holding reference to .xlsx file
            HSSFWorkbook workbook1 = new HSSFWorkbook(excellFile1);
            HSSFWorkbook workbook2 = new HSSFWorkbook(excellFile2);

            // Get first/desired sheet from the workbook
            HSSFSheet mainSheet = workbook1.getSheetAt(0);
            HSSFSheet sheet2 = workbook2.getSheetAt(0);

            // add sheet2 to mainSheet
            addSheet(mainSheet, sheet2, mapHeaders(sheet2, mainSheet));
            excellFile1.close();
            excellFile2.close();
            
            // save merged file
            File mergedFile = new File("D:\\merged.xlsx");
            if (!mergedFile.exists()) {
                mergedFile.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(mergedFile);
            workbook1.write(out);
            out.close();
            System.out.println("Files were merged succussfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addSheet(HSSFSheet mainSheet, HSSFSheet sheet, HashMap<Integer, Integer> map) {
        
        //get column number
        Set<Integer> colNumbs = map.keySet();
        // map for cell styles
        Map<Integer, HSSFCellStyle> styleMap = new HashMap<Integer, HSSFCellStyle>();

        // This parameter is for appending sheet rows to mergedSheet in the end
        int len = mainSheet.getLastRowNum();
        for (int j = sheet.getFirstRowNum() + 1 ; j <= sheet.getLastRowNum(); j++) {

            HSSFRow row = sheet.getRow(j);
            
            // Create row in main sheet
            HSSFRow mrow = mainSheet.createRow(len + j);

            for (Integer k : colNumbs) {
                HSSFCell cell = row.getCell(k.intValue());
                
                // Create column in main sheet
                HSSFCell mcell = mrow.createCell(map.get(k).intValue());

               /* if (cell.getSheet().getWorkbook() == mcell.getSheet()
                        .getWorkbook()) {
                    mcell.setCellStyle(cell.getCellStyle());
                } else {
                    int stHashCode = cell.getCellStyle().hashCode();
                    HSSFCellStyle newCellStyle = styleMap.get(stHashCode);
                    if (newCellStyle == null) {
                        newCellStyle = mcell.getSheet().getWorkbook()
                                .createCellStyle();
                        newCellStyle.cloneStyleFrom(cell.getCellStyle());
                        styleMap.put(stHashCode, newCellStyle);
                    }
                    mcell.setCellStyle(newCellStyle);
               }*/

                // set value based on cell type
                switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_FORMULA:
                    mcell.setCellFormula(cell.getCellFormula());
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    mcell.setCellValue(cell.getNumericCellValue());
                    break;
                case HSSFCell.CELL_TYPE_STRING:
                    mcell.setCellValue(cell.getStringCellValue());
                    break;
                case HSSFCell.CELL_TYPE_BLANK:
                    mcell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                    break;
                case HSSFCell.CELL_TYPE_BOOLEAN:
                    mcell.setCellValue(cell.getBooleanCellValue());
                    break;
                case HSSFCell.CELL_TYPE_ERROR:
                    mcell.setCellErrorValue(cell.getErrorCellValue());
                    break;
                default:
                    mcell.setCellValue(cell.getStringCellValue());
                    break;
                }
            }
        }
    }

    // results MAP of <secondary sheet, main sheet>
    public static HashMap<Integer, Integer> mapHeaders(HSSFSheet sheet1,
    		HSSFSheet sheet2) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        HSSFRow row1 = sheet1.getRow(0);
        HSSFRow row2 = sheet2.getRow(0);
        for (int i = row1.getFirstCellNum(); i < row1.getLastCellNum(); i++) {
            for (int j = row2.getFirstCellNum(); j < row2.getLastCellNum(); j++) {
                if (row1.getCell(i).getStringCellValue()
                        .equals(row2.getCell(j).getStringCellValue())) {
                    map.put(new Integer(i), new Integer(j));
                }
            }
        }
        return map;
    }
}
