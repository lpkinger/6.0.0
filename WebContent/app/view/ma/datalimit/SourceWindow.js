Ext.define('erp.view.ma.datalimit.SourceWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.sourcewindow',
	width : '85%',
	height :'90%',
	layout:'border',
	limitId_:null,
	title:'<div align="center">设置权限</div>',
	closeAction : 'destroy',
	table:null,
	LimitType:null,
	initComponent : function() {
		var me = this;
		Ext.apply(me,{
			items:[me.createQueryGrid(),me.createDataGrid()],
			dockedItems: [{
				xtype: 'toolbar',
				dock: 'top',
				ui: 'footer',
				items: [{
					text: '筛选',
					itemId:'query',
					iconCls:'x-button-icon-query'
				},{
					text:'选择数据',
					itemId:'selectdata',
					iconCls:'x-button-icon-execute',
					hidden:me.LimitType!='detail'
				},{
					text:'生成条件',
					itemId:'createSql',
					iconCls:'x-button-icon-scan',
					hidden:me.LimitType=='detail'
				}]
			}]
		});
		this.callParent(arguments);

	},
	createQueryGrid:function(){
		var config={
				id:'querygrid',
				region:'west',
				width:'50%',
				foreceFit:true,
				columnLines:true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1,
					listeners: {
						beforeedit: function(e){
							if(e.field == 'value'){
								var record = e.record;
								var column = e.column;
								var f = record.data['type'];
								switch(f){
								case 'D':
									switch(record.data['relative']){
									case 'Between And':
										column.setEditor(new erp.view.core.form.FtDateField({
											id: f,
											name: f
										}));break;
									default:
										column.setEditor(new Ext.form.field.Date({
											id: f,
											name: f
										}));break;
									}
									break;
								case 'S':
									switch(record.data['relative']){
									case 'Between And':
										column.setEditor(new erp.view.core.form.FtField({
											id: f,
											name: f,
											value: e.value
										}));break;
									default:
										column.setEditor(new Ext.form.field.Text({
											id: f,
											name: f
										}));break;
									}
									break;
								case 'N':
									switch(record.data['relative']){
									case 'Between And':
										column.setEditor(new erp.view.core.form.FtNumberField({
											id: f,
											name: f
										}));break;
									default:
										column.setEditor(new Ext.form.field.Number({
											id: f,
											name: f
										}));break;
									}
									break;
								case 'T':
									column.dbfind = record.get('sl_dbfind');
									switch(record.data['relative']){
									case 'Between And':
										column.setEditor(new erp.view.core.form.FtFindField({
											id: f,
											name: f
										}));break;
									default:
										column.setEditor(new erp.view.core.trigger.DbfindTrigger({
											id: f,
											name: f
										}));break;
									}
									break;
								default:
									column.setEditor(null);
								}
							}
						}
					}
				})],
				columns:[{
					text:'字段',
					dataIndex:'field',
					flex:1,
					sortable:false,
					editor:{
						xtype: 'combo',
						listConfig:{
							maxHeight:180
						},
						store: Ext.create('Ext.data.Store', {
							fields: ['display', 'value','type'],
							data :[]
						}),
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						onTriggerClick:function(trigger){
							var me=this,grid=Ext.getCmp('querygrid'),store=this.getStore();
							if(store.totalCount<1){
								var data=grid.getDataDictionaryData(grid.ownerCt.table);
								store.loadData(data);
							}
							if (!me.readOnly && !me.disabled) {
								if (me.isExpanded) {
									me.collapse();
								} else {
									me.expand();
								}
								me.inputEl.focus();
							}    
						}
					},
					processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
						if (type == 'click' || type == 'dbclick') {
							return true;
						}
						return false;
					},
					renderer : function(val, meta, record, x, y, store, view) {
						if (val) {
							var column = view.ownerCt.headerCt.getHeaderAtIndex(y);
							if(column && typeof column.getEditor != 'undefined') {
								var	editor = column.getEditor(record);
								if (editor && editor.lastSelection.length > 0) {
									var t=editor.lastSelection[0].get('type');
									if (record.get('type') != t)
										record.set('type', t);
									return editor.lastSelection[0].get('display');
								}
							}
						} 
						return val;
					}
				},{
					text: '关系',
					flex: 1,
					dataIndex: 'relative',
					xtype:'combocolumn',
					sortable:false,
					editor: {
						xtype: 'combo',
						store: Ext.create('Ext.data.Store', {
							fields: ['display', 'value'],
							data : [{"display": '等于', "value": '='},
							        {"display": '大于', "value": '>'},
							        {"display": '大于等于', "value": '>='},
							        {"display": '小于', "value": '<'},
							        {"display": '小于等于', "value": '<='},
							        {"display": '不等于', "value": '<>'},
							        {"display": '介于', "value": 'Between And'},
							        {"display": '包含', "value": 'like'},
							        {"display": '不包含', "value": 'not like'},
							        {"display": '开头是', "value": 'begin like'},
							        {"display": '开头不是', "value": 'begin not like'},
							        {"display": '结尾是', "value": 'end like'},
							        {"display": '结尾不是', "value": 'end not like'}]
						}),
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						editable: false				
					},
					processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
						if (type == 'click' || type == 'dbclick') {
							return true;
						}
						return false;
					},
					renderer : function(val, meta, record, x, y, store, view) {
						var g = view.ownerCt,h = g.columns[y],field=h.field;
						if (val && field) {
							var t = field.store.findRecord('value', val);
							if (t)
								return t.get('display');
						}else if(record.get('field')){							
							if (record.get('relative') != "=") record.set('relative',"=");
							val="等于";
						} 
						return val;
					}
				},{
					text:'值',
					dataIndex:'value',
					sortable:false,
					flex:1,
					renderer: function(val){
						if(Ext.isDate(val)){
							return Ext.Date.format(val, 'Y-m-d');
						}
						return val;
					}
				},{
					dataIndex:'type',
					flex:0,
					width:0
				}],
				store:Ext.create('Ext.data.Store',{
					fields:['field','relative','value','type'],
					data:[{},{},{},{},{},{},{},{},{},{},{}]
				}),
				getDataDictionaryData: function(tablename){
					var me = this, grid = Ext.getCmp('querygrid'),data=[];
					Ext.Ajax.request({
						url : basePath + 'ma/getDataDictionary.action',
						async: false,
						params: {
							table: tablename
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);return;
							} else if(res.success) {
								data=me.parseDictionary(res.datadictionary);
							}
						}
					});
					return data;
				},
				getCondition: function(){  
					var condition="",store=this.getStore(),data;
					Ext.each(store.data.items, function(d){
						data=d.data;
						if(data.field && data.relative && data.value){
							if(data.relative == 'Between And'){
								var v1 = data.value.split('~')[0];
								var v2 = data.value.split('~')[1];
								if(data.type == 'D'){
									if(condition == ''){
										condition = '(' + data.field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
										+ v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
									} else {
										condition += ' AND (' + data.field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
										+ v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
									}
								} else if(data.type == 'N'){
									if(condition == ''){
										condition = '(' + data.field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
									} else {
										condition += ' AND  (' + data.field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
									}
								} else{
									if(condition == ''){
										condition = '(' + data.field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
									} else {
										condition += ' AND  (' + data.field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
									}
								}
							} else {
								if(data.type == 'D'){
									var v = data.value, field = data.field;
									if(Ext.isDate(v)) {
										v = Ext.Date.format(v, 'Y-m-d');
									}
									if(data.relative == '<' || data.relative == '<=' || data.relative == '>' || data.relative == '>='){
										v = "to_date('" + v + "','yyyy-MM-dd')";
									}else {
										v = Ext.Date.format(data.value, 'Ymd');
										field = "to_char(" + field + ",'yyyymmdd')";
									}
									if(condition == ''){
										condition = '(' + field + data.relative + v + ') ';
									} else {
										condition += ' AND  (' + field + data.relative + v + ') ';
									}
								} else {
									var v = data.value;
									if(data.relative == 'like' || data.relative=='not like'){
										v = " '%" + data.value + "%'";
									}else if(data.relative =='begin like' || data.relative =='begin not like'){
										v = " '" + data.value + "%'";
										data.relative=data.relative.substring(5);
									}else if(data.relative =='end like' || data.relative=='end not like'){
										v = " '%" + data.value + "'";
										data.relative=data.relative.substring(3);
									}else {
										v = " '" + data.value + "'";
									}
									console.log(data);
									if(condition == ''){
										condition = '(' + data.field + " " + data.relative + v + ") ";
									} else {
										condition += ' AND  (' + data.field + " " + data.relative + v + ") ";
									}
								}
							}
						}  					
					});
					return condition;
				},
				parseDictionary: function(dictionary) {
					var me = this, data = this.data;
					var combodata=new Array(),o=null;
					Ext.each(dictionary, function(d, index){
						o={
								value:d.column_name,
								display:d.comments	
						};
						if(contains(d.data_type, 'VARCHAR2', true)){
							o.type = 'S';
						} else if(contains(d.data_type, 'TIMESTAMP', true)){
							o.type  = 'DT';
						} else if(d.ddd_fieldtype == 'DATE'){
							o.type  = 'D';
						} else if(d.ddd_fieldtype == 'NUMBER'){
							o.type  = 'N';
						} else if(d.ddd_fieldtype == 'FLOAT'){
							o.type  = 'F';
						} else {
							o.type  = 'S';
						}
						combodata.push(o);
					});
					return combodata;
				}
		};
		return Ext.create('Ext.grid.Panel',config);
	},
	createDataGrid:function(){
		var me=this;
		var config={
				id:'datagrid',
				region:'center',				 
				columnLines:true,
				foreceFit:true,
				requires: ['erp.view.core.grid.HeaderFilter'],
				plugins : [Ext.create('erp.view.core.grid.HeaderFilter'),Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				selModel: Ext.create('Ext.selection.CheckboxModel',{
					checkOnly : true,
					ignoreRightMouseSelection : false,
					getEditor: function(){
						return null;
					},
					onHeaderClick: function(headerCt, header, e) {
						if (header.isCheckerHd) {
							e.stopEvent();
							var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
							if (isChecked && this.getSelection().length > 0) {
								this.deselectAll(true);
							} else {
								this.selectAll(true);
								this.view.ownerCt.selectall = true;
							}
						}
					}
				}),
				columns:[{
					text:'代码',
					flex:0.5,
					dataIndex:'CODE_',
					filter: {xtype: 'textfield', filterName: 'CODE_'},
				},{
					text:'名称',
					flex:1,
					dataIndex:'DESC_',
					filter: {xtype: 'textfield', filterName: 'DESC_'},
				}],
				store:Ext.create('Ext.data.Store',{
					fields:['CODE_','DESC_'],
					proxy: {
						type: 'ajax',
						url : basePath+'/ma/datalimit/getSourceData.action',
						extraParams:{
							limitId_:me.limitId_
						},
						reader: {
							type: 'json',
							root: 'jobs'
						}
					},
					autoLoad: true
				})
		};
		return Ext.create('Ext.grid.Panel',config);
	},
	setDefaultValue : function(form) {
		var me = this;
		me.down('textfield[name=name]').setValue(form.title);
		if (form.codeField) {
			var c = form.down('#' + form.codeField);
			if (c) {
				me.down('textfield[name=sourcecode]').setValue(c.getValue());
			}
			var u = new String(window.location.href);
			u = u.substr(u.indexOf('jsps'));
			me.down('field[name=sourcelink]').setValue(u);
		}
		if (form.uulistener) {
			var t = me.down('fieldcontainer[name=resourcename]');
			Ext.each(form.uulistener, function(u){
				var f = form.down('#' + u.uu_field);
				if(f) {
					if(!(u.uu_ftype == 1 && f.value == em_code) && !(u.uu_ftype == 2 && f.value == em_name)) {//排除自己
						if(f.value && !t.down('checkbox[boxLabel=' + f.value + ']')) {
							t.insert(0, {
								xtype : 'checkbox',
								name : 'man',
								isFormField : false,
								checked : true,
								boxLabel : f.value
							});
						}
					}
				}
			});
		}
	}
});