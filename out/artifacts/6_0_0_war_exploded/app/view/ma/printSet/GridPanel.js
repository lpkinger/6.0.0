Ext.define('erp.view.ma.printSet.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpPrintSetGridPanel',
	layout : 'fit',
	id: 'grid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    bodyStyle: 'background-color:#f1f1f1;',
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	plugins: [
	Ext.create('erp.view.core.grid.HeaderFilter'),
	Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	dockedItems: [{
    	id : 'pagingtoolbar',
        xtype: 'erpPrintSetToolbar',
        dock: 'bottom',
        displayInfo: true
	}],
	initComponent : function(){
		var me=this;
		Ext.apply(me,{
			columns:[{
				dataIndex: 'id',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	width: 0,
		    	format:'',
		    	text: 'ID',
		    	filter:{xtype:"numberfield",	filterName: 'id'}
			},{
				dataIndex:'caller',
				cls: "x-grid-header-1",
				text:'<br/>CALLER',
				sortable:false,format:'',
				renderer:function(val){},
				width:150,
				filter: {xtype:"textfield", filterName:"caller"},
				renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
					}
				},
				editor:{
					xtype:'textfield',
					field:'caller'
				}
			},{
		    	dataIndex: 'title',
    			cls: 'x-grid-header-1',
    			sortable: false,
    			text: '<br/>打印标题',
    			width: 150,format:'',
    			filter:{xtype:"textfield",filterName:'title'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
					}
				},
				editor:{
					xtype:'textfield',
					field:'title'
				}
    		},{
		    	dataIndex: 'reportname',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>文件名称',
		    	width: 150,format:'',
		    	filter:{xtype: 'textfield',filterName:'reportname'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + '' + '</span>';
					}
				},
				editor:{
					xtype:'textfield',
					field:'reportname'
				}		
		    },{
		    	dataIndex: 'printtype',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>输出类型',
		    	width: 150,format:'PREVIEW',
		    	defaultValue:'',
		    	filter:{
		    		xtype: 'combo',
		    		queryMode: 'local',
		    		filterName:'printtype',
		    		displayField: 'display',
		    		valueField: 'value',
		    		store:{data:[{display: "所有", value: ""},
		    		             {display: "直接下载PDF", value: "PDF"},{display: "直接下载纯数据EXCEL", value: "EXCEL"}
		    					,{display: "直接打印", value: "PRINT"},{display: "进入预览", value: "PREVIEW"}],
		    			   fields: ["display", "value"]
		    		}},
    			renderer:function(val){
					switch (val) {
					 case 'PDF'		:rVal = "直接下载PDF"; break;
                     case 'EXCEL'	:rVal = "直接下载纯数据EXCEL";  break;
                     case 'PRINT'	:rVal = "直接打印"; break;
                     case 'PREVIEW' :rVal = "进入预览";break;
                     default :rVal = "进入预览";break;
                    }
                    return rVal;
				},
				editor:{
					xtype:'combo',
					field:'printtype',
					queryMode: 'local',
		    		displayField: 'display',
		    		valueField: 'value',
					store:{data:[{display: "直接下载PDF", value: "PDF"},{display: "直接下载纯数据EXCEL", value: "EXCEL"}
		    					,{display: "直接打印", value: "PRINT"},{display: "进入预览", value: "PREVIEW"}],
		    		fields: ["display", "value"]}
				}		
		    },{
		    	dataIndex: 'isdefault',
		    	xtype: 'checkcolumn',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>默认',
		    	width: 60,format:'',
		    	editor: {
					cls: 'x-grid-checkheader-editor',
					editable: true,
					xtype: 'checkbox'
		    	},
				filter:{xtype:'textfield',filterName:'isdefault'}
    		},{
    			dataIndex: 'needaudit',
    			xtype: 'checkcolumn',
    			cls: 'x-grid-header-1',
    			sortable: false,
    			text: '已审核<br/>才能打印',
    			width: 80,format:'',
		    	editor: {
					cls: 'x-grid-checkheader-editor',
					editable: true,
					xtype: 'checkbox'
		    	},
				filter:{xtype: 'textfield',filterName:'needaudit'}
    		},{
	    		dataIndex: 'nopost',
		    	xtype: 'checkcolumn',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '已过账<br/>不允许打印',
		    	width: 80,format:'',
		    	editor: {
					cls: 'x-grid-checkheader-editor',
					editable: true,
					xtype: 'checkbox'
		    	},
				filter:{xtype: 'textfield',filterName:'nopost'}
		    },{
		    	dataIndex: 'needenoughstock',
		    	xtype: 'checkcolumn',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '库存不足<br/>不允许打印',
		    	width: 80,format:'',
		    	editor: {
					cls: 'x-grid-checkheader-editor',
					editable: true,
					xtype: 'checkbox'
		    	},
				filter:{xtype: 'textfield',filterName:'needenoughstock'}
    		},{
		    	dataIndex: 'allowmultiple',
		    	xtype: 'checkcolumn',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '允许<br/>多次打印',
		    	width: 80,
		    	editor: {
					cls: 'x-grid-checkheader-editor',
					editable: true,
					xtype: 'checkbox'
		    	},
				filter:{xtype: 'textfield',filterName:'allowmultiple'}
		    },{
		    	dataIndex: 'countfield',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>打印次数字段',
		    	width: 100,
		    	filter:{xtype: 'textfield',filterName:'countfield'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {return '';}
				},
				editor:{
					xtype:'textfield',
					field:'countfield'
				}
		    },{
		    	dataIndex: 'statusfield',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>打印状态字段',
		    	width: 100,format:'',
    			filter:{xtype: 'textfield',filterName:'statusfield'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {return '';}
				},
				editor:{
					xtype:'textfield',
					field:'statusfield'
				}
   			 },{
		    	dataIndex: 'statuscodefield',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>打印状态码字段',
		    	width: 110,format:'',
		    	filter:{xtype: 'textfield',filterName:'statuscodefield'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {
						return '';
					}
				},
				editor:{
					xtype:'textfield',
					field:'statuscodefield'
				}
    		},{
		    	dataIndex: 'handlermethod',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>打印前执行逻辑',
		    	width: 120,format:'',
		    	filter:{xtype: 'textfield',filterName:'handlermethod'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {return '';}
				},
				editor:{
					xtype:'textfield',
					field:'handlermethod'
				}		
    		},{
		    	dataIndex: 'defaultcondition',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>默认条件',
		    	width: 150,format:'',
		    	filter:{xtype: 'textfield',filterName:'defaultcondition'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {return '';}
				},
				editor:{
					xtype:'textfield',
					field:'defaultcondition'
				}		
  			},{
		    	dataIndex: 'tablename',
		    	cls: 'x-grid-header-1',
		    	sortable: false,
		    	text: '<br/>表名',
		    	width: 150,format:'',
		    	filter:{xtype: 'textfield',filterName:'tablename'},
    			renderer:function(val){
					if(val != null && val.toString().trim() != ''){
						return val;
					} else {return '';}
				},
				editor:{
					xtype:'textfield',
					field:'tablename'
				}		
  			}],
			store:Ext.create('Ext.data.Store',{
				fields:['id','caller','title','reportname','printtype','isdefault','needaudit',
				'nopost','needenoughstock','countfield','statusfield','statuscodefield','allowmultiple',
				'handlermethod','defaultcondition','tablename']
			})     
		});
		this.getCount();
		this.callParent(arguments);
	},
	getCount:function(g,con){
		g = g || page;
		var me=this;
		this.setLoading(true);//loading...
		 Ext.Ajax.request({
	    	  url : basePath + 'common/JasperReportPrint/getCount.action',
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
		me.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/JasperReportPrint/getData.action",
        	params: {
        		condition:  con, 
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
        		var toolbar=me.down('erpPrintSetToolbar');
        		toolbar.afterOnLoad();
        		//自定义event
        		me.addEvents({
        		    storeloaded: true
        		});
        		me.fireEvent('storeloaded', me, data);
        	}
        });
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {console.log(filters);
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
                this.getCount(page,condition);
        	} else {
        		this.allowFilter = true;
        	}
        	return false;
        }
    },
    getGridStore:function(){
		var me = this,grid=this,
		jsonGridData = new Array();
		var s = grid.getStore().data.items;//获取store里面的数据
		for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
			var data = s[i].data;
			dd = new Object();
			if(s[i].dirty && !me.GridUtil.isBlank(me, data)){
				Ext.each(grid.columns, function(c){
					if(c.dataIndex=='needenoughstock'||c.dataIndex=='allowmultiple'||c.dataIndex=='nopost'
					||c.dataIndex=='needaudit'||c.dataIndex=='isdefault'){
						dd[c.dataIndex] = s[i].data[c.dataIndex]?-1:0;
					}else{
						dd[c.dataIndex] = s[i].data[c.dataIndex];
					}
					
				});
				jsonGridData.push(Ext.JSON.encode(dd));
			}
		}
		return jsonGridData;	
    },
    save:function(){
    	var grid=this;
    	var param = grid.getGridStore();
    	grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath +'common/JasperReportPrint/save.action',
			params : {param:unescape(param)},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					Ext.Msg.alert('提示', '保存成功', function(){
						grid.getCount(1,grid.filterCondition);
					});					
				}else{
					saveFailure();
				}
			}
		});
    },
    deleteRecord:function(){
    	var grid=this;
    	var records = grid.selModel.getSelection();
		if(records.length > 0&&records[0].data.id!=0&&records[0].data.id!=''){
			warnMsg($I18N.common.msg.ask_del, function(btn){
				if(btn == 'yes'){
					grid.setLoading(true);//loading...
					Ext.Ajax.request({
						url : basePath + 'common/JasperReportPrint/delete.action',
						params: {id:records[0].data.id},
						method : 'post',
				 		callback : function(options,success,response){
				   			grid.setLoading(false);
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.success){
				   				Ext.Msg.alert('提示', '删除成功', function(){
									grid.getCount(1,'');
								});	
				   			}else {
							   delFailure();
							}
				   		}
					});							   		
				}
			});
		}
    }					
});