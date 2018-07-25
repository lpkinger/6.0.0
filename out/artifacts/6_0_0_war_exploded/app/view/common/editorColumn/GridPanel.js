Ext.util.Sorter.prototype.defaultSorterFn=function(d, c) {
		var b = this, a = b.transform, g = b.getRoot(d)[b.property], e = b.getRoot(c)[b.property];
		if (a) {
	       g = a(g);
	       e = a(e)
		}
		if (typeof(g) == "string") {  
            return g.localeCompare(e);  
        }  
       return g > e ? 1 : (g < e ? -1 : 0)
   };
Ext.define('erp.view.common.editorColumn.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpEditorColumnGridPanel',
	id: 'editorColumnGridPanel', 
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true,
	store: [],
	columns: [],
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	bodyStyle:'background-color:#f1f1f1;',
	multiselected: new Array(),
	features : [Ext.create('Ext.grid.feature.Grouping',{
		groupHeaderTpl: '{name} (Count:{rows.length})'
	})],
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		ignoreRightMouseSelection : false,
		checkOnly: true,
		listeners:{
	        selectionchange:function(selModel, selected, options){
	        	selModel.view.ownerCt.ownerCt.summary(true);
	        }
	    },
		getEditor: function(){
			return null;
		}
	}),
	caller: null,
	condition: null,
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	initComponent : function(){
		// 额外的plugin
		if(this.pluginConfig)
			this.plugins = Ext.Array.merge(this.plugins, this.pluginConfig);
		this.getGridColumnsAndStore(this.condition || condition);
		this.addEvents({
			storeloaded: true
		});
		this.callParent(arguments); 
	},
	getEffectData: function(){
		var grid = this;
		var items = grid.selModel.getSelection();
		Ext.each(items, function(item, index){
			if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
				&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
				grid.multiselected.push(item);
			}
		});
		var records = this.unique(grid.multiselected);
		if(records.length > 0){
			var data = new Array();
			Ext.each(records, function(record, index){
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
					&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0)){
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			return data;
		}
	},
	unique: function(items) {
		var d = new Object();
		Ext.Array.each(items, function(item){
			d[item.id] = item;
		});
		return Ext.Object.getValues(d);
	},
	updateAction: function(url){
		var grid = this, btn = parent.Ext.getCmp('win').down('button[name=confirm]');
		var data = grid.getEffectData();
		if(data != null){
			grid.setLoading(true);
			Ext.Ajax.request({
				url : basePath + url,
				params: {
					caller: caller,
					data: Ext.encode(data)
				},
				method : 'post',
				async: false,
				callback : function(options,success,response){
					grid.setLoading(false);
					btn.setDisabled(false);
					grid.multiselected = new Array();
					var localJson = new Ext.decode(response.responseText);
					if(localJson.exceptionInfo){
						showError(localJson.exceptionInfo);
						return "";
					}
					if(localJson.success){
						if(localJson.log){
							showMessage("提示", localJson.log);
						}
						Ext.Msg.alert("提示", "处理成功!", function(){
							window.location.href = window.location.href;
						});
					}
				}
			});
		} else {
			btn.setDisabled(false);
		}
	},
	reloadData: function(condition, callback) {
		var grid = this;
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'common/singleGridPanel.action',
			params: {
				caller: this.caller || caller,
				condition: condition
			},
			method : 'post',
			callback : function(opt, s, resp){
				grid.setLoading(false);
				var res = new Ext.decode(resp.responseText);
				callback && callback.call(null, res.data ? 
						Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : []);
			}
		});
	},
	getGridColumnsAndStore: function(condition){
		var grid = this;
		grid.setLoading(true);
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + 'common/singleGridPanel.action',
			params: {
				caller: this.caller || caller,
				condition: condition
			},
			method : 'post',
			async: false,
			callback : function(options,success,response){
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				var data = [];
				if(!res.data || res.data.length == 2){
					grid.GridUtil.add10EmptyData(grid.detno, data);
					grid.GridUtil.add10EmptyData(grid.detno, data);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
				}
				if(grid.columns && grid.columns.length > 2){
					grid.store.loadData(data);
					if(grid.normalGrid){
						grid.normalGrid.on('scrollershow',function(scroller){
							if (scroller && scroller.scrollEl) {
								scroller.clearManagedListeners();  
								scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
							}
						});
					}
					grid.fireEvent('scrollershow', grid.verticalScroller, 'vertical');
				} else {
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
							/**
							 *****如果有固定咧，grid不能分组***** 
							 */
						});
						//store
						var store = Ext.create('Ext.data.Store', {
							storeId: 'gridStore',
							fields: res.fields,
							data: data,
							groupField: grid.groupField,
							listeners:{
			                	'update':grid.syncSummaryData,
			                	'remove':grid.syncSummaryData
			                } 
						});
						if(grid.selModel.views == null){
							grid.selModel.views = [];
						}
						if(res.dbfinds.length > 0){
							grid.dbfinds = res.dbfinds;
						}
						grid.columns = res.columns;
						if(grid.autoRowNumber) {
							Ext.Array.insert(grid.columns, 0, [{xtype: 'rownumberer', width: 35, locked: true, cls: 'x-grid-header-1'}]);
						}
						
						if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				grid.GridUtil.setToolbar(grid, grid.columns, grid.necessaryField, []);
            			}
						grid.store = store;
						//grid.fireEvent('storeloaded', grid, store);
					}
				}
			}
		});
	},
	viewConfig: {// 显示分仓库库存
		listeners: {
			render: function(view) {
				var prodfield = Ext.getCmp('editorColumnGridPanel').getProdField();
				if(prodfield && !view.tip) {
					view.tip = Ext.create('Ext.tip.ToolTip', {
						target: view.el,
						delegate: view.itemSelector,
						trackMouse: true,
						renderTo: Ext.getBody(),
						listeners: {
							beforeshow: function updateTipBody(tip) {
								var record = view.getRecord(tip.triggerElement),
								grid = view.ownerCt.ownerCt;
								if(record && grid.productwh) {
									var c = record.get(prodfield), pws = new Array();
									Ext.each(grid.productwh, function(d){
										if(d.PW_PRODCODE == c) {
											pws.push(d);
										}
									});
									tip.down('grid').setTitle(c);
									tip.down('grid').store.loadData(pws);
								}
							}
						},
						items: [{
							xtype: 'grid',
							width: 300,
							columns: [{
								text: '仓库编号',
								cls: 'x-grid-header-1',
								dataIndex: 'PW_WHCODE',
								width: 80
							},{
								text: '仓库名称',
								cls: 'x-grid-header-1',
								dataIndex: 'WH_DESCRIPTION',
								width: 120
							},{
								text: '库存',
								cls: 'x-grid-header-1',
								xtype: 'numbercolumn',
								align: 'right',
								dataIndex: 'PW_ONHAND',
								width: 90
							}],
							columnLines: true,
							title: '物料分仓库存',
							store: new Ext.data.Store({
								fields: ['PW_WHCODE', 'WH_DESCRIPTION', 'PW_ONHAND'],
								data: [{}]
							})
						}]
					});
				}
			}
		}
	},
	getProdField : function() {
		var f = null;
		switch (caller){
		case 'SendNotify!ToProdIN!Deal' ://通知单转出货
			f = 'snd_prodcode';
			break;
			/*case 'Sale!ToAccept!Deal' ://订单转出货
			f = 'sd_prodcode';
			break;*/
		}
		return f;
	},
	/**同步汇总数据*/
     syncSummaryData:function(store,record,operation){
    	 var g = Ext.getCmp('editorColumnGridPanel');
    	 g.summary(true);
     },
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(onlySelected){
		var me = this,store = this.store, items = store.data.items, selected = me.selModel.getSelection(), 
			value, bar = (onlySelected ? me.down('toolbar[to=select]') : me.down('erpToolbar'));
		Ext.each(me.columns, function(c){
			if (onlySelected && !bar)
				bar = me.addDocked({
			    	xtype: 'toolbar',
			    	dock: 'bottom',
			    	to: 'select',
			    	items: [{
			    		xtype: 'tbtext',
			    		text: '已勾选',
			    		style: {
			    			marginLeft: '6px'
			    		}
			    	}]
			    })[0];
			if(c.summaryType == 'sum'){
				me.updateSummary(c, me.getSum(onlySelected ? selected : items, c.dataIndex), 'sum', bar);
			} else if(c.summaryType == 'count'){
                me.updateSummary(c, (onlySelected ? selected.length : items.length), 'count', bar);
			}
		});
		if (bar) {
			var counter = bar.down('tbtext[itemId=count]');
			if (!counter) {
				bar.add('->');
				counter = bar.add({
					xtype: 'tbtext',
					itemId: 'count'
				});
			}
			counter.setText(onlySelected ? ('已选: ' + selected.length + ' 条' ) : ('共: ' + items.length + ' 条'));
		}
	},
	updateSummary: function(column, value, type, scope) {
		var id = column.dataIndex + '_' + type + (scope.to == 'select' ? '_select' : '');
		id=id.replace(/,/g,'$');
		b = scope.down('tbtext[id=' + id + ']');
		if (!b) {
			scope.add('-');
			b = scope.add({xtype: 'tbtext', id: id});
		}
		if(column.xtype == 'numbercolumn') {
			value = Ext.util.Format.number(value, (column.format || '0,000.000'));
		}
		b.setText(column.text + '(' + type + '):' + value);
	},
	getSum: function(records, field) {
        var total = 0,
            i = 0,
            len = records.length;
        (len == 0) && (records = this.store.data.items); 
        for (; i < len; ++i) {
			total += records[i].get(field);
		}
        return total;
	}
});