Ext.define('erp.view.b2c.common.b2cBatchDealGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
 	region:'center',
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle: 'background-color:#f1f1f1;',
    invalidateScrollerOnRefresh: false,
    viewConfig: {
        trackOver: false
    },
    headerCt: Ext.create("Ext.grid.header.Container",{
	   forceFit: false,
       sortable: true,
       enableColumnMove:true,
       enableColumnResize:true,
       enableColumnHide: true
    }),
    dockedItems: caller=="MRPOnhandThrow"?[{
        xtype: 'toolbar',
        items:[{
        	xtype: 'tbtext',
        	id: 'storeCount',
        	tpl: '筛选结果: {count}条'
        },'-',{
           xtype:'erpGetB2CProductKindButton'
        }, '->', {
        	xtype: 'tbtext',
        	id: 'selectedCount',
        	tpl: '已选择: {count}条'
        }],
        dock: 'bottom'
	}]:[{xtype: 'erpToolbar', dock: 'bottom', enableAdd: false, enableDelete: false, enableCopy: false, enablePaste: false, enableUp: false, enableDown: false}],
	sync: true,
    plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
    	remoteFilter: true
    }),
     Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1,
        listeners:{
        	'edit':function(editor,e,Opts){
        	}
        }
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
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
    	checkOnly: true,
		listeners:{
			selectionchange:function(selModel, selected, options){
				var grid = selModel.view.ownerCt;		
				if(grid.down('#selectedCount')){
					grid.down('#selectedCount').update({
						count: selected.length
					});
				}
            },
            select:function(row,record,index){
            	var grid = row.view.ownerCt;
              //if(!Ext.Array.contains(grid.multiselected,record)){
            	  grid.multiselected.push(record);
            //  }
            },
            deselect:function(row,record,index){
            	var grid = row.view.ownerCt;
            	Ext.Array.remove(grid.multiselected, record);
            }
        },
        getEditor: function(){
        	return null;
        }
    }),
    maxDataSize: 3000,
	initComponent : function(){ 
	    this.GridUtil = Ext.create('erp.util.GridUtil');
	    this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    this.RenderUtil = Ext.create('erp.util.RenderUtil');
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: caller, condition: '',start:1,end:1000,_config:getUrlParam('_config')};
    	this.getGridColumnsAndStore(this);
    	this.callParent(arguments);
	},
	getGridColumnsAndStore: function(grid){
			var me = this;
    		grid.setLoading(false);
    		if(grid.gridcolumns){
    			var limits = grid.limits, limitArr = new Array();
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
    			Ext.each(grid.gridcolumns, function(column, y){
    				if(column.xtype=='textareatrigger'){
    					column.xtype='';
    					column.renderer='texttrigger';
    				}
    				//yncoloumn支持配置默认是/否
    				if(column.xtype &&reg.test(column.xtype)&&(column.xtype.substring(8)==-1||column.xtype.substring(8)==-0)){
    					Ext.each(grid.gridfields, function(field, y){
            				if(field.type=='yn' && column.dataIndex==field.name){
            					field.defaultValue=0-column.xtype.substring(9);
            				}
    					})
    					column.xtype='yncolumn';
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
    				me.setRenderer(grid, column);
    				//logictype
    				me.setLogicType(grid, column, {
    					headerColor: grid.necessaryFieldColor
    				});
    			});
    			//data
        		var data = [];
        		if(!grid.data || grid.data.length == 2){
        			if (grid.buffered) {
            			me.add10EmptyData(grid.detno, data);
            			me.add10EmptyData(grid.detno, data);//添加20条空白数据            				
        			} else {
        				grid.on('reconfigure', function(){// 改为Grid加载后再添加空行,节约200~700ms
            				me.add10EmptyItems(grid, 40, false);
            			});
        			}
        		} 
        		//store
        		var store = me.setStore(grid, grid.gridfields, data, grid.groupField, grid.necessaryField);
        		//view
        		if(grid.selModel && grid.selModel.views == null){
        			grid.selModel.views = [];
        		}
        		//reconfigure
        		if(grid.sync) {//同步加载的Grid
        			grid.reconfigure(store, grid.gridcolumns);
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
        		var vp = Ext.getCmp("form");
	    		if(vp){ 
					grid.readOnly = !!vp.readOnly;//grid不可编辑
					vp.on('afterload', function(){
						grid.readOnly = !!vp.readOnly;
					});
				}
    		} else {
    			grid.hide();
    			var vp = Ext.getCmp("form");
    			if(vp && !vp.isStatic) {
    				if(vp.items.items.length == 0) {
    					vp.on('afterload', function(){
        					me.updateFormPosition(vp);//字段较少时，修改form布局
        				});
    				} else {
    					me.updateFormPosition(vp);//字段较少时，修改form布局
    				}
    			}
    		}

        
	},
	getMultiSelected: function(){
		var grid = this;
		grid.multiselected = [];
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		return Ext.Array.unique(grid.multiselected);
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	summary: function(selected){
		var me = this,
			store = this.store,
			items = selected || store.data.items,
			value;
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = store.getSum(c.dataIndex);
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.header + '(sum):' + value);		
			} else if(c.summaryType == 'count'){
                value = items.length;
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.header + '(count):' + value);			
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);       		        			
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.header + '(average):' + value);
			}		
		});
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn],f = grid.getHeaderFilterField(fn);
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
               if(QueryCondition!=""&&QueryCondition!=null){
            	   QueryCondition= this.filterCondition!=""&&this.filterCondition!=null?QueryCondition+" AND "+this.filterCondition:QueryCondition;
               }else{
            	   QueryCondition= this.filterCondition!=""&&this.filterCondition!=null?this.filterCondition:QueryCondition;
               }
                this.loadNewStore(grid,{caller:caller,fields:grid.datafields,condition:QueryCondition, start: 1, end: 3000,tablename:this.tablename});
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
    reconfigure: function(store, columns){
    	//改写reconfigure方法
    	var d = this.headerCt;
    	if (this.columns.length <= 1 && columns) {//this.columns.length > 1表示grid的columns已存在，没必要remove再add
			d.suspendLayout = true;
			d.removeAll();
			d.add(columns);
		}
		if (store) {
			this.bindStore(store);
		} else {
			this.getView().refresh();
		}
		if (columns) {
			d.suspendLayout = false;
			this.forceComponentLayout();
		}
		this.fireEvent("reconfigure", this);
    },
    setRenderer: function(grid, column){
		if(!column.haveRendered){
			if((column.renderer != null && column.renderer != "")||(column.editor&&column.editor.xtype=='textareatrigger')) {
				if(!grid.RenderUtil){
					grid.RenderUtil = Ext.create('erp.util.RenderUtil');
				}
	    		var renderName = column.renderer;
	    		if(column.editor&&column.editor.xtype=='textareatrigger'){
	    			var form = Ext.ComponentQuery.query('form');
	    			if(form[0].readOnly){
	    				renderName = 'texttrigger';
	    			}
	    		}
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
			} else if(column.readOnly){
				column.renderer = function(val, meta, record, x, y, store, view){
					meta.style = "background: #e0e0e0;";
					var c = this.columns[y];
					if(val != null && val.toString().trim() != ''){
						if(c.xtype == 'datecolumn' && typeof val === 'object'){
							val = Ext.Date.format(val, 'Y-m-d');
						} else if(c.xtype == 'numbercolumn' && val.toString().trim() == '0') {
							val = '';
						}
						return val;
					} else {
						if(c.xtype == 'datecolumn'){
							val = '';
						}
						return val;
					}
				};
				column.haveRendered = true;
			}
    		
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
				grid.keyField = column.dataIndex.split(" ")[0];
			} else if(logic == 'mainField'){
				grid.mainField = column.dataIndex.split(" ")[0];
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
							}else if(c.xtype == 'combocolumn'){
								if(!Ext.isEmpty(val)) {
									var g = view.ownerCt,h = g.columns[y],f = h.field, k;
									if ((k = (h.editor || h.filter)) && k.store) {
										var t = null,dd = k.store.data;
								   		t = Ext.Array.filter(dd, function(d, index){
										    return d.value == val;
									    });
									    if (t && t.length > 0) {
									    	return t[0].display;
									    }
									} else if (f) {
							   	   		if(f.store) {
											var t = f.store.findRecord('value', val);
										    if (t)
										    	return t.get('display');
										} else
							   	   			return f.rawValue;
							   		}
								   return val;
								}						   
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
		var me=this;
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
			    grid:grid,
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
                listeners:{
                	'update':me.syncSummaryData,
                	'remove':me.syncSummaryData
                } ,
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
			config.pageSize = grid.bufferSize||200;
			config.purgePageCount = 0;
			config.proxy = {
                type: 'memory'
            };
		} else {
			config.data = data;
		}
		if(grid.detno) {
			// sort by detno property
			config.sorters = [{
				property: grid.detno,
				direction: 'ASC'
			}];
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
	setToolbar: function(grid, columns, necessaryField, limitArr){
		var items = [];
		var bool = true;
		if(!grid.dockedItems)
			return;
		Ext.each(grid.dockedItems.items, function(item){
			if(item.dock == 'bottom' && item.items){//bbar已存在
				bool = false;
			}
		});
		if(bool){
    		Ext.each(columns, function(column){
    			if(limitArr.length == 0 || !Ext.Array.contains(limitArr, column.dataIndex)) {
    				if(column.summaryType == 'sum'){
        				items.push('-',{
        					id: (column.dataIndex + '_sum').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(sum):000000'
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
        					/*id: column.dataIndex + '_count',
        					itemId: column.dataIndex,*/
        					id: (column.dataIndex + '_count').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
        					xtype: 'tbtext',
        					text: column.text + '(count):0'
        				});
        			}
    			}
    			if(column.dataIndex == necessaryField){
    				column.renderer = function(val){
    					if(val != null && val.toString().trim() != ''){
							return val;
						} else {
							return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
				  			'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
						}
    				};
    			}
    		});
			grid.addDocked({
    			xtype: 'toolbar',
    	        dock: 'bottom',
    	        items: items
    		});
		} else {
			var bars = Ext.ComponentQuery.query('erpToolbar');
			if(bars.length > 0){
				Ext.each(columns, function(column){
        			if(column.summaryType == 'sum'){
        				bars[0].add('-');
        				bars[0].add({
        					/*id: column.dataIndex + '_sum',
        					itemId: column.dataIndex,*/
        					id: (column.dataIndex + '_sum').replace(/,/g,'$'),
        					itemId: (column.dataIndex).replace(/,/g,'$'),
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
        					/*id: column.dataIndex + '_count',
        					itemId: column.dataIndex,*/
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
	updateFormPosition: function(form){
		var height = window.innerHeight;
		var width = window.innerWidth;
		if(form){
			if(form.items.items.length > 12){
				form.setHeight(height);
			} else {//少于12个字段的单form页面，强制居中显示
				if(form.items.items == 0){
					form.on('afterlayout', function(){
						if(0 < form.items.items.length <= 12){
							Ext.each(form.items.items, function(item){
								if(item.columnWidth >= 0.25 && item.columnWidth < 0.6){
									item.setWidth(width*0.65*0.5);
									item.columnWidth = 0.5;
								} else if(item.columnWidth >= 0.6) {
									item.setWidth(width*0.65);
									item.columnWidth = 1;
								}
								
							});
						}
					});
				} else {
					Ext.each(form.items.items, function(item){
						if(item.columnWidth >= 0.25 && item.columnWidth < 0.6){
							item.setWidth(width*0.65*0.5);
							item.columnWidth = 0.5;
						} else if(item.columnWidth >= 0.6) {
							item.setWidth(width*0.65);
							item.columnWidth = 1;
						}
						
					});
				}
				form.setHeight('60%');
				form.setWidth('70%');
				form.el.applyStyles('margin:10% auto;border-width: 0 1px 1px 1px;');
			}
		}
	},
	/**
	 * 从index行开始，往grid里面加十空行
	 * @param detno 编号字段
	 * @param data 需要添加空白数据的data
	 */
	add10EmptyData: function(detno, data){
		if(detno){
			var index = data.length == 0 ? 0 : Number(data[data.length-1][detno]);
			for(var i=0;i<20;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				data.push(o);
			}
		} else {
			for(var i=0;i<20;i++){
				var o = new Object();
				data.push(o);
			}
		}
	},
	/**
	 * 从index行开始，往grid里面加十空行
	 * @param grid 
	 */
	add10EmptyItems: function(grid, count, append){
		var store = grid.store, 
			items = store.data.items, arr = new Array();
		var detno = grid.detno;
		count = count || 10;
		append = append === undefined ? true : false;
		if(typeof grid.sequenceFn === 'function')
			grid.sequenceFn.call(grid, count);
		else {
			if(detno){
				var index = items.length == 0 ? 0 : Number(store.last().get(detno));
				for(var i=0;i < count;i++ ){
					var o = new Object();
					o[detno] = index + i + 1;
					arr.push(o);
				}
			} else {
				for(var i=0;i < count;i++ ){
					var o = new Object();
					arr.push(o);
				}
			}
			store.loadData(arr, append);
			var i = 0;
			store.each(function(item, x){
				if(item.index) {
					i = item.index;
				} else {
					if (i) {
						item.index = i++;
					} else {
						item.index = x;
					}
				}
			});
		}
	},
	syncSummaryData:function(store,record,operation){
    	 var g=this.grid,cols=g.columns,bar=g.down('erpToolbar');
    	 if(bar){
    	 	Ext.Array.each(cols,function(column){
    	 	var sumItem=bar.getComponent(column.dataIndex),summaryData=0,store=g.getStore();
    		 if(column.summaryType && column.xtype=='numbercolumn' && sumItem){    			 
    			 switch(column.summaryType){
    			 case 'sum':
    				 summaryData=store.sum(column.dataIndex);    				
    				 break;
    			 case 'min':
    				 summaryData=store.min(column.dataIndex);
    				 break;
    			 case 'max':
    				 summaryData=store.average(column.dataIndex);
    				 break;	 
    			 }
    			 if(sumItem)sumItem.update(column.text+':'+summaryData);
    		 }							
    	 });  
    	 }    	 	 
     },
     loadNewStore :function(grid, gridParam){
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url : basePath + "b2c/getFieldsDatas.action",
			params: gridParam,
			timeout:60000,
			method : 'post',
			//改为同步
			async:false,
			callback : function(options,success,response){
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				var data = res.data;
				if(data==null){
					data=[];
				}
				grid.store.loadData(data);
				if(!data || data.length == 0){
	    			grid.store.removeAll();
	    			me.add10EmptyItems(grid);
        		} else {
        			if(grid.buffered) {
        				var ln = data.length, records = [], i = 0;
        			    for (; i < ln; i++) {
        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
        			    }
        			    grid.store.purgeRecords();
        			    grid.store.cacheRecords(records);
        			    grid.store.totalCount = ln;
        			    grid.store.guaranteedStart = -1;
        			    grid.store.guaranteedEnd = -1;
        			    var a = grid.store.pageSize - 1;
        			    a = a > ln - 1 ? ln - 1 : a;
        			    grid.store.guaranteeRange(0, a);
        			} else {
        				grid.store.loadData(data);
        			}
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
				
			}
		});     	
     },
     getProdField : function() {
 		var f = null;
 		switch (caller){
 		case 'SendNotify!ToProdIN!Deal' ://通知单转出货
 			f = 'snd_prodcode';
 			break;
 		}
 		return f;
 	}
});