Ext.define('erp.view.oa.attention.AttentionMainGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpAttentionMainGridPanel',
	layout : 'auto',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    multiselected: [],
    bodyStyle: 'background: #f1f1f1;', 
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpDatalistToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	plugins: [{
            ptype: 'rowexpander',
            rowBodyTpl : [
                 '<p><b>工作状态:</b> {ap_status}</p><br>',
                '<p><b>所有关注项:</b> {ap_maindata}</p>',
                
            ]
        }],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
	initComponent : function(){ 
		this.addEvents({
			beforereconfigure: true
		});
		//condition = this.BaseUtil.getUrlParam('urlcondition');
		//condition = (condition == null) ? "" : condition;
    	//caller = this.BaseUtil.getUrlParam('whoami');
		this.getCount(caller,condition);
		this.callParent(arguments); 
		Ext.getCmp("pagingtoolbar").bind(this.store);
		Ext.getCmp('pagingtoolbar').updateInfo();
	} ,
	getColumnsAndStore: function(c, d, g, s){
	//var gridParam = {caller: "AttentionManage", condition: condition};
		c = c || "AttentionManage";
		d = d || "1=1";
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
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'oa/attention/getAttentionDataAndColumns.action',
        	params: {
        		caller: c,
        		condition:  f, 
        		page: g,
        		pageSize: s
        	},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		main.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        		if(me.columns && me.columns.length > 2){
        			me.store.loadData(data);
        		} else {
        			var store = Ext.create('Ext.data.Store', {
            		    storeId: 'gridStore',
            		    fields: res.fields,
            		    data: data
            		});
        			Ext.each(res.columns, function(c){
        				me.setRenderer(c);
    					if(c.editor){
    						c.locked = true;
    						c.sortable = false;
    					}
    				});
    				var arr=new Array();   			
    				arr.push(res.columns);
        			var columns=res.columns;
        			me.columns=arr;
        			me.store=store;       		
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
        	url : basePath + 'oa/attention/AttentionCounts.action',
        	params: {
        		caller: c,
        		condition: d
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
	listeners: {
        'beforeheaderfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn];
                    if(!Ext.isEmpty(value)){
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
	}
});