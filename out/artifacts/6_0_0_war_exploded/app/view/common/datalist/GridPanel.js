Ext.require([
    'erp.util.*'
]);
Ext.override(Ext.grid.column.Column, {
	// 点列头后，进行后台数据排序查找
	doSort: function(state) {
		var tablePanel = this.up('tablepanel');
		if(typeof tablePanel.getCount === 'function') {
			var sortParam = this.getSortParam(),column=this;
			//var column = tablePanel.down('gridcolumn[dataIndex=' + sortParam + ']');
			/*
			 * nlssort函数 数据类型超长排序出错 暂取消
			 * if(column && (Ext.isEmpty(column.xtype) || column.xtype == 'textcolumn')) {
				sortParam = "nlssort(" + sortParam + ",'NLS_SORT=SCHINESE_PINYIN_M')";
			}*/
			tablePanel.sorts = 'ORDER BY ' + sortParam + ' ' + state;
			tablePanel.getCount();
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
Ext.define('erp.view.common.datalist.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.erpDatalistGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: new Array(),
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpDatalistToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	showRowNum:true,
	autoQuery: true,
	noSpecialQuery:false,//无特殊查询
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('erp.view.core.plugin.GridMultiHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		this.defaultCondition = this.defaultCondition?this.defaultCondition+condition:condition;//固定条件；从url里面获取
    	caller = this.caller || this.BaseUtil.getUrlParam('whoami');
    	if(this.autoQuery) {
    		this.getCount(caller, condition);
    	} else {
    		//易方数据量大  可能明细显示的是datalist 
    		var gridCondition=this.BaseUtil.getUrlParam('gridCondition');
    		if(gridCondition){
    			condition=gridCondition.replace(/IS/g, "=");
    			this.getCount(caller,condition);
    		}
    		else this.getCount(caller, '1=2');
    	}
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	} ,
	_noc: 0,
	_f: 0,
	getColumnsAndStore: function(c, d, g, s,n){
		c = c || caller;
		d = d || this.getCondition();
		g = g || page;
		s = s || pageSize;
		var me = this, rendered = (me.columns && me.columns.length > 2);
		var f = d;
		this.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + (rendered ? 'common/datalist/data.action' : 'common/datalist.action'),
        	params: {
        		_noc: (me._noc || getUrlParam('_noc')),
        		_f: (me._f || getUrlParam('_f')),
        		caller: c,
        		condition:  f, 
        		page: g,
        		pageSize: s,
        		orderby: me.sorts,
        		_self:getUrlParam('_self'),
        		_jobemployee:getUrlParam('_jobemployee'),//岗位权限属性
        		_config:getUrlParam('_config'),
        		fromHeader: this.fromHeader|| false
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		this.fromFilter = false;
        		if (!response) return;
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		_self=res._self;
        		_jobemployee=res._jobemployee;//岗位权限属性
        		var data = res.data != null ? (rendered ? res.data : Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'))) : [];//一定要去掉多余逗号，ie对此很敏感
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
        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
            			Ext.each(me.store.data.items, function(item){
            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
            					me.selModel.select(item);
            				}
            			});
            		}
        			//修改pagingtoolbar信息
        			me.down('erpDatalistToolbar').afterOnLoad(page);
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
            		    //模糊查询的结果在Ext.Array.filter()方法之后，部分数据被过滤掉,设置为false不调用该方法
            		    //yingp
            		    filterOnLoad: false 
            		});
        			var limits = res.limits, limitArr = new Array();
        			if(limits != null && limits.length > 0) {//权限外字段
    					limitArr = Ext.Array.pluck(limits, 'lf_field');
    				}
        			var grid = this;
                    Ext.Array.each(res.columns, function(column, y) {
                    	// power
        				if(limitArr.length > 0 && Ext.Array.contains(limitArr, column.dataIndex)) {
        					column.hidden = true;
        					column.hideable= false;
        				}
        				// 处理render
        				if(!column.haveRendered && column.renderer != null && column.renderer != ""){
        					var grid = Ext.getCmp('grid');
        					grid = grid?grid:this;
        					if(!grid.RenderUtil){
        						grid.RenderUtil = Ext.create('erp.util.RenderUtil');
        					}
                    		var renderName = column.renderer;
                    		if(contains(column.renderer, ':', true)){
                    			var args = new Array();
                    			var arr = column.renderer.split(':');
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
                    			//hey start 列表行颜色  rowstyle:{value1=color1,value2=color2,...}
        	    				if(renderName=='rowstyle')
        	    				{
            	    		    	var Arr = new Array();
            	    		    	if(arr[1]){
            	    		    		var str = arr[1].substring(1,arr[1].length-1);
            	    		    		Arr = str.split(',');
            	    		    	}
            	    		    	if(Arr){ 
        	    		    			Ext.apply(grid.getView(),{
        	    		    				 getRowClass: function(record, rowIndex, rowParams, store){	
        	    		    				 	var color;
        	    		    				 	Ext.Array.each(Arr, function(a) {
        	    		    				 		var file = a.split('=');
        	    		    				 		if(file[0]==record.get(column.dataIndex)){
        	    		    				 			color = file[1];
        	    		    				 		}
        	    		    				 	});
        	    		    				 	return color?color:null;
        	    		    				 }
        	    		    			});
            	    		    	}	
        	    				}
        	    				//hey end 列表行颜色
                    		}
                    		column.renderer = grid.RenderUtil[renderName];
                    		column.haveRendered = true;
                    	}
                    });
                    var col = me.showRowNum?Ext.Array.insert(res.columns, 0, [{xtype: !me.noSpecialQuery?'newrownumberer':'rownumberer', width: 35, cls: !me.noSpecialQuery?'x-grid-header-1':'x-rownumberer', align: 'center'}]):res.columns;
            		me.reconfigure(store, col);//用这个方法每次都会add一个checkbox列
            		me.basecolumns=res.basecolumns;
            		//拿到datalist对应的单表的关键词
            		keyField = res.keyField;//form表主键字段
            		pfField = res.pfField;//grid表主键字段
            		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
            		relative = res.relative;
            		if(res.vastbutton && res.vastbutton == 'erpAddButton'){//[新增]功能
            			Ext.getCmp('erpAddButton').show();
            		}
        		}
        		var toolbar=me.down('erpDatalistToolbar');
        		toolbar.afterOnLoad();
        		//没有关联查询  则关联查询按钮隐藏  
        		if(relative==null){
        			Ext.getCmp('relativelist').hide();
        		}
        		me.setSummary(toolbar,rendered?me.columns:res.columns,res.summarydata);
        		if(!rendered){
        			me.setQueryCls(res.defaultFilterCondition);	
        		}
        	}
        });
	},
	getCount: function(c, d, m,n){
		c = c || this.caller;//兼容多个datalistpanel caller 不一致的
		c = c || caller;
		d = d || this.getCondition();
		var me = this;
		var f = d;
		if(!me.filterCondition && !me.searchGrid && !m) {// 大数据如果没有筛选条件 
			var _f = me._f || getUrlParam('_f');
			if(_f == 1) {
				dataCount = 1000*pageSize;// 直接作1000页数据处理
				me.dataCount=dataCount;
				me.noCount = true;
        		me.getColumnsAndStore(c, d);
        		return;
			}
		}
		Ext.Ajax.request({//拿到grid的数据总数count
        	url : basePath + 'common/datalistCount.action',
        	params: {
        		caller: c,
        		condition: f,
        		_noc: (me._noc || getUrlParam('_noc')),     		
        		fromHeader:this.fromHeader || false,
        		_self:getUrlParam('_self'),
        		_config:getUrlParam('_config'),
        		_jobemployee:getUrlParam('_jobemployee')//岗位权限属性
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}       
        		dataCount = res.count;
        		me.dataCount=dataCount;
        		me.getColumnsAndStore(c, d,'','',n);
        	}
        });
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
        },
        viewready: function () {
           Ext.getCmp('grid').view.el.on('scroll', function () {
           	   var scrollY = Ext.ComponentQuery.query('gridscroller')[1];			   
               if(scrollY.lastScrl!=this.dom.scrollLeft){
               	    Ext.Array.each(Ext.getCmp('grid').columns,function(o){
               	    	if(o.firstFocus){
               	    		o.firstFocus = false
               	    	}
               	    });
               }
           })
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
    getCondition: function(isForm){
    	var grid=this;
    	var condition = '';
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
    	if(!isForm && this.formCondition) {
    		if(condition.length > 0)
    			condition += ' AND (' + this.formCondition + ')';
			else
				condition = this.formCondition;
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
    	if(!Ext.isEmpty(this.filterCondition)||this.fromFilter) {
    		if(!Ext.isEmpty(this.filterCondition)){
	    		if(condition == '') {
	    			condition = this.filterCondition;
	    		} else {
	    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
	    		}
    		}
    	}else if(!Ext.isEmpty(this.defaultFilterCondition)){
    		if(condition == '') {
    			condition = this.defaultFilterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.defaultFilterCondition + ')';
    		}
    	}
    	//考虑部分应用于查询界面，存在form条件 2018-05-16 dyl 反馈：2018050342
        var form = Ext.getCmp('dealform');
        var formCon='';
        if(form && !isForm){
        	formCon=form.getCondition(grid);
        };
        if(!Ext.isEmpty(formCon)){
        	if(condition == '') {
    			condition = formCon;
    		} else {
    			condition = '(' + condition + ') AND (' + formCon + ')';
    		}
        }
    	return condition;// .replace(/=/g, '%3D')
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
    	if(items.length>0) t.down('#list_summary').update(items.join(" | ")); 
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