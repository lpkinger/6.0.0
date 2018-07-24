Ext.define('erp.view.common.tempStore.TempStoreGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpTempStoreGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'tempStoreGridPanel', 
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
            var me = this,
	            data = {},
	            store = me.view.store,
	            columns = me.view.headerCt.getColumnsForTpl(),
	            i = 0,
	            length = columns.length,
	            comp, value;
            //将feature的data打印在toolbar上面
	        for (i = 0, length = columns.length; i < length; ++i) {
	            comp = Ext.getCmp(columns[i].id);
	            data[comp.id] = me.getSummary(store, comp.summaryType, comp.dataIndex, false);
	            var tb = Ext.getCmp(columns[i].dataIndex + '_' + comp.summaryType);
	            if(tb){
	            	value = data[comp.id];
	            	if(comp.xtype == 'numbercolumn') {
	        			value = Ext.util.Format.number(value, (comp.format || '0,000.000'));
	        		}
	            	tb.setText(tb.text.split(':')[0] + ':' + value);
	            }
	        }
	        return data;
        }
    }],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	checkOnly : true,
		ignoreRightMouseSelection : false,
		listeners:{
	        selectionchange:function(selModel, selected, options){
	        	selModel.view.ownerCt.summary();
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
		var gridParam = {caller: caller, condition: ""};
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "",true);
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
	getGridColumnsAndStore: function(grid, url, param, no,sync){
		var me = this;
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
        			});
        			var checkdata=[];
        			var d1=parent.Ext.getCmp('batchDealGridPanel').tempStore;//暂存区数据
        			Ext.each(d1,function(d){
			    		var keys=Ext.Object.getKeys(d);
						Ext.each(keys, function(k){
							checkdata.push(d[k].data);
						});
			    	});
        			//data
            		var data = checkdata;
            		//store
            		var store = me.GridUtil.setStore(grid, res.fields, data, grid.groupField, grid.necessaryField);
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
            		}
            		grid.readOnly =true;
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
	setToolbar: function(columns){
		var grid = this;
		var items = [];
		var bool = true;
		Ext.each(grid.dockedItems.items, function(item){
			if(item.dock == 'bottom' && item.items){//bbar已存在
				bool = false;
			}
		});
		if(bool){
			Ext.each(columns, function(column){
				if(column.summaryType == 'sum'){
					items.push('-',{
						id: column.dataIndex + '_sum',
						itemId: column.dataIndex,
						xtype: 'tbtext',
						text: column.text + '(sum):0'
					});
				} else if(column.summaryType == 'average') {
					items.push('-',{
						id: column.dataIndex + '_average',
						itemId: column.dataIndex,
						xtype: 'tbtext',
						text: column.text + '(average):0'
					});
				} else if(column.summaryType == 'count') {
					items.push('-',{
						id: column.dataIndex + '_count',
						itemId: column.dataIndex,
						xtype: 'tbtext',
						text: column.text + '(count):0'
					});
				}
			});
			grid.addDocked({
	    			xtype: 'toolbar',
	    	        dock: 'bottom',
	    	        items: items
	    	});
		}else{
			var bars = Ext.ComponentQuery.query('erpToolbar');
			if(bars.length > 0){
				Ext.each(columns, function(column){
        			if(column.summaryType == 'sum'){
        				bars[0].add('-');
        				bars[0].add({
        					id: column.dataIndex + '_sum',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.text + '(sum):0'
        				});
        			} else if(column.summaryType == 'average') {
        				bars[0].add('-');
        				bars[0].add({
        					id: column.dataIndex + '_average',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.text + '(average):0'
        				});
        			} else if(column.summaryType == 'count') {
        				bars[0].add('-');
        				bars[0].add({
        					id: column.dataIndex + '_count',
        					itemId: column.dataIndex,
        					xtype: 'tbtext',
        					text: column.text + '(count):0'
        				});
        			}
        		});
			}
		}		
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(){
		var me = this,
			store = this.store, items = store.data.items, selected = me.selModel.getSelection(), 
			value, m = me.down('erpToolbar'), n = me.down('toolbar[to=select]');	
		Ext.each(me.columns, function(c){
			if(c.summaryType && !n)
				n = me.addDocked({
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
				me.updateSummary(c, me.getSum(items, c.dataIndex), 'sum', m);
				me.updateSummary(c, me.getSum(selected, c.dataIndex), 'sum', n);
			} else if(c.summaryType == 'count'){
                me.updateSummary(c, items.length, 'count', m);
                me.updateSummary(c, selected.length, 'count', n);
			}
		});
		if(n) {
			var count = n.down('tbtext[id=selected-count]');
			if(!count) {
				n.add('->');
				count = n.add({
					xtype: 'tbtext',
					id: 'selected-count'
				});
			}
			count.setText('共: ' + items.length + ' 条, 已选: ' + selected.length + ' 条');
		}
	},
	updateSummary: function(column, value, type, scope) {
		var id = column.dataIndex + '_' + type + (scope.to == 'select' ? '_select' : ''), 
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
	}
});