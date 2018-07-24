package com.uas.erp.controller.common;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.HxlsAbstract;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.InitDetail;
import com.uas.erp.service.common.InitService;
import com.uas.erp.service.common.impl.CheckUtil;

/**
 * 初始化导入excel， 支持大数量excel, 支持wps、MS excel03-07
 */
public class InitImport extends HxlsAbstract {

	private List<InitDetail> inits;
	private List<InitDetail> columns;
	private List<String> data;
	private InitService service;
	private String caller;
	private int count = 0;
	private static StringBuffer sb;
	private Integer ilid;

	public List<InitDetail> getInits() {
		return inits;
	}

	public void setInits(List<InitDetail> inits) {
		this.inits = inits;
	}

	public InitImport(InputStream is, String caller) throws IOException, SQLException {
		super(is);
		this.service = (InitService) ContextUtil.getBean("initService");
		this.inits = service.getInitDetails(caller);
		this.columns = new ArrayList<InitDetail>();
		this.data = new ArrayList<String>();
		this.caller = caller;
		this.process();
		this.afterProcess();
	}

	public InitService getService() {
		return service;
	}

	public void setService(InitService service) {
		this.service = service;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public Integer getIlid() {
		return ilid;
	}

	public void setIlid(Integer ilid) {
		this.ilid = ilid;
	}

	public GridPanel getPanel() {
		GridPanel panel = new GridPanel();
		panel.setDataCount(this.count);
		panel.setJsonList(this.data);
		return panel;
	}

	public List<InitDetail> getColumns() {
		return columns;
	}

	public void setColumns(List<InitDetail> columns) {
		this.columns = columns;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void optRows(int sheetIndex, int curRow, List<String> rowlist) throws SQLException {
		int len = rowlist.size();
		if(len > 0) {
			if (curRow > 0) {// data
				sb = new StringBuffer(1000);
				sb.append("{");
				boolean valid = false;
				String str = null;
				for (int i = 0; i < len; i++) {
					InitDetail detail = this.columns.get(i);
					sb.append(detail.getId_field());
					sb.append(":\"");
					str = rowlist.get(i);
					if (str != null && !" ".equals(str) && !"\\".equals(str) && !"/".equals(str)) {
						if(CheckUtil.isNumberType(detail.getId_type()) || CheckUtil.isDateType(detail.getId_type())||CheckUtil.isVarcharType(detail.getId_type()))// number和字符型必须去掉空格
							str = str.trim();
						if(detail.getId_logic()!=null && detail.getId_logic().indexOf(CheckUtil.UPPER)>0)
							str=str.toUpperCase();
						sb.append(str.replace("\"", "\\\""));
						if(str.trim().length() > 0)
							valid = true;
					}
					sb.append("\",");
				}
				if(valid) {
					sb.append("}");
					this.data.add(replaceBlank(sb.toString()));
					this.count++;
					if (count % 5000 == 0) {// 每隔5000条保存一次
						this.ilid = this.service.saveInitData(this.caller, this.data, this.ilid);
						this.data = new ArrayList<String>();
					}
				}
			} else {// columns
				for (String cell : rowlist) {
					for (InitDetail detail : this.inits) {
						if (detail.getId_caption().equals(cell) || (detail.getId_caption()+"(必填)").equals(cell)) {
							this.columns.add(detail);
						}
					}
				}
			}
		}
	}

	public void afterProcess() throws SQLException {
		if (this.data.size() > 0) {
			this.ilid = this.service.saveInitData(this.caller, this.data, this.ilid);
		} else {
			if (this.ilid == null) {
				this.ilid = this.service.saveInitData(this.caller, this.data, this.ilid);
			}
		}
	}
	
	/**
	 * 去除特殊符号 \n 回车 \t 水平制表符  \r 换行
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = str;
		if (str != null) {
			Pattern p = Pattern.compile("\t|\r|\n|'");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

}
