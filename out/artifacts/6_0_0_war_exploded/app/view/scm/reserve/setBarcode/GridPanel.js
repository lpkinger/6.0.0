Ext.define('erp.view.scm.reserve.setBarcode.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpSetBarcodeGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'setBarcodeGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    FormUtil: Ext.create('erp.util.FormUtil'),
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
    		checkOnly:true,
	    	ignoreRightMouseSelection : false,
			listeners:{
	            selectionchange:function(selModel, selected, options){
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
	                    var grid = Ext.getCmp('setBarcodeGridPanel');
	                    this.deselect(grid.multiselected);
	                    grid.multiselected = new Array();
	                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                } else {
	                	var grid = Ext.getCmp('setBarcodeGridPanel');
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
	            Ext.getCmp('setBarcodeGridPanel').summary();
	        }
	}),
	tbar:['->',
	      {
			 id : 'refresh',
			 text : '刷新',
			 width : 60,			
			 iconCls : 'x-button-icon-save',
			 cls : 'x-btn-gray'	,
		},{
			id : 'batchGenBarcode',
			cls : 'x-btn-gray',
			text : '生成条码',
			style : 'margin-left:20px',
			iconCls: 'x-button-icon-check',
			cls: 'x-btn-gray',
			width:100
	  },{
		  xtype:'erpPrintMoreButton'
	  },{
	  	  xtype:'erpPrintMoreBoxButton'
	  },{
	      xtype:'erpDeleteAllDetailsButton'
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
    	this.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
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
		grid.setLoading(true);
		if(!param._config) param._config=getUrlParam('_config');
		Ext.Ajax.request({//拿到grid的columns
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
        				Ext.each(limits,function(l){
        					if(l.lf_field.indexOf(' ') > -1){
        						limitArr.push(l.lf_field.split(' ')[1]);
        					}else{
        						limitArr.push(l.lf_field);
        					}
        				});
    				}
        			var reg =new RegExp("^yncolumn-{1}\\d{0,1}$");
        			Ext.each(res.columns, function(column, y){
        				if(column.xtype=='textareatrigger'){
        					column.xtype='';
        					column.renderer='texttrigger';
        				}
        				//yncoloumn支持配置默认是/否
        				if(column.xtype &&reg.test(column.xtype)&&(column.xtype.substring(8)==-1||column.xtype.substring(8)==-0)){
        					Ext.each(res.fields, function(field, y){
                				if(field.type=='yn' && column.dataIndex==field.name){
                					field.defaultValue=0-column.xtype.substring(9);
                				}
        					});
        					column.xtype='yncolumn';
        				}
        				// column有取别名
        				if(column.dataIndex.indexOf(' ') > -1) {
        					column.dataIndex = column.dataIndex.split(' ')[1];
        				}
        				//power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        					column.hideable= false;
        				}
        				//renderer
        				me.setRenderer(grid, column);
        				//logictype
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
                				me.GridUtil.add10EmptyItems(grid, 40, false);
                			});
            			}
            		} else {
            			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
            		}
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
            				me.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
            			});
            		} else {
            			//toolbar
            			if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				me.setToolbar(grid, res.columns, grid.necessaryField, limitArr);
            			}else{
            				grid.limitArr=limitArr;
            			}
            			grid.reconfigure(store, res.columns);
            		}
            		if(grid.buffered) {//缓冲数据的Grid
            			grid.verticalScroller = Ext.create('Ext.grid.PagingScroller', {
            				activePrefetch: false,
            				store: store
            			});
            			store.guaranteeRange(0, Math.min(store.pageSize, store.prefetchData.length) - 1);
            		}
            		var vp = grid.up('viewport'), form = (vp ? vp.down('form') : null);
        			if(form){ 
        				grid.readOnly = !!form.readOnly;//grid不可编辑
        				form.on('afterload', function(){
        					grid.readOnly = !!form.readOnly;
        				});
        			}
        		} else {
        			grid.hide();
        			var vp = grid.up('viewport'), form = (vp ? vp.down('form') : null);
        			if(form && !form.isStatic) {
        				if(form.items.items.length == 0) {
        					form.on('afterload', function(){
            					me.updateFormPosition(form);//字段较少时，修改form布局
            				});
        				} else {
        					me.updateFormPosition(form);//字段较少时，修改form布局
        				}
        			}
        		}
        		grid.on('afterrender', function(){
        			me.summary();
    			});
        		
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
		var me = this,store = this.store, items = store.data.items,m = me.down('erpToolbar');
		m.add('->');
		var b = m.add({xtype: 'tbtext', id: 'selected-count'});
		b.setText('共: ' + items.length + ' 条');
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
	}
});