Ext.QuickTips.init(); 
Ext.define('erp.view.sysmng.upgrade.sql.UpgradSqlDatalist', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.upgradSqlList',
	enableTools : true,
	columnLines : true,
	autoScroll :true ,
	autoHeight:true,
	autoWidth:true,
	layout:'fit',
	id : 'upgradsqllist',
	emptyText : '无数据',
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	requires: ['erp.view.core.plugin.CopyPasteMenu','erp.view.core.grid.HeaderFilter'],
	plugins : [ Ext.create('erp.view.core.grid.HeaderFilter'),
			Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	dockedItems: [{
		    	id : 'upgradsqlToolBar',
		        xtype: 'upgradsqlToolBar',
		        dock: 'bottom',
		        displayInfo: true
			}],
	initComponent : function() {
		var me = this;
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		this.defaultCondition = this.defaultCondition?this.defaultCondition+condition:condition;
		Ext.apply(me, {
			columns : [{
				xtype: 'rownumberer', 
				width: 35, 
				cls: 'x-grid-header-1', 
				align: 'center'
			},{
				text : 'ID',
				flex:1,
				draggable : false,
				cls : 'x-grid-header-simple',
				dataIndex : 'NUM_',
				filter : {
					dataIndex:"NUM_",
					filtertype:'numberfield',
					xtype:"textfield"
				},
				fixed : true
			},{text : '创建人',
				dataIndex : 'MAN_',
				flex:3,
				id:'man_',
				cls : 'x-grid-header-1',
				filter : {
					dataIndex:"MAN_",
					xtype:"textfield"
				}
			},
			 {
				text : '创建时间',
				xtype : 'datecolumn',
				draggable : false,
				cls : 'x-grid-header-1',
				flex:4,
				dataIndex : 'DATE_',
				format : 'Y-m-d H:i:s',
				filter : {
					xtype : 'datefield',
					format : 'Y-m-d',
					dataIndex : 'DATE_'
				}
			},{
				text : '截止版本号',
				dataIndex : 'VERSION_',
				flex:2,
				id:'version_',
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'textfield',
					filtertype:'numberfield',
					dataIndex : 'VERSION_'
				}
			},{
				text : '说明',
				dataIndex : 'DESC_',
				flex:12,
				id:'desc_',
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'textfield',
					dataIndex : 'DESC_'
				}
			},{
				text : '状态',
				dataIndex : 'STATUS_',
				flex:4,
				//xtype:'combocolumn',
				id:'status_',
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'combo',
					dataIndex: 'STATUS_',
					autoDim:true,
					exactSearch:true,
					store: {
					    fields: ['display','value'],
					    data : [
					    	{display:'', value:''},
					        {display:'审核通过', value:1},
					        {display:'未审核通过',value:0}
					]},
				    queryMode: 'local', 
				    displayField: 'display',
				    valueField: 'value'
				},
				renderer:function(value){
					if(value == 0){
						return '未审核通过';
					}else if(value == 1){
						return '审核通过';
					}
				}
			}
			]}),
			
		this.callParent(arguments);
	},
	store : Ext.create('Ext.data.Store', {
				fields: [
						{name: 'MAN_', type: 'int'},
	        			{name: 'MAN_', type: 'string'},
	        			{name: 'DATE_',type: 'string',convert:function(value){  
	            			var DATE_ = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
	            				return DATE_;  
	         				} 
	        			},
	         			{name: 'VERSION_',type: 'float'},
	        			{name: 'DESC_',  type: 'string'},
						{name: 'STATUS_',type: 'int'}, 
						{name: 'NUM_',type: 'int'}            
     			]}),
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
                page = 1;
                this.getCount();
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
	},
	getCount:function(g,con){
		g = g || page;
		var me=this;
		d = this.getCondition();
		var con = d;
		this.setLoading(true);//loading...
		 Ext.Ajax.request({
	    	  url : basePath + 'upgrade/getUpgradeSqlCount.action',
	   	      params: {
	    			   condition:con
	    		   },
	    	 method : 'post',
	    	 callback : function(opt, s, res){
		    	 var r = new Ext.decode(res.responseText);
		    	 me.setLoading(false);
		    	 if(r.exceptionInfo){
		    		 showError(r.exceptionInfo);return;
		    	 } else{
		    		dataCount = r.count;
		    		me.dataCount=dataCount;
        			me.getData(g,con);
		    	 }
	    	 }
	  	 });
	},
	getData:function(g,con){
		var me=this;
		g = g || page;
		d = this.getCondition();
		var con = d;
		me.setLoading(true);
		Ext.Ajax.request({
        	url : basePath + "upgrade/getUpgradeSqlData.action",
        	params: {
        		condition:con, 
        		page: g,
        		pageSize: pageSize
        	},
        	remoteSort: true ,
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(!data || data.length == 0){
        			me.store.removeAll();
        		} else {
        			me.store.loadData(data);
        		}
        		var toolbar=me.down('upgradsqlToolBar');
 
        		toolbar.onLoad();
        		toolbar.onPagingBlur();
        	}
        });
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