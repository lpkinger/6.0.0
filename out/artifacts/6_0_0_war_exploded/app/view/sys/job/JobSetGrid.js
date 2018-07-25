Ext.define('erp.view.sys.job.JobSetGrid',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.jobsetgrid',
	id:'saasjobsetgrid',
	columnLines: true,
	viewConfig: {stripeRows:true},
	//frame: true,
	/*	plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToMoveEditor: 1,        
		autoCancel: false
	})],*/
	border:true,
	caller:null,
	combofield:null,
	listeners:{
		itemclick:function(grid,record){
			Ext.Ajax.request({
				url:basePath+'hr/employee/loadNewData.action',
				params: {
					"jo_id":record.data.jo_id
				},
				method : 'post',
				callback : function(options,success,response){
					var local=Ext.decode(response.responseText);
					if(local.success) {
						Ext.getCmp('titleRemort').setValue(local.jo_description);
						Ext.getCmp('myjobbutton').value=local.jo_name;
						if(local.jo_name!=''&&local.jo_name!=null){
							Ext.getCmp('myFieldIdssss').getEl().update('岗位名称:<b>'+local.jo_name+'</b>');
						}
						Ext.each(Ext.getCmp('saasjobpowerform').items.items, function(item){
							item.setValue(local.jo_powerdes);
						});
						data = Ext.decode(local.data.replace(/,}/g, '}').replace(/,]/g, ']'));
						Ext.getCmp('saasjobpersongrid').store.loadData(data);
						if(record.data.jo_id!=0){
							var savejobbtn=Ext.getCmp('savejob11button');
							savejobbtn.value=record.data.jo_id;
							savejobbtn.setDisabled(false);
						}else{
							var savejobbtn=Ext.getCmp('savejob11button');
							savejobbtn.value=record.data.jo_id;
							savejobbtn.setDisabled(true);
						}
					}else {
						showResult('提示',local.exceptionInfo);
					}
				}
			});
		}
	},
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		ui: 'footer',
		/*	layout: {
			pack: 'left'
		},*/
		items: [{
			iconCls:'btn-add',
			text: '添加岗位',
			toolTip:'添加岗位',
			scope:this,
			listeners:{
				click:function(btn){
					btn.up('jobsetgrid').addRecord(btn);
				}	
			}
		//handler: Ext.bind(this.addRecord, this)
		}/*,{
			iconCls:'btn-edit',
			text: '编辑岗位',
			toolTip:'编辑岗位',
			scope:this,
			listeners:{
				click:function(btn){
					btn.up('combosetgrid').addRecord(btn);
				}	
			}
		//handler: Ext.bind(this.addRecord, this)
		}*/]
	}],
	initComponent : function(){
		var me=this;
		me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
			clicksToEdit:2,
			listeners:{
				'edit':function(editor,e,Opts){
					var record=e.record,update={
							jo_id:record.data.jo_id==0?null:record.data.jo_id,
						    jo_name:record.data.jo_name,
						    jo_status:'已审核',
						    jo_statuscode:'AUDITED'
					};
					var savejobbtn=Ext.getCmp('savejob11button');
					savejobbtn.setDisabled(true);
					if(e.originalValue!=e.value && e.value){
						var param=new Array();
						param.push(Ext.JSON.encode(update));
						Ext.Ajax.request({
							url:basePath+'hr/employee/saveSaasJobs.action',
							params: {
								gridStore:unescape(param.toString())
							},
							method : 'post',
							callback : function(options,success,response){
								var local=Ext.decode(response.responseText);
								if(local.success) {
									showResult('提示','修改成功!');
									Ext.data.StoreManager.lookup('sys.JobStore').load();
									Ext.getCmp('saasjobsetgrid').getComboData(Ext.getCmp('saasjobsetgrid'));
								}else {
									showResult('提示',local.exceptionInfo);
								}
							}
						});

					}
				},
			}
		})];
		Ext.applyIf(me,{
			columns:me.getColumns(me),
			store:Ext.create('Ext.data.Store',{
				fields:[{name:'jo_name',type:'string'},{name:'jo_id',type:'int'}]
			})

		});
		this.callParent(arguments);
		this.getComboData(this);
	},
	getComboData: function(grid){
		var me = this;
		var params={
				
		};
		//this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', params, "");
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'hr/employee/getSaasJobs.action',
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
			params: '',
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
		var savejobbtn=Ext.getCmp('savejob11button');
		savejobbtn.setDisabled(true);
		Ext.Ajax.request({
			url : basePath + 'hr/employee/deleteSaasJob.action',
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					/*showError(localJson.exceptionInfo);return;*/
					showResult('提示',localJson.exceptionInfo);
				}
				if(localJson.success){
					showResult('提示','删除成功!');
					grid.getStore().remove(record);
				}/* else {
					showResult('提示',localJson.exceptionInfo);
				}*/
			}
		});
	},
	getColumns:function(grid){
		var editor={
				xtype:grid.editType||'textfield'
		};
		return [{
			width:235,
			dataIndex:'jo_name',
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
			    		   if(grid.getStore().getAt(rowIndex).data['jo_id']==0 && grid.getStore().getAt(rowIndex).data['jo_name']!=''){
			    			   return false;
			    		   }
			    		   if(grid.getStore().getAt(rowIndex).data['jo_id']==0 && grid.getStore().getAt(rowIndex).data['jo_name']==''){
			    			   Ext.Msg.confirm('删除数据?', '确定要删除当前选中行(行号:'+(rowIndex+1)+')?',
				    				   function(choice) {
				    			   if(choice === 'yes') {
				    				   var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
				    				   grid.getStore().remove(record);
				    				   var savejobbtn=Ext.getCmp('savejob11button');
				    				   savejobbtn.setDisabled(true);
				    			   }else{
				    				   var savejobbtn=Ext.getCmp('savejob11button');
				    				   savejobbtn.setDisabled(true);
				    			   }
				    		   }
				    		   );
			    			   return false;
			    		   }
			    		  Ext.Msg.confirm('删除数据?', '确定要删除当前选中行(行号:'+(rowIndex+1)+')?',
			    				   function(choice) {
			    			   if(choice === 'yes') {
			    				   var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
			    				   gridpanel.removeDetail(gridpanel,record.get('jo_id'),record);
			    			   	   Ext.data.StoreManager.lookup('sys.JobStore').load();
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
		var combogrid=btn.up('jobsetgrid'),
		edit = combogrid.cellEditingPlugin;
		edit.cancelEdit();
		combogrid.store.insert(0, '');
		edit.startEditByPosition({
			row: 0,
			column: 1
		});
	}
});