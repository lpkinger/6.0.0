Ext.QuickTips.init(); 
Ext.define('erp.view.sysmng.basicset.dictionary.DictionnaryDatalist', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpdictionnarydatalist',
	enableTools : true,
	columnLines : true,
	autoScroll :true ,
	layout:'fit',
	id : 'dictionnarydatalist',
	emptyText : '无数据',
	GridUtil : Ext.create('erp.util.GridUtil'),
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	requires: ['erp.view.core.plugin.CopyPasteMenu','erp.view.core.grid.HeaderFilter'],
	plugins : [ Ext.create('erp.view.core.grid.HeaderFilter'),
			Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	dockedItems: [{
		    	id : 'datadictionaryToolBar',
		        xtype: 'erpdatadictionaryToolBar',
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
			}, {
				text : '表名',
				dataIndex : 'OBJECT_NAME',
				flex:1,
				id:'OBJECT_NAME',
				cls : 'x-grid-header-1',
				filter : {
					xtype : 'textfield',
					cls : 'object_name',
					filterName : 'OBJECT_NAME',

				},
			}, {
				text : '注释',
				draggable : false,
				cls : 'x-grid-header-1',
				flex:1.5,
				dataIndex : 'COMMENTS',
				filter : {
					xtype : 'textfield',
					filterName : 'COMMENTS',
	                id:'test',
				},

			}, 
			 {
				text : '创建时间',
				draggable : false,
				cls : 'x-grid-header-1',
				flex:1,
				dataIndex : 'CREATED',
				filter : {
					xtype : 'datetimefield',
					format : 'Y-m-d H:i:s',
					filterName : 'CREATED'
				},

			}, {
				text : '修改时间',
				draggable : false,
				cls : 'x-grid-header-1',
				flex:1,
				dataIndex : 'TIMESTAMP',
				filter : {
					xtype : 'datetimefield',
					format : 'Y-m-d H:i:s',
					filterName : 'TIMESTAMP'
				},

			}, {
				text : '最后DDL时间',
				draggable : false,
				cls : 'x-grid-header-1',
				flex:1,
				dataIndex : 'LAST_DDL_TIME',
				format : 'Y-m-d H:i:s',
				filter : {
					xtype : 'datetimefield',
					format : 'Y-m-d H:i:s',
					filterName : 'LAST_DDL_TIME',
				},
			},{
				text : 'ID',
				draggable : false,
				cls : 'x-grid-header-simple',
				dataIndex : 'OBJECT_ID',
				hidden:true,
				}
			],
			store : Ext.create('Ext.data.Store', {
			fields: [
        			 {name: 'OBJECT_NAME', type: 'string'},
        			 {name: 'COMMENTS',  type: 'string'},
        			 {name: 'CREATED',type: 'string',convert:function(value){  
            				var CREATED = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
            					 return CREATED;  
         				} 
        				 },
         			{name: 'TIMESTAMP',type: 'string'},
        			 {name: 'LAST_DDL_TIME',type: 'string',convert:function(value){  
            			var LAST_DDL_TIME = Ext.Date.format(new Date(value),"Y-m-d H:i:s");
            			 return LAST_DDL_TIME;  
        			 } 
        			 },
        			 {name: 'OBJECT_ID',  type: 'string'},             
     ]
			})
		});
		this.callParent(arguments);
		this.getCount();
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
	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "'  ";
	                    	} else if(exp_t.test(value)){
	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "'  ";
	                    	} else{
	                    		 if(f.xtype == 'datefield') {
	                    			value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%'  ";
	                    		} else if(f.xtype == 'datecolumn') {
	                    			value = "to_char(" + fn + ",'yyyy-MM-dd' H:i:s) like '%" + value + "%' ";
	                    		}else if(f.column && f.column.xtype == 'numbercolumn'){
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
    	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')"; //%f% 包含字母搜索    f%首字母搜索
    	                    			}else value = fn + " LIKE '%" + value + "%' ";       	                    			
    	                    			
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
            this.getCount(page,condition);
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
		if(con==null||con==""){
			con=con+"  object_type='TABLE'";
		}else{
			con=con+" and  object_type='TABLE'"
		}
		this.setLoading(true);//loading...
		 Ext.Ajax.request({
	    	  url : basePath + 'ma/getPowerCount.action',
	   	      params: {
	    			   condition:con,
	    			   tableName:'USER_OBJECTS A LEFT JOIN USER_TAB_COMMENTS  B ON A.OBJECT_NAME=b.TABLE_NAME'
	    		   },
	    	 method : 'post',
	    	 success : function(res){
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
		if(con==null||con==""){
			con=con+"  object_type='TABLE'";
		}else{
			con=con+"and  object_type='TABLE'"
		}
		me.setLoading(true);
		Ext.Ajax.request({
        	url : basePath+"sysmng/getDictionaryData.action",
        	params: {
        		condition:con, 
        		tableName:'USER_OBJECTS A LEFT JOIN USER_TAB_COMMENTS  B ON A.OBJECT_NAME=b.TABLE_NAME',
        		page: g,
        		pageSize: pageSize
        	},
        	remoteSort: true ,
        	method : 'post',
        	success : function(response){
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
        		var toolbar=me.down('erpdatadictionaryToolBar');
        		//toolbar.afterOnLoad();
        		toolbar.onLoad();
        		toolbar.onPagingBlur();
        		//自定义event
        		me.addEvents({
        		    storeloaded: true
        		});
        		me.fireEvent('storeloaded', me, data);
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