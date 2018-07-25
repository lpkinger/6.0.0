Ext.define('erp.view.common.batchUpdate.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchUpdateFormPanel',
	requires: ['erp.view.core.button.UseableReplace','erp.view.core.button.Consistency', 'erp.view.core.button.VastPost'],
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	items:[{
	  columnWidth:1.07,	
	  html:'<div style="color:blue;">只支持.xls格式  EXCEL的导入  ，日期格式必须采用标准日期格式(例如:2013-9-5) 限制行数不能超过5000行</div></br>'
	 },{
		xtype: 'filefield',
		name: 'file',
        fieldLable:'选择文件',
        width: 40,
        columnWidth:'1',
        labelWidth: 50,
        emptyText: '选择文件...',
		buttonConfig: {
			iconCls: 'x-button-icon-excel',
			text: $I18N.common.button.erpImportExcelButton,
			id:'filebutton'
        },
        listeners: {
			change: function(field){
				field.ownerCt.upexcel(field);
			}
		}
	},{
	   xtype:'button',
	   name:'download',
	   text:'下载模板',
	   height:24,
	   margin:'2 3 0 3',
	   iconCls: 'x-button-icon-download',
	   handler:function(btn){
		   var title = parent.Ext.getCmp("content-panel").getActiveTab().tabConfig.tooltip +"模板  ";
		   var grid=Ext.getCmp('grid');
		   var store = grid.getView().getStore();
		   // 先清空数据
		   store.loadData([{}]);
		   btn.ownerCt.BaseUtil.exportGrid(grid,title);
		/*	var columns = grid.columns,cm = new Array(),datas = new Array();
			Ext.Array.each(columns, function(c){
				if(!c.hidden && c.width > 0 && !c.isCheckerHd) {
					if(c.items && c.items.length > 0) {
						var items = c.items.items;
						Ext.Array.each(items, function(item){
							if(!item.hidden)
								cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')) + '(' + item.text.replace(/<br>/g, '\n') + ')', 
									dataIndex: item.dataIndex, width: item.width});
						});
					} else {
						cm.push({text: (Ext.isEmpty(c.text) ? ' ' : c.text.replace(/<br>/g, '\n')), dataIndex: c.dataIndex, width: c.width});
					}
				}
			});
		   if (!Ext.fly('ext-grid-excel')) {  
				var frm = document.createElement('form');  
				frm.id = 'ext-grid-excel';  
				frm.name = id;  
				frm.className = 'x-hidden';
				document.body.appendChild(frm);  
			}
		   Ext.Ajax.request({
				url: basePath + 'common/excel/grid.xls',
				method: 'post',
				form: Ext.fly('ext-grid-excel'),
				isUpload: true,
				params: {
					datas: unescape(Ext.JSON.encode(datas).replace(/\\/g,"%")),
					columns: unescape(Ext.encode(cm).replace(/\\/g,"%")),
					title: title
				}
			});*/
	   }
	},{
		xtype:'hidden',
		name:'caller',
		id:'caller',
		value:caller
	},{
		xtype: 'htmleditor',
        enableColors: false,
        enableAlignments: false,
        columnWidth:1.125,
        enableFont: false,
        enableFontSize: false,
        enableFormat: false,
        enableLinks: false,
        enableLists: false,
        enableSourceEdit: false,
		cls :'form-field-allowBlank',
		style:'background:#fff;color:#515151;',
		allowBlank:true,
		frame: false,
		id:'html',
		height:250,
		readOnly:true,
	    listeners: {
	    	afterrender: function(editor){
	    		editor.getToolbar().hide();		    		
	    	}	    	
	    }}],
	initComponent : function(){ 
    	this.addEvents({alladded: true});//items加载完
		this.callParent(arguments);
		this.addKeyBoardEvents();//监听Ctrl+Alt+S事件
	},
	upexcel: function(field){
		var me=this;
			this.getForm().submit({
        		url: basePath + 'common/update/batchUpdate.action',
        		waitMsg: "正在解析Excel",
        		success: function(fp, o){
        			Ext.Msg.alert('提示','更新成功');
        			field.reset();
        			Ext.getCmp('html').setValue(o.result.data);        		
        		},
        		failure: function(fp, o){
        			if(o.result.size){
        				showError(o.result.error + "&nbsp;" + Ext.util.Format.fileSize(o.result.size));
        				field.reset();
        			} else {
        				showError(o.result.error);
        				field.reset();
        			}
        		}
        	});
	},
	onQuery: function(select){
		var grid = Ext.getCmp('batchDealGridPanel'),sel = [];
		if(!grid){
			grid = Ext.getCmp('grid');
		}
		grid.multiselected = new Array();
		if(select == true) {
			sel = grid.selModel.getSelection();
		}
		var form = this;
		var cond = form.getCondition();
		if(Ext.isEmpty(cond)) {
			cond = '1=1';
		}
		form.beforeQuery(caller, cond);//执行查询前逻辑
		var gridParam = {caller: caller, condition: cond + form.getOrderBy(grid), start: 1, end: 1000};
		if(grid.getGridColumnsAndStore){
			grid.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
		} else {
			//grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
			grid.GridUtil.loadNewStore(grid, gridParam);
		}
		if(select == true) {
			Ext.each(sel, function(){
				grid.selModel.select(this.index);
			});
		}
	},
	getCondition: function(grid){
		grid = grid || Ext.getCmp('batchDealGridPanel');
		if(!grid){
			grid = Ext.getCmp('grid');
		}
		var form = this;
		var condition = Ext.isEmpty(grid.defaultCondition) ? '' : ('(' + grid.defaultCondition + ')');
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if((f.xtype == 'checkbox' || f.xtype == 'radio')){
					if(f.value == true) {
						if(condition == ''){
							condition += f.logic;
						} else {
							condition += ' AND ' + f.logic;
						}
					}
				} else if(f.xtype == 'datefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(idx == 0){
									condition += f.logic + "='" + d.data.value + "'";
								} else {
									condition += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += ')';
					}
				} else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(!Ext.isEmpty(f.value)){
							if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value.toString(), '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else if(f.value.toString().charAt(0) == '!'){ 
								if(condition == ''){
									condition += 'nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "'";
								} else {
									condition += ' AND (nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "')";
								}
							} else {
								if(condition == ''){
									condition += f.logic + "='" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			}
		});
		/*if(urlcondition !=null || urlcondition !=''){
			condition =condition+urlcondition; 
		}*/
		return condition;
	},
	getOrderBy: function(grid){
		var ob = new Array();
		if(grid.mainField) {
			ob.push(grid.mainField + ' desc');
		}
		if(grid.detno) {
			ob.push(grid.detno + ' asc');
		}
		if(grid.keyField) {
			ob.push(grid.keyField + ' desc');
		}
		var order = '';
		if(ob.length > 0) {
			order = ' order by ' + ob.join(',');
		}
		return order;
	},
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
	},
	beforeQuery: function(call, cond) {
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: call,
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
		});
	}
});