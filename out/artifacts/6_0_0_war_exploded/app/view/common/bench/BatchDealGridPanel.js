Ext.define('erp.view.common.bench.BatchDealGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    dockedItems: [{xtype: 'erpToolbar', dock: 'bottom', enableAdd: false, enableDelete: false, enableCopy: false, enablePaste: false, enableUp: false, enableDown: false}],
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    headerCt: Ext.create("Ext.grid.header.Container",{
 	    forceFit: false,
        sortable: true,
        enableColumnMove:true,
        enableColumnResize:true,
        enableColumnHide: true
     }),
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		hideGroupedHeader: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    }),{
        ftype : 'summary',
        showSummaryRow : false,//不显示默认合计行
        generateSummaryData: function(){
	        return {};
        }
    }],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	checkOnly : true,
		ignoreRightMouseSelection : false,
		listeners:{
	        selectionchange:function(selModel, selected, options){
	        	selModel.view.ownerCt.summary(true);
	        	selModel.view.ownerCt.selectall = false;
	        }
	    },
	    getEditor: function(){
	    	return null;
	    },
	    onHeaderClick: function(headerCt, header, e) {
	        if (header.isCheckerHd) {
	            e.stopEvent();
	            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
	                this.deselectAll(true);
	            } else {
	                this.selectAll(true);
	                this.view.ownerCt.selectall = true;
	            }
	        }
	    }
	}),
	initComponent : function(){ 
	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    this.RenderUtil = Ext.create('erp.util.RenderUtil');
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
    	this.addEvents({
		    storeloaded: true
		});
    	this.callParent(arguments);
    	this.initRecords();
	},
	sync: true,
	getMultiSelected: function(){
		var grid = this;
		grid.multiselected = [];
        var items = grid.selModel.getSelection();
        if(grid.selectall && items.length == grid.store.pageSize && grid.store.prefetchData) {
        	items = grid.store.prefetchData.items;
        }
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		return grid.multiselected;
	},
	unique: function(items) {
		var d = new Object();
		Ext.Array.each(items, function(item){
			d[item.id] = item;
		});
		return Ext.Object.getValues(d);
	},
	getGridColumnsAndStore: function(grid, url, sync){
		var me = this;
		var condition = this.defaultCondition + me.getOrderBy();
		var param = {caller: caller, condition: condition, _config:getUrlParam('_config'), _noc: getUrlParam('_noc')};
		if(!url){
			url = 'common/singleGridPanel.action';
		}
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: sync?false:true,
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			var form = me.previousSibling('form');
        			var sort = me.getOrderBy()||form.fo_detailGridOrderBy;
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
    					limitArr = Ext.Array.pluck(limits, 'lf_field');
    				}
        			Ext.each(res.columns, function(column, y){
        				if(column.xtype=='textareatrigger'){
        					column.xtype='';
        					column.renderer='texttrigger';
        				}
        				// column有取别名
        				if(column.dataIndex.indexOf(' ') > -1) {
        					column.dataIndex = column.dataIndex.split(' ')[1];
        				}
        				//power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        				}
        				//renderer
        				me.setRenderer(column);
        				//logictype
        				me.setLogicType(column, column.logic,  {headerColor: res.necessaryFieldColor},y);
        				if (column.logic == 'necessaryField') {
							column.style = 'color:rgb(191, 60, 60);';  /*color:#fb3c3c*/
						}
        			});
        			//data
            		var data =  Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		if(data.length==0){
            			if(sync){
	            				Ext.Msg.alert('提示','选择的数据没有符合'+getUrlParam('operation')+'操作的条件！',function(){
	            				var main = parent.Ext.getCmp("content-panel"); 
					    		if(main){
					    			main.getActiveTab().close();
					    		}else parent.Ext.getCmp('win').close();
	            			})
            			}else{
            				var main = parent.Ext.getCmp("content-panel"); 
				    		if(main){
				    			main.getActiveTab().close();
				    		}else parent.Ext.getCmp('win').close();
            			}
            		}
            		//store
            		var store = me.GridUtil.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
            		//sort
            		if(!sort){
            			store.sort(me.getSort());
            		}
            		//view
            		if(grid.selModel && grid.selModel.views == null){
            			grid.selModel.views = [];
            		}
            		//dbfind
            		if(res.dbfinds && res.dbfinds.length > 0){
            			grid.dbfinds = res.dbfinds;
            		}
            		//reconfigure
            		if(grid.sync) {//同步加载的Grid
            			grid.reconfigure(store, res.columns);
            			grid.on('afterrender', function(){
            				me.GridUtil.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
            				grid.summary();
            			});
            		} else {
            			//toolbar
            			if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				me.GridUtil.setToolbar(grid, res.columns, grid.necessaryField, limitArr);
            			}
            			grid.reconfigure(store, res.columns);
            			grid.summary();
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
        		if(sync){
        			grid.selModel.selectAll();
        		}
        	}
        });
	},
	setRenderer: function(column){
		var grid = this;
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
    		column.renderName=renderName;
    		column.haveRendered = true;
    	}
	},
	setLogicType: function(column, logic, headerCss,y){
		var grid = this;
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
			if(headerCss.headerColor)
				column.style = 'color:#' + headerCss.headerColor;
		} else if(logic == 'groupField'){
			grid.groupField = column.dataIndex;
		}
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(onlySelected){
		var me = this,
			store = this.store, items = store.data.items, selected = me.selModel.getSelection(), 
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
	initRecords: function(){
		var records = this.store.data.items;
		var count = 0;
		Ext.each(records, function(record){
			if(!record.index){
				record.index = count++;
			}
		});
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
	},
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	viewConfig: {// 显示分仓库库存
		listeners: {
			render: function(view) {
				var prodfield = view.ownerCt.getProdField();
				if(prodfield && !view.tip) {
					view.tip = Ext.create('Ext.tip.ToolTip', {
				        target: view.el,
				        delegate: view.itemSelector,
				        trackMouse: true,
				        renderTo: Ext.getBody(),
				        listeners: {
				            beforeshow: function updateTipBody(tip) {
				            	var record = view.getRecord(tip.triggerElement),
				            		grid = view.ownerCt;
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
		case 'Sale!ToAccept!Deal' ://订单转出货
			f = 'sd_prodcode';
			break;
		}
		return f;
	},
	getOrderBy: function(){
		var grid = this;
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
	getSort: function(store){
		var grid = this;
		var ob = new Array();
		if(grid.mainField) {
			ob.push({'property':grid.mainField,'direction':'desc'});
		}
		if(grid.detno) {
			ob.push({'property':grid.detno,'direction':'asc'});
		}
		if(grid.keyField) {
			ob.push({'property':grid.keyField,'direction':'desc'});
		}
		return ob;
	}
});