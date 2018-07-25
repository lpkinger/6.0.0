Ext.define('erp.view.crm.chance.ChanceGrid',{    
	extend: 'Ext.grid.Panel', 
	columns:[],
	id:'chancegrid',
	alias: 'widget.chancegrid',
	columnLines: true,
	viewConfig: {
        stripeRows: true,
        enableTextSelection: true//允许选中文字
    },
	frame: true,
	GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', this.params);
		this.callParent(arguments);
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		var param={
				caller:this.caller,
				condition:'1=1'
		};
		//this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', params, "");
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + url,
			params: param,
			async: (grid.sync ? false : true),
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}

				if(res.columns){
					var limits = res.limits, limitArr = new Array();
					if(limits != null && limits.length > 0) {//权限外字段
						limitArr = Ext.Array.pluck(limits, 'lf_field');
					}
					Ext.each(res.columns, function(column, y){
						// column有取别名
						if(column.dataIndex.indexOf(' ') > -1) {
							column.dataIndex = column.dataIndex.split(' ')[1];
						}
						//power
						if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
							column.hidden = true;
						}
						if(column.xtype=='checkcolumn') delete column['renderer'];
					});
					//data
					var data = [];
					if(res.data && res.data.length > 2){
						data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					}
					//store
					var store = me.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
					//view
					if(grid.selModel && grid.selModel.views == null){
						grid.selModel.views = [];
					}
					//dbfind
					if(res.dbfinds && res.dbfinds.length > 0){
						grid.dbfinds = res.dbfinds;
					}
					if(grid.sync) {//同步加载的Grid
						grid.reconfigure(store, res.columns);
						/*		grid.on('afterrender', function(){
            				me.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
            			});*/
					} else {
						grid.reconfigure(store, res.columns);
					}
				} else {
					grid.hide();
				}
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
	}
})