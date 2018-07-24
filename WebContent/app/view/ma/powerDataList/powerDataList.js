Ext.define('erp.view.ma.powerDataList.powerDataList', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.erppowerdatalist',
	enableTools : true,
	columnLines : true,
	//title : '岗位权限列表',
	id : 'powerdatalist',
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
		condition = this.BaseUtil.getUrlParam('urlcondition');     //获取condition
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@@/,"'%").replace(/@@/,"%'");
		this.defaultCondition = this.defaultCondition?this.defaultCondition+condition:condition;
		Ext.apply(me, {
			columns : [ {
				text : '员工姓名',
				dataIndex : 'JP_EMNAME',
				cls : 'x-grid-header-simple',
				width : 80,
				filter : {
					xtype : 'textfield',
					filterName : 'JP_EMNAME'
				},
				fixed : true,
			}, {
				text : '岗位名称',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 80,
				dataIndex : 'JP_JONAME',
				filter : {
					xtype : 'textfield',
					filterName : 'JP_JONAME'
				},
				fixed : true,

			}, {
				text : '导航描述',
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'JP_DISPLAYNAME',
				filter : {
					xtype : 'textfield',
					filterName : 'JP_DISPLAYNAME'
				},
				fixed : true,
			}, {
				text : 'Caller',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 150,
				dataIndex : 'JP_CALLER',
				filter : {
					xtype : 'textfield',
					filterName : 'JP_CALLER'
				},
				fixed : true,

			}, {
				text : '浏览',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_SEE',
				filter : {
					xtype : 'combo',
					filterName : 'JP_SEE',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{
						afterrender : function(comb) { // 设置下拉框默认值
							comb.setValue(null);
						}
						
					}
					
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '新增',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_ADD',
				filter : {
					xtype : 'combo',
					filterName : 'JP_ADD',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '删除',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_DELETE',
				filter : {
					xtype : 'combo',
					filterName : 'JP_DELETE',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '保存',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_SAVE',
				filter : {
					xtype : 'combo',
					filterName : 'JP_SAVE',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '提交',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_COMMIT',
				filter : {
					xtype : 'combo',
					filterName : 'JP_COMMIT',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '反提交',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_UNCOMMIT',
				filter : {
					xtype : 'combo',
					filterName : 'JP_UNCOMMIT',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '审核',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_AUDIT',
				filter : {
					xtype : 'combo',
					filterName : 'JP_AUDIT',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '反审核',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_UNAUDIT',
				filter : {
					xtype : 'combo',
					filterName : 'JP_UNAUDIT',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),				
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '打印',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_PRINT',
				filter : {
					xtype : 'combo',
					filterName : 'JP_PRINT',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				
				fixed : true
			}, {
				text : '禁用',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_DISABLE',
				filter : {
					xtype : 'combo',
					filterName : 'JP_DISABLE',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '反禁用',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_UNDISABLE',
				filter : {
					xtype : 'combo',
					filterName : 'JP_UNDISABLE',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '操作',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_CLOSED',
				filter : {
					xtype : 'combo',
					filterName : 'JP_CLOSED',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '反操作',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_UNCLOSED',
				filter : {
					xtype : 'combo',
					filterName : 'JP_UNCLOSED',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '过账',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_POSTING',
				filter : {
					xtype : 'combo',
					filterName : 'JP_POSTING',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '反过账',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_UNPOSTING',
				filter : {
					xtype : 'combo',
					filterName : 'JP_UNPOSTING',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '浏览所有',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_ALLLIST',
				filter : {
					xtype : 'combo',
					filterName : 'JP_ALLLIST',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '修改他人',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_SAVEOTH',
				filter : {
					xtype : 'combo',
					filterName : 'JP_SAVEOTH',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '打印他人',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_PRINTOTH',
				filter : {
					xtype : 'combo',
					filterName : 'JP_PRINTOTH',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),					
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},
				fixed : true
			}, {
				text : '浏览自己',
				draggable : false,
				cls : 'x-grid-header-simple',
				width : 50,
				dataIndex : 'JP_SELFLIST',
				filter : {
					xtype : 'combo',
					filterName : 'JP_SELFLIST',
					store : Ext.create('Ext.data.Store', {
						fields : [ "Name", "Value" ],
						data : [ {
							Name : "有",
							Value : 1
						}, {
							Name : "无",
							Value : 0
						}, {
							Name : "所有",
							Value : null
						} ]
					}),
					displayField : 'Name',
					valueField : 'Value',
					listeners:{afterrender : function(comb) { // 设置下拉框默认值
						comb.setValue(null);}
					},
				},
				renderer : function readstatus(val, meta, record) {
					if (val == 1)
						return '<span>有</span>';
					else if (val == 0)
						return '<span>无</span>';
				},

				fixed : true
			}, ],
			store : Ext.create('Ext.data.Store', {
				fields : [ 'JP_EMNAME', 'JP_JONAME', 'JP_DISPLAYNAME',
						'JP_CALLER', 'JP_SEE', 'JP_ADD', 'JP_DELETE',
						'JP_SAVE', 'JP_COMMIT', 'JP_UNCOMMIT', 'JP_AUDIT',
						'JP_UNAUDIT', 'JP_PRINT', 'JP_DISABLE', 'JP_UNDISABLE',
						'JP_CLOSED', 'JP_UNCLOSED', 'JP_POSTING',
						'JP_UNPOSTING', 'JP_ALLLIST', 'JP_SAVEOTH',
						'JP_PRINTOTH', 'JP_SELFLIST' ],
			})
		});
		this.callParent(arguments);
		 this.getCount();
	},
	listeners: {	
        'headerfiltersapply': function(grid, filters) {        //重写headerfiltersapply方法
        	if(this.allowFilter){
        		var condition = null;
                for(var fn in filters){
                    var value = filters[fn], f = grid.getHeaderFilterField(fn);
                    console.log(fn);
                    if(f.xtype == 'datefield')
                    	value = f.getRawValue();
                    if(!Ext.isEmpty(value)) {
                    	if("null"!=value){
                    	if(f.filtertype) {
                    		if (f.filtertype == 'numberfield') {
                    			value = fn + "=" + value + " ";
                    		}
                    	} else {
    	                    		if (f.xtype == 'combo' || f.xtype == 'combofield') {
    	                    			if (value == '-所有-') {
    	                    				continue;
    	                    			} else if (value == '-无-') {
        	                    				value = 'nvl(' + fn + ',\' \')=\' \'';
        	                    			} else {
        	                    				value = fn + " LIKE '" + value + "%' ";
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
                    	else value ="nvl("+fn+",' ')=' '";
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
                this.getData(page,condition);
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
	getCount:function(g,con){
		g = g || page;
		con = con || condition;
		var me=this;
		this.setLoading(true);//loading...
		 Ext.Ajax.request({
	    	  url : basePath + 'ma/getPowerCount.action',
	   	      params: {
	    			   condition:con,
	    			   tableName:'JOB_POWER_VIEW'
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
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "ma/getPowerData.action",
        	params: {
        		condition:  con, 
        		tableName:'JOB_POWER_VIEW',
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
        		toolbar.afterOnLoad(page);
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