Ext.define('erp.view.ma.specialPowerDataList.specialPowerDataList', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpspecialpowerdatalist',
	enableTools : true,
	columnLines : true,
	//title : '特殊岗位权限列表',
	id : 'specialpowerdatalist',
	emptyText : '无数据',
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	plugins : [ Ext.create('erp.view.core.grid.HeaderFilter'),
			Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	dockedItems: [{
		    	id : 'powerDataListpagingtoolbar',
		        xtype: 'erppowerDataListToolBar',
		        dock: 'bottom',
		        displayInfo: true
			}],
	//bbar: {xtype: 'erpToolbar',id:'toolbar'},
	initComponent : function() {
		var me = this;
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		this.defaultCondition = this.defaultCondition?this.defaultCondition+condition:condition;
		Ext.apply(me, {
			columns : [ {
				text : '员工姓名',
				dataIndex : 'JS_EMNAME',
				cls : 'x-grid-header-simple',
				width : 100,
				filter : {
					xtype : 'textfield',
					filterName : 'JS_EMNAME'
				},
				fixed : true,
			}, {
				text : '岗位名称',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'JS_JONAME',
				filter : {
					xtype : 'textfield',
					filterName : 'JS_JONAME'
				},
				fixed : true,

			}, {
				text : '导航描述',
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'JS_DISPLAYNAME',
				filter : {
					xtype : 'textfield',
					filterName : 'JS_DISPLAYNAME'
				},
				fixed : true,
			}, {
				text : 'Caller',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'JS_CALLER',
				filter : {
					xtype : 'textfield',
					filterName : 'JS_CALLER'
				},
				fixed : true,

			}, {
				text : '权限描述',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 200,
				dataIndex : 'SSP_DESC',
				filter : {
					xtype : 'textfield',
					filterName : 'SSP_DESC',
				},
				fixed : true
			} ],
			store : Ext.create('Ext.data.Store', {
				fields : [ 'JP_EMNAME', 'JP_JONAME', 'JP_DISPLAYNAME',
						'JP_CALLER', 'SSP_DESC',],
			})
		});
		this.callParent(arguments);
		 this.getCount();
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {						//重写headerfiltersapply方法
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
    	                    			} 
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
		this.setLoading(true);//loading...
		 Ext.Ajax.request({
	    	  url : basePath + 'ma/getPowerCount.action',
	   	      params: {
	    			   condition:con,
	    			   tableName:'JOB_SPECIALPOWER_VIEW'
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
        	url : basePath + "ma/getPowerData.action",
        	params: {
        		condition:con, 
        		tableName:'JOB_SPECIALPOWER_VIEW',
        		page: g,
        		pageSize: pageSize
        	},
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
        		var toolbar=me.down('erppowerDataListToolBar');
        		toolbar.afterOnLoad();
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