Ext.require([
    'erp.util.*' 
]);
Ext.define('erp.view.drp.aftersale.repair2order.GridPanel',{
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpRepair2OrderGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: new Array(),
    multiselected: [],
    bodyStyle:'background-color:#f1f1f1;',
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
        xtype: 'erpRepair2OrderToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	initComponent : function(){ 
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;//固定条件；从url里面获取
    	caller = this.caller || this.BaseUtil.getUrlParam('whoami');
		this.getCount(caller, condition);
		this.callParent(arguments); 
		this.addEvents({
		    keydown: true
		});
	} ,
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
		this.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/datalist.action',
        	params: {
        		caller: c,
        		condition:  f, 
        		page: g,
        		pageSize: s
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];//一定要去掉多余逗号，ie对此很敏感
        		if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        			if(me.lastSelected && me.lastSelected.length > 0){//grid刷新后，仍然选中上次选中的record
            			Ext.each(me.store.data.items, function(item){
            				if(item.data[keyField] == me.lastSelected[0].data[keyField]){
            					me.selModel.select(item);
            				}
            			});
            		}
        			//修改pagingtoolbar信息
            		Ext.getCmp('pagingtoolbar').afterOnLoad();
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
    				//处理render
        			var grid = this;
                    Ext.Array.each(res.columns, function(column, y) {   
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
            		me.reconfigure(store, res.columns);//用这个方法每次都会add一个checkbox列
        		}
        		//修改pagingtoolbar信息
        		Ext.getCmp('pagingtoolbar').afterOnLoad();
        		//拿到datalist对应的单表的关键词
        		keyField = res.keyField;//form表主键字段
        		pfField = res.pfField;//grid表主键字段
        		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
        		relative = res.relative;
        		if(res.vastbutton && res.vastbutton == 'erpAddButton'){//[新增]功能
        			Ext.getCmp('erpAddButton').show();
        		}
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
        		condition: f
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}
        		dataCount = res.count;
        		me.getColumnsAndStore(c, d);
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
                page = 1;
                this.getCount();
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
    	var condition = '';
    	if(!Ext.isEmpty(this.defaultCondition)) {
    		condition = this.defaultCondition;
    	}
    	if(!Ext.isEmpty(this.filterCondition)) {
    		if(condition == '') {
    			condition = this.filterCondition;
    		} else {
    			condition = '(' + condition + ') AND (' + this.filterCondition + ')';
    		}
    	}
    	return condition.replace(/=/g, '%3D');
    }
});