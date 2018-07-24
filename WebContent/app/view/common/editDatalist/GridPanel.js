Ext.require([
    'erp.util.*' 
]);
Ext.define('erp.view.common.editDatalist.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpEditDatalistGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
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
        xtype: 'erpEditDatalistToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
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
		d = d || this.getCondition();
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
        			Ext.getCmp('pagingtoolbar').afterOnLoad(page);
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
                    res.columns.push({
	  				    filter: {xtype: 'textfield',editable:false,readOnly:true},
	  				    filterJson_: {},
	                    flex:1,
	                    listeners:{
	                    	afterrender:function(c){
	                    		c.el.dom.children[0].classList.add('x-dl-lastrow')
	                    	}
	                    }
	                });
            		me.reconfigure(store, res.columns);//用这个方法每次都会add一个checkbox列
            		me.getButtons();
            		//修改pagingtoolbar信息
            		Ext.getCmp('pagingtoolbar').afterOnLoad();
            		
        		}       	
        		//拿到datalist对应的单表的关键词
        		keyField = res.keyField;//form表主键字段
        		pfField = res.pfField;//grid表主键字段
        		url = basePath + res.url;//grid行选择之后iframe嵌入的页面链接
        		relative = res.relative;
        		me.setSummary(toolbar,res.columns,res.summarydata);
        	}
        });
	},
	getCount: function(c, d){
		c = c || caller;
		d = d || this.getCondition();
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
        		me.getColumnsAndStore(c);
        	}
        });
	},
	listeners: {
		'itemclick': function(view,record){
			this.lastSelectRecord = record;
		},
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
                    	} else if(f.xtype == 'combo') {
                    		value = fn + " = '" + value + "' ";
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
        },
        viewready: function (grid) {// tip显示单元格完整信息
            var view = grid.view;
            grid.mon(view, {
                uievent: function (type, view, cell, recordIndex, cellIndex, e) {
                    grid.cellIndex = cellIndex;
                    grid.recordIndex = recordIndex;
                }
            });
            grid.tip = Ext.create('Ext.tip.ToolTip', {
                target: view.el,
                delegate: '.x-grid-cell',
                trackMouse: true,
                renderTo: Ext.getBody(),
                listeners: {
                    beforeshow: function updateTipBody(tip) {
                        if (!Ext.isEmpty(grid.cellIndex) && grid.cellIndex !== -1) {
                            header = grid.headerCt.getGridColumns()[grid.cellIndex];
                            tip.update(grid.getStore().getAt(grid.recordIndex).get(header.dataIndex));
                        }
                    }
                }
            });
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
//    	return condition.replace(/=/g, '%3D');
    	return condition;
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
	getEffectData: function(){
		var grid = this;
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
		grid.store.each(function(record){
			if(record.dirty) {
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
			}
		});
		return data;
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
    	if(items.length>0) Ext.getCmp('pagingtoolbar').down('#list_summary').update(items.join(" | ")); 
    }
});