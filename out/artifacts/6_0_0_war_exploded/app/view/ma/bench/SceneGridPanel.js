Ext.QuickTips.init();

Ext.define('erp.view.ma.bench.SceneGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.sceneGridPanel',
	layout : 'fit',
	id: 'grid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true,
	store: [],
	columns: [],
	plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),
	GridUtil: Ext.create('erp.util.GridUtil'),
	bbar: {xtype: 'erpToolbar'},
	initComponent : function(){
		condition = getUrlParam('gridCondition');
		condition = condition != null ? condition.replace(/IS/g, '=') : null;
		if(bscode == ''){
			this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', 
					{caller: this.caller, condition: gridCondition, _m: 0});
		} else {
			this.getGridColumnsAndStore();
		}
	
		this.callParent(arguments);  
	},
	listeners: {
		afterrender: function() {
			this.down('erpToolbar').add({
				id: 'erpSetComboButton',
				text: '重置下拉框',
				cls: 'x-btn-blue'
			},{
				xtype:'erpComboButton',
				cls: 'x-btn-blue'
			});
		}
	},
	detno: 'sg_detno',
	caller: 'SceneSet',
	necessaryFields: ['sg_field'],
	keyField: 'sg_id',
	getGridColumnsAndStore: function(){
		var grid = this;
		var main = parent.Ext.getCmp('bench_'+bench);
		if(!main){
			main = parent.Ext.getCmp("content-panel") || parent.parent.Ext.getCmp("content-panel");
		}
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + 'common/singleGridPanel.action',
			async: false,
			params: {
				caller: caller||this.caller,
				condition: condition
			},
			method : 'post',
			callback : function(options,success,response){
				main.getActiveTab().setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				if(res.columns){
					grid.columns = res.columns;
					grid.fields = res.fields;
					grid.columns.push({
						xtype: 'checkcolumn',
						text: '配置',
						width: 60,
						dataIndex: 'deploy',
						cls: "x-grid-header-1",
						logic:'ignore',
						locked: true,
						editor: {
							xtype: 'checkbox',
							cls: "x-grid-checkheader-editor"
						}
					});
					grid.fields.push({name: 'deploy', type: 'bool'});
					//renderer
					grid.getRenderer();
					var data = [];
					if(res.data){
						data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					}
					
					Ext.each(data, function(d){
						d.deploy = true;
					});
					
					if(res.dbfinds.length > 0){
						grid.dbfinds = res.dbfinds;
					}
					//取数据字典配置
					if(data.length>0){
						grid.data = data;
						var tab = data[0].sg_table;
						if(tab) {
							grid.getDataDictionaryData(tab.split(' ')[0]);
						}
					}else{
						var data = new Array();
						for(var i=0;i<20;i++){
							var o = new Object();
							o.sg_detno = i+1;
							o.sg_bscode = bscode;
							data.push(o);
						}
						grid.data = data;
					}
					
					grid.reconfigureGrid();
				}
			}
		});
	},
	getRenderer: function(){
		var grid = this;
		Ext.each(grid.columns, function(column, y){
			//logictype
			var logic = column.logic;
			if(logic == 'detno'){
				grid.detno = column.dataIndex;
			} else if(logic == 'keyField'){
				grid.keyField = column.dataIndex;
			} else if(logic == 'mainField'){
				grid.mainField = column.dataIndex;
			} else if(grid.necessaryFields.indexOf(column.dataIndex)>-1){
				if(!column.haveRendered){
					column.renderer = function(val, meta, item){
						if(item.data['sg_id'] != null && item.data['sg_id'] != '' && item.data['sg_id'] != '0' 
							&& item.data['sg_id'] != 0) {
							return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png">' +   
							'<span style="color:blue;" title="已配置">' + val + '</span>';
						} else {
							return '<img src="' + basePath + 'resource/images/renderer/important.png">' +  
							'<span style="color:red;" title="未配置">' + val + '</span>';
						}
					};
				}
			} else if(logic == 'groupField'){
				grid.groupField = column.dataIndex;
			}
		});
	},
	reconfigureGrid: function(){
		var grid = this;
		grid.store = Ext.create('Ext.data.Store', {
			storeId: 'gridStore',
			fields: grid.fields,
			data: grid.data,
			groupField: grid.groupField
		});
	},
	getDataDictionaryData: function(tablename){
		var me = this,data = this.data;
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
					//取Max(序号)
					var dets = Ext.Array.pluck(data, me.detno);
					Ext.Array.sort(dets, function(a, b){
						return b - a;
					});
					var det = dets[0];
					//data里面包含的字段
					var sel = Ext.Array.pluck(data, 'sg_field');
					var o = null;
					Ext.each(res.datadictionary, function(d, index){
						//将DataDictionary的数据转化成DataListDetail数据
						if(!Ext.Array.contains(sel, d.column_name)){
							o = new Object();
							o.sg_table = d.table_name;
							o.sg_field = d.column_name;
							o.sg_text = d.comments;
							o.sg_text_fan = d.comments;
							o.sg_text_en = d.comments;
							o.sg_editable = false;
							o.sg_width = 80;
							o.deploy = false;
							if(contains(d.data_type, 'VARCHAR2', true)){
								o.sg_type = 'S';
							} else if(contains(d.data_type, 'TIMESTAMP', true)){
								o.sg_type = 'DT';
							}else if(d.data_type == 'DATE'){
								o.sg_type = 'D';
							} else if(d.data_type == 'NUMBER'){
								o.sg_type = 'N';
							} else if(d.data_type == 'FLOAT'){
								o.sg_type = 'N';
							} else {
								o.sg_type = 'S';
							}
							o.sg_detno = ++det;
							data.push(o);
						}
					});
				}
			}
		});
	},
	removeKey: function(d, key){
		var a = new Object(),keys = Ext.Object.getKeys(d);
		Ext.each(keys, function(k){
			if(k != key) {
				a[k] = d[k];
			}
		});
		return a;
	},
	getChange: function(){
		var grid = this,items = grid.store.data.items,key = grid.keyField,
		added = new Array(),updated = new Array(),deleted = new Array(),d = null,e = null;
		Ext.each(items, function(item){
			d = item.data;
			if (item.dirty) {
				e = grid.removeKey(d, 'deploy');
				if(d[key] == 0 && d['deploy'] == true) {
					added.push(e);
				}
				if(!Ext.isEmpty(d[key]) && d[key] != 0 && d['deploy'] == true) {
					var form = Ext.getCmp('form');
					if(grid.mainField && form && form.keyField){
						e[grid.mainField] = Ext.getCmp(form.keyField).value;
					}
					updated.push(e);
				}
				if(!Ext.isEmpty(d[key])&& d[key] != 0 && d['deploy'] == false) {
					deleted.push(e);
				}
			}
		});
		return {
			added: added,
			updated: updated,
			deleted: deleted
		};
	}
});