/**
 * ERP项目gridpanel通用样式6，针对成套发料4.2
 */
Ext.define('erp.view.core.grid.Panel6',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel6',
	layout : 'fit',
	id: 'grid',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    condition:null,
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
    plugins:[ Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    selType: 'cellmodel',
    caller: null,
	initComponent : function(){ 
	    var me = this, condition = me.condition || '';
	    if(typeof me.getCondition === 'function'){
	    	condition = me.getCondition.call(null, me);
	    	me.condition=me.condition||condition;
	    }
    	var gridParam = {caller: this.caller || caller, condition: condition};
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments); 
		if(!this.boxready) {
			if(this.allowExtraButtons)// 加载其它按钮
				this.on('reconfigure', this.loadExtraButton, this, {single: true, delay: 1000});
			this.on('summary', this.generateSummaryData, this, {single: true, delay: 1000});
		}
	},
	generateSummaryData : function() {
		var store = this.store,
		columns = this.columns, s = this.features[this.features.length - 1],
		i = 0, length = columns.length, comp, bar = this.down('erpToolbar');
		if (!bar) return;
		var limitArr=this.limitArr;
		//将feature的data打印在toolbar上面
		for (; i < length; i++ ) {
			comp = columns[i];
			if((limitArr.length == 0 || !Ext.Array.contains(limitArr, comp.dataIndex))&&comp.summaryType) {
				var tb = Ext.getCmp(comp.dataIndex + '_' + comp.summaryType);
				if(!tb){
					bar.add('-');
					tb = bar.add({
						id: comp.dataIndex + '_' + comp.summaryType,
						itemId: comp.dataIndex,
						xtype: 'tbtext'
					});
				}
				var val = s.getSummary(store, comp.summaryType, comp.dataIndex, false);
				if(comp.xtype == 'numbercolumn') {
					val = Ext.util.Format.number(val, (comp.format || '0,000.000'));
				}
				tb.setText(comp.text + ':' + val);
			}
		}   	
	},
	loadExtraButton: function() {
		var me = this;
		Ext.Ajax.request({
			url : basePath + "common/gridButton.action",
			params: {
				caller: me.caller
			},
			method : 'post',
			async: false,
			callback : function(options, success, response){
				var r = new Ext.decode(response.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				}
				if(r.buttons){
					var buttons = Ext.decode(r.buttons), tb = me.down('toolbar');
					if(tb) {
						Ext.each(buttons, function(b){
							try {
								tb.add({
									xtype: b.xtype, 
									disabled: true,
									style: {
										marginLeft: '0'
									}
								});
							} catch(e) {
								tb.add({
									text: $I18N.common.button[b.xtype],
									id: b.xtype,
									cls: 'x-btn-gray',
									disabled: true
								});
							}
						});
					}
				}
			}
		});
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		grid.setLoading(true);
		if(!param._config) param._config=getUrlParam('_config');
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	async: false,
        	method : 'post',
        	callback : function(options,success,response){
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = [];
				if(!res.data || res.data.length == 2){
					grid.add10EmptyData(grid.detno, data);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
				}
				if(res.columns){
					Ext.each(res.columns, function(column, y){
						//render
						if(!column.haveRendered && column.renderer != null && column.renderer != ""){
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
						//logictype
						var logic = column.logic;
						if(logic != null){
							if(logic == 'detno'){
								grid.detno = column.dataIndex;
							} else if(logic == 'keyField'){
								grid.keyField = column.dataIndex;
							} else if(logic == 'mainField'){
								grid.mainField = column.dataIndex;
							} else if(logic == 'necessaryField'){
								grid.necessaryField = column.dataIndex;
								if(!grid.necessaryFields){
									grid.necessaryFields = new Array();
								}
								grid.necessaryFields.push(column.dataIndex);
								if(!column.haveRendered){
									column.renderer = function(val, meta, record, x, y, store, view){
										var c = this.columns[y];
										if(val != null && val.toString().trim() != ''){
											if(c.xtype == 'datecolumn'){
												val = Ext.Date.format(val, 'Y-m-d');
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
							} else if(logic == 'groupField'){
								grid.groupField = column.dataIndex;
							}
						}
					});
					var store = Ext.create('Ext.data.Store', {
						storeId: 'gridStore',
				    	fields: res.fields,
				        data: data
				    });
					if(grid.selModel && grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
					if(res.dbfinds.length > 0){
						grid.dbfinds = res.dbfinds;
					}
					grid.columns = res.columns;
					if(grid.autoRowNumber) {
						Ext.Array.insert(grid.columns, 0, [{xtype: 'rownumberer', width: 35, locked: true, cls: 'x-grid-header-1'}]);
					}
					grid.store = store;
					//grid.fireEvent('storeloaded', grid, store);
				}
			}
        });
	},
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<20;i++){
				var o = new Array();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<20;i++){
				var o = new Array();
				data.push(o);
			}
		}
	},
});