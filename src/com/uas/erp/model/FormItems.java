package com.uas.erp.model;

import java.io.Serializable;
import java.util.List;

import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

/**
 * formpanel的items对象
 */
public class FormItems implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fieldLabel;
	private String name;
	private String id;
	private String html;
	private int group;
	private String groupName;
	private String table;
	private String xtype = "textfield";
	private boolean readOnly = false;
	private String dataIndex;
	private int maxLength = 2000;
	private String maxLengthText = "字数太长了哟";
	private boolean hideTrigger = false;
	private boolean editable = true;
	private float columnWidth = 1;
	private Object value = "";
	private boolean allowBlank;
	private String cls = "form-field-allowBlank";
	private String fieldStyle;
	private String queryMode;
	private String displayField;
	private String valueField;
	private ComboStore store;
	private String labelAlign;
	private String firstname;// 第一个字段
	private String firstxtype;
	private String secondname;// 另外的一个字段
	private String logic;
	private String text;
	private String boxLabel;
	private String minValue;
	private String maxValue;
	private String inputValue;
	private boolean checked;
	private String fieldConfig;
	private String defaultValue;
	private String title;
	private String iniValue;
	// 针对dbfind设置正则校验
	private String maskRe;
	//非在录入状态下可修改
	private boolean modify;
	private boolean allowDecimals=true;
	private String renderfn;
	private String margin;//间距
	public String getMargin() {
		return margin;
	}

	public void setMargin(String margin) {
		this.margin = margin;
	}

	public String getIniValue() {
		return iniValue;
	}

	public void setIniValue(String iniValue) {
		this.iniValue = iniValue;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getXtype() {
		return xtype;
	}

	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public String getBoxLabel() {
		return boxLabel;
	}

	public void setBoxLabel(String boxLabel) {
		this.boxLabel = boxLabel;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFieldConfig() {
		return fieldConfig;
	}

	public void setFieldConfig(String fieldConfig) {
		this.fieldConfig = fieldConfig;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getMaxLengthText() {
		return maxLengthText;
	}

	public void setMaxLengthText(String maxLengthText) {
		this.maxLengthText = maxLengthText;
	}

	public boolean isHideTrigger() {
		return hideTrigger;
	}

	public void setHideTrigger(boolean hideTrigger) {
		this.hideTrigger = hideTrigger;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public float getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(float columnWidth) {
		this.columnWidth = columnWidth;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isAllowBlank() {
		return allowBlank;
	}

	public void setAllowBlank(boolean allowBlank) {
		this.allowBlank = allowBlank;
	}

	public String getQueryMode() {
		return queryMode;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getFieldStyle() {
		return fieldStyle;
	}

	public void setFieldStyle(String fieldStyle) {
		this.fieldStyle = fieldStyle;
	}

	public void setQueryMode(String queryMode) {
		this.queryMode = queryMode;
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public ComboStore getStore() {
		return store;
	}

	public void setStore(ComboStore store) {
		this.store = store;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFirstxtype() {
		return firstxtype;
	}

	public void setFirstxtype(String firstxtype) {
		this.firstxtype = firstxtype;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public String getLabelAlign() {
		return labelAlign;
	}

	public void setLabelAlign(String labelAlign) {
		this.labelAlign = labelAlign;
	}

	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getMaskRe() {
		return maskRe;
	}

	public void setMaskRe(String maskRe) {
		this.maskRe = maskRe;
	}     
	public boolean isAllowDecimals() {
		return allowDecimals;
	}
	public void setAllowDecimals(boolean allowDecimals) {
		this.allowDecimals = allowDecimals;
	}
	public FormItems() {

	}

	public FormItems(int groupId, String groupName, FormDetail formDetail, List<DataListCombo> combos) {
		String language = SystemSession.getLang();
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		this.logic = formDetail.getFd_logictype();
		this.group = groupId;
		this.margin = "3 0 3 0";
		this.groupName = groupName;
		this.columnWidth = (float) formDetail.getFd_columnwidth() / (float) 4.0;
		this.id = formDetail.getFd_field();
		if (id.contains(" ")) {// field有取别名
			String[] strs = id.split(" ");
			id = strs[strs.length - 1];
		}
		this.name = this.id;
		this.renderfn=formDetail.getFd_render();
		this.dataIndex = this.id;
		this.labelAlign = "left";
		this.table= formDetail.getFd_table();
		if ("zh_CN".equals(language)) {
			this.fieldLabel = formDetail.getFd_caption();
		} else if ("en_US".equals(language)) {
			this.fieldLabel = formDetail.getFd_captionen();
		} else if ("zh_TW".equals(language)) {
			this.fieldLabel = formDetail.getFd_captionfan();
		} else {
			this.fieldLabel = formDetail.getFd_caption();
		}
		String type = formDetail.getFd_type();

		String defaultvalue = type.equals("IG") ? "" : formDetail.getFd_defaultvalue();
		this.value = type.equals("IG") ? "" : ((defaultvalue == null || defaultvalue.equals("null")) ? "" : defaultvalue);
		this.readOnly = "T".equals(formDetail.getFd_readonly());
		this.allowBlank = "T".equals(formDetail.getFd_allowblank());
		this.modify = "T".equals(formDetail.getFd_modify());
		this.fieldStyle = this.allowBlank ? "background:#FFFAFA;color:#515151;" : "background:#E0E0FF;color:#515151;";
		/*
		 * if (StringUtils.hasText(this.fieldLabel) && !this.allowBlank) this.fieldLabel += "<span class='x-form-necessary'>&nbsp;&nbsp;&nbsp;&nbsp;</span>";
		 */
		if ("S".equals(type)) {
			this.xtype = "textfield";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("N")) {
			this.xtype = "numberfield";
			this.hideTrigger = true;
			if (formDetail.getFd_minvalue() != null)
				this.minValue = formDetail.getFd_minvalue();
			if (formDetail.getFd_maxvalue() != null)
				this.maxValue = formDetail.getFd_maxvalue();
		} else if(type.equals("IN")){
			this.xtype = "numberfield";
			this.hideTrigger = true;
			this.allowDecimals=false;
			if (formDetail.getFd_minvalue() != null)
				this.minValue = formDetail.getFd_minvalue();
		}else if (type.equals("SN")) {
			this.xtype = "separnumberfield";
			if (formDetail.getFd_minvalue() != null)
				this.minValue = formDetail.getFd_minvalue();
		} else if (type.equals("D")) {
			this.xtype = "datefield";
		} else if (type.equals("DT")) {
			this.xtype = "datetimefield";
			this.minValue = "0:00 AM";
			this.maxValue = "0:00 PM";
		} else if (type.equals("TF")) {
			this.xtype = "timefield";
		} else if (type.equals("T")) {
			this.xtype = "textareatrigger";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("H")) {
			this.xtype = "hidden";
			this.cls = "form-field-allowBlank-hidden";
		} else if (type.equals("Html")) {
			this.xtype = "htmleditor";
			this.labelAlign = "top";
		} else if (type.equals("CF")) {
			this.xtype = "colorfield";
		} else if (type.equals("CBG")) {
			this.xtype = "erpcheckboxgroup";
		} else if (type.equals("CBGS")) {
			this.xtype = "erpcheckboxgroupendwiths";
		} else if (type.equals("RG")) {
			this.xtype = "erpradiogroup";
		} else if (type.equals("SCF")) {
			this.xtype = "specialcontianfield";
			this.fieldStyle = null;
			this.labelAlign = "right";
			this.fieldLabel = null;
			this.cls = null;
		} else if (type.equals("CBC")) {
			this.labelAlign = "top";
			this.xtype = "erpcheckboxcontainer";
		} else if (type.equals("B")) {
			this.xtype = "checkbox";
			this.boxLabel = this.fieldLabel;
			this.fieldStyle = null;
			this.labelAlign = "right";
			this.fieldLabel = null;
			this.cls = null;
			if (defaultvalue != null && defaultvalue.equals("1")) {
				this.checked = true;
			}
			this.inputValue = "1";
			if(formDetail.getFd_logictype()!= null && formDetail.getFd_logictype().contains("session:")){				
				String str=baseDao.parseEmpCondition(formDetail.getFd_logictype());
				this.logic=str;
			}
	
		} else if (type.equals("CG")) {
			this.xtype = "checkgroup";
		} else if (type.equals("R")) {
			this.xtype = "radio";
			this.boxLabel = this.fieldLabel;
			this.fieldStyle = null;
			this.labelAlign = "right";
			this.fieldLabel = null;
			this.cls = null;
			this.id = null;
		} else if (type.equals("C")) {
			this.xtype = "combo";
			this.editable = false;
			this.queryMode = "local";
			this.displayField = "display";
			this.valueField = "value";
			this.store = new ComboStore(combos, this.dataIndex, language);
		} else if (type.equals("EC")) {// editable combobox
			this.xtype = "combo";
			this.editable = true;
			this.queryMode = "local";
			this.displayField = "display";
			this.valueField = "value";
			this.store = new ComboStore(combos, this.dataIndex, language);
		} else if (type.equals("CPCC")) {// country province city combobox
			this.xtype = "cascadingcityfield";
			this.secondname = formDetail.getFd_logictype();					
		}else if (type.equals("Bar")) {
			this.xtype = "progressbar";
			this.cls = "form-progressbar";
			this.text = formDetail.getFd_caption();
		} else if (type.equals("TA")) {
			this.xtype = "textareafield";
			this.labelAlign = "top";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("TAS")) {
			this.xtype = "erpTextAreaSelectField";
			this.labelAlign = "top";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("AC")) {
			this.xtype = "autocodetrigger";
			this.hideTrigger = false;
			this.editable = !this.readOnly;
			this.readOnly = false;
		} else if (type.equals("HF")) {
			this.xtype = "hreffield";
		} else if (type.equals("FILE")) {
			this.xtype = "filefield";
			this.name = "file";
			this.id = "file";
			this.hideTrigger = false;
		} else if (type.equals("MF")) {
			this.xtype = "mfilefield";
			this.title = this.fieldLabel;
			this.hideTrigger = false;
		} else if (type.equals("PF")) {
			this.xtype = "photofield";
			this.hideTrigger = false;
			this.cls = "";
		} else if (type.equals("DTF")) {
			this.xtype = "detailtextfield";
			this.hideTrigger = false;
		} else if (type.equals("CSF")) {
			this.xtype = "customerselectfield";
			this.hideTrigger = true;
		}else if (type.equals("BTF")) {
			this.xtype = "businesstripfield";
			/*this.hideTrigger = true;*/
		} else if (type.equals("FT")) {
			this.xtype = "erpFtField";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("DF")) {
			this.xtype = "displayfield";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("YN")) {
			this.xtype = "erpYnField";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		} else if (type.equals("CF")) {
			this.xtype = "colorfield";
		} else if (type.equals("CT")) {
			this.xtype = "colortypefield";
		} else if (type.equals("SF")) {
			this.xtype = "statusfield";
		} else if (type.equals("CD")) {
			this.xtype = "condatefield";
			/* 反馈编号:2018040511 */
			if(!(defaultvalue == null || defaultvalue.equals("null"))){
				switch(defaultvalue){
					case "今天": this.value = 1; break;
					case "昨天": this.value = 2; break;
					case "本月": this.value = 3; break;
					case "上个月": this.value = 4; break;
					case "本年度": this.value = 5; break;
					case "上年度": this.value = 6; break;
					case "近半年": this.value = 8; break;
					case "自定义": this.value = 7; break;
					default: 
						this.value = defaultvalue; break;
				}
			}
		} else if (type.equals("FTD")) {
			this.xtype = "ftdatefield";
		} else if (type.equals("WS")) {
			this.xtype = "wordsizefield";
		} else if (type.equals("AF")) {
			this.xtype = "argsfield";
		} else if (type.equals("FTF")) {
			this.xtype = "ftfindfield";
		} else if (type.equals("MT")) {// MultiField
			this.xtype = "multifield";
			this.secondname = formDetail.getFd_logictype();
			this.editable = !this.readOnly;
			this.readOnly = false;
			this.fieldConfig = formDetail.getFd_dbfind();
		} else if (type.equals("MD")) {// 选择年月
			this.xtype = "monthdatefield";
		} else if (type.equals("CMD")) {// 选择年月
			this.xtype = "conmonthdatefield";
		} else if (type.equals("YD")) {// 选择年份
			this.xtype = "yeardatefield";
		} else if (type.equals("FREQ")) {//推送頻率
			this.xtype = "frequencytrigger";
		} else if ("UU".equals(type)) {
			this.xtype = "uutrigger";
		} else if ("MS".equals(type)) {// 选择帐套
			this.xtype = "mastertrigger";
			this.editable = false;
		} else if ("STF".equals(type)) {
			this.xtype = "splittextfield";
		} else if ("IG".equals(type)) {
			this.xtype = "itemgrid";
			this.title = formDetail.getFd_caption();
			this.iniValue = formDetail.getFd_defaultvalue();
			// this.id = formDetail.getFd_id();
		} else if ("JNM".equals(type)) {
			this.xtype = "JNodeManSetField";
		} else if ("HOS".equals(type)) {
			this.secondname = formDetail.getFd_logictype();
			this.xtype = "HrOrgSelectfield";
		} else if ("DHM".equals(type)) {
			this.xtype = "datehourminutefield";
		}else if ("DHMC".equals(type)) {
			this.xtype = "datetimecombofield";
		} else if ("CDHM".equals(type)) {
			this.xtype = "condatehourminutefield";
			this.secondname = formDetail.getFd_logictype();
		} else if ("FTN".equals(type)) {
			this.xtype = "erpFtNumberField";
			if (formDetail.getFd_minvalue() != null)
				this.minValue = formDetail.getFd_minvalue();
		} else if ("SCH".equals(type)) {
			this.xtype = "SchedulerTrigger";
		} else if ("TMF".equals(type)) {
			this.xtype = "timeminutefield";
		} else if("QD".equals(type)){
			this.xtype = "quarterfield";
		}else if ("ES".equals(type)) {
			this.secondname = formDetail.getFd_logictype();
			this.xtype = "EmpSelectfield";
		}else if("LLQ".equals(type)){
			this.secondname = formDetail.getFd_logictype();
			this.xtype = "longitudeandlatitudetrigger";
		}else if("PT".equals(type)){
			this.xtype = "producttypetrigger";
		}else if ("FTPF".equals(type)) {
			this.xtype = "ftpfilefield";
			this.title = this.fieldLabel;
			this.hideTrigger = false;
		}else if ("DMT".equals(type)) {
			this.xtype = "docMenuTrigger";
			this.hideTrigger = false;
			this.editable = !this.readOnly;
			this.readOnly = false;
		} else if ("BNT".equals(type)) {
			this.xtype="bankNameTrigger";
		} else if ("KE".equals(type)) {
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
			this.xtype="extkindeditor";
		} else {
			this.xtype = "textfield";
			this.maxLength = formDetail.getFd_fieldlength();
			this.maxLengthText = "字段长度不能超过" + formDetail.getFd_fieldlength() + "字符!";
		}
		String dbfind = formDetail.getFd_dbfind();
		if (dbfind.equals("T") && !type.equals("MT") && !type.equals("FTF")) {
			this.xtype = "dbfindtrigger";
			this.hideTrigger = false;
			this.editable = !this.readOnly;
			this.maxLength = formDetail.getFd_fieldlength();
			this.readOnly = false;
			if ("N".equals(type))
				this.maskRe = "^[0-9]*$";
		} else if (dbfind.equals("M")) {
			this.xtype = "multidbfindtrigger";
			this.hideTrigger = false;
			this.editable = !this.readOnly;
			this.readOnly = false;
		} else if ("ET".equals(dbfind)) {
			this.xtype = "emptrigger";
			this.hideTrigger = false;
		} else if (dbfind.equals("CT")) {
			this.xtype = "hrOrgTreeDbfindTrigger";
			this.hideTrigger = false;
		} else if (dbfind.equals("TT")) {
			this.xtype = "cateTreeDbfindTrigger";
			this.hideTrigger = false;
			this.editable = !this.readOnly;
			this.readOnly = false;
		} else if (dbfind.equals("AT")) {
			this.xtype = "adddbfindtrigger";
			this.hideTrigger = false;
			this.readOnly = false;
		} 
		if (this.name.contains(":")) {
			this.xtype = "doublefield";
			if (formDetail.getFd_dbfind().equals("T")) {
				this.firstxtype = "dbfindtrigger";
				this.firstname = this.name.split(":")[0];
				this.secondname = this.name.split(":")[1];
			}
		}
	}

	public FormItems(int index, String groupName) {
		this.html = "<div onclick=\"javascript:collapse(" + index + ");\" class=\"x-form-group-label\" id=\"group" + index
				+ "\" style=\"background-color: #bfbfbe;height:22px!important;\" title=\"收拢\"><h6>" + groupName + "</h6></div>";
		this.columnWidth = 1;
		this.xtype = "container";
		this.cls = "x-form-group";
		//UI 新增属性
		this.title = groupName;
		this.margin = "0";
	}

	public FormItems(RelativeSearch.Form form) {
		this.margin = "7 0 3 0";
		this.logic = form.getRsf_logic();
		this.labelAlign = "left";
		this.fieldLabel = form.getRsf_caption();
		this.name = form.getRsf_field();
		this.editable = false;
		this.columnWidth = form.getRsf_width() / 4;
		this.allowBlank = true;
		this.cls = "form-field-gray";
		this.fieldStyle = "background:#f1f1f1;border: none;border-right: 2px solid #c6c6c6;border-left: 2px solid #c6c6c6;";
		if ("combo".equals(form.getRsf_type())) {
			this.xtype = "combo";
			this.queryMode = "local";
			this.displayField = "display";
			this.valueField = "value";
			this.hideTrigger = false;
		} else if ("datefield".equals(form.getRsf_type())) {
			this.xtype = "datefield";
		} else if ("hidden".equals(form.getRsf_type())) {
			this.xtype = "hidden";
		} else {
			this.xtype = "textfield";
			this.readOnly = true;
		}
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public String getRenderfn() {
		return renderfn;
	}

	public void setRenderfn(String renderfn) {
		this.renderfn = renderfn;
	}
	
}

