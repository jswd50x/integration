package com.kang.common.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoiExcelUtils {
    public static final String KEY_HEAD = "head";
    public static final String KEY_BODY = "body";

    public static byte[] genWorkbookBytes(WorkBookData workBookData)
            throws IOException {
        Workbook workbook = genWorkbook(workBookData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }

    public static Workbook genWorkbook(WorkBookData workBookData) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(workBookData.sheetName);
        mergedRegion(sheet, workBookData);
        buildExcel(workBookData, workbook, sheet);
        return workbook;
    }

    private static void mergedRegion(HSSFSheet sheet, WorkBookData workBookData) {
        List<List<Integer>> mergedRegions = workBookData.getMergedRegions();
        if ((mergedRegions != null) && (mergedRegions.size() > 0)) {
            for (List<Integer> mergedRegion : mergedRegions) {
                sheet.addMergedRegion(new CellRangeAddress(((Integer) mergedRegion.get(0)).intValue(), ((Integer) mergedRegion.get(1)).intValue(), ((Integer) mergedRegion.get(2)).intValue(), ((Integer) mergedRegion.get(3)).intValue()));
            }
        }
    }

    public static Map<String, List<List<String>>> readExcel(byte[] workbookBytes, int startHeadRowIndex, int headRows, int startBodyRowIndex, int bodyRows)
            throws IOException {
        Map<String, List<List<String>>> workbookData = new HashMap();

        ByteArrayInputStream bais = new ByteArrayInputStream(workbookBytes);
        HSSFWorkbook wb = new HSSFWorkbook(bais);
        HSSFSheet sheet = wb.getSheetAt(0);

        List<List<String>> head = new ArrayList();
        List<List<String>> body = new ArrayList();
        if (headRows > 0) {
            head = readHead(sheet, startHeadRowIndex, headRows);
        }
        if (bodyRows > 0) {
            body = readBody(sheet, startBodyRowIndex, bodyRows);
        }
        workbookData.put("head", head);
        workbookData.put("body", body);

        return workbookData;
    }

    public static Map<String, List<List<String>>> readExcelAll(byte[] workbookBytes, int startHeadRowIndex, int headRows, int startBodyRowIndex)
            throws IOException {
        Map<String, List<List<String>>> workbookData = new HashMap();

        ByteArrayInputStream bais = new ByteArrayInputStream(workbookBytes);
        HSSFWorkbook wb = new HSSFWorkbook(bais);
        HSSFSheet sheet = wb.getSheetAt(0);

        List<List<String>> head = new ArrayList();
        List<List<String>> body = new ArrayList();
        if (headRows > 0) {
            head = readHead(sheet, startHeadRowIndex, headRows);
        }
        int bodyRows = sheet.getLastRowNum() - startBodyRowIndex;
        if (bodyRows > 0) {
            body = readBody(sheet, startBodyRowIndex, bodyRows);
        }
        workbookData.put("head", head);
        workbookData.put("body", body);

        return workbookData;
    }

    private static List<List<String>> readHead(HSSFSheet sheet, int startHeadRowIndex, int headRows) {
        List<List<String>> head = new ArrayList(headRows);
        for (int i = 0; i < headRows; i++) {
            HSSFRow row = sheet.getRow(startHeadRowIndex + i);
            if (row == null) {
                break;
            }
            List<String> rowData = readRow(row);
            head.add(rowData);
        }
        return head;
    }

    private static List<List<String>> readBody(HSSFSheet sheet, int startBodyRowIndex, int bodyRows) {
        List<List<String>> body = new ArrayList();
        for (int i = 0; i < bodyRows; i++) {
            HSSFRow row = sheet.getRow(startBodyRowIndex + i);
            if (row == null) {
                break;
            }
            List<String> rowData = readRow(row);
            body.add(rowData);
        }
        return body;
    }

    private static List<String> readRow(HSSFRow row) {
        List<String> rowData = new ArrayList();
        int lastCellNum = row.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            HSSFCell cell = row.getCell(i);
            if (cell == null) {
                rowData.add("");
            } else {
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        rowData.add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        rowData.add(numTypeChange(cell));
                        break;
                    case BOOLEAN:
                        rowData.add(String.valueOf(cell.getBooleanCellValue()));
                        break;
                    case _NONE:
                    default:
                        rowData.add("");
                }
            }
        }
        return rowData;
    }

    private static String numTypeChange(HSSFCell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue().toString();
        }
        String cellValue = "";
        short dataFormatIndex = cell.getCellStyle().getDataFormat();
        double d = cell.getNumericCellValue();
        switch (dataFormatIndex) {
            case 0:
            case 1:
                cellValue = new DecimalFormat("0").format(d);
                break;
            case 2:
                cellValue = new DecimalFormat("0.00").format(d);
                break;
            case 3:
                cellValue = new DecimalFormat("#,###").format(d);
                break;
            case 4:
                cellValue = new DecimalFormat("#,##0.00").format(d);
                break;
            default:
                cellValue = String.valueOf(d);
        }
        return cellValue;
    }

    private static void buildExcel(WorkBookData workBookData, HSSFWorkbook workbook, HSSFSheet sheet) {
        int rowIdx = 0;

        rowIdx = buildHead(workbook, sheet, rowIdx, workBookData.headHead, workBookData.headData);

        buildBody(workbook, sheet, rowIdx, workBookData.bodyHead, workBookData.bodyData);
        for (int i = 0; i < workBookData.bodyHead.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static int buildHead(HSSFWorkbook workbook, HSSFSheet sheet, int rowIdx, List<String> headHead, List<String> headData) {
        HSSFCellStyle headStyle = createHeadHeadStyle(workbook);
        HSSFCellStyle dataStyle = createHeadDataStyle(workbook);

        int returnRowIdx = buildRow(sheet, headStyle, rowIdx, headHead);
        returnRowIdx = buildRow(sheet, dataStyle, returnRowIdx, headData);
        return returnRowIdx;
    }

    private static int buildBody(HSSFWorkbook workbook, HSSFSheet sheet, int rowIdx, List<String> bodyHead, List<List<String>> bodyData) {
        HSSFCellStyle headStyle = createBodyHeadStyle(workbook);
        HSSFCellStyle dataStyle = createBodyDataStyle(workbook);

        int returnRowIdx = buildRow(sheet, headStyle, rowIdx, bodyHead);
        for (List<String> rowData : bodyData) {
            returnRowIdx = buildRow(sheet, dataStyle, returnRowIdx, rowData);
        }
        return returnRowIdx;
    }

    private static int buildRow(HSSFSheet sheet, HSSFCellStyle style, int rowIdx, List<String> list) {
        if (list == null) {
            return rowIdx;
        }
        HSSFRow row = sheet.createRow(rowIdx);
        int colIdx = 0;
        for (String s : list) {
            HSSFCell cell = row.createCell(colIdx++);
            cell.setCellValue(s);
            if (style != null) {
                cell.setCellStyle(style);
            }
        }
        rowIdx++;
        return rowIdx;
    }

    private static HSSFCellStyle createHeadHeadStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.WHITE.index);
        style.setFillForegroundColor(IndexedColors.WHITE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.index);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.index);

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setFontName("����");
        font.setBold(true);

        style.setFont(font);
        return style;
    }

    private static HSSFCellStyle createHeadDataStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.WHITE.index);
        style.setFillForegroundColor(IndexedColors.WHITE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.index);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.index);

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("����");
        font.setBold(true);

        style.setFont(font);
        return style;
    }

    private static HSSFCellStyle createBodyHeadStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.index);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.index);

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("����");
        font.setBold(true);

        style.setFont(font);
        return style;
    }

    private static HSSFCellStyle createBodyDataStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.index);
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.index);

        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("����");

        style.setFont(font);
        return style;
    }

    public static class WorkBookData {
        private String sheetName;
        private List<String> headHead;
        private List<String> headData;
        private List<String> bodyHead;
        private List<List<String>> bodyData;
        private List<List<Integer>> mergedRegions;

        public List<List<Integer>> getMergedRegions() {
            return this.mergedRegions;
        }

        public void setMergedRegions(List<List<Integer>> mergedRegions) {
            this.mergedRegions = mergedRegions;
        }

        public String getSheetName() {
            return this.sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public List<String> getHeadHead() {
            return this.headHead;
        }

        public void setHeadHead(List<String> headHead) {
            this.headHead = headHead;
        }

        public List<String> getHeadData() {
            return this.headData;
        }

        public void setHeadData(List<String> headData) {
            this.headData = headData;
        }

        public List<String> getBodyHead() {
            return this.bodyHead;
        }

        public void setBodyHead(List<String> bodyHead) {
            this.bodyHead = bodyHead;
        }

        public List<List<String>> getBodyData() {
            return this.bodyData;
        }

        public void setBodyData(List<List<String>> bodyData) {
            this.bodyData = bodyData;
        }
    }
}
