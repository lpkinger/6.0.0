Ext.define('erp.view.common.batchDeal.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    tempStore:new Object(),
    store: [],
    columns: [],
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
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: caller, condition: condition,_config:getUrlParam('_config')};
    	//this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
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
        		if(me.columns && me.columns.length > 2){
        			var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        			var sel=Ext.Object.getKeys(me.tempStore),form=Ext.getCmp('dealform');
        			var keys=new Array();
					if(form.detailkeyfield){
						keys=form.detailkeyfield.split('#');
					}else{
						keys.push(grid.keyField);
					}
        			Ext.each(data, function(d){
        				var s='';
        				Ext.each(keys,function(k){
						});
        				if(sel.length&&Ext.Array.contains(sel,s)){
        					d.turned='是';
        					Ext.each(me.columns,function(col){
        						if(col.dataIndex&&col.readOnly){
        							me.tempStore[s][col.dataIndex]=d[col.dataIndex];
        						}
        					});
        				}else
        				d.turned='否';
        			});
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
        		} else if(res.columns){
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
    					limitArr = Ext.Array.pluck(limits, 'lf_field');
    				}
	    			var column_add=[{
			        				text: '已暂存',
			        				dataIndex: 'turned',
			        				cls: "x-grid-header-1",
			        				locked: true,
			        				width: 50,
			        				filter: {dataIndex: "turned",
										displayField: "display",
										exactSearch: false,
										filtertype: null,
										hideTrigger: false,
										ignoreCase: false,
										queryMode: "local",
										store: null,
										valueField: "value",
										xtype: "textfield"}
			        		}];
			        res.columns=Ext.Array.merge(column_add,res.columns);
			        res.fields.push({name: 'turned',type: "string"});
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
							column.style = 'color:#1e1e1e;';  /*color:#fb3c3c*/
							column.cls = 'x-grid-necessary-filter';
						}else{
							column.cls = 'x-grid-normal-filter';
						}
    					if(column.editor){
        					column.editor.margin = '2 5 0 0';
        				}
        			});
        			//data
            		var data = [];
            		if(!res.data || res.data.length == 2){
            			if (grid.buffered) {
                			me.GridUtil.add10EmptyData(grid.detno, data);
                			me.GridUtil.add10EmptyData(grid.detno, data);//添加20条空白数据            				
            			} else {
            				grid.on('reconfigure', function(){// 改为Grid加载后再添加空行,节约200~700ms
//                				me.GridUtil.add10EmptyItems(grid, 40, false);
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
            				me.GridUtil.setToolbar(grid, grid.columns, grid.necessaryField, limitArr);
            			});
            		} else {
            			//toolbar
            			if (grid.generateSummaryData === undefined) {// 改为Grid加载后再添加合计,节约60ms
            				me.GridUtil.setToolbar(grid, res.columns, grid.necessaryField, limitArr);
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
            		var form = Ext.ComponentQuery.query('form');
        			if(form && form.length > 0){ 
        				grid.readOnly = form[0].readOnly;//grid不可编辑
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
    			var arr = column.renderer.split(':');
    			if(arr[0]!='rowstyle'){//判断是否是rowstyle
    				Ext.each(arr, function(a, index){
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
    			}else{//hey start 行样式加载	   
    		    	var arr = column.renderer.split(':');
    		    	switch(arr.length)
    		    	{
    		    		case 2:						
    		    			Ext.apply(grid.getView(),{
    		    				 getRowClass: function(record, rowIndex, rowParams, store){	
    		    				 	return (record.get(column.dataIndex)==arr[1]) ? 'default' : null;		
    		    				 }
    		    			});
    		    			break;
    		    		case 3:	 
    		    			Ext.apply(grid.getView(),{
    		    				 getRowClass: function(record, rowIndex, rowParams, store){	
    		    				 	return (record.get(column.dataIndex)==arr[1]) ? arr[2] : null;		
    		    				 }
    		    			});
    		    			break;
    		    		default:
    		    	}	
    			}
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
						id: (column.dataIndex + '_sum').replace(/,/g,'$'),
						itemId: (column.dataIndex).replace(/,/g,'$'),
						xtype: 'tbtext',
						text: column.text + '(sum):0'
					});
				} else if(column.summaryType == 'average') {
					items.push('-',{
						id: (column.dataIndex + '_average').replace(/,/g,'$'),
						itemId: (column.dataIndex).replace(/,/g,'$'),
						xtype: 'tbtext',
						text: column.text + '(average):0'
					});
				} else if(column.summaryType == 'count') {
					items.push('-',{
						id: (column.dataIndex + '_count').replace(/,/g,'$'),
						itemId: (column.dataIndex).replace(/,/g,'$'),
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
        					id: (column.dataIndex+'_sum').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(sum):0'
        				});
        			} else if(column.summaryType == 'average') {
        				bars[0].add('-');
        				bars[0].add({
        					id: (column.dataIndex + '_average').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(average):0'
        				});
        			} else if(column.summaryType == 'count') {
        				bars[0].add('-');
        				bars[0].add({
        					id: (column.dataIndex + '_count').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(count):0'
        				});
        			}
        		});
			}
		}		
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
		 'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
        		delete filters['null'];
                for(var fn in filters){
                    var value = filters[fn],f = grid.getHeaderFilterField(fn);
                    if(f.dataIndex.indexOf(' ')>-1 && f.dataIndex.split(" ")[0].indexOf(".")>-1){//表名.字段名  别名    处理
            			fn=f.dataIndex.split(" ")[0];
                    }
                    if(!Ext.isEmpty(value)){
                    	if(f.filtertype) {
                    		if (f.filtertype == 'numberfield') {
                    			value = fn + "=" + value + " ";
                    		}
                    	} else {
                    		if(Ext.isDate(value)){
                        		value = Ext.Date.toString(value);
                        		value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
                        	} else {
                        		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
                        		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
    	                    	if(exp_d.test(value)){
    	                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd') ";
    	                    	} else if(exp_t.test(value)){
    	                    		value = fn + "=to_date('" + value + "','yyyy-MM-dd HH24:mi:ss') ";
    	                    	} else{
    	                    		value = fn + " LIKE '%" + value + "%' ";
    	                    	}
                        	}
                    	}
                    	if(condition == null){
                    		condition = value;
                    	} else {
                    		condition = condition + " AND " + value;
                    	}
                    }
                }
                this.filterCondition = condition;
                var QueryCondition=Ext.getCmp('dealform').getCondition();
                QueryCondition=QueryCondition!=""&&QueryCondition!=null?
                		QueryCondition+(grid.defaultCondition!=""&&grid.defaultCondition!=null?" AND "+grid.defaultCondition:""):grid.defaultCondition;
                //在筛选头后面拼接筛选前的条件
                var constr=Ext.getCmp('dealform').beforeQuery(caller, QueryCondition);//获取筛选前的条件
               	constr =  constr!=""&&constr!=null?(constr):(" 1=1");
               if(QueryCondition!=""&&QueryCondition!=null){
            	   QueryCondition= this.filterCondition!=""&&this.filterCondition!=null?QueryCondition+" AND "+this.filterCondition+" AND "+constr:QueryCondition+" AND "+constr;
            	   }else{              	 	
            	   QueryCondition= this.filterCondition!=""&&this.filterCondition!=null?this.filterCondition+" AND "+constr:QueryCondition+constr;
            	   }
               if (QueryCondition==null||QueryCondition=="") QueryCondition='1=1';
                this.GridUtil.loadNewStore(grid,{caller:caller,condition:QueryCondition, start: 1, end: 1000});
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        },
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
		    		e.add('->');
		    		e.add(m);
		    	} else {
		    		m.show();
		    	}
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
		},
        getRowClass: function(record) { 
        	if(record.get('turned') =='是'){
        		return 'custom-turned';//'x-grid-row-turned';
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
	}
});