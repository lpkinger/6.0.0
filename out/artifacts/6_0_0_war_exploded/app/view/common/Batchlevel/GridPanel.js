Ext.define('erp.view.common.Batchlevel.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealGridPanel',
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		//startCollapsed: true,
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
	            //fieldData,
	            //key,
	            comp;
            //将feature的data打印在toolbar上面
	        for (i = 0, length = columns.length; i < length; ++i) {
	            comp = Ext.getCmp(columns[i].id);
	            data[comp.id] = me.getSummary(store, comp.summaryType, comp.dataIndex, false);
	            var tb = Ext.getCmp(columns[i].dataIndex + '_' + comp.summaryType);
	            if(tb){
	            	tb.setText(tb.text.split(':')[0] + ':' + data[comp.id]);
	            }
	        }
	        return data;
        }
    }],
    selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
			listeners:{
	            selectionchange:function(selectionModel, selected, options){
	          
	            }
	        },
	        getEditor: function(){
	        	return null;
	        },
	        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
	        	var me = Ext.getCmp('batchDealGridPanel');
	        	var bool = true;
	        	var items = me.selModel.getSelection();
	            Ext.each(items, function(item, index){
	            	if(this.index == record.index){
	            		bool = false;
	            		me.selModel.deselect(record);
	            		Ext.Array.remove(items, item);
	            		Ext.Array.remove(me.multiselected, record);
	            	}
	            });
	            Ext.each(me.multiselected, function(item, index){
	            	items.push(item);
	            });
	            me.selModel.select(items);
	        	if(bool){
	        		view.el.focus();
		        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
		        	if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
		        		me.multiselected.push(record);
		        		items.push(record);
		        		me.selModel.select(items);
		        	} else {
		        		me.selModel.deselect(record);
		        		Ext.Array.remove(me.multiselected, record);
		        	}
	        	}
	        	me.summary();
	        },
	        onHeaderClick: function(headerCt, header, e) {
	            if (header.isCheckerHd) {
	                e.stopEvent();
	                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	                if (isChecked) {
	                    this.deselectAll(true);
	                    var grid = Ext.getCmp('batchDealGridPanel');
	                    this.deselect(grid.multiselected);
	                    grid.multiselected = new Array();
	                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                } else {
	                	var grid = Ext.getCmp('batchDealGridPanel');
	                	this.deselect(grid.multiselected);
		                grid.multiselected = new Array();
		                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    this.selectAll(true);
	                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                }
	            }
	            Ext.getCmp('batchDealGridPanel').summary();
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
		var gridParam = {caller: caller, condition: condition};
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
    	this.callParent(arguments);
    	this.initRecords();
	},
	getMultiSelected: function(){
		var grid = this;
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(!grid.keyField) {
        		grid.multiselected.push(item);
        	} else {
        		if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
            		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
            		grid.multiselected.push(item);
            	}
        	}
        });
		return Ext.Array.unique(grid.multiselected);
	},
	getGridColumnsAndStore: function(grid, url, param, no){
		var me = this;
		var main = parent.Ext.getCmp("content-panel");
		if(main){
			main.getActiveTab().setLoading(true);//loading...
		}
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		if(main){
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(me.columns && me.columns.length > 2){
        			var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        			me.store.loadData(data);
        			me.initRecords();
        			cmp = Ext.getCmp('pr_level');
        			var arr;
        			if(!cmp){
        				cmp = Ext.getCmp('bo_style');
        				/*arr = Ext.Array.unique(Ext.Array.pluck(data, 'bo_style'));
        				cmp.checkValue(Ext.Array.concate(arr, ','));*/
        			}else{
        				arr = Ext.Array.unique(Ext.Array.pluck(data, 'pr_level'));
            			cmp.checkValue(Ext.Array.concate(arr, ','));
        			}
        		} else {
        			if(res.columns){
        				Ext.each(res.columns, function(column, y){
        					//render
        					me.setRenderer(column);
        					//logictype
        					var logic = column.logic;
        					if(logic != null){
        						me.setLogicType(column, logic, y);
        					}
        				});
            			//store
            			me.store = me.setDefaultStore(res.data, res.fields);
            			//view
                		if(me.selModel.views == null){
                			me.selModel.views = [];
                		}
                		if(res.dbfinds.length > 0){
                			me.dbfinds = res.dbfinds;
                		}
        				//toolbar
                		me.setToolbar(res.columns);
                		//reconfigure store&columns
                		me.columns = res.columns;
            		}
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
    		column.haveRendered = true;
    	}
	},
	setLogicType: function(column, logic, y){
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
		} else if(logic == 'groupField'){
			grid.groupField = column.dataIndex;
		}
	},
	setToolbar: function(columns){
		var grid = this;
		var items = [];
		Ext.each(columns, function(column){
			if(column.summaryType == 'sum'){
				items.push('-',{
					id: column.dataIndex + '_sum',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.header + '(sum):0'
				});
			} else if(column.summaryType == 'average') {
				items.push('-',{
					id: column.dataIndex + '_average',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.header + '(average):0'
				});
			} else if(column.summaryType == 'count') {
				items.push('-',{
					id: column.dataIndex + '_count',
					itemId: column.dataIndex,
					xtype: 'tbtext',
					text: column.header + '(count):0'
				});
			}
		});
		grid.bbar = {
			xtype: 'toolbar',
	        dock: 'bottom',
	        items: items
		};
	},
	setDefaultStore: function(d, f){
		var me = this;
		var data = [];
		if(!d || d.length == 2){
			me.GridUtil.add10EmptyData(me.detno, data);
			me.GridUtil.add10EmptyData(me.detno, data);
		} else {
			data = Ext.decode(d.replace(/,}/g, '}').replace(/,]/g, ']'));
		}
		var store = Ext.create('Ext.data.Store', {
		    fields: f,
		    data: data,
		    groupField: me.groupField,
		    getSum: function(field) {
	            var records = me.selModel.getSelection(),
	            	total = 0,
	                i = 0,
	                len = records.length;
	            for (; i < len; ++i) {
	            	total += records[i].get(field);
	            }
	            return total;
		    },
		    getCount: function() {
		    	var records = me.selModel.getSelection(),
		    		count = 0;
		    	Ext.each(records, function(item){
		    		count++;
		    	});
		        return count;
		    },
		    getAverage: function(field) {
		    	var records = me.selModel.getSelection(),
		    		count = 0,
		    		sum = 0;
		    	Ext.each(records, function(item){
		    		if(item.data[me.necessaryField] != null && item.data[me.necessaryField] != ''){
		    			count++;sum += item.data[field];
		    		}
		    	});
		        return Ext.Number.format(sum/count, '0.00');
		    }
		});
		return store;
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(){
		var me = this,
			store = this.store,
			value;
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = store.getSum(c.dataIndex);
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.header + '(sum):' + value);		
			} else if(c.summaryType == 'count'){
                value = store.getCount();
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.header + '(count):' + value);			
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);        		        			
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.header + '(average):' + value);
			}		
		});
	},
	initRecords: function(){
		var records = this.store.data.items;
		var count = 0;
		Ext.each(records, function(record){
			if(!record.index){
				record.index = count++;
			}
		});
	}
});