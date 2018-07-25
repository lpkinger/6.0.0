Ext.define('erp.view.crm.chance.ProcessGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.processgrid',
	layout: 'fit', 
	id:'processgrid',
	hideBorders: true,
	store: [],
	columns: [],
	columnLines:true,
	autoScroll:true,
	viewConfig: {
		stripeRows: true
	},
	initComponent : function(){ 
		var me=this,param=new Object();
		param.condition='1=1';
		me.getGridColumnsAndStore(this, 'crm/business/getProcessInfoByCondition.action', param, 1)
		me.callParent(arguments); 
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		grid.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + url,
			params: param,
			async: (grid.sync ? false : true),
			method : 'get',
			callback : function(options,success,response){
				grid.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				if(res.columns){
					grid.stages=res.stages;
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
						if(column.logic=='process'){
							column.renderer=function(val,meta,record){								
								var rendercolor=val?me.getStageColor(grid,column.text):'';
								meta.tdCls='x-grid-cell-process';
								var detno=column.dataIndex.substring(6);
								return '<div style="width: 100%; height:16px; margin:5px 0px 5px 0px;font-family: Verdana,sans-serif;padding-top:3px;padding-left:2px;  font-size: 11px;vertical-align: top; background-color: #'+rendercolor+'; line-height: 10px; color:white; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">'+val+'</div>';
							}
						}
						me.setLogicType(grid, column, {
							headerColor: res.necessaryFieldColor
						});
					});
					//data
					var data = [];
					if(!res.data || res.data.length == 2){
						if (grid.buffered) {
							me.add10EmptyData(grid.detno, data);
							me.add10EmptyData(grid.detno, data);//添加20条空白数据            				
						} else {
							grid.on('reconfigure', function(){// 改为Grid加载后再添加空行,节约200~700ms
								me.add10EmptyItems(grid, 40, false);
							});
						}
					} else {
						data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
					}
					//store
					var store = me.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
					if(grid.selModel && grid.selModel.views == null){
						grid.selModel.views = [];
					}
					//dbfind
					if(res.dbfinds && res.dbfinds.length > 0){
						grid.dbfinds = res.dbfinds;
					}
					console.log(res.columns);
					//reconfigure
					if(grid.sync) {//同步加载的Grid
						grid.reconfigure(store, res.columns);
						grid.on('afterrender', function(){
							me.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
						});
					} else {
						grid.reconfigure(store, res.columns);
					}
					if(grid.buffered) {//缓冲数据的Grid
						grid.verticalScroller = Ext.create('Ext.grid.PagingScroller', {
							activePrefetch: false,
							store: store
						});
						store.guaranteeRange(0, Math.min(store.pageSize, store.prefetchData.length) - 1);
					}
					var form = Ext.ComponentQuery.query('form');
					if(form && form.length > 0){ 
						grid.readOnly = form[0].readOnly;//grid不可编辑
					}
				} 
			}
		});
	},
	setRenderer: function(grid, column){
		if(!column.haveRendered && column.renderer != null && column.renderer != ""){
			if(!grid.RenderUtil){
				grid.RenderUtil = Ext.create('erp.util.RenderUtil');
			}
			var renderName = column.renderer;
			if(contains(column.renderer, ':', true)){
				var args = new Array();
				Ext.each(column.renderer.split(':'), function(a, index){
					if(index == 0){
						renderName = a;
					} else {
						args.push(a);
					}
				});
				if(!grid.RenderUtil.args[renderName]){
					grid.RenderUtil.args[renderName] = new Object();
				}
				grid.RenderUtil.args[renderName][column.dataIndex] = args;
			}
			column.renderer = grid.RenderUtil[renderName];
			column.haveRendered = true;
		}
	},
	setLogicType: function(grid, column, headerCss){
		var logic = column.logic;
		if(logic != null){
			if(logic == 'detno'){
				grid.detno = column.dataIndex;
				column.width = 40;
				column.align = 'center';
				column.renderer = function(val, meta) {
					meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
					return val;
				};
			} else if(logic == 'keyField'){
				grid.keyField = column.dataIndex;
			} else if(logic == 'mainField'){
				grid.mainField = column.dataIndex;
			}else if(logic == 'orNecessField'){
				if(!grid.orNecessField){
					grid.orNecessField = new Array();
				}
				grid.orNecessField.push(column.dataIndex);


			}else if(logic == 'necessaryField'){
				grid.necessaryField = column.dataIndex;
				if(!grid.necessaryFields){
					grid.necessaryFields = new Array();
				}
				grid.necessaryFields.push(column.dataIndex);
				if(!column.haveRendered){
					column.renderer = function(val, meta, record, x, y, store, view){
						var c = this.columns[y];
						if(val != null && val.toString().trim() != ''){
							if(c.xtype == 'datecolumn' && typeof val === 'object'){
								val = Ext.Date.format(val, 'Y-m-d');
							} else if(c.xtype == 'numbercolumn') {
								val = Ext.util.Format.number(val, c.format || '0,000.00');
							}
							return val;
						} else {
							if(c.xtype == 'datecolumn'){
								val = '';
							}
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
							'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
					};
				}
				if(headerCss.headerColor)
					column.style = 'color:#' + headerCss.headerColor;
			} else if(logic == 'groupField'){
				grid.groupField = column.dataIndex;
			}
		}
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
	getStageColor:function(grid,title){
		var c;
		Ext.Array.each(grid.stages,function(s){
			if(s['bs_name']==title) c=s['bs_color'];
		});		
		return c;
	},
	loadNewData:function(grid,condition){
			var grid = Ext.getCmp('processgrid');
			grid.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'crm/business/getProcessDataByCondition.action',
				params: {
					condition: condition
				},
				method:'get',
				callback: function(opt, s, r) {
					var res = new Ext.decode(r.responseText);
					if(grid && res.data) {
						grid.store.loadData(Ext.decode(res.data));
						grid.setLoading(false);
					}
				}	
			 });
	}
});