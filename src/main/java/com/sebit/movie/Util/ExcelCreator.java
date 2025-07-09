package com.sebit.movie.Util;

import com.sebit.movie.Entity.Movie;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelCreator {
    public static byte[] generateExcelReport(List<Movie> report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("movies");
            createHeaderRow(workbook, sheet);
            createDataRows(workbook, sheet, report);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private static void createHeaderRow(Workbook workbook, Sheet sheet) {
        String[] headers = {
                "Title", "Year", "Genre", "IMDB Rating"
        };

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.PLUM.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 22 * 256);
        }
    }

    private static void createDataRows(Workbook workbook, Sheet sheet, List<Movie> reports) {
        CellStyle oddStyle = createCellStyle(workbook, false, false);
        CellStyle evenStyle = createCellStyle(workbook, true, false);
        CellStyle oddDateStyle = createCellStyle(workbook, false, true);
        CellStyle evenDateStyle = createCellStyle(workbook, true, true);

        int rowIndex = 1;
        for (Movie m : reports) {
            Row excelRow = sheet.createRow(rowIndex);
            boolean isEven = rowIndex % 2 == 0;

            int colIndex = 0;

            Object[] values = {
                    m.getTitle(),
                    m.getYear(),
                    m.getGenre(),
                    m.getImdbRating()
            };

            for (Object value : values) {
                Cell cell = excelRow.createCell(colIndex++);
               if (value != "N/A") {
                    cell.setCellValue(value.toString());
                    cell.setCellStyle(isEven ? evenStyle : oddStyle);
                } else {
                    cell.setCellValue("0");
                    cell.setCellStyle(isEven ? evenStyle : oddStyle);
                }
            }
            rowIndex++;
        }
    }


    private static CellStyle createCellStyle(Workbook workbook, boolean isEven, boolean isDate) {
        CellStyle style = workbook.createCellStyle();

        if (isEven) {
            style.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        } else {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        }

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        if (isDate) {
            style.setDataFormat(workbook.createDataFormat().getFormat("m/d/yyyy h:mm:ss AM/PM"));
        }

        return style;
    }
}
