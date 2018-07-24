Ext.QuickTips.init();

Ext.override(Ext.grid.column.Column, {
	// 点列头后，进行后台数据排序查找
	doSort: function(state) {
		var tablePanel = this.up('tablepanel');
		if(typeof tablePanel.getColumnsAndStore === 'function') {
			var sortParam = this.getSortParam(),column=this;
			if(column && (Ext.isEmpty(column.xtype) || column.xtype == 'textcolumn')) {
				sortParam = "nlssort(" + sortParam + ",'NLS_SORT=SCHINESE_PINYIN_M')";
			}
			tablePanel.sorts = 'ORDER BY ' + sortParam + ' ' + state;
			tablePanel.getColumnsAndStore();
		} else {
			var tablePanel = this.up('tablepanel'),
				store = tablePanel.store;
			if (tablePanel.ownerLockable && store.isNodeStore) {
				store = tablePanel.ownerLockable.lockedGrid.store;
			}
			store.sort({
				property: this.getSortParam(),
				direction: state
			});
		}
    }
});

Ext.define('erp.view.common.bench.SceneGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.erpSceneGridPanel',
	id:'sceneGrid',
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	showRowNum:true,
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		checkOnly:true,
		listeners: {
            select:function(selModel, record, index, opts){//选中
            	var grid=selModel.view.ownerCt;
            	var d=record.data;
	            delete d.RN;
	            var name = "";
            	if(grid.keyField != null && grid.keyField != ''){
	    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
	    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(record.data);//vd_vsid@vd_id+vd_class@vd_class
					   	Ext.Array.each(arr, function(r){
						   ff = r.split('@');
						   if(fields.indexOf(ff[1]) > -1) {
							   val = record.get(ff[1]);
				    		   if(val instanceof Date)
				    			   val = Ext.Date.format(val, 'Y-m-d');
						   } else {
							   val = ff[1];
						   }
						   name += val;
					   });
	    		   	} else {
	    		   		name = d[grid.keyField];
	    		   	}
		           	grid.selectObject[name]=d;
	        	}
            },
            deselect:function(selModel, record, index, opts){//取消选中
            	var grid=selModel.view.ownerCt;
            	var d=record.data;
	            delete d.RN;
	            var name = "";
            	if(grid.keyField != null && grid.keyField != ''){
	    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
	    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(record.data);//vd_vsid@vd_id+vd_class@vd_class
					   	Ext.Array.each(arr, function(r){
						   ff = r.split('@');
						   if(fields.indexOf(ff[1]) > -1) {
							   val = record.get(ff[1]);
				    		   if(val instanceof Date)
				    			   val = Ext.Date.format(val, 'Y-m-d');
						   } else {
							   val = ff[1];
						   }
						   name += val;
					   });
	    		   	} else {
	    		   		name = d[grid.keyField];
	    		   	}
	    		   	delete grid.selectObject[name];
	        	}
            }
        },
      	onHeaderClick : function(b, d, a) {
			if (d.isCheckerHd) {
				a.stopEvent();
				var c = d.el.hasCls(Ext.baseCSSPrefix
						+ "grid-hd-checker-on");
				if (c) {
					this.deselectAll(true)
				} else {
					this.selectAll(true)
				}
				var grid = b.ownerCt;
				var selected = grid.getSelectionModel().selected;
				if(selected.length==0){
	        		Ext.each(grid.store.data.items,function(deselect){
	    				var d=deselect.data;
	    				delete d.RN;
	    				var name = "";
	    				if(grid.keyField != null && grid.keyField != ''){
			    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
			    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(d);//vd_vsid@vd_id+vd_class@vd_class
							   	Ext.Array.each(arr, function(r){
								   ff = r.split('@');
								   if(fields.indexOf(ff[1]) > -1) {
									   val = d[ff[1]];
						    		   if(val instanceof Date)
						    			   val = Ext.Date.format(val, 'Y-m-d');
								   } else {
									   val = ff[1];
								   }
								   name += val;
							   });
			    		   	} else {
			    		   		name = d[grid.keyField];
			    		   	}
			    		   	delete grid.selectObject[name];
			        	}
	    			});
	        	}else if(selected.length==grid.store.getCount()){
	        		Ext.each(selected.items,function(select){
	        			var d=select.data;
	    				delete d.RN;
	    				var name = "";
	    				if(grid.keyField != null && grid.keyField != ''){
			    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
			    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(d);//vd_vsid@vd_id+vd_class@vd_class
							   	Ext.Array.each(arr, function(r){
								   ff = r.split('@');
								   if(fields.indexOf(ff[1]) > -1) {
									   val = d[ff[1]];
						    		   if(val instanceof Date)
						    			   val = Ext.Date.format(val, 'Y-m-d');
								   } else {
									   val = ff[1];
								   }
								   name += val;
							   });
			    		   	} else {
			    		   		name = d[grid.keyField];
			    		   	}
			    		   	grid.selectObject[name]=d;
			        	}
	    			});
	        	}
			}
		}
	}),
	dockedItems: [{
        xtype: 'erpBenchGridToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.plugin.GridMultiHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu'),Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1})];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		this.defaultCondition = this.defaultCondition?this.defaultCondition+condition:condition;//固定条件；从url里面获取
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	} ,
	_noc: 0,
	keyField: null,
	pfField: null,
	url: null,
	relative:null,
	getCount: function(){
		page = 1;
		this.getColumnsAndStore();
	},
	getColumnsAndStore: function(d, g, s){
		d = d || this.getCondition();
		g = g || page;
		//重新计算每页行数
		if(repeatCount){
			s = pageSize = parseInt((height-93)/27);  //减去头尾高度
			repeatCount = false;
		} else{
			s = s || pageSize;
		}
		
		var me = this;
		this.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'bench/datalist/data.action',
        	params: {
        		bscode: Scene,
        		condition:  d, 
        		page: g,
        		pageSize: s,
        		orderby: me.sorts,
        		_noc: (me._noc || getUrlParam('_noc')),
        		_config:getUrlParam('_config'),
        		fromHeader: this.fromHeader|| false
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		if (!response) return;
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		me.setColumnAndStore(res,true);
        	 }
    	 });
	},
	setColumnAndStore:function(res,rendered){
		var me = this;
		var data = res.data != null ? res.data : [];
		dataCount = res.count;
        me.dataCount=dataCount;
        
    	var businesses = parent.Ext.getCmp('businesses');
    	var switchBtn = businesses.layout.getActiveItem().down('erpBusinessFormPanel erpSwitchButton');
    	var activeBtn = switchBtn.getActive();
        if(activeBtn.data.count>0){
        	if(activeBtn.data.bs_iscount!=0){
        		activeBtn.setStat(dataCount);
        	}
        	var switchBtn1 = parent.Ext.getCmp('switch');
        	var activeBtn1 = switchBtn1.getActive();
        	if(!activeBtn1.noactive){
	        	var total = 0;
		        Ext.Array.each(switchBtn.items.items,function(btn,index){
					total += btn.stat;
				});
				activeBtn1.setStat(total);
	        }
        }
       
        var toolbar=me.down('erpBenchGridToolbar');
        if(res.defaultFilterCondition!=null){
			me.defaultFilterCondition = res.defaultFilterCondition;
		}
		if(rendered){
			me.store.loadData(data);
			//zhouy 列表多次筛选出现列错位的情况 暂时解决办法
		    var scrollers=me.query('gridscroller');
		    Ext.Array.each(scrollers,function(scroller){
		    	if(scroller.dock=="bottom"){
		    		var el = scroller.scrollEl,
					elDom = el && el.dom;
					if (elDom) {
						elDom.scrollLeft = Ext.Number.constrain(elDom.scrollLeft, 0, elDom.scrollWidth - elDom.clientWidth)-1;
					}
		    	}
		    });
		    selectRecord(me);
			//修改pagingtoolbar信息
			toolbar.afterOnLoad(page);
		} else {
			if(!Ext.isChrome){
				Ext.each(res.fields, function(f){
					if(f.type == 'date'){
						f.dateFormat = 'Y-m-d H:i:s';
					}
				});
			}
			var store = Ext.create('Ext.data.Store', {
    		    fields: res.fields,
    		    data: data,
    		    filterOnLoad: false 
    		});
			var grid = this;
			columns = Ext.clone(res.columns);
            Ext.Array.each(res.columns, function(column, y) {
				// 处理render
				if(!column.haveRendered && column.renderer != null && column.renderer != ""){
					if(!grid.RenderUtil){
						grid.RenderUtil = Ext.create('erp.util.RenderUtil');
					}
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
            			//这里只能用column.dataIndex来标志，不能用x,y,index等，
            			//grid在render时，checkbox占一列
            		}
            		column.renderer = grid.RenderUtil[renderName];
            		column.haveRendered = true;
            	}
            });
            columns = Ext.Array.insert(columns, 0, [{xtype: 'rownumberer', width: 35, cls: 'x-grid-header-1', align: 'center'}]);
            fields = res.fields;
            var col = me.showRowNum?Ext.Array.insert(res.columns, 0, [{xtype: !me.noSpecialQuery?'newrownumberer':'rownumberer', width: 35, cls: 'x-grid-header-1', align: 'center'}]):res.columns;
    		me.reconfigure(store, col);//用这个方法每次都会add一个checkbox列
    		me.keyField = res.keyField;//form表主键字段
    		me.pfField = res.pfField;//grid表主键字段
    		me.url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
    		me.relative = res.relative;
    		me.batchSet = res.batchSet;
		}
		
		toolbar.afterOnLoad();
		me.setSummary(toolbar,rendered?me.columns:res.columns,res.summarydata);
		if(!rendered){
			me.setQueryCls(res.defaultFilterCondition);	
		}
	},
    listeners: {
         'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		//获取筛选条件
        		grid.getFilterCondition(grid, filters);
                page = 1;
                //考虑部分应用于查询界面，存在form条件
                var form = Ext.getCmp('dealform');
                if(form){
                  this.getCount(caller,form.getCondition(grid));
                }else this.getCount();

        	} else {
        		this.allowFilter = true;
        	}
        	return false;
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
    getCondition: function(){
    	var condition = '',grid = this;
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	if (this.searchGrid) {
    		var s = this.searchGrid.getCondition();
    		if(s != null && s.length > 0) {
    			if(condition.length > 0)
        			condition += ' AND (' + s + ')';
    			else
    				condition = s;
    		}
    	}
    	var rendered = (grid.columns && grid.columns.length > 2);
    	if(rendered && !grid.hasfilter){
			var filter = grid.getPlugin('gridheaderfilters');
			if(filter){
				var filters = filter.parseFilters();
		        if(filters){
		        	grid.getFilterCondition(grid, filters);
		        }
			}
		}
    	if(!Ext.isEmpty(this.filterCondition)) {
    		if(condition == '') {
    			condition = this.filterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
    		}
    	}else if(!Ext.isEmpty(this.defaultFilterCondition)){
    		if(condition == '') {
    			condition = this.defaultFilterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.defaultFilterCondition + ')';
    		}
    	}
    	return condition;
    },
    setSummary:function(t,cols,d){
    	var items = [];
    	if(d){
    		Ext.Array.each(d,function(o){
    			Ext.Array.each(cols,function(c){
    				if(c.dataIndex==o.field && c.text){
    					items.push(c.text + '('+o.type+'):'+Ext.util.Format.number(o.value,'0,000.00'));
    					return false;
    				}
    			});
    	    });
    	}
    	if(items.length>0) t.items.items[18].update(items.join(" | ")); 
    },
    setQueryCls:function(defaultFilterCondition){
		var clean=document.getElementById("clean");
	    clean.classList.remove(defaultFilterCondition==null?"newrownum_clean":"newrownum_uclean");
	    clean.classList.add(defaultFilterCondition==null?"newrownum_uclean":"newrownum_clean");
   },
    getFilterCondition: function(grid,filters){
    	grid.hasfilter = true;
    	if(this.allowFilter){
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
	    	                    		if(f.filterSelect||f.inputEl.dom.disabled||(f.rawValue==''&&f.emptyText==value)){
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
	    	                    			if(!f.autoDim) {
	        	                    			if(SimplizedValue!=value){
	        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
	        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    					        	                    			
	        	                    		}else{
	        	                    			if(SimplizedValue!=value){
	    	                    					value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
	    	                    				}else value = fn + " LIKE '%" + value + "%' ";
	    	                    			}
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
	        this.fromHeader = true;
            this.fromFilter = true;
        }
        this.filterCondition = condition;
    }
});
