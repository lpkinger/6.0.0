package com.uas.erp.core;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.examples.html.HSSFHtmlHelper;
import org.apache.poi.ss.examples.html.HtmlHelper;
import org.apache.poi.ss.examples.html.XSSFHtmlHelper;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;

public class ExcelToHtml {

	//以下为org.apache.poi官网的例子，具体原理就是解析xlsx文件中的内容和格式，转换成html形式的内容和样式，写到流中，最后输出
	public static class ToHtmlchange {
		// private final Workbook wb;
		private Workbook wb;
		// private final Appendable output;
		private Appendable output;
		private boolean completeHTML;
		private Formatter out;
		private boolean gotBounds;
		private int firstColumn;
		private int endColumn;
		private HtmlHelper helper;

		private static final String DEFAULTS_CLASS = "excelDefaults";
		private static final String COL_HEAD_CLASS = "colHeader";
		private static final String ROW_HEAD_CLASS = "rowHeader";

		public ToHtmlchange() {

		}

		public ToHtmlchange create(Workbook wb, Appendable output) {
			return new ToHtmlchange(wb, output);
		}

		public ToHtmlchange create(String path, Appendable output)
				throws Exception {
			return create(new FileInputStream(path), output);
		}

		public ToHtmlchange create(InputStream in, Appendable output)
				throws IOException {
			try {
				Workbook wb = WorkbookFactory.create(in);
				return create(wb, output);
			} catch (InvalidFormatException e) {
				throw new IllegalArgumentException(
						"Cannot create workbook from stream", e);
			}
		}

		private ToHtmlchange(Workbook wb, Appendable output) {
			if (wb == null)
				throw new NullPointerException("wb");
			if (output == null)
				throw new NullPointerException("output");
			this.wb = wb;
			this.output = output;
			setupColorMap();
		}

		private void setupColorMap() {
			if (wb instanceof HSSFWorkbook)
				helper = new HSSFHtmlHelper((HSSFWorkbook) wb);
			else if (wb instanceof XSSFWorkbook)
				helper = new XSSFHtmlHelper((XSSFWorkbook) wb);
			else
				throw new IllegalArgumentException("unknown workbook type: "
						+ wb.getClass().getSimpleName());
		}

		public void setCompleteHTML(boolean completeHTML) {
			this.completeHTML = completeHTML;
		}

		public void printPage() throws IOException {
			try {
				ensureOut();
				if (completeHTML) {
					out.format("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>%n");
					out.format("<html>%n");
					out.format("<head>%n");
					out.format("</head>%n");
					out.format("<body>%n");
					out.format("<script type=\"text/javascript\">function show(n){var oDiv=document.getElementsByTagName('div');for(var i=0;i<oDiv.length;i++){if(i==n){oDiv[i].style.display='block';}else{oDiv[i].style.display='none';}}}window.onload=function(){var divs=document.getElementsByTagName('div');for(var i=0;i<divs.length;i++){if(i!=0){divs[i].style.display='none';}}}</script>");

				}

				print();

				if (completeHTML) {
					out.format("</body>%n");
					out.format("</html>%n");
				}
			} finally {
				if (out != null)
					out.close();
				if (output instanceof Closeable) {
					Closeable closeable = (Closeable) output;
					closeable.close();
				}
			}
		}

		public void print() {
			printInlineStyle();
			printSheets();
		}

		private void printInlineStyle() {
			out.format("<style type=\"text/css\">%n");
			printStyles();
			out.format("</style>%n");
		}

		private void ensureOut() {
			if (out == null)
				out = new Formatter(output);
		}

		public void printStyles() {
			ensureOut();

			// First, copy the base css
			BufferedReader in = null;
			try {
				  
				//获取excelStyle.css的路径
				  String sysPath = PathUtil.getSysPath();  
				  InputStream ins = new FileInputStream(sysPath + "resource" + File.separator + "css" + File.separator + "excelStyle.css");
				  
				//in = new BufferedReader(new InputStreamReader(getClass()
				//		.getResourceAsStream(clsPathRep)));
				  
				in = new BufferedReader(new InputStreamReader(ins));
				  
				String line;
				while ((line = in.readLine()) != null) {
					out.format("%s%n", line);
				}
			} catch (IOException e) {
				throw new IllegalStateException("Reading standard css", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// noinspection ThrowFromFinallyBlock
						throw new IllegalStateException("Reading standard css",
								e);
					}
				}
			}

			// now add css for each used style
			Set<CellStyle> seen = new HashSet<CellStyle>();
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				Sheet sheet = wb.getSheetAt(i);
				Iterator<Row> rows = sheet.rowIterator();
				while (rows.hasNext()) {
					Row row = rows.next();
					for (Cell cell : row) {
						CellStyle style = cell.getCellStyle();
						if (!seen.contains(style)) {
							printStyle(style);
							seen.add(style);
						}
					}
				}
			}
		}

		private void printStyle(CellStyle style) {
			out.format(".%s .%s {%n", DEFAULTS_CLASS, styleName(style));
			styleContents(style);
			out.format("}%n");
		}

		private void styleContents(CellStyle style) {
			fontStyle(style);
			helper.colorStyles(style, out);
		}

		private void fontStyle(CellStyle style) {
			Font font = wb.getFontAt(style.getFontIndex());

			if (font.getBold())
				out.format("  font-weight: bold;%n");
			if (font.getItalic())
				out.format("  font-style: italic;%n");

			int fontheight = font.getFontHeightInPoints();
			if (fontheight == 9) {
				// fix for stupid ol Windows
				fontheight = 10;
			}
			out.format("  font-size: %dpt;%n", fontheight);

		}

		private String styleName(CellStyle style) {
			if (style == null)
				style = wb.getCellStyleAt((short) 0);
			StringBuilder sb = new StringBuilder();
			Formatter fmt = new Formatter(sb);
			try {
				fmt.format("style_%02x", style.getIndex());
				return fmt.toString();
			} finally {
				fmt.close();
			}
		}

		private void printSheets() {
			//out.format("<div>");

			
			
			int num = wb.getNumberOfSheets();
			for (int i = 0; i < num; i++) {
				ensureOut();
				Sheet sheet = wb.getSheetAt(i);

				int rowNum = sheet.getLastRowNum();

				out.format("<input type='button' value='Sheet"+(i+1)+"' style='height:20px' onclick='show("+i+")'></input>");
				
				out.format("<div>");
				
				// 如果该工作薄为空，则不显示
				if (rowNum != 0) {
					printSheet(sheet);
				}

				out.format("</div>");
				
				// 换行，使两个sheet表中有一定间隔
				//out.format("<br></br><br></br><br></br>");
			}

			
			//out.format("</div>");

		}

		public void printSheet(Sheet sheet) {
			ensureOut();
			out.format("<table class=%s>%n", DEFAULTS_CLASS);
			printCols(sheet);
			printSheetContent(sheet);
			out.format("</table>%n");
		}

		private void printCols(Sheet sheet) {
			out.format("<col/>%n");
			ensureColumnBounds(sheet);
			for (int i = firstColumn; i < endColumn; i++) {
				out.format("<col/>%n");
			}
		}

		private void ensureColumnBounds(Sheet sheet) {
			if (gotBounds)
				return;

			Iterator<Row> iter = sheet.rowIterator();
			firstColumn = (iter.hasNext() ? Integer.MAX_VALUE : 0);
			endColumn = 0;
			while (iter.hasNext()) {
				Row row = iter.next();
				short firstCell = row.getFirstCellNum();
				if (firstCell >= 0) {
					firstColumn = Math.min(firstColumn, firstCell);
					endColumn = Math.max(endColumn, row.getLastCellNum());
				}
			}
			gotBounds = true;
		}

		private void printColumnHeads() {
			out.format("<thead>%n");
			out.format("  <tr class=%s>%n", COL_HEAD_CLASS);
			out.format("    <th class=%s>&#x25CA;</th>%n", COL_HEAD_CLASS);
			StringBuilder colName = new StringBuilder();
			for (int i = firstColumn; i < endColumn; i++) {
				colName.setLength(0);
				int cnum = i;
				do {
					colName.insert(0, (char) ('A' + cnum % 26));
					cnum /= 26;
				} while (cnum > 0);
				out.format("    <th class=%s>%s</th>%n", COL_HEAD_CLASS,
						colName);
			}
			out.format("  </tr>%n");
			out.format("</thead>%n");
		}

		private void printSheetContent(Sheet sheet) {
			printColumnHeads();

			out.format("<tbody>%n");
			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();

				out.format("  <tr>%n");
				out.format("    <td class=%s>%d</td>%n", ROW_HEAD_CLASS,
						row.getRowNum() + 1);
				for (int i = firstColumn; i < endColumn; i++) {
					String content = "&nbsp;";
					String attrs = "";
					CellStyle style = null;
					if (i >= row.getFirstCellNum() && i < row.getLastCellNum()) {
						Cell cell = row.getCell(i);
						if (cell != null) {
							style = cell.getCellStyle();
							String dataFormat = style.getDataFormatString();
							if(dataFormat!=null){
								CellFormat cf = CellFormat.getInstance(style
										.getDataFormatString());
								CellFormatResult result = cf.apply(cell);
								content = result.text;
								if (content.equals(""))
									content = "&nbsp;";					
							}
						}
					}
					out.format("    <td class=%s %s>%s</td>%n",
							styleName(style), attrs, content);
				}
				out.format("  </tr>%n");
			}
			out.format("</tbody>%n");
		}

		public void start(String xlsxPath, String outHtml) {
			ToHtmlchange ToHtmlchange = null;
			try {
				ToHtmlchange = create(xlsxPath, new PrintWriter(new FileWriter(
						outHtml)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToHtmlchange.setCompleteHTML(true);
			try {
				ToHtmlchange.printPage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void start(InputStream in, String outHtml) {
			ToHtmlchange ToHtmlchange = null;
			try {
				ToHtmlchange = create(in, new PrintWriter(new FileWriter(
						outHtml)));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToHtmlchange.setCompleteHTML(true);
			try {
				ToHtmlchange.printPage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static String getHtml(String filepath, String fileName) throws Exception {
		
		InputStream input = null;
		String name = null;
		if (filepath.startsWith("https://dfs.ubtob.com")) {
			name = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.lastIndexOf(".") + 1);
		}else if(!(filepath.startsWith("http:") || filepath.startsWith("https:") || filepath.startsWith("B2B://"))){
			name = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.lastIndexOf(".") + 1);
		}
		
		if (name==null) {
			return null;
		}
		
		final String path = PathUtil.getTempPath() + File.separator;
		File newFile = new File(path, name + "html");
		if (newFile.exists())
			return "temp" + File.separator + newFile.getName();
		
		if (filepath.startsWith("https://dfs.ubtob.com")) {
			input = HttpUtil.download(filepath);
		}else if(!(filepath.startsWith("http:") || filepath.startsWith("https:") || filepath.startsWith("B2B://"))){
			//先检查文件存不存在,不存在则返回null
			try{
				File file = new File(filepath);
				input = new FileInputStream(file);
			}catch(FileNotFoundException e){
				throw new FileNotFoundException();
			}
		}
		
		if (filepath.endsWith(".xlsx") || (fileName!=null&&fileName.endsWith(".xlsx"))) {
			try {
				String outputPath = "temp" + File.separator + newFile.getName();

				newFile.getParentFile().mkdirs();
				newFile.createNewFile();

				String absolutePath = newFile.getAbsolutePath();
				ToHtmlchange thc = new ToHtmlchange();
				thc.start(input, absolutePath);
				return outputPath;
			} finally {
			}
		} else {
			try {

				HSSFWorkbook excelBook = new HSSFWorkbook(input);
				ArrayList<String> sheet = new ArrayList<String>();
				int sheetCount = excelBook.getNumberOfSheets();
				for (int i = 0; i < sheetCount; i++) {
					sheet.add(excelBook.getSheetName(i));
				}
				ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(
						DocumentBuilderFactory.newInstance()
								.newDocumentBuilder().newDocument());
				excelToHtmlConverter.setOutputColumnHeaders(false);
				excelToHtmlConverter.setOutputRowNumbers(false);// 去掉序列号
				excelToHtmlConverter.processWorkbook(excelBook);
				@SuppressWarnings("rawtypes")
				List pics = excelBook.getAllPictures();
				if (pics != null) {
					for (int i = 0; i < pics.size(); i++) {
						Picture pic = (Picture) pics.get(i);
						try {
							pic.writeImageContent(new FileOutputStream(path
									+ pic.suggestFullFileName()));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				Document htmlDocument = excelToHtmlConverter.getDocument();
				excelToHtmlConverter.setOutputColumnHeaders(false);
				excelToHtmlConverter.setOutputRowNumbers(false);
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				DOMSource domSource = new DOMSource(htmlDocument);
				StreamResult streamResult = new StreamResult(outStream);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer serializer = tf.newTransformer();
				serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				serializer.setOutputProperty(OutputKeys.METHOD, "html");
				serializer.transform(domSource, streamResult);
				outStream.close();
				String content = new String(outStream.toByteArray());
				StringBuffer content1 = new StringBuffer(content);
				String s1 = "<style type=\"text/css\">";
				content1.insert(content1.indexOf(s1) + s1.length(),
						"table tr,td{border:1px solid black;}");
				String s2 = "</head>";
				String add2 = "<script type=\"text/javascript\">function show(n){var oDiv=document.getElementsByTagName('div');for(var i=0;i<oDiv.length;i++){if(i==n){oDiv[i].style.display='block';}else{oDiv[i].style.display='none';}}}window.onload=function(){var oTable=document.getElementsByTagName('table');for(var i=0;i<oTable.length;i++){var oCol=oTable[i].children[0].children;var width=0;for(var j=0;j<oCol.length;j++){width= Number(width)+ Number(oCol[j].width);}oTable[i].style.width=width;}}</script>";
				content1.insert(content1.indexOf(s2), add2);
				String s3 = "<body class=\"b1\">";
				StringBuffer button = new StringBuffer();
				for (int i = 0; i < sheetCount; i++) {
					String str = "<input type='button' value='" + sheet.get(i)
							+ "' onclick='show(" + i + ")'/>";
					button.append(str);
				}
				String add3 = button.toString();
				content1.insert(content1.indexOf(s3) + s3.length(), add3);
				for (int i = 0; i < sheetCount; i++) {// 将每个sheet放入一个div中
					String s = "<h2>" + sheet.get(i) + "</h2>";
					String r = "";
					if (i == 0) {
						r = "<div id='div" + i + "' style='display:block;'>";
					} else {
						r = "</div><div id='div" + i
								+ "' style='display:none;'>";
					}
					content1.replace(content1.indexOf(s), content1.indexOf(s)+ s.length(), r);
				}
				String sl = "</body>";
				String al = "</div>";
				content1.insert(content1.indexOf(sl), al);
				FileUtils.write(newFile, content1, "utf-8");
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new Exception();
			}
			return "temp" + File.separator + newFile.getName();
		}
	}

}
