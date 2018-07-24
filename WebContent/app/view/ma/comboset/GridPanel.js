//问题反馈编号：2016120061
Ext.define('erp.view.ma.comboset.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.erpCombolistGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    bodyStyle:'background-color:#f1f1f1;',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	plugins : [ Ext.create('erp.view.core.grid.HeaderFilter'),
			Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	initComponent : function(){ 
		var me = this;
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		me.defaultCondition = this.defaultCondition?this.defaultCondition+condition:condition;//固定条件；从url里面获取
    	Ext.override(Ext.grid.column.Column, {
			// 点列头后，进行后台数据排序查找
			doSort: function(state) {
				var store = me.store;
				store.sort({
					property: this.getSortParam(),
					direction: state
				});
				me.getData();
		    }
		});
    	Ext.apply(me, {
			columns : [{
				xtype: 'rownumberer', 
				cls: 'x-grid-header-1', 
				width:50,
				align: 'center'
			},{
				text : 'ID',
				flex:0,
				width:100,
				cls : 'x-grid-header-1',
				filterName : 'ID',
				hidden:true
				
			},{
				text : '页面CALLER',
				dataIndex : 'CALLER',
				flex:0,
				width:200,
				cls : 'x-grid-header-1',
				filter : {
					filterName:"CALLER",
					xtype:"textfield"
				}
			},{
				text : '界面标题',
				dataIndex : 'TITLE',
				flex:0,
				width:200,
				cls : 'x-grid-header-1',
				filter : {
					filterName:"TITLE",
					xtype:"textfield"
				}
			},{
				text : '界面字段',
				dataIndex : 'FIELDNAME',
				flex:0,
				width:150,
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'textfield',
					filterName : 'FIELDNAME'
				}
			},{
				text : '界面字段描述',
				dataIndex : 'CAPTION',
				flex:0,
				width:200,
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'textfield',
					filterName : 'fieldCAPTION'
				}
			},{
				text : '使用中',
				xtype:'yncolumn',
				dataIndex : 'USING',
				flex:0,
				width:80,
				align: 'center',
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'combo',
					filterName: 'USING',
					autoDim:true,
					exactSearch:true,
					hideTrigger:false,
					ignoreCase:false,
					listeners:{
						change:function(c,nv,ov){
	                    	me.plugins[0].onFilterContainerEnter();// apply when combo change 	                 
						}
					},
					store: {
					    fields: ['display','value'],
					    data : [
					    	{display:'-所有-', value:'-所有-'},
					        {display:'是', value:'是'},
					        {display:'否',value:'否'},
					        {display:'-无-', value:'-无-'}
						]},
				    queryMode: 'local', 
				    displayField: 'display',
				    valueField: 'value'
				}
			},{
				text : '界面类型',
				dataIndex : 'USETYPE',
				flex:0,
				width:80,
				align: 'center',
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'combo',
					filterName: 'USETYPE',
					autoDim:true,
					exactSearch:true,
					hideTrigger:false,
					ignoreCase:false,
					listeners:{
						change:function(c,nv,ov){
	                    	me.plugins[0].onFilterContainerEnter();// apply when combo change 	                 
						}
					},
					store: {
					    fields: ['display','value'],
					    data : [
					    	{display:'-所有-', value:'-所有-'},
					        {display:'FORM', value:'FORM'},
					        {display:'GRID',value:'GRID'},
					        {display:'-无-', value:'-无-'}
						]},
				    queryMode: 'local', 
				    displayField: 'display',
				    valueField: 'value'
				}
			}]
    	});
    	
		this.callParent(arguments);	
		this.getData();
	} ,
	store:Ext.create('Ext.data.Store', {
		filterOnLoad: false,
		storeId:'MyStore',
		fields: [
			{name: 'ID', type: 'int'},
        	{name: 'CALLER', type: 'string'},
        	{name: 'FIELDNAME',  type: 'string'}, 
			{name: 'USING', type: 'int'},
			{name: 'USETYPE', type: 'string'},
        	{name: 'TITLE', type: 'string'},
        	{name: 'CAPTION',  type: 'string'}      
     	],
		autoLoad: false,
	    pageSize: pageSize, // 每页显示条数
	    proxy: {
	        type: 'ajax',
	        url: basePath + 'common/Datalist/getComboData.action',  // 请求URL加载数据
	        reader: {
	            type: 'json',
	            root: 'data',
	            totalProperty: 'count'
	        }
	    }
	}),
	dockedItems: [{
        xtype: 'pagingtoolbar',
        dock: 'bottom',
        store: Ext.data.StoreManager.lookup('MyStore'),
        displayMsg:"显示{0}-{1}条数据，共{2}条数据",
		beforePageText: '第',
        afterPageText: '页,共{0}页',
        displayInfo: true
	}],
	_noc: 0,
	_f: 0,
	getData: function(d, m){
		d = d || this.getCondition();
		var me = this;
		var f = d;
		var params = new Object();
		params._noc = this._noc || getUrlParam('_noc');
		params.condition = f;
		if(!me.filterCondition && !me.searchGrid && !m) {// 大数据如果没有筛选条件 
			var _f = me._f || getUrlParam('_f');
			if(_f == 1) {
				Ext.apply(me.store.pageSize,1000*pageSize);// 直接作1000页数据处理
			}
		}
		
		Ext.apply(me.store.proxy.extraParams,params);
		me.store.loadPage(1);
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn], f = grid.getHeaderFilterField(fn);
                    if(f.xtype == 'datefield')
                    	value = f.getRawValue();
                    if(!Ext.isEmpty(value)) {
                    	if("null"!=value){
                    	if(f.filtertype) {
                    		if (f.filtertype == 'numberfield') {
                    			value = fn + "=" + value + " ";
                    		}
                    	} else {
                    		if(Ext.isDate(value)){
                        		value = Ext.Date.toString(value);
                        		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
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
    	                    				continue;
    	                    			} else {
    	                    				if (f.column && f.column.xtype == 'yncolumn'){
    	                    					if (value == '-无-') {
            	                    				value = fn + ' is null';
            	                    			} else {
            	                    				value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
            	                    			}
             	                    		} else {
             	                    			if (value == '-无-') {
            	                    				value = 'nvl(' + fn + ',\' \')=\' \'';
            	                    			} else {
            	                    				value = fn + " LIKE '" + value + "%' ";
            	                    			}
             	                    		}
    	                    			}
    	                    		} else if(f.xtype == 'datefield') {
    	                    			value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
    	                    		} else if(f.column && f.column.xtype == 'numbercolumn'){
    	                    			if(f.column.format) {
    	                    				var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length; 
    	                    				value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
    	                    			} else
    	                    				value = "to_char(" + fn + ") like '%" + value + "%' ";
    	                    		} else {
    	                    			/**字符串转换下简体*/
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
        	                    			
        	                    		} else if(f.exactSearch){
        	                    			value=fn+"='"+value+"'";
        	                    		} else {
        	                    			if(SimplizedValue!=value){
        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
        	                    			}else value = fn + " LIKE '%" + value + "%' ";       	                    			        	                    			
        	                    		}
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
                this.getData();
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
    getCondition: function(isForm){
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