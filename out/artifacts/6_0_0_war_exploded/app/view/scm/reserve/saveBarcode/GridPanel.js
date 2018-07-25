Ext.define('erp.view.scm.reserve.saveBarcode.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSaveBarcodeGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'saveBarcodeGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    FormUtil: Ext.create('erp.util.FormUtil'),
    dockedItems: [{xtype: 'erpToolbar', dock: 'bottom', enableAdd: false, enableDelete: true, enableCopy: true, enablePaste: true, enableUp: false, enableDown: false}],
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
	                if (isChecked) {
	                    this.deselectAll(true);
	                    var grid = Ext.getCmp('saveBarcodeGridPanel');
	                    this.deselect(grid.multiselected);
	                    grid.multiselected = new Array();
	                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                } else {
	                	var grid = Ext.getCmp('saveBarcodeGridPanel');
	                	this.deselect(grid.multiselected);
		                grid.multiselected = new Array();
		                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    this.selectAll(true);
	                    this.view.ownerCt.selectall = true;
	                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                }
	            }
	            Ext.getCmp('saveBarcodeGridPanel').summary();
	        }
	}),
	tbar:['->',{
			xtype:'combobox',
			fieldLabel:'管控类型',
			labelWidth: 65,
			width:150,
			allowBlank: true,
	        id: 'kind',
	        store: Ext.create('Ext.data.Store', {
	            fields: ['display', 'value'],
	            data : [
	                {"display": '全部', "value": '-1'},
	                {"display": '单件管控', "value": '1'},
	                {"display": '批管控', "value": '2'}
	            ]
	        }),
	    	displayField: 'display',
	    	valueField: 'value',
	    	editable: false,
	    	value: '-1'
		  },{
			id: 'query',
			text: $I18N.common.button.erpQueryButton,
			iconCls: 'x-button-icon-query',
	    	cls: 'x-btn-gray',
	        handler: function(btn){
	    		//限制点击筛选时间间隔不能超过2秒
	    		var grid=btn.ownerCt.ownerCt;
	    		if(grid.prevTime==null){
	    			grid.prevTime=new Date().getTime();
	    			grid.onQuery();
	    		}else {
	    			var nowtime=new Date().getTime();
	    			if((nowtime-grid.prevTime)/1000<2){
	    				showError('请控制筛选时间间隔不能小于2秒!');
	    				return;
	    			}else {
	    				grid.prevTime=nowtime;
	    				grid.onQuery();
	    			}
	    		}				
	    	}
			},{
                xtype: 'checkboxfield',
                name: 'autoPrint',
                id:'autoPrint',
                boxLabel: '自动打印条码',
            	style:'margin-left:50px',
            	checked:true
            },
	      {
			 id : 'confirm',
			 text : '确认生成',
			 width : 90,			
			 iconCls: 'x-button-icon-check',
		     cls: 'x-btn-gray',	
		     style:'margin-right:50px'
	      },{
	              margin: '5 0 0 5',
	              xtype: 'numberfield',
	              width:127,
	              growMin:100,
	              grow:true,
	              labelWidth: 65,
	              fieldLabel: '外箱容量',
	              name: 'pr_boxqty',
	              id: 'pr_boxqty',
	              allowDecimals: false,
	              minValue: 0,
	              maxValue: 100000000,
	              autoStripChars:true
	          },{
				 id : 'boxqtySet',
				 text : '刷新',
				 width : 50,			
			     cls: 'x-btn-gray',	
			     style:'margin-right:50px'
		      },{
					xtype:'erpCloseButton'
			  },'->'],
			  
	initComponent : function(){ 
	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    this.RenderUtil = Ext.create('erp.util.RenderUtil');
		condition = this.BaseUtil.getUrlParam('gridCondition');
		condition = (condition == null) ? "" : condition.replace(/IS/g,"=");
		this.defaultCondition = condition;
		var gridParam = {caller: caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
    	this.addEvents({
		    storeloaded: true
		});
    	this.callParent(arguments);
    	this.initRecords();
    	this.addKeyBoardEvents();
    	
	},
	sync: true,
	getMultiSelected: function(){
		var grid = this;
		grid.multiselected = [];
        var items = grid.selModel.getSelection();
        if(grid.selectall && items.length == grid.store.pageSize) {
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
        		if(me.columns && me.columns.length > 2){
        			var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        			me.store.loadData(data);
        			me.setMore(data.length);
        			//解决固定列左右不对齐的情况
                      var lockedView = me.view.lockedView;
                      if(lockedView){
                          var tableEl = lockedView.el.child('.x-grid-table');
                          if(tableEl){
                        	  tableEl.dom.style.marginBottom = '7px';
                          }
                      }
                    me.view.refresh(); 
        			me.initRecords();
        			grid.fireEvent('storeloaded', grid);
        			grid.summary();
        		} else {
        			if(res.columns){
        				Ext.each(res.columns, function(column, y){
        					me.setRenderer(column);
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
                		me.view.doLayout();
                		
                		
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
			store = this.store, items = store.data.items, selected = me.selModel.getSelection(), 
			value, m = me.down('erpToolbar');
			 b = me.down('tbtext[id=selected-count]');
		    if (!b) {
			  m.add('-');
			  b = m.add({xtype: 'tbtext', id: 'selected-count'});
		   }
			b.setText('共: ' + items.length + ' 条, 已选: ' + selected.length + ' 条');
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
	setMore : function(c) {
		if(c >= 1000) {
			var g = this, e = g.down('erpToolbar');
			if(!g.bigVolume) {
		    	var m = e.down('tool[name=more]');
		    	if(!m) {
		    		m = Ext.create('Ext.panel.Tool', {
		    			name: 'more',
		    			type: 'right',
						margin: '0 5 0 5',
						handler: function() {
							g.bigVolume = true;
							g.ownerCt.down('form').onQuery();
							m.disable();
						}
		    		});
		    		e.add('->')
		    		e.add(m);
		    	} else {
		    		m.show();
		    	}
		    }
		}
	},
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?whoami=" + caller+"&formParam=''");
				}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?whoami=" + caller+"&formParam=''");
				}
	    	});
		}
	},
	onQuery: function() {
 		var grid = Ext.getCmp('saveBarcodeGridPanel');
 		var condition = grid.defaultCondition || '';
 		var value = Ext.getCmp('kind').value;
 		if(value == -1){
 			condition = '1=1' +' and '+condition;
 		}else{
 			condition = 'pr_tracekind = '+value +' and '+ condition;
 		}
 		grid.beforeQuery(caller, condition);//
 		var gridParam = {caller: caller, condition: condition, start: 1, end: getUrlParam('_end')||1000};
 		grid.GridUtil.loadNewStore(grid, gridParam);
 	},
     beforeQuery: function(call, cond) {
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: 'saveBarcode',
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
		});
	}
});