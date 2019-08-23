/**
 * BEYONDSOFT.COM INC
 */
package com.kang.common.util;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuwentao
 * @version $Id: ExcelUtil.java, v 0.1 2017年4月13日 上午10:30:37 xuwentao Exp $
 */
public class ExcelUtil<T> {

    public static final int EXCEL_LINE = 65536;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    Class<T> clazz;

    public ExcelUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings({"deprecation", "static-access"})
    public List<T> importExcel(String sheetName, InputStream input, int k)
            throws Exception {
        List<T> list = new ArrayList<T>();
        try {
            //此方法能解决office高版本兼容问题
            Workbook workbook = WorkbookFactory.create(input);
            Sheet sheet = workbook.getSheet(sheetName);
            if (!("").equals(sheetName.trim())) {
                // 如果指定sheet名,则取指定sheet中的内容.
                sheet = workbook.getSheet(sheetName);
            }
            if (sheet == null) {
                // 如果传入的sheet名不存在则默认指向第1个sheet.
                sheet = workbook.getSheetAt(0);
            }
            int rows = sheet.getLastRowNum() + 1;

            // 有数据时才处理
            if (rows > 0) {
                // 得到类的所有field.
                Field[] allFields = clazz.getDeclaredFields();
                // 定义一个map用于存放列的序号和field.
                Map<Integer, Field> fieldsMap = new HashMap<Integer, Field>();
                for (Field field : allFields) {
                    // 将有注解的field存放到map中.  
                    if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                        ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                        // 获得列号
                        int col = getExcelCol(attr.column());
                        // 设置类的私有字段属性可访问.
                        field.setAccessible(true);
                        fieldsMap.put(col, field);
                    }
                }
                // 从第k+1行开始取数据, // 默认第一行是表头.
                for (int i = k; i < rows; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }

                    int cellNum = row.getLastCellNum();
                    T entity = null;
                    // ??????????????要=吗
                    for (int j = 0; j < cellNum; j++) {
                        Cell cell = row.getCell(j);
                        if (cell == null) {
                            continue;
                        }
                        if (row.getCell(j) != null) {
                            row.getCell(j).setCellType(Cell.CELL_TYPE_STRING);
                        }

                        String c = "";
                        if (cell != null || !("").equals(cell)) {
                            c = cell.getStringCellValue();
                        }
                        // 如果不存在实例则新建.
                        entity = (entity == null ? clazz.newInstance() : entity);
                        // 从map中得到对应列的field.
                        Field field = fieldsMap.get(j);
                        // 取得类型,并根据对象类型设置值.  
                        Class<?> fieldType = field.getType();
                        if (String.class == fieldType) {
                            field.set(entity, String.valueOf(c));
                        } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                            field.set(entity, Integer.parseInt(c));
                        } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                            field.set(entity, Long.valueOf(c));
                        } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                            field.set(entity, Float.valueOf(c));
                        } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                            field.set(entity, Short.valueOf(c));
                        } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                            field.set(entity, Double.valueOf(c));
                        } else if (Character.TYPE == fieldType) {
                            if ((c != null) && (c.length() > 0)) {
                                field.set(entity, Character.valueOf(c.charAt(0)));
                            }
                        }

                    }
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("excel导入失败", e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return list;
    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param sheetName 工作表的名称
     * @param sheetSize 每个sheet中数据的行数,此数值必须小于65536
     * @param output    java输出流
     */
    @SuppressWarnings({"resource", "deprecation"})
    public boolean exportExcel(List<T> list, String sheetName, int sheetSize, OutputStream output) {
        // 得到所有定义字段
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<Field>();
        // 得到所有field并存放到一个list中.  
        for (Field field : allFields) {
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                fields.add(field);
            }
        }
        // 产生工作薄对象
        XSSFWorkbook workbook = new XSSFWorkbook();

        // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.  
        if (sheetSize > EXCEL_LINE || sheetSize < 1) {
            sheetSize = EXCEL_LINE;
        }
        // 取出一共有多少个sheet.
        double sheetNo = Math.ceil(list.size() / sheetSize);
        for (int index = 0; index <= sheetNo; index++) {
            // 产生工作表对象
            XSSFSheet sheet = workbook.createSheet();
            if (sheetNo == 0) {
                workbook.setSheetName(index, sheetName);
            } else {
                // 设置工作表的名称.
                workbook.setSheetName(index, sheetName + index);
            }
            XSSFRow row;
            XSSFCell cell;// 产生单元格  

            // 产生一行
            row = sheet.createRow(0);
            // 写入各个字段的列头名称  
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                // 获得列号
                int col = getExcelCol(attr.column());
                // 创建列
                cell = row.createCell(col);
                // 设置列中写入内容为String类型
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                // 写入列名
                cell.setCellValue(attr.name());

                // 如果设置了提示信息则鼠标放上去提示.  
                if (!("").equals(attr.prompt().trim())) {
                    // 这里默认设了2-101列提示.
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);
                }
                // 如果设置了combo属性则本列只能选择不能输入  
                if (attr.combo().length > 0) {
                    // 这里默认设了2-101列只能选择不能输入.
                    setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);
                }
            }

            int startNo = index * sheetSize;
            int endNo = Math.min(startNo + sheetSize, list.size());
            // 写入各条记录,每条记录对应excel表中的一行  
            for (int i = startNo; i < endNo; i++) {
                row = sheet.createRow(i + 1 - startNo);
                // 得到导出对象.
                T vo = (T) list.get(i);
                for (int j = 0; j < fields.size(); j++) {
                    // 获得field.
                    Field field = fields.get(j);
                    // 设置实体类私有属性可访问
                    field.setAccessible(true);
                    ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);

                    // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                    try {
                        if (attr.isExport()) {
                            // 创建cell
                            cell = row.createCell(getExcelCol(attr.column()));
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            cell.setCellValue(
                                    // 如果数据存在就填入,不存在填入空格.
                                    field.get(vo) == null ? "" : String.valueOf(field.get(vo)));
                        }
                    } catch (IllegalAccessException e) {
                        LOGGER.error("excel导出失败", e);
                    }

                }
            }
        }
        try {
            output.flush();
            workbook.write(output);
            return true;
        } catch (IOException e) {
            LOGGER.error("excel导出失败", e);
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LOGGER.error("excel导出失败", e);
                }
            }
        }

    }

    /**
     * 对list数据源将其里面的数据导入到excel表单
     *
     * @param sheetName 工作表的名称
     * @param sheetSize 每个sheet中数据的行数,此数值必须小于65536
     * @param
     */
    @SuppressWarnings({"resource", "deprecation"})
    public InputStream exportExcelOut(List<T> list, String sheetName, int sheetSize) {
        // 得到所有定义字段
        Field[] allFields = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<Field>();
        // 得到所有field并存放到一个list中.
        for (Field field : allFields) {
            if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
                fields.add(field);
            }
        }
        // 产生工作薄对象
        XSSFWorkbook workbook = new XSSFWorkbook();

        // excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
        if (sheetSize > EXCEL_LINE || sheetSize < 1) {
            sheetSize = EXCEL_LINE;
        }
        // 取出一共有多少个sheet.
        double sheetNo = Math.ceil(list.size() / sheetSize);
        for (int index = 0; index <= sheetNo; index++) {
            // 产生工作表对象
            XSSFSheet sheet = workbook.createSheet();
            if (sheetNo == 0) {
                workbook.setSheetName(index, sheetName);
            } else {
                // 设置工作表的名称.
                workbook.setSheetName(index, sheetName + index);
            }
            XSSFRow row;
            XSSFCell cell;// 产生单元格

            // 产生一行
            row = sheet.createRow(0);
            // 写入各个字段的列头名称
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                // 获得列号
                int col = getExcelCol(attr.column());
                // 创建列
                cell = row.createCell(col);
                // 设置列中写入内容为String类型
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                // 写入列名
                cell.setCellValue(attr.name());

                // 如果设置了提示信息则鼠标放上去提示.
                if (!("").equals(attr.prompt().trim())) {
                    // 这里默认设了2-101列提示.
                    setHSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);
                }
                // 如果设置了combo属性则本列只能选择不能输入
                if (attr.combo().length > 0) {
                    // 这里默认设了2-101列只能选择不能输入.
                    setHSSFValidation(sheet, attr.combo(), 1, 100, col, col);
                }
            }

            int startNo = index * sheetSize;
            int endNo = Math.min(startNo + sheetSize, list.size());
            // 写入各条记录,每条记录对应excel表中的一行
            for (int i = startNo; i < endNo; i++) {
                row = sheet.createRow(i + 1 - startNo);
                // 得到导出对象.
                T vo = (T) list.get(i);
                for (int j = 0; j < fields.size(); j++) {
                    // 获得field.
                    Field field = fields.get(j);
                    // 设置实体类私有属性可访问
                    field.setAccessible(true);
                    ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
                    try {
                        // 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
                        if (attr.isExport()) {
                            // 创建cell
                            cell = row.createCell(getExcelCol(attr.column()));
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            cell.setCellValue(
                                    // 如果数据存在就填入,不存在填入空格.
                                    field.get(vo) == null ? "" : String.valueOf(field.get(vo)));
                        }
                    } catch (IllegalArgumentException e) {
                        LOGGER.error("excel导出失败", e);
                    } catch (IllegalAccessException e) {
                        LOGGER.error("excel导出失败", e);
                    }
                }
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        return is;
    }

    /**
     * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
     *
     * @param col
     */
    public static int getExcelCol(String col) {
        col = col.toUpperCase();
        // 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。  
        int count = -1;
        char[] cs = col.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
        }
        return count;
    }

    /**
     * 设置单元格上提示
     *
     * @param sheet         要设置的sheet.
     * @param promptTitle   标题
     * @param promptContent 内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     * @return 设置好的sheet.
     */
    public static XSSFSheet setHSSFPrompt(XSSFSheet sheet, String promptTitle, String promptContent,
                                          int firstRow, int endRow, int firstCol, int endCol) {
        // 构造constraint对象  
        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("DD1");
        // 四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象  
        HSSFDataValidation dataValidationView = new HSSFDataValidation(regions, constraint);
        dataValidationView.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(dataValidationView);
        return sheet;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    public static XSSFSheet setHSSFValidation(XSSFSheet sheet, String[] textlist, int firstRow,
                                              int endRow, int firstCol, int endCol) {
        // 加载下拉列表内容  
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象  
        HSSFDataValidation dataValidationList = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(dataValidationList);
        return sheet;
    }
}
