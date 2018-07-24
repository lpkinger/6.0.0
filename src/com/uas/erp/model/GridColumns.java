package com.uas.erp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Bench.BenchSceneGrid;

/**
 * gridpanel的columns属性
 * 
 * @author yingp
 * @date 2012-07-30 18:00:03
 */
public class GridColumns implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String header;
	private String text;
	private String dataIndex;
	private String fullName;// 字段全名，显示列表个性化设置时使用
	private String cls = "x-grid-header-1";
	private Editor editor = new Editor();
	private Filter filter = new Filter();
	private String align = "left";
	private String format = "";
	private String xtype = "";
	private String dbfind = "";
	private String renderer = "";
	private float flex;
	private float width;
	private boolean readOnly = false;
	private boolean hidden = false;
	private String summaryType = "";
	private String logic;
	private boolean locked;// 固定列
	private String orderby;
	private String priority;
	private boolean autoEdit = false;
	private boolean modify;// 非在录入状态可编辑
	private List<GridColumns> gridcolumns = new ArrayList<GridColumns>();
	private String name_ = ""; //查询方案名称
	private Map<Object,Object> filterJson_ = new HashMap<Object,Object>();//查询方案json
	private int id_ = 0; //查询方案ID
	private boolean useNull=false;
	private boolean autodecimal=false;//自动展示小数位

	
	public int getId_() {
		return id_;
	}

	public void setId_(int id_) {
		this.id_ = id_;
	}
	public String getName_() {
		return name_;
	}

	public void setName_(String name_) {
		this.name_ = name_;
	}
	public Map<Object,Object> getFilterJson_() {
		return filterJson_;
	}

	public void setFilterJson_(Map<Object,Object> filterJson_) {
		this.filterJson_ = filterJson_;
	}
	public String getSummaryType() {
		return summaryType;
	}

	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public String getFullName() {
		return fullName;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getFormat() {
		return format;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getXtype() {
		return xtype;
	}

	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public String getDbfind() {
		return dbfind;
	}

	public void setDbfind(String dbfind) {
		this.dbfind = dbfind;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}

	public String getRenderer() {
		return renderer;
	}

	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}

	public float getFlex() {
		return flex;
	}

	public void setFlex(float flex) {
		this.flex = flex;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public List<GridColumns> getGridcolumns() {
		return gridcolumns;
	}

	public void setGridcolumns(List<GridColumns> gridcolumns) {
		this.gridcolumns = gridcolumns;
	}

	public boolean isAutoEdit() {
		return autoEdit;
	}

	public void setAutoEdit(boolean autoEdit) {
		this.autoEdit = autoEdit;
	}

	public GridColumns() {

	}

	/**
	 * 根据detailgrid表信息设置gridpanel的columns
	 */
	public GridColumns(DetailGrid grid, List<DataListCombo> combos) {
		this.renderer = grid.getDg_renderer();
		this.dataIndex = grid.getDg_field();
		String language = SystemSession.getLang();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		this.setModify("T".equals(grid.getDg_modify()));
		this.logic = grid.getDg_logictype();
		if (language.equals("en_US")) {
			this.header = grid.getDg_captionen();
		} else if (language.equals("zh_TW")) {
			this.header = grid.getDg_captionfan();
		} else {
			this.header = grid.getDg_caption();
		}
		this.text = this.header;
		this.width = grid.getDg_width();
		this.locked = grid.getDg_locked() == 1;
		this.filter = new Filter(grid, combos, language);
		String type = grid.getDg_type();
		if (grid.getDg_editable() == -1) {
			if (type.equals("numbercolumn")) {
				this.align = "right";
				this.editor = new Editor("numbercolumn");
				this.xtype = "numbercolumn";
				if (grid.getDg_renderer() == null) {
					this.format = "0,000";
				}
				if (grid.getDg_minvalue() != null)
					this.editor.setMinValue(grid.getDg_minvalue());
			} else if (type.equals("floatcolumn")) {
				this.editor = new Editor("floatcolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
				if (grid.getDg_minvalue() != null)
					this.editor.setMinValue(grid.getDg_minvalue());
			} else if (type.matches("^floatcolumn([0-9]|10){1}$")||type.matches("^nfloatcolumn([0-9]|10){1}$")) {//  nfloatcolumn自动展示小数位  类型 
				this.editor = new Editor(type);
				this.align = "right";
				this.xtype = "numbercolumn";
				int length = 0;
				if(type.indexOf("nfloatcolumn")>-1){
					this.autodecimal = true;
					length = Integer.parseInt(type.replace("nfloatcolumn", ""));
					this.editor = new Editor("floatcolumn"+length);
				}else{
					length = Integer.parseInt(type.replace("floatcolumn", ""));
				}
				this.format = "0,000.";
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
				if (grid.getDg_minvalue() != null)
					this.editor.setMinValue(grid.getDg_minvalue());
			}else if (type.matches("^nullnumbercolumn\\d{1}$")) {
				this.editor = new Editor(type.replace("nullnumbercolumn", "floatcolumn"));
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				this.useNull=true;
				int length = Integer.parseInt(type.replace("nullnumbercolumn", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
				if (grid.getDg_minvalue() != null)
					this.editor.setMinValue(grid.getDg_minvalue());
			} else if (type.equals("numbernoformat")) {
				this.editor = new Editor("textcolumn");
				this.align = "right";
				if (grid.getDg_minvalue() != null)
					this.editor.setMinValue(grid.getDg_minvalue());
			} else if(type.equals("integer")){
				this.editor = new Editor("integer");
				this.align = "right";
				this.xtype = "numbercolumn"; 
				if (grid.getDg_minvalue() != null)
					this.editor.setMinValue(grid.getDg_minvalue());
			} else if (type.equals("datecolumn")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.editor = new Editor("datecolumn");
			} else if (type.equals("datetimecolumn")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datetimecolumn");
			} else if (type.equals("datetimecolumn2")) {
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datehourminutefield");
			}else if (type.equals("monthcolumn")) {
				this.editor = new Editor("monthcolumn");
			} else if (type.equals("timecolumn")) {
				this.editor = new Editor("timecolumn");
			} else if (type.equals("combo")) {
				this.xtype = "combocolumn";
				this.editor = new Editor(grid.getDg_field(), combos, language);
			} else if (type.equals("editcombo")) {// editable combo
				this.xtype = "combocolumn";
				this.editor = new Editor(grid.getDg_field(), combos, language, true);
			} else if (type.equals("text")) {
				this.editor = new Editor("textcolumn");
			} else if (type.equals("textarea")) {
				this.editor = new Editor("textareafield");
			} else if (type.equals("texttrigger")) {
				// this.autoEdit = true;
				this.editor = new Editor("textareatrigger");
			} else if (type.equals("boolean")) {
				this.xtype = "booleancolumn";
				this.editor = new Editor("textcolumn");
			} else if (type.matches("^yncolumn-?\\d{0,1}$")) {
				this.xtype = type;
			} else if (type.equals("ynnvcolumn")) {
				this.xtype = "ynnvcolumn";
			} else if (type.equals("tfcolumn")) {
				this.xtype = "tfcolumn";
			} else if (type.matches("^checkcolumn-?\\d{0,1}$")) {
				this.xtype = "checkcolumn";
				this.editor = new Editor("checkbox");
			} else if (type.equals("wbscolumn")) {
				this.xtype = "wbscolumn";
			} else if (type.contains("treecolumn")) {
				if (type.contains("booleancolumn")) {
					this.xtype = "booleancolumn";
					this.editor = new Editor("textcolumn");
				} else if (type.contains("checkcolumn")) {
					this.editor = new Editor("checkbox");
				} else {
					this.xtype = "treecolumn";
					if (type.contains("&")) {
						this.editor = new Editor(type.split("&")[1]);
					} else {
						this.editor = new Editor("textcolumn");
					}
				}
			}else if(type.contains("BNT")){
				this.editor=new Editor("bankNameTrigger");
			} else {
				this.editor = null;
			}
			if (grid.getDg_dbbutton() == -1) {
				this.editor = new Editor("dbfindtrigger");
				// dg_findfunctionname格式为：caller|field
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -2) {
				this.editor = new Editor("multidbfindtrigger");
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -4) {
				this.editor = new Editor("emptrigger");
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -3) {
				this.editor = new Editor("cateTreeDbfindTrigger");
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -5) {
				this.editor = new Editor("multidbfindtrigger2");
				this.dbfind = grid.getDg_findfunctionname();
			}
			if (grid.getDg_maxlength() != null && (type.equals("text") || type.equals("textarea") || type.equals("texttrigger"))) {
				this.editor.setMaxLength(grid.getDg_maxlength());
			}
		} else if (grid.getDg_editable() == 0) {
			this.editor = null;
			if (type.equals("numbercolumn")) {
				this.align = "right";
				this.format = "0,000";
				this.xtype = "numbercolumn";
				this.readOnly = true;
			} else if (type.equals("floatcolumn")) {
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
			} else if (type.matches("^floatcolumn\\d{1}$")||type.matches("^nfloatcolumn\\d{1}$")) {//  nfloatcolumn 自动展示小数位  类型 
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				int length = 0;
				if(type.indexOf("nfloatcolumn")>-1){
					this.autodecimal = true;
					length = Integer.parseInt(type.replace("nfloatcolumn", ""));
				}else{
					length = Integer.parseInt(type.replace("floatcolumn", ""));
				}
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			} else if (type.matches("^nullnumbercolumn\\d{1}$")) {
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				this.useNull=true;
				int length = Integer.parseInt(type.replace("nullnumbercolumn", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			}else if (type.equals("texttrigger")) {
				this.xtype = "textareatrigger";
				// this.autoEdit = true;
				// this.editor = new Editor("textareatrigger",false);
			} else if (type.contains("datecolumn")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.readOnly = true;
			} else if (type.contains("datetimecolumn")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.readOnly = true;
			} else if (type.contains("treecolumn")) {
				this.xtype = "treecolumn";
			} else if (type.equals("yncolumn")) {
				this.xtype = "yncolumn";
				this.readOnly = true;
			} else if (type.equals("ynnvcolumn")) {
				this.xtype = "ynnvcolumn";
			} else if (type.equals("combo")) {
				this.xtype = "combocolumn";
			} else {
				this.readOnly = true;
			}
			if (grid.getDg_dbbutton() == -1) {
				this.editor = new Editor("dbfindtrigger", false);
				// dg_findfunctionname格式为：caller|field
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -2) {
				this.editor = new Editor("multidbfindtrigger", false);
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -4) {
				this.editor = new Editor("emptrigger", false);
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -3) {
				this.editor = new Editor("cateTreeDbfindTrigger", false);
				this.dbfind = grid.getDg_findfunctionname();
			} else if (grid.getDg_dbbutton() == -5) {
				this.editor = new Editor("multidbfindtrigger2", false);
				this.dbfind = grid.getDg_findfunctionname();
			}
		}
		if (grid.getDg_summarytype() != null) {
			type = grid.getDg_summarytype();
			if (type.equals("sum")) {// grid toolbar合计、求平均数等
				this.summaryType = "sum";
			} else if (type.equals("average")) {
				this.summaryType = "average";
			} else if (type.equals("count")) {
				this.summaryType = "count";
			}
		}
		if (this.width == 0) {
			this.hidden = true;
		}
	}

	/**
	 * 根据datalistdetail表信息设置gridpanel的columns
	 */
	public GridColumns(DataListDetail detail, List<DataListCombo> combos, String language) {
		this.dataIndex = detail.getDld_field();
		this.fullName = detail.getDld_field();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (language.equals("en_US")) {
			this.header = detail.getDld_caption_en();
		} else if (language.equals("zh_TW")) {
			this.header = detail.getDld_caption_fan();
		} else {
			this.header = detail.getDld_caption();
		}
		if (detail.getDld_render() != null) {
			this.renderer = detail.getDld_render();
		}
		if (detail.getDld_flex() != 0) {
			this.flex = detail.getDld_flex();
		}
		if (detail.getDde_orderby() != null) {
			this.orderby = detail.getDde_orderby();
		}
		if (detail.getDde_priority() != null) {
			this.priority = detail.getDde_priority();
		}
		this.text = this.header;
		this.width = detail.getDld_width();
		this.filter = new Filter(detail, combos, language);
		String type = detail.getDld_fieldtype();
		if (type.equals("N")) {
			this.xtype = "numbercolumn";
			this.format = "0,000";
			this.align = "right";
		} else if (type.equals("F")) {
			this.xtype = "numbercolumn";
			this.format = "0,000.00";
			this.align = "right";
		} else if (type.matches("^F\\d{1}$")) {
			this.xtype = "numbercolumn";
			this.align = "right";
			this.format = "0,000.";
			int length = Integer.parseInt(type.replace("F", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.equals("D")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d H:i:s";
		} else if (type.equals("yncolumn") || type.equals("YN")) {
			this.xtype = "yncolumn";
		} else if (type.equals("ynnvcolumn")) {
			this.xtype = "ynnvcolumn";
		} else if (type.equals("tfcolumn")) {
			this.xtype = "tfcolumn";
		} else if ("C".equals(type)) {
			this.xtype = "combocolumn";
		} else if ("H".equals(type)) {
			this.width = 0;
		}
		if (detail.getDld_editable() == -1) {
			if (type.equals("N")) {
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
			} else if (type.equals("PN")) {
				// 正数
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
				this.editor.setMinValue("0");
			} else if (type.equals("F")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
			} else if (type.matches("^F\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				int length = Integer.parseInt(type.replace("F", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			} else if (type.equals("PF")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
				this.editor = new Editor("numbercolumn");
				this.editor.setMinValue("0");
			} else if (type.matches("^PF\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				this.editor = new Editor("numbercolumn");
				int length = Integer.parseInt(type.replace("PF", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
				this.editor.setMinValue("0");
			} else if (type.equals("D")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.editor = new Editor("datecolumn");
			} else if (type.equals("DT")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datetimecolumn");
			} else if (type.equals("C")) {
				this.editor = new Editor(this.dataIndex, combos, language);
			} else if (type.equals("S")) {
				this.editor = new Editor("textcolumn");
			} else {
				this.editor = new Editor("");
			}
		} else {
			this.editor = null;
			this.readOnly = true;
		}
		if (this.width == 0) {
			this.hidden = true;
		}
	}

	/**
	 * 根据dbfindSetDetail表信息设置gridpanel的columns
	 */
	public GridColumns(DBFindSetDetail detail, List<DataListCombo> combos) {
		String language = SystemSession.getLang();
		this.dataIndex = detail.getDd_fieldname();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (detail.getDd_summarytype()!=null) {// 取得合计类型
			this.summaryType = detail.getDd_summarytype();
		}
		this.header = detail.getDd_fieldcaption();
		this.width = detail.getDd_fieldwidth();
		this.filter = new Filter(detail, combos, language);
		String type = detail.getDd_fieldtype();
		if (this.width == 0) {
			this.hidden = true;
		}
		if (type != null) {
			if (type.equals("N")) {
				this.xtype = "numbercolumn";
				this.format = "0,000";
				this.align = "right";
			} else if (type.matches("^F\\d{1}$")) {
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				int length = Integer.parseInt(type.replace("F", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			} else if (type.equals("D")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
			} else if (type.equals("DT")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
			} else if (type.equals("yncolumn")) {
				this.xtype = "yncolumn";
			} else if (type.equals("tfcolumn")) {
				this.xtype = "tfcolumn";
			} else if ("C".equals(type)) {
				this.xtype = "combocolumn";
			}
		}
		if (detail.getDd_editable() == 1) {
			if (type.equals("N")) {
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
			} else if (type.equals("D")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.editor = new Editor("datecolumn");
			} else if (type.equals("DT")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datetimecolumn");
			} else if (type.equals("C")) {
				this.xtype = "combocolumn";
				this.editor = new Editor(detail.getDd_fieldname(), combos, language);
			} else if (type.equals("S")) {
				this.editor = new Editor("textcolumn");
			} else {
				this.editor = new Editor("");
			}
		} else {
			this.editor = null;
			this.readOnly = true;
		}
		if (detail.getDd_render() != null) {
			this.renderer = detail.getDd_render();
		}
	}

	/**
	 * 设置gridpanel的columns
	 */
	public GridColumns(String field, String caption, int width) {
		this.dataIndex = field;
		if (dataIndex.contains("."))
			dataIndex = dataIndex.replace(".", "_");
		else if (dataIndex.contains(" ")) {
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		this.header = caption;
		this.text = caption;
		this.width = width;
		if (this.width == 0) {
			this.hidden = true;
		}
	}

	public GridColumns(String field, String caption, int width, boolean locked) {
		this(field, caption, width);
		this.locked = locked;
	}

	public GridColumns(String field, String caption, int width, String type) {
		this(field, caption, width);
		setPropertiesByDataType(type);
	}

	public GridColumns(RelativeSearch.Grid grid) {
		this.dataIndex = grid.getRsg_field();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		this.header = grid.getRsg_caption();
		this.text = grid.getRsg_caption();
		this.summaryType = grid.getRsg_sumtype();
		this.filter = new Filter(grid);
		if (grid.getRsg_width() > 0 && grid.getRsg_width() < 10) {
			this.flex = grid.getRsg_width();
		} else {
			this.width = grid.getRsg_width();
		}
		if (grid.getRsg_width() == 0) {
			this.hidden = true;
		}
		this.editor = null;
		if (grid.getRsg_url() != null) {
			this.logic = grid.getRsg_url();
		}
		setPropertiesByDataType(grid.getRsg_type());
	}

	/**
	 * 按数据类型设置列属性
	 * 
	 * @param type
	 */
	private void setPropertiesByDataType(String type) {
		if (type.equals("numbercolumn")) {
			this.align = "right";
			this.format = "0,000";
			this.xtype = "numbercolumn";
			this.readOnly = true;
		} else if (type.equals("floatcolumn")) {
			this.align = "right";
			this.xtype = "numbercolumn";
			this.format = "0,000.00";
		} else if (type.matches("^floatcolumn\\d{1}$")) {
			this.align = "right";
			this.xtype = "numbercolumn";
			this.format = "0,000.";
			int length = Integer.parseInt(type.replace("floatcolumn", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.contains("datecolumn")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d";
			this.readOnly = true;
		} else if (type.contains("datetimecolumn")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d H:i:s";
			this.readOnly = true;
		} else if (type.contains("treecolumn")) {
			this.xtype = "treecolumn";
		} else if (type.equals("yncolumn")) {
			this.xtype = "yncolumn";
		} else {
			this.readOnly = true;
		}
	}

	public GridColumns(String field, String caption, int width, String type, List<DataListCombo> combos) {
		this.dataIndex = field;
		String language = SystemSession.getLang();
		if (dataIndex.contains("."))
			dataIndex = dataIndex.replace(".", "_");
		else if (dataIndex.contains(" ")) {
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		this.header = caption;
		this.text = caption;
		this.filter = new Filter(field, type, combos, language);
		this.width = width;
		if (this.width == 0) {
			this.hidden = true;
		}
		if (type.equals("N")) {
			this.xtype = "numbercolumn";
			this.format = "0,000";
			this.align = "right";
		} else if (type.equals("F")) {
			this.xtype = "numbercolumn";
			this.format = "0,000.00";
			this.align = "right";
		} else if (type.equals("D")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d H:i:s";
		} else if (type.equals("YN")) {
			this.xtype = "yncolumn";
		} else if ("C".equals(type)) {
			this.xtype = "combocolumn";
			this.editor = null;
		}
	}
	
	/**
	 * 根据datalistdetail表信息设置gridpanel的columns
	 */
	public GridColumns(DataListDetail detail, List<DataListCombo> combos, String language, String name_, List<Map<Object,Object>> filterlist, int id_) {
		this.dataIndex = detail.getDld_field();
		this.fullName = detail.getDld_field();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (language.equals("en_US")) {
			this.header = detail.getDld_caption_en();
		} else if (language.equals("zh_TW")) {
			this.header = detail.getDld_caption_fan();
		} else {
			this.header = detail.getDld_caption();
		}
		if (detail.getDld_render() != null) {
			this.renderer = detail.getDld_render();
		}
		if (detail.getDld_flex() != 0) {
			this.flex = detail.getDld_flex();
		}
		if (detail.getDde_orderby() != null) {
			this.orderby = detail.getDde_orderby();
		}
		if (detail.getDde_priority() != null) {
			this.priority = detail.getDde_priority();
		}
		this.text = this.header;
		this.width = detail.getDld_width();
		this.filter = new Filter(detail, combos, language);
		this.name_ = name_;
		this.id_ = id_;		
		for(Map<Object,Object> map : filterlist){
			if(map.containsValue(dataIndex)){
				this.filterJson_ = map;
				break;
			}
		}
		String type = detail.getDld_fieldtype();
		if (type.equals("N")) {
			this.xtype = "numbercolumn";
			this.format = "0,000";
			this.align = "right";
		} else if (type.equals("F")) {
			this.xtype = "numbercolumn";
			this.format = "0,000.00";
			this.align = "right";
		} else if (type.matches("^F\\d{1}$")) {
			this.xtype = "numbercolumn";
			this.align = "right";
			this.format = "0,000.";
			int length = Integer.parseInt(type.replace("F", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.equals("D")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d H:i:s";
		} else if (type.equals("yncolumn") || type.equals("YN")) {
			this.xtype = "yncolumn";
		} else if (type.equals("ynnvcolumn")) {
			this.xtype = "ynnvcolumn";
		} else if (type.equals("tfcolumn")) {
			this.xtype = "tfcolumn";
		} else if ("C".equals(type)) {
			this.xtype = "combocolumn";
		} else if ("H".equals(type)) {
			this.width = 0;
		}
		if (detail.getDld_editable() == -1) {
			if (type.equals("N")) {
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
			} else if (type.equals("PN")) {
				// 正数
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
				this.editor.setMinValue("0");
			} else if (type.equals("F")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
			} else if (type.matches("^F\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				int length = Integer.parseInt(type.replace("F", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			} else if (type.equals("PF")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
				this.editor = new Editor("numbercolumn");
				this.editor.setMinValue("0");
			} else if (type.matches("^PF\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				this.editor = new Editor("numbercolumn");
				int length = Integer.parseInt(type.replace("PF", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
				this.editor.setMinValue("0");
			} else if (type.equals("D")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.editor = new Editor("datecolumn");
			} else if (type.equals("DT")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datetimecolumn");
			} else if (type.equals("C")) {
				this.editor = new Editor(this.dataIndex, combos, language);
			} else if (type.equals("S")) {
				this.editor = new Editor("textcolumn");
			} else {
				this.editor = new Editor("");
			}
		} else {
			this.editor = null;
			this.readOnly = true;
		}
		if (this.width == 0) {
			this.hidden = true;
		}
	}
	
	/**
	 * 根据scenegrid表信息设置gridpanel的columns
	 */
	public GridColumns(BenchSceneGrid detail, List<DataListCombo> combos, String language) {
		this.dataIndex = detail.getSg_field();
		this.fullName = detail.getSg_field();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (language.equals("en_US")) {
			this.header = detail.getSg_text_en();
		} else if (language.equals("zh_TW")) {
			this.header = detail.getSg_text_fan();
		} else {
			this.header = detail.getSg_text();
		}
		if (detail.getSg_render() != null) {
			this.renderer = detail.getSg_render();
		}
		this.text = this.header;
		this.width = detail.getSg_width();
		this.filter = new Filter(detail, combos, language);
		String type = detail.getSg_type();
		if (type.equals("N")) {
			this.xtype = "numbercolumn";
			this.format = "0,000";
			this.align = "right";
		} else if (type.equals("F")) {
			this.xtype = "numbercolumn";
			this.format = "0,000.00";
			this.align = "right";
		} else if (type.matches("^F\\d{1}$")) {
			this.xtype = "numbercolumn";
			this.align = "right";
			this.format = "0,000.";
			int length = Integer.parseInt(type.replace("F", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.equals("D")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d H:i:s";
		} else if (type.equals("yncolumn") || type.equals("YN")) {
			this.xtype = "yncolumn";
		} else if (type.equals("ynnvcolumn")) {
			this.xtype = "ynnvcolumn";
		} else if (type.equals("tfcolumn")) {
			this.xtype = "tfcolumn";
		} else if ("C".equals(type)) {
			this.xtype = "combocolumn";
		} else if ("H".equals(type)) {
			this.width = 0;
		}
		if (detail.getSg_editable() == -1) {
			if (type.equals("N")) {
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
			} else if (type.equals("PN")) {
				// 正数
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
				this.editor.setMinValue("0");
			} else if (type.equals("F")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
			} else if (type.matches("^F\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				int length = Integer.parseInt(type.replace("F", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			} else if (type.equals("PF")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
				this.editor = new Editor("numbercolumn");
				this.editor.setMinValue("0");
			} else if (type.matches("^PF\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				this.editor = new Editor("numbercolumn");
				int length = Integer.parseInt(type.replace("PF", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
				this.editor.setMinValue("0");
			} else if (type.equals("D")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.editor = new Editor("datecolumn");
			} else if (type.equals("DT")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datetimecolumn");
			} else if (type.equals("C")) {
				this.editor = new Editor(this.dataIndex, combos, language);
			} else if (type.equals("S")) {
				this.editor = new Editor("textcolumn");
			} else {
				this.editor = new Editor("");
			}
		} else {
			this.editor = null;
			this.readOnly = true;
		}
		if (this.width == 0) {
			this.hidden = true;
		}
	}
	
	/**
	 * 根据scenegrid表信息设置gridpanel的columns
	 */
	public GridColumns(BenchSceneGrid detail, List<DataListCombo> combos, String language, String name_, List<Map<Object,Object>> filterlist, int id_) {
		this.dataIndex = detail.getSg_field();
		this.fullName = detail.getSg_field();
		if (dataIndex.contains(" ")) {// column有取别名
			String[] strs = dataIndex.split(" ");
			dataIndex = strs[strs.length - 1];
		}
		if (language.equals("en_US")) {
			this.header = detail.getSg_text_en();
		} else if (language.equals("zh_TW")) {
			this.header = detail.getSg_text_fan();
		} else {
			this.header = detail.getSg_text();
		}
		if (detail.getSg_render() != null) {
			this.renderer = detail.getSg_render();
		}
		this.text = this.header;
		this.width = detail.getSg_width();
		this.filter = new Filter(detail, combos, language);
		this.name_ = name_;
		this.id_ = id_;		
		for(Map<Object,Object> map : filterlist){
			if(map.containsValue(dataIndex)){
				this.filterJson_ = map;
				break;
			}
		}
		String type = detail.getSg_type();
		if (type.equals("N")) {
			this.xtype = "numbercolumn";
			this.format = "0,000";
			this.align = "right";
		} else if (type.equals("F")) {
			this.xtype = "numbercolumn";
			this.format = "0,000.00";
			this.align = "right";
		} else if (type.matches("^F\\d{1}$")) {
			this.xtype = "numbercolumn";
			this.align = "right";
			this.format = "0,000.";
			int length = Integer.parseInt(type.replace("F", ""));
			for (int i = 0; i < length; i++) {
				this.format += "0";
			}
		} else if (type.equals("D")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d";
		} else if (type.equals("DT")) {
			this.xtype = "datecolumn";
			this.format = "Y-m-d H:i:s";
		} else if (type.equals("yncolumn") || type.equals("YN")) {
			this.xtype = "yncolumn";
		} else if (type.equals("ynnvcolumn")) {
			this.xtype = "ynnvcolumn";
		} else if (type.equals("tfcolumn")) {
			this.xtype = "tfcolumn";
		} else if ("C".equals(type)) {
			this.xtype = "combocolumn";
		} else if ("H".equals(type)) {
			this.width = 0;
		}
		if (detail.getSg_editable() == -1) {
			if (type.equals("N")) {
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
			} else if (type.equals("PN")) {
				// 正数
				this.editor = new Editor("numbercolumn");
				this.align = "right";
				this.xtype = "numbercolumn";
				this.format = "0,000";
				this.editor.setMinValue("0");
			} else if (type.equals("F")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
			} else if (type.matches("^F\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				int length = Integer.parseInt(type.replace("F", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
			} else if (type.equals("PF")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.00";
				this.editor = new Editor("numbercolumn");
				this.editor.setMinValue("0");
			} else if (type.matches("^PF\\d{1}$")) {
				this.xtype = "numbercolumn";
				this.format = "0,000.";
				this.editor = new Editor("numbercolumn");
				int length = Integer.parseInt(type.replace("PF", ""));
				for (int i = 0; i < length; i++) {
					this.format += "0";
				}
				this.editor.setMinValue("0");
			}else if (type.equals("D")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d";
				this.editor = new Editor("datecolumn");
			} else if (type.equals("DT")) {
				this.xtype = "datecolumn";
				this.format = "Y-m-d H:i:s";
				this.editor = new Editor("datetimecolumn");
			} else if (type.equals("C")) {
				this.editor = new Editor(this.dataIndex, combos, language);
			} else if (type.equals("S")) {
				this.editor = new Editor("textcolumn");
			} else {
				this.editor = new Editor("");
			}
		} else {
			this.editor = null;
			this.readOnly = true;
		}
		if (this.width == 0) {
			this.hidden = true;
		}
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public boolean isUseNull() {
		return useNull;
	}

	public void setUseNull(boolean useNull) {
		this.useNull = useNull;
	}

	public boolean isAutodecimal() {
		return autodecimal;
	}

	public void setAutodecimal(boolean autodecimal) {
		this.autodecimal = autodecimal;
	}
}
