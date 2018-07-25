Ext.define('erp.view.sys.init.InitImportGrid',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.initimportgrid',
	columnLines: true,
	 forceFit: true,
	viewConfig: {
		stripeRows: true,
		enableTextSelection: true
	},
	autoScroll:true,
	columns:[{
		text:'导入项目',
		dataIndex:'in_desc',
		style:'text-align:center',
		width:150,
		renderer:function(val,meta){
			if(val &&  val.indexOf('*')>0) return '<span style="color:red;">' + val + '</span>';
				return val;
				
		}
	},{
		text:'数据导入',
		style:'text-align:center',
		columns:[{
			dataIndex:'in_caller',
			text:'导入地址',
			style:'text-align:center',
			width:150,
			renderer:function(val, meta, record){
				return '<a href="'+basePath+'/ma/sysinit/initImportData.action?whoami='+val+'&title='+record.get('parentName')+'-'+record.get('in_desc')+'" target="_blank">数据导入</a>';
			}
		},{
			text:'记录数',
			style:'text-align:center'
		},{
			text:'导入日志',
			style:'text-align:center'
		},{
			text:'状态',
			width:40	
		}]
	
	},{
		text:'最近更新日期',
		align:'center'
	},{
		text:'最近更新人',
		align:'center'
	}],
	features: [{
		id: 'group',
		ftype: 'grouping',
		groupHeaderTpl:  Ext.create('Ext.XTemplate',
			    '{rows:this.formatName}',
			    {
			        formatName: function(f) {
			        return f[0].data.parentName;
			        }
			    }
			),
		//hideGroupedHeader: true,
		enableGroupingMenu: false
	}],
	initComponent : function(){
		this.getGridColumnsAndStore(this, 'ma/sysinit/getImportDataItem.action');
		this.callParent(arguments);
		
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params: param,
			async: false,
			method : 'get',
			callback : function(options,success,response){
				grid.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				console.log(res.data);
				grid.store=Ext.create('Ext.data.Store',{
					fields:[ {name: 'in_pid', type: 'int'},
					         {name:'in_desc',type:'string'},
					         {name:'in_caller',type:'string'},
					         {name:'parentName',type:'string'}],
					         groupField: 'in_pid',
					         data:res.data,
					         sorters: {property: 'in_detno', direction: 'ASC'},
				});
				console.log(grid.store);
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
	removeDetail:function(grid,id){
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + grid.deleteUrl,
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
					grid.loadNewStore(grid,grid.params);
				} else {
					delFailure();
				}
			}
		});
	},
	setColumns:function(columns){
		Ext.Array.each(columns,function(column){
			if(column.xtype=='yncolumn'){
				column.xtype='checkcolumn';
				column.editor= {
						xtype: 'checkbox',
						cls: 'x-grid-checkheader-editor'
				};
			}
		});
		return columns;
	},
	DetailUpdateSuccess:function(btn,type){
		var tabP=Ext.getCmp('saletabpanel'),_activeTab=tabP.activeTab;
		_activeTab.loadNewStore(_activeTab,_activeTab.params);
		var win=btn.up('window');
		if(win) win.close();
	}
})