Ext.define('erp.view.common.vastDatalist.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpVastDatalistGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: [],
    bodyStyle: 'background: #f1f1f1;',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	ignoreRightMouseSelection : false,
		listeners:{
            selectionchange:function(selectionModel, selected, options){
          
            }
        },
        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
        	var me = Ext.getCmp('grid');
        	var bool = true;
        	var items = me.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(item &&record==item&&item.index == record.index){
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
        },
        onHeaderClick: function(headerCt, header, e) {
        	if (header.isCheckerHd) {
                e.stopEvent();
                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
                if (isChecked) {
                    this.deselectAll(true);
                    var grid = Ext.getCmp('grid');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
	                Ext.each(els, function(el, index){
	                	el.setAttribute('class','x-grid-row-checker');
	                });
                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                } else {
                	var grid = Ext.getCmp('grid');
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
        }
	}),
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpVastDatalistToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters'),
              Ext.create('Ext.grid.plugin.CellEditing', {
                  clicksToEdit: 1
              })],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){ 
		this.addEvents({
			beforereconfigure: true
		});
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
    	caller = this.BaseUtil.getUrlParam('whoami');
		this.getCount(caller, condition);
		this.callParent(arguments); 
		Ext.getCmp("pagingtoolbar").bind(this.store);
		Ext.getCmp('pagingtoolbar').updateInfo();
	} ,
	_noc: 0,
	getColumnsAndStore: function(c, d, g, s){
		c = c || caller;
		d = d || condition;
		g = g || page;
		s = s || pageSize;
		var me = this;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		this.multiselected = [];
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: c,
        		_noc:(me._noc || getUrlParam('_noc')),
        		condition:  f, 
        		page: g,
        		pageSize: s
        	},
        	method : 'post',
        	//async: false,
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        		if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        			//修改pagingtoolbar信息
        		Ext.getCmp('pagingtoolbar').updateInfo();
        		} else {
        			var store = Ext.create('Ext.data.Store', {
            		    storeId: 'gridStore',
            		    fields: res.fields,
            		    data: data
            		});
        			//me.fireEvent('beforereconfigure', me, res.columns, store);
        			Ext.each(res.columns, function(c){
        				me.setRenderer(c);
    					if(c.editor){
//    						c.locked = true;
    						c.sortable = false;
    					}
    				});
        			/*me.columns = res.columns;
        			me.store = store;*/
            		me.reconfigure(store, res.columns);
            		//拿到datalist对应的单表的关键词
            		keyField = res.keyField;
            		/*me.on('afterrender', function(){
            			me.getButtons();
            		});*/
        		}
        		//修改pagingtoolbar信息
        		Ext.getCmp('pagingtoolbar').updateInfo();
        	}
        });
	},
	getCount: function(c, d){
		c = c || caller;
		d = d || condition;
		var me = this;
		var f = d;
		if(me.filterCondition){
			if(d == null || d == ''){
				f = me.filterCondition;
			} else {
				f += ' AND ' + me.filterCondition;
			}
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + '/common/datalistCount.action',
        	params: {
        		caller: c,
        		condition: f,
        		_noc:(me._noc || getUrlParam('_noc'))
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		dataCount = res.count;
        		me.getColumnsAndStore();
        	}
        });
	},
	/**
	 * buttons由原先在datalist里面配置改为在GridButton表配置；
	 * 支持多个button；
	 * 支持传递actionName
	 */
	getButtons: function(){
		Ext.Ajax.request({
	   		url : basePath + "common/gridButton.action",
	   		params: {
	   			caller: caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.exceptionInfo){
    				showError(localJson.exceptionInfo);
    			}
    			if(localJson.buttons){
    				var buttons = Ext.decode(localJson.buttons);
    				Ext.each(buttons, function(b){
    					var btn = Ext.ComponentQuery.query(b.xtype);
                		if(btn.length > 0){
                			btn[0].url = b.url;
                			btn[0].show();
                		}
    				});
    			}
	   		}
		});
	},
	listeners: {
        'beforeheaderfiltersapply': function(grid, filters) {
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
                this.fromHeader = true;
                this.fromFilter = true;
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
    viewConfig: {
        stripeRows: true
    },
    getMultiSelected: function(){
		var grid = this;
		var records = grid.multiselected;
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	records.push(item);
        });
		return Ext.Array.unique(records);
	},
	getEffectData: function(){
		var grid = this;
    	var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[keyField] != null && this.data[keyField] != ''
        		&& this.data[keyField] != '0' && this.data[keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
        var records = Ext.Array.unique(grid.multiselected);
        var fields = new Array();
		fields.push({name: keyField, type: 'number'});
		Ext.each(grid.columns, function(c, index){
			if (c.editor != null || (c.getEditor && c.getEditor() != null)) {
				var type = 'string';
				if(c.xtype == 'datecolumn'){
					type = 'date';
				} else if(c.xtype == 'datetimecolumn'){
					type = 'datetime';
				}
				fields.push({
					name: c.dataIndex,
					type: type 
				});
			}
		});
		var data = new Array();
		var o = null;
		Ext.each(records, function(record, index){
			o = new Object();
			Ext.each(fields, function(f){
				var v = record.data[f.name];
				if(f.type == 'date'){
					if (Ext.isDate(v)) {
						v = Ext.Date.format(v, 'Y-m-d');
					}
				} else if(f.type == 'datetime'){
					if (Ext.isDate(v)) {
						v = Ext.Date.format(v, 'Y-m-d H:i:s');
					}
				}
				o[f.name] = v;
			});
			data.push(o);
		});
		return data;
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
	 getCondition: function(){
	    	var condition = '';
	    	if(!Ext.isEmpty(this.defaultCondition)) {
	    		condition = this.defaultCondition;
	    	}
	    	if(this.formCondition) {
	    		if(condition.length > 0)
	    			condition += ' AND (' + this.formCondition + ')';
				else
					condition = this.formCondition;
	    	}
	    	if(!Ext.isEmpty(this.filterCondition)) {
	    		if(condition == '') {
	    			condition = this.filterCondition;
	    		} else {
	    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
	    		}
	    	}
	    	return condition;// .replace(/=/g, '%3D')
	    }
});