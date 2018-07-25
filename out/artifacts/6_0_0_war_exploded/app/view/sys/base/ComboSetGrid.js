Ext.define('erp.view.sys.base.ComboSetGrid',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.combosetgrid',
	columnLines: true,
	viewConfig: {stripeRows:true},
	//frame: true,
	collapsible: false,
	plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToMoveEditor: 1,        
		autoCancel: false
	})],
	border:true,
	caller:null,
	combofield:null,
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		ui: 'footer',
		/*	layout: {
			pack: 'left'
		},*/
		items: [{
			iconCls:'btn-add',
			text: '添加',
			toolTip:'添加条目',
			scope:this,
			listeners:{
				click:function(btn){
					btn.up('combosetgrid').addRecord(btn);
				}	
			}
		//handler: Ext.bind(this.addRecord, this)
		}]
	}],
	initComponent : function(){
		var me=this;
		me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
			clicksToEdit:1,
			listeners:{
				'edit':function(editor,e,Opts){
					var record=e.record,update={
							dlc_id:record.data.dlc_id==0?null:record.data.dlc_id,
						    dlc_value:e.value,
						    dlc_display:e.value,
						    dlc_fieldname:e.grid.field,
						    dlc_caller:e.grid.caller
					};

					if(e.originalValue!=e.value && e.value){
						var param=new Array();
						param.push(Ext.JSON.encode(update));
						Ext.Ajax.request({
							url:basePath+'common/saveCombo.action',
							params: {
								gridStore:unescape(param.toString())
							},
							method : 'post',
							callback : function(options,success,response){
								var local=Ext.decode(response.responseText);
								if(local.success) {
									showResult('提示','修改成功!');
									//record.commit();
									me.getComboData(me);
								}else {
									showResult('提示',local.exceptionInfo);
								}
							}
						});

					}
				}
			}
		})];
		Ext.applyIf(me,{
			columns:me.getColumns(me),
			store:Ext.create('Ext.data.Store',{
				fields:[{name:'dlc_value',type:'string'},{name:'dlc_id',type:'int'}]
			})

		});
		this.callParent(arguments);
		this.getComboData(this);
	},
	getComboData: function(grid){
		var me = this;
		var params={
				caller:this.caller,
				field:this.field
		};
		//this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', params, "");
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'common/getComboDataByCallerAndField.action',
			params: params,
			async: (grid.sync ? false : true),
			method : 'get',
			callback : function(options,success,response){
				grid.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				grid.store.loadData(res.data);
			}
		});
	},
	setStore: function(grid, fields, data, groupField, necessaryField){
		Ext.each(fields, function(f){
			if(f.name.indexOf(' ') > -1) {// column有取别名
				f.name = f.name.split(' ')[1];
			}
			if(!Ext.isChrome){
				if(f.type == 'date'){
					f.dateFormat = 'Y-m-d H:i:s';
				}
			}
		});
		var modelName = 'ext-model-' + grid.id;
		Ext.define(modelName, {
			extend: 'Ext.data.Model',
			fields: fields
		});
		var config = {
				model: modelName,
				groupField: groupField,
				getSum: function(records, field) {
					if (arguments.length  < 2) {
						return 0;
					}
					var total = 0,
					i = 0,
					len = records.length;
					if(necessaryField) {
						for (; i < len; ++i) {//重写getSum,grid在合计时，只合计填写了必要信息的行
							var necessary = records[i].get(necessaryField);
							if(necessary != null && necessary != ''){
								total += records[i].get(field);
							}
						}
					} else {
						for (; i < len; ++i) {
							total += records[i].get(field);
						}
					}
					return total;
				},
				getCount: function() {
					if(necessaryField) {
						var count = 0;
						Ext.each(this.data.items, function(item){//重写getCount,grid在合计时，只合计填写了必要信息的行
							if(item.data[necessaryField] != null && item.data[necessaryField] != ''){
								count++;
							}
						});
						return count;
					}
					return this.data.items.length;
				}	
		};
		if(grid.buffered) {//grid数据缓存
			config.buffered = true;
			config.pageSize = 200;
			config.purgePageCount = 0;
			config.proxy = {
					type: 'memory'
			};
		} else {
			config.data = data;
		}
		var store = Ext.create('Ext.data.Store', config);
		store.each(function(item, x){
			item.index = x;
		});
		if(grid.buffered) {
			var ln = data.length, records = [], i = 0;
			for (; i < ln; i++) {
				records.push(Ext.create(modelName, data[i]));
			}
			store.cacheRecords(records);
		}
		return store;
	},
	loadNewStore: function(grid, param){
		var me = this;
		param=param||grid.params;
		grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/loadNewGridStore.action",
			params: param,
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
				if(!data || data.length == 0){
					grid.store.removeAll();
					me.add10EmptyItems(grid);
				} else {
					grid.store.loadData(data);
				}
				//自定义event
				grid.addEvents({
					storeloaded: true
				});
				grid.fireEvent('storeloaded', grid, data);
			}
		});
	},
	removeDetail:function(grid,id,record){
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'common/deleteCombo.action',
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					showResult('提示','删除成功!');
					grid.getStore().remove(record);
				} else {
					delFailure();
				}
			}
		});
	},
	getColumns:function(grid){
		var editor={
				xtype:grid.editType||'textfield'
		};
		return [{
			width:grid.fieldWidth||150,//200,
			dataIndex:'dlc_value',
			text:grid.title,
			editor:editor
		},{
			xtype:'actioncolumn',
			width:45,
			items:[
			       {
			    	   iconCls:'btn-delete',
			    	   tooltip:'删除',
			    	   width:50,
			    	   handler:function(grid, rowIndex, colIndex) {
			    		   Ext.Msg.confirm('删除数据?', '确定要删除当前选中行(行号:'+(rowIndex+1)+')?',
			    				   function(choice) {
			    			   if(choice === 'yes') {
			    				   //var reviewStore = Ext.data.StoreMgr.lookup('reviewStore');
			    				   var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
			    				   gridpanel.removeDetail(gridpanel,record.get('dlc_id'),record);
			    			   }
			    		   }
			    		   );   
			    	   }
			       }]
		}];
	},
	DetailUpdateSuccess:function(btn,type){
		var tabP=Ext.getCmp('saletabpanel'),_activeTab=tabP.activeTab;
		_activeTab.loadNewStore(_activeTab,_activeTab.params);
		var win=btn.up('window');
		if(win) win.close();
	},
	addRecord:function(btn){
		var combogrid=btn.up('combosetgrid'),
		edit = combogrid.cellEditingPlugin;
		edit.cancelEdit();
		combogrid.store.insert(0, {});
		edit.startEditByPosition({
			row: 0,
			column: 1
		});
	}
});