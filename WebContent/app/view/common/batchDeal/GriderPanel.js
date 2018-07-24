Ext.define('erp.view.common.batchDeal.GriderPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealerGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'batchDealerGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    tempStore:new Object(),
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    dockedItems: [{
        xtype: 'erpBatchToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.grid.HeaderFilter'),
    Ext.create('erp.view.core.plugin.CopyPasteMenu')],
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
	        },
	        select:function(selModel, record, index, opts){//选中
            	var grid=selModel.view.ownerCt;
            	var d=record.data;
	            delete d.RN;
	            var name = "";
            	if(grid.keyField != null && grid.keyField != ''){
    		   		name = d[grid.keyField];
		           	grid.selectObject[name]=d;
	        	}
            },
            deselect:function(selModel, record, index, opts){//取消选中
            	var grid=selModel.view.ownerCt;
            	var d=record.data;
	            delete d.RN;
	            var name = "";
            	if(grid.keyField != null && grid.keyField != ''){
    		   		name = d[grid.keyField];
	    		   	delete grid.selectObject[name];
	        	}
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
    	
    	this.getGridColumnsAndStore(this, 'common/batchdeal/data.action');
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
	getGridColumnsAndStore: function(grid, url,param){
		var me = this;
		if(!param){
			var param = {caller: caller, condition: condition,_config:getUrlParam('_config')};
			param.page =  page;
			param.pageSize =  pageSize;
		}
		if(!(param.condition||param.condition!='')){
			param.condition=Ext.getCmp('dealerform').getCondition(grid);
		}
		if(!url){
			url = 'common/batchdeal/data.action';
		}
		
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	async:false,
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(me.columns && me.columns.length > 2){
        			var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        			var sel=Ext.Object.getKeys(me.tempStore),
        			form=Ext.getCmp('dealerform');
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
        			var dataCount = res.count;
			        me.dataCount=dataCount;
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
        			columns = Ext.clone(res.columns);
	    			var column_add=[{
			        				text: '是否已暂存',
			        				dataIndex: 'turned',
			        				cls: "x-grid-header-1",
			        				locked: true,
			        				width: 80,
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
			        fields = res.fields;
			        res.fields.push({name: 'turned',type: "string"});
			        var dataCount = res.count;
			        me.dataCount=dataCount;
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
        		var toolbar=me.down('erpBatchToolbar');
        		toolbar.afterOnLoad();
        	}
        });
		selectRecord(me);
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
			value, bar = (onlySelected ? me.down('toolbar[to=select]') : me.down('erpBatchToolbar'));
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
	setMore : function(c) {
		if(c >= 100) {
			var g = this, e = g.down('erpBatchToolbar');
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
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var me = this ;
        		var toolbar=me.down('erpBatchToolbar');
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn], f = grid.getHeaderFilterField(fn);
                    if(!Ext.isEmpty(value)) {
                    	if("null"!=value){
	                    	if(f.originalxtype == 'numberfield') {
                    			if(value.indexOf('>=')==0||value.indexOf('<=')==0||value.indexOf('>')==0||value.indexOf('<')==0||value.indexOf('!=')==0||value.indexOf('=')==0){
                					if(value.indexOf('!=')==0){
                						value = "("+fn + value + " or "+fn +" is null) ";
                					}else{
                						value = fn + value + " ";
                					}
                    			}else if(value.indexOf('~')>-1){
                    				var arr = value.split('~');
                    				value = fn + " between " + arr[0] + " and "+arr[1]+" ";
                    			}else{
                					value = fn + "=" + value + " ";
                				}
	                    	} else if(f.originalxtype == 'datefield'){
	                    			if(value.indexOf('=')>-1){
	                    				var valueX = value.split('=')[1];
	                    				var length = valueX.split('-').length;
	                    				if(length<3){
	                    					if(length == 1){
	                    						var value1 = Ext.Date.toString(new Date(valueX+'-01-01'));
	                    						var value2 = Ext.Date.toString(new Date(valueX+'-12-31'));
	                    						value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    					}else if(length == 2){
	                    						var day = new Date(valueX.split('-')[0],valueX.split('-')[1],0);
	                    						var value1 = Ext.Date.toString(new Date(valueX+'-01'));
	                    						var value2 = Ext.Date.toString(new Date(valueX+'-'+day.getDate()));
	                    						value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    					}
		                    			}else {
		                    				if(value.indexOf('>=')==0){
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')>='" + value + "' ";
			                    			}else if(value.indexOf('<=')==0){
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')<='" + value + "' ";
			                    			}else {
			                    				value = Ext.Date.toString(new Date(valueX));
			                            		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
			                    			}
		                    			}
	                    			}else if(value.indexOf('~')>-1){
                    					var value1 = Ext.Date.toString(new Date(value.split('~')[0]));
                        				var value2 = Ext.Date.toString(new Date(value.split('~')[1]));
	                            		value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '"+ value2 +"'";
	                    			}else{
	                    				value = Ext.Date.toString(new Date(value));
	                            		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
	                    			}
		                        } else {
	                        		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
	                        		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
	    	                    	if(exp_d.test(value)){
	    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
	    	                    	} else if(exp_t.test(value)){
	    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
	    	                    	} else{
	    	                    		if (f.xtype == 'combo' || f.xtype == 'combofield') {
	    	                    			if (value == '-所有-') {
	    	                    				value = ' 1=1 ';
	    	                    			} else {
	    	                    				if (f.column && f.column.xtype == 'yncolumn'){
	    	                    					if (value == '-无-') {
	            	                    				value = fn + ' is null ';
	            	                    			} else {
	            	                    				value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
	            	                    			}
	             	                    		} else {
	             	                    			if (value == '-无-') {
	            	                    				value = 'nvl(to_char(' + fn + '),\' \')=\' \'';
	            	                    			} else {
	            	                    				if(value)value=value.replace(/\'/g,"''");
	            	                    				value = fn + " LIKE '" + value + "%' ";
	            	                    			}
	             	                    		}
	    	                    			}
	    	                    		} else if(f.xtype == 'datefield') {
	    	                    			value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
	    	                    		} else if(f.column && f.column.xtype == 'numbercolumn'){
	    	                    			if(f.column.format) {
	    	                    				var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length;
	    	                    				//防止to_char去除小数点前面的0
	    	                    				if(-1<value&&value<1){
		    	                    				var number = value;
		    	                    				value = "to_char(round(" + fn + "," + precision + "),";	    	                    		
		    	                    				value += "'fm0.";
		    	                    				for(var i=0;i<precision;i++){
		    	                    					value += "0";
		    	                    				}
		    	                    				value += "') like '%" + number + "%' ";
		    	                    			}else{
		    	                    				value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
		    	                    			}
	    	                    			} else
	    	                    				value = "to_char(" + fn + ") like '%" + value + "%' ";
	    	                    		} else {
	    	                    			/**字符串转换下简体*/
	    	                    			if(value)value=value.replace(/\'/g,"''");
	    	                    			var SimplizedValue=this.BaseUtil.Simplized(value);   	                    	
	    	                    			//可能就是按繁体筛选  
	    	                    			if(f.ignoreCase) {// 忽略大小写
	        	                    			fn = 'upper(' + fn + ')';
	        	                    			value = value.toUpperCase();
	        	                    		}
	        	                    		if(!f.autoDim) {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    			
	        	                    			
	        	                    		} else if(f.filterSelect||f.inputEl.dom.disabled||(f.rawValue==''&&f.emptyText==value)){
		        	                    		if(f.filterType == 'direct'){
		        	                    			value=fn+"='"+value+"'";
		        	                    		} else if(f.filterType == 'nodirect'){
		        	                    			value="nvl("+fn+",' ')<>'"+value+"'";
		        	                    		} else if(f.filterType == 'head'){
		        	                    			value = fn + " LIKE '" + value + "%' ";
		        	                    		} else if(f.filterType == 'end'){
		        	                    			value = fn + " LIKE '%" + value + "' ";
		        	                    		} else if(f.filterType == 'null'){
		        	                    			value = fn + " is null";
		        	                    		} else if(f.filterType == 'novague'){
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " not LIKE '%" + value + "%' and "+fn+" not LIKE '%"+SimplizedValue+"%' or "+fn+" is null)";
		        	                    			}else value = "("+fn + " not LIKE '%" + value + "%' or "+fn+" is null)";
		        	                    		} else{
		        	                    			if(SimplizedValue!=value){
		        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
		        	                    			}else value = fn + " LIKE '%" + value + "%' ";
		        	                    			f.filterType = '';
		        	                    		}
		        	                    		f.filterSelect = false;
	        	                    		}else {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '%" + value + "%' ";
	        	                    			f.filterType = '';
	        	                    		}
	    	                    		}
	    	                    	}
	                        	}
                    	}else value ="nvl("+fn+",' ')=' '";
                    	if(condition == null){
                    		condition = value;
                    	} else {
                    		condition = condition + " AND " + value;
                    	}
                    }
                }
                this.filterCondition = condition;
                if(caller=='ProductSaler!Base'){
                	condition = me.getAllCondition(grid);
                }
                var param = {caller: caller, condition: condition,_config:getUrlParam('_config'),page:1,pageSize:pageSize};
                this.getGridColumnsAndStore(grid, null,param);
                page = 1 ;
                toolbar.afterOnLoad(page);
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
    getAllCondition:function(grid){
    	var me = this ;
    	var fCondition = me.filterCondition;
    	var gDefaultCondition= grid.defaultCondition;
    	var QueryCondition=Ext.getCmp('dealerform').getCondition();
        QueryCondition=QueryCondition!=""&&QueryCondition!=null?
        		QueryCondition+(gDefaultCondition!=""&&gDefaultCondition!=null?" AND "+gDefaultCondition:""):gDefaultCondition;
       
       if(QueryCondition!=""&&QueryCondition!=null){
    	   QueryCondition= fCondition!=""&&fCondition!=null?QueryCondition+" AND "+fCondition:QueryCondition;
       }else{
    	   QueryCondition= fCondition!=""&&fCondition!=null?fCondition:QueryCondition;
       }
       if (QueryCondition==null||QueryCondition=="") QueryCondition='1=1';
       return QueryCondition;
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