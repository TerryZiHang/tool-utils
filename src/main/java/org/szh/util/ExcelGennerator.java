package org.szh.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.szh.annotation.Excel;
import org.szh.bean.TestExcel;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 自定义注解式excel生成类
 * 
 * @author Terry Zi
 *
 */
public class ExcelGennerator {

	private HSSFWorkbook wb;

	private HSSFFont font;

	private HSSFCellStyle style;

	public ExcelGennerator() {
		wb = new HSSFWorkbook();
		font = wb.createFont();
		// 字体大小
		font.setFontHeightInPoints((short) 12);
		font.setFontName("宋体");
		// 字体颜色
		font.setColor(HSSFFont.COLOR_NORMAL);
		// 粗体
		font.setBold(true);
		style = wb.createCellStyle();
		// 水平居中
		style.setAlignment(HorizontalAlignment.CENTER);
		// 垂直居中
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);
	}

	public void createSheet(List<?> list, String name, Class<?> clazz) {
		HSSFSheet sheet = wb.createSheet(name);
		List<ExcelSort> excelSort = init(clazz);
		title(excelSort, sheet);
		table(excelSort, list, sheet);
	}

	/**
	 * 创建标题和表头行
	 * 
	 * @param excelSort
	 */
	private void title(List<ExcelSort> excelSort, HSSFSheet sheet) {
		HSSFRow row = sheet.createRow(0);
		int width = 35;
		for (int i = 0, len = excelSort.size(); i < len; i++) {
			sheet.setColumnWidth(i, 255 * width + 184);
			cell(row.createCell(i), excelSort.get(i).excel.name());
		}
	}

	private void cell(HSSFCell cell, String text) {
		cell.setCellStyle(style);
		cell.setCellValue(text);
	}

	/**
	 * 创建表主内容
	 * 
	 * @param excelSort
	 */
	private void table(List<ExcelSort> excelSort, List<?> list, HSSFSheet sheet) {
		HSSFRow row;
		String[] text;
		for (int i = 0, len = list.size(); i < len; i++) {
			row = sheet.createRow(i + 1);
			text = value(excelSort, list.get(i));
			for (int j = 0; j < text.length; j++) {
				if (text[j] != null && text[j].indexOf("[") != -1) {
					createColDownList(text[j].substring(text[j].indexOf("[") + 1, text[j].lastIndexOf("]")), sheet, j);
					continue;
				}
				cell(row.createCell(j), text[j]);
			}
		}
	}

	private String[] value(List<ExcelSort> excel, Object object) {
		Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String str = gson.toJson(object);
		JSONObject json = JSONObject.parseObject(str);
		String[] text = new String[excel.size()];
		String fomat;
		// StringBuilder p = new StringBuilder();
		for (int i = 0, len = excel.size(); i < len; i++) {
			text[i] = json.getString(excel.get(i).field.getName());
			if (excel.get(i).excel.formatPrice()) {
				// 处理价格
				StringUtils.format(Integer.parseInt(text[i])/100.0, "#.##");
				continue;
			}
			if (excel.get(i).excel.format().equals("")) {
				continue;
			}
			fomat = ";" + excel.get(i).excel.format() + ";";
			if (fomat.indexOf(";" + text[i] + ":") < 0) {
				text[i] = "";
				continue;
			}
			fomat = fomat.substring(fomat.indexOf(";" + text[i] + ":") + 1);
			fomat = fomat.substring(0, fomat.indexOf(";"));
			text[i] = fomat.replace(text[i] + ":", "");
		}
		return text;
	}

	/**
	 * 排序
	 * 
	 * @param clazz
	 * @return
	 */
	private List<ExcelSort> init(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		List<ExcelSort> fieldList = new LinkedList<ExcelSort>();
		for (int i = 0; i < fields.length; i++) {
			Excel excel = fields[i].getAnnotation(Excel.class);
			if (excel == null || excel.skip() == true) {
				continue;
			}
			fieldList.add(new ExcelSort(excel, fields[i]));
		}
		Collections.sort(fieldList);
		return fieldList;
	}

	class ExcelSort implements Comparable<ExcelSort> {
		Excel excel;
		Field field;

		@Override
		public int compareTo(ExcelSort o) {
			return this.excel.sort() - o.excel.sort();
		}

		public ExcelSort(Excel excel, Field field) {
			this.excel = excel;
			this.field = field;
		}

	}

	/**
	 * 创建某一列下拉框
	 *
	 * @param string
	 * @date 2019年9月19日
	 */
	private void createColDownList(String text, HSSFSheet sheet, int col) {
		String[] data = text.split(",");
		/*
		 * System.out.println(Arrays.toString(data)); //生成一个工作簿对象 HSSFWorkbook workbook
		 * = new HSSFWorkbook(); //生成一个名称为Info的表单 HSSFSheet sheet =
		 * workbook.createSheet("Info"); 设置下拉列表作用的单元格(firstrow, lastrow, firstcol,
		 * lastcol)
		 */
		CellRangeAddressList regions = new CellRangeAddressList(1, 100, col, col);
		// 生成并设置数据有效性验证
		DVConstraint constraint = DVConstraint.createExplicitListConstraint(data);
		HSSFDataValidation dataValidationList = new HSSFDataValidation(regions, constraint);
		// 将有效性验证添加到表单
		sheet.addValidationData(dataValidationList);
	}

	/**
	 * 输出excel
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void outPutExcel(OutputStream out) throws IOException {
		wb.write(out);
	}

	public static void main(String[] args) {
		 List<TestExcel> list = new LinkedList<>();
		 TestExcel test = new TestExcel();
		 test.setName("小石");
		 List<String> ids = new LinkedList<>();
		 ids.add("1234");
		 ids.add("5678");
		 test.setPersonIDs(ids);
		 list.add(test);
		 ExcelGennerator gennerator = new ExcelGennerator();
		 gennerator.createSheet(list, "测试", TestExcel.class);
	}
}
