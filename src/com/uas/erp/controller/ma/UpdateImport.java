package com.uas.erp.controller.ma;

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
import com.uas.erp.model.UpdateSchemeDetail;
import com.uas.erp.service.common.impl.CheckUtil;
import com.uas.erp.service.ma.UpdateSchemeService;

public class UpdateImport extends HxlsAbstract {


	private List<UpdateSchemeDetail> updates;
	private List<UpdateSchemeDetail> columns;
	private List<String> data;
	private UpdateSchemeService service;
	private Integer id;
	private int count = 0;
	private static StringBuffer sb;
	private Integer ulid;



	public UpdateImport(InputStream is, int id) throws IOException, SQLException {
		super(is);
		this.service = (UpdateSchemeService) ContextUtil.getBean("updateSchemeServiceImpl");
		this.updates = service.getIndexFields(id);
		this.updates.addAll(service.getUpdateDetails(id,"checked_=1 "));
		this.columns = new ArrayList<UpdateSchemeDetail>();
		this.data = new ArrayList<String>();
		this.id = id;
		this.process();
		this.afterProcess();
	}

	
	public GridPanel getPanel() {
		GridPanel panel = new GridPanel();
		panel.setDataCount(this.count);
		panel.setJsonList(this.data);
		return panel;
	}

	public List<UpdateSchemeDetail> getColumns() {
		return columns;
	}

	public void setColumns(List<UpdateSchemeDetail> columns) {
		this.columns = columns;
	}
	
	public List<UpdateSchemeDetail> getUpdates() {
		return updates;
	}


	public void setUpdates(List<UpdateSchemeDetail> updates) {
		this.updates = updates;
	}


	public UpdateSchemeService getService() {
		return service;
	}


	public void setService(UpdateSchemeService service) {
		this.service = service;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public static StringBuffer getSb() {
		return sb;
	}


	public static void setSb(StringBuffer sb) {
		UpdateImport.sb = sb;
	}


	public Integer getUlid() {
		return ulid;
	}


	public void setUlid(Integer ulid) {
		this.ulid = ulid;
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
					UpdateSchemeDetail detail = this.columns.get(i);
					sb.append(detail.getField_());
					sb.append(":\"");
					str = rowlist.get(i);
					if (str != null && !"\\".equals(str) && !"/".equals(str)) {
						if(CheckUtil.isNumberType(detail.getType_()) || CheckUtil.isDateType(detail.getType_()))// number型必须去掉空格
							str = str.trim();
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
						this.ulid = this.service.saveUpdateData(this.id, this.data, this.ulid);
						this.data = new ArrayList<String>();
					}
				}
			} else {// columns
				for (String cell : rowlist) {
					for (UpdateSchemeDetail detail : this.updates) {
						if (detail.getCaption_().equals(cell)) {
							this.columns.add(detail);
						}
					}
				}
			}
		}
	}

	public void afterProcess() throws SQLException {
		if (this.data.size() > 0) {
			this.ulid = this.service.saveUpdateData(this.id, this.data, this.ulid);
		} else {
			if (this.ulid == null) {
				this.ulid = this.service.saveUpdateData(this.id, this.data, this.ulid);
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
			Pattern p = Pattern.compile("\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

}