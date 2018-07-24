package com.uas.erp.core;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.uas.erp.model.Employee;

public class PdfUtil extends AbstractPdfView {

	private String title;
	private Map<String, String> headers;
	private Map<String, Integer> widths;
	private Map<String, String> types;
	private List<Map<Object, Object>> datas;
	private Employee employee;
	private static int pageSize = 20;

	public PdfUtil() {

	}

	private static String getTitle(String title) {
		try {
			return new String(title.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return title;
		}
	}
	
	public PdfUtil(List<Map<Object, Object>> columns, List<Map<Object, Object>> datas, String title, Employee employee) {
		this();
		headers = new LinkedHashMap<String, String>();
		widths = new HashMap<String, Integer>();
		types = new HashMap<String, String>();
		this.employee = employee;
		this.title = title;
		this.datas = datas;
		Object cm = null;
		for (Map<Object, Object> m : columns) {
			cm = m.get("dataIndex");
			if (cm != null) {
				headers.put(cm.toString(), m.get("text").toString());
				widths.put(cm.toString(), Integer.parseInt(String.valueOf(m.get("width"))));
				if ("numbercolumn".equals(String.valueOf(m.get("xtype")))) {
					String f = String.valueOf(m.get("format"));
					if (f.contains("0.")) {
						types.put(cm.toString(), f.substring(f.indexOf("0.")));
					} else {
						types.put(cm.toString(), "0");
					}
				} else {
					types.put(cm.toString(), "");
				}
			}
		}
	}

	private static void setCreatorInfo(Document document, String title, String author, String subject, String keywords,
			String creator) {
		if (document != null) {
			document.addTitle(title);
			document.addAuthor(author);
			document.addSubject(subject);
			document.addKeywords(keywords);// 文档关键字信息
			document.addCreator(creator);// 应用程序名称
		}
	}

	/**
	 * 中文字符集
	 * 
	 * @return
	 */
	private static Font getChineseFont() {
		BaseFont bfChinese;
		try {
			bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			return new Font(bfChinese, 8, Font.NORMAL);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void createDocument(Document document, PdfWriter writer) {
		try {
			if (headers != null) {
				document.setPageSize(PageSize.A2);
				document.newPage();
				writer.setPageEvent(new HeadFootInfoPdfPageEvent());
				setCreatorInfo(document, getTitle(title), employee.getEm_name(), getTitle(title), getTitle(title), "www.usoftchina.com");
				Font fontChinese = getChineseFont();
				PdfPTable table = new PdfPTable(headers.size());
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
				Set<String> keys = headers.keySet();
				PdfPCell headerCell = new PdfPCell();
				headerCell.setBackgroundColor(new Color(213, 141, 69));
				for (String key : keys) {
					headerCell.setPhrase(new Paragraph(headers.get(key), fontChinese));
					table.addCell(headerCell);
				}
				if (datas != null && datas.size() > 0) {
					PdfPCell contentCell = new PdfPCell();
					contentCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					contentCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
					int i = 0;
					int len = datas.size();
					for (Map<Object, Object> d : datas) {
						if (++i % pageSize == 0 || i == len) {
							document.add(table);
							table.deleteBodyRows();
						}
						for (String key : keys) {
							String v = String.valueOf(d.get(key));
							if (types.get(key).length() != 0) {
								if (d.get(key) != null) {
									if ("".equals(v) || "null".equals(v)) {
										v = "0";
									}
								} else {
									v = "0";
								}
							}
							contentCell.setPhrase(new Paragraph(v, fontChinese));
							table.addCell(contentCell);
						}
					}
					if(table.getRows() != null && !table.getRows().isEmpty()){
						document.add(table);
						table.deleteBodyRows();
					}
				}
			}
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void buildPdfDocument(Map<String, Object> arg0, Document arg1, PdfWriter arg2, HttpServletRequest arg3,
			HttpServletResponse arg4) throws Exception {
		createDocument(arg1, arg2);
		String filename = title + ".pdf";
		arg4.setContentType("APPLICATION/OCTET-STREAM");
		arg4.setHeader("Content-disposition", "attachment;filename=" + filename);
	}

	class HeadFootInfoPdfPageEvent extends PdfPageEventHelper {

		public HeadFootInfoPdfPageEvent() {

		}

		public void onEndPage(PdfWriter writer, Document document) {
			try {
				PdfContentByte headAndFootPdfContent = writer.getDirectContent();
				headAndFootPdfContent.saveState();
				headAndFootPdfContent.beginText();
				BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				headAndFootPdfContent.setFontAndSize(bfChinese, 10);
				// 文档页头信息设置
				float x = document.top(-20);
				// 页头信息左面
				headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, getTitle(title), document.left(), x, 0);
				// 页头信息中间
				headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_CENTER, "第" + writer.getPageNumber() + "页",
						(document.right() + document.left()) / 2, x, 0);
				// 页头信息右面
				headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_RIGHT, "www.usoftchina.com",
						document.right(), x, 0);
				// 文档页脚信息设置
				float y = document.bottom(-20);
				// 页脚信息左面
				headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_LEFT, "--", document.left(), y, 0);
				// 页脚信息中间
				headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_CENTER, "-",
						(document.right() + document.left()) / 2, y, 0);
				// 页脚信息右面
				headAndFootPdfContent.showTextAligned(PdfContentByte.ALIGN_RIGHT, "--", document.right(), y, 0);
				headAndFootPdfContent.endText();
				headAndFootPdfContent.restoreState();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
