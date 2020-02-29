package org.szh.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * excel导入，导出工具
 * 
 * @author Terry Zi
 *
 */
public class ExcelUtils {

	/**
	 * 判断excel文件后缀名，生成不同的workbook
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:40:35
	 *
	 * @param is
	 * @param excelFileName
	 * @return
	 * @throws IOException
	 */
	public static Workbook createWorkbook(InputStream is, String excelFileName) throws IOException {
		if (excelFileName.endsWith(".xls")) {
			return new HSSFWorkbook(is);
		} else if (excelFileName.endsWith(".xlsx")) {
			return new XSSFWorkbook(is);
		}
		return null;
	}

	/**
	 * 根据sheet索引号获取对应的sheet
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:40:45
	 *
	 * @param workbook
	 * @param sheetIndex
	 * @return
	 */
	public static Sheet getSheet(Workbook workbook, int sheetIndex) {
		return workbook.getSheetAt(0);
	}

	/**
	 * 将sheet中的数据保存到list中 1、调用此方法时，vo的属性个数必须和excel文件每行数据的列数相同且一一对应，vo的所有属性都为String
	 * 2、在action调用此方法时，需声明 private File excelFile;上传的文件 private String
	 * excelFileName;原始文件的文件名 3、页面的file控件name需对应File的文件名
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:40:52
	 *
	 * @param vo
	 * @param is
	 * @param excelFileName
	 * @return
	 */
	public static List<Object> importDataFromExcel(Object vo, InputStream is, String excelFileName) {
		List<Object> list = new ArrayList<Object>();
		try {
			// 创建工作簿
			Workbook workbook = ExcelUtils.createWorkbook(is, excelFileName);
			// 创建工作表sheet
			Sheet sheet = ExcelUtils.getSheet(workbook, 0);
			// 获取sheet中数据的行数
			int rows = sheet.getPhysicalNumberOfRows();
			// 获取表头单元格个数
			int cells = sheet.getRow(0).getPhysicalNumberOfCells();
			// 利用反射，给JavaBean的属性进行赋值
			Field[] fields = vo.getClass().getDeclaredFields();
			for (int i = 1; i < rows; i++) {// 第一行为标题栏，从第二行开始取数据
				Row row = sheet.getRow(i);
				System.out.println("index=" + i);

				int index = 0;
				while (index < cells) {
					Cell cell = row.getCell(index);
					if (null == cell) {
						cell = row.createCell(index);
					}
					cell.setCellType(CellType.STRING);
					String value = null == cell.getStringCellValue() ? "" : cell.getStringCellValue();
					// System.out.println(value);
					Field field = fields[index];
					String fieldName = field.getName();
					String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Method setMethod = vo.getClass().getMethod(methodName, new Class[] { String.class });
					setMethod.invoke(vo, new Object[] { value });
					index++;
				}
				if (isHasValues(vo)) {// 判断对象属性是否有值
					list.add(vo);
					vo = vo.getClass().getConstructor(new Class[] {}).newInstance(new Object[] {});// 重新创建一个vo对象
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();// 关闭流
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;

	}

	/**
	 * 从excel导入数据
	 * 
	 * @author 00014092
	 * @date 2018年1月10日 下午2:13:41
	 * 
	 * @param vo
	 * @param workbook
	 * @return
	 */
	public static List<Object> importDataFromExcelWorkbook(Object vo, Workbook workbook) {
		List<Object> list = new ArrayList<Object>();
		try {
			// 创建工作簿
			// Workbook workbook = ExcelUtils.createWorkbook(is, excelFileName);
			// 创建工作表sheet
			Sheet sheet = ExcelUtils.getSheet(workbook, 0);
			// 获取sheet中数据的行数
			int rows = sheet.getPhysicalNumberOfRows();
			// 获取表头单元格个数
			int cells = sheet.getRow(0).getPhysicalNumberOfCells();
			// 利用反射，给JavaBean的属性进行赋值
			Field[] fields = vo.getClass().getDeclaredFields();
			for (int i = 1; i < rows; i++) {// 第一行为标题栏，从第二行开始取数据
				Row row = sheet.getRow(i);
				// System.out.println("index=" + i);
				int index = 0;
				while (index < cells) {
					Cell cell = row.getCell(index);
					if (cell == null) {
						cell = row.createCell(index);
					}
					cell.setCellType(CellType.STRING);
					String value = cell.getStringCellValue() == null ? "" : cell.getStringCellValue();
					Field field = fields[index];
					String fieldName = field.getName();
					// System.out.println(fieldName+":"+value);
					String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Method setMethod = vo.getClass().getMethod(methodName, new Class[] { String.class });
					setMethod.invoke(vo, new Object[] { value });
					index++;
				}
				if (isHasValues(vo)) {// 判断对象属性是否有值
					list.add(vo);
					vo = vo.getClass().getConstructor(new Class[] {}).newInstance(new Object[] {});// 重新创建一个vo对象
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// is.close();// 关闭流
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return list;

	}

	/**
	 * 判断一个对象所有属性是否有值，如果一个属性有值(分空)，则返回true
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:42:38
	 *
	 * @param object
	 * @return
	 */
	public static boolean isHasValues(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		boolean flag = false;
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			Method getMethod;
			try {
				getMethod = object.getClass().getMethod(methodName);
				Object obj = getMethod.invoke(object);
				if (null != obj && !"".equals(obj)) {
					flag = true;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return flag;

	}

	/**
	 * 导出excel
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:42:45
	 *
	 * @param list
	 * @param headers
	 * @param title
	 * @param os
	 */
	public static <T> void exportDataToExcel(List<T> list, String[] headers, String title, OutputStream os) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽15个字节
		sheet.setDefaultColumnWidth(15);
		// 生成一个样式
		HSSFCellStyle style = ExcelUtils.getCellStyle(workbook);
		// 创建一个DataFormat对象
		HSSFDataFormat format = workbook.createDataFormat();
		// @指文本型
		style.setDataFormat(format.getFormat("@"));
		// 生成一个字体
		HSSFFont font = ExcelUtils.getFont(workbook);
		// 把字体应用到当前样式
		style.setFont(font);

		// 生成表格标题
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 300);
		HSSFCell cell = null;

		sheet.autoSizeColumn(1);

		for (int i = 0; i < headers.length; i++) {
			cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		// 将数据放入sheet中
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			T t = list.get(i);
			// 利用反射，根据JavaBean属性的先后顺序，动态调用get方法得到属性的值
			Field[] fields = t.getClass().getDeclaredFields();
			try {
				for (int j = 0; j < fields.length; j++) {
					cell = row.createCell(j);
					HSSFCellStyle newStyle = workbook.createCellStyle();
					Field field = fields[j];
					String fieldName = field.getName();
					String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					Method getMethod = t.getClass().getMethod(methodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					newStyle.setAlignment(HorizontalAlignment.CENTER);
					if (null == value)
						value = "";
					cell.setCellStyle(newStyle);
					cell.setCellValue(new HSSFRichTextString(value.toString()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			workbook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取单元格格式
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:46:42
	 *
	 * @param workbook
	 * @return
	 */
	public static HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor((short) 12);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}

	/**
	 * 生成字体样式
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:46:27
	 *
	 * @param workbook
	 * @return
	 */
	public static HSSFFont getFont(HSSFWorkbook workbook) {
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFFont.COLOR_NORMAL);
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		;
		return font;
	}

	/**
	 * 判断是否为ie浏览器
	 *
	 * @author 00014092
	 * @date 2017年10月23日 上午9:45:59
	 *
	 * @param request
	 * @return
	 */
	public boolean isIE(HttpServletRequest request) {
		return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 ? true : false;
	}

	public static void main(String[] args) throws IOException {
		// List<Book> list = new ArrayList<>();
		// String[] headers = { "id", "name", "other" };
		// OutputStream outputStream = new FileOutputStream(new
		// File("C:/Users/00014092/Desktop/book3.xls"));
		// exportDataToExcel(list, headers, "sheet1", outputStream);

		// Book book = new Book();
		// InputStream is = new FileInputStream(new
		// File("C:/Users/00014092/Desktop/book1.xls"));
		//
		// List<Object> bookList = importDataFromExcel(book, is, "book1.xls");
		// System.out.println(bookList);
	}
}
