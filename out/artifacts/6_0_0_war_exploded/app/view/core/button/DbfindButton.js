Ext.define('erp.view.core.button.DbfindButton',{ 
	extend: 'Ext.Button', 
	alias: 'widget.erpDbfindButton',
	text: 'DBfind设置',
	cls: 'x-btn-gray',
	hidden: false,
	disabled:true,
	style: {
		marginLeft: '10px'
	},
	width: 120,
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	FormUtil:Ext.create('erp.util.FormUtil'),
	dbfindSetUI: function(caller, field, grid){
		var me=this;
		var formData=null,gridData=null,findData=null;
		var arr=new Array();
		grid = grid || Ext.getCmp('grid');
		Ext.Array.each(grid.data,function(item){
			var obj=new Object();
			//obj.display=item.fd_caption;
			obj.display=item.fd_field+(item.fd_caption!=''?' '+item.fd_caption:'');
			obj.value=item.fd_field;
			arr.push(obj);
		});	
		var obj=new Object();		
		obj.display='忽略';
		obj.value='ignore';
		arr.push(obj);		
		var localJson=me.getDbFindSetUI(caller,field);
		if(localJson!=null && localJson.griddata!=null){
			gridData=localJson.griddata;
			formData=localJson.formdata;
			findData=localJson.fields;
		}else {
			gridData=new Array();
			for(var i=0;i<10;i++){
				var o = new Object();
				gridData.push(o);
			}
		}
		function showButton(value,cellmeta){
			var returnStr = "<INPUT align='center' type='button' value='删除' onclick='Delete();'>";
			return returnStr;
		};
		Ext.create('Ext.Window', {
			width: '90%',
			height: '100%',
			autoShow: true,
			layout: 'border',
			title:'<h1>Form查找配置</h1>',
			items: [{
				region:'north',
				xtype: 'form',
				id:'dbform',
				layout: 'column',
				autoScroll:true,
				buttonAlign:'left',//'center',
				bodyStyle: 'background:#f1f1f1;',
				fieldDefaults: {
					labelWidth: 80,
					fieldStyle:'background:#fff;color:#515151;'
				},
				items: [{
					xtype: 'textfield',
					fieldLabel: '描述',
					allowBlank:false,
					/*fieldStyle:'background:#fffac0;color:#515151;',*/
					id:'ds_caption',
					name: 'ds_caption',
					columnWidth: 0.25
				},{
					xtype: 'dbfindtrigger',//'textfield',
					fieldLabel: '查找表名',
					name: 'ds_whichdbfind',
					id:'ds_whichdbfind',
					allowBlank:false,
					/*fieldStyle:'background:#fffac0;color:#515151;',*/
					columnWidth: 0.5
				},{
					xtype: 'dbfindtrigger',
					fieldLabel: '关联字段',
					name: 'ds_likefield',
					id: 'ds_likefield',
					allowBlank:false,
					/*fieldStyle:'background:#fffac0;color:#515151;',*/
					columnWidth: 0.25
				},{
					xtype:'adddbfindtrigger',//'multidbfindtrigger',
					fieldLabel:'所需表',
					name: 'ds_tables',
					id:'ds_tables',
					allowBlank:false,
					/*fieldStyle:'background:#fffac0;color:#515151;',*/
					columnWidth: 0.5,
					listeners:{
						change:function(){
							Ext.getCmp('submitchange').setDisabled(false);			
						}						
					}
				},{
					xtype: 'textfield',
					fieldLabel: '条件',
					name: 'ds_uifixedcondition',
					columnWidth: 0.5
				},{
					xtype: 'textfield',
					fieldLabel: '排序',
					name: 'ds_orderby',
					columnWidth: 0.5
				},{
					xtype: 'textfield',
					fieldLabel: '关联下拉项',
					name: 'ds_dlccaller',
					id:'ds_dlccaller',
					columnWidth: 0.47
				},{
					xtype: 'button',
			        iconCls: 'dlccaller',
			        cls: 'x-btn-tb',
			        columnWidth: 0.02,
			        tooltip: '获取关联下拉项',
			        hidden: false,
			    	handler: function(){
			    		var table=Ext.getCmp('ds_tables').value;
			    		var fields='';
			    		var items=Ext.getCmp('dbgrid').store.data.items;
			    		Ext.each(items,function(i){
	    					if(i.data['ds_type']=='C'){
	    						fields+="'"+i.data['ds_findtoui_f']+"',";
	    					}
	    				});
				    	if(fields.length>0&&table){
				    		fields=fields.substring(0,fields.length-1);
				    		var arr=table.split('#');
				    		var con='';
				    		Ext.each(arr,function(v){
				    			con+="'"+v+"',";
				    		});
				    		con=con.substring(0,con.length-1).toUpperCase();
				    		Ext.Ajax.request({
								url : basePath +'common/getDlccallerByTables.action',
								params: {
									table:con,
									fields:fields
								},
								async: false,
								method : 'post',
								callback : function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo != null){
										showError(res.exceptionInfo);return;
									}
									if(res.success){
										var arr=arr1=arr2=new Array();
										var s1=Ext.getCmp('ds_dlccaller').value;
										arr1=s1.split(',');
										var s2=res.callers;
										var arr2=s2.split(',');
										var arr=Ext.Array.union(arr1,arr2);
										arr=Ext.Array.remove(arr,'');
										Ext.getCmp('ds_dlccaller').setValue(arr.join(','));				                 
									}
								} 
							});
				    	}else{
				    		showError("未配置选择所需表或类型中未配置下拉类型");
				    	}
			    	}
	    		},{
					xtype: 'textfield',
					fieldLabel: '容错提示',
					name: 'ds_error',
					columnWidth: 0.5
				},{
					xtype:'hidden',
					name:'ds_id'
				},{
					xtype:'hidden',
					name:'ds_caller',
					value:caller
				},{
					xtype:'hidden',
					name:'ds_whichui',
					value:field
				}],
				buttons:[{
					xtype : 'tbtext',
					text:'<font color=gray>*点击【确认】根据所需表载入取值字段</font>' //'*点击【确认】根据所需表载入取值字段'
				},'->',{
					xtype:'button',
					text: '确认',
					id:'submitchange',
					iconCls: 'x-button-icon-submit',
					cls: 'x-btn-gray',				
					width: 60,
					disabled:true,
					handler:function(btn){
						var newValue=Ext.getCmp('ds_tables').value;
						Ext.Ajax.request({
							url : basePath +'common/getDbFindFields.action',
							params: {
								table:newValue
							},
							async: false,
							method : 'post',
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo != null){
									showError(res.exceptionInfo);return;
								}
								if(res.success){
									findData=Ext.decode(res.findfields); 					                 
								}

							} 

						});
					}
				},{
					xtype:'button',
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
					cls: 'x-btn-gray',
					formBind: true,//form.isValid() == false时,按钮disabled
					width: 60,
					style: {
						marginLeft: '10px'
					},
					handler:function(){
						me.save(me,caller,field);
					}
				},{
					xtype:'button',
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					width: 60,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){
						btn.ownerCt.ownerCt.ownerCt.close();
					} 
				},'->','->']
			},{
				region:'center',
				xtype: 'grid',
				id:'dbgrid',
				columnLines:true,
				store:Ext.create('Ext.data.Store',{
					fields:['ds_findtoui_f','ds_findtoui_i','ds_dbcaption',{name:'ds_dbwidth',type:'float'},{name:'ds_type',defaultValue:'S'}]
				}),
				necessaryFields:['ds_findtoui_f','ds_dbcaption'],//,'ds_findtoui_i'
				viewConfig: {
					plugins: {
						ptype: 'gridviewdragdrop',
						dragGroup: 'dbgrid',
						dropGroup: 'dbgrid'
					}	    					
				},
				emptyText : $I18N.common.grid.emptyText,
				bodyStyle: 'background-color:#f1f1f1;',
				plugins: Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}),
				listeners:{
					itemclick:function(selModel, record,e,index){			    
						var grid=selModel.ownerCt;
						if(index.toString() == 'NaN'){
							index = '';
						}
						if(index == grid.store.data.items.length-1){//如果选择了最后一行
							var items=grid.store.data.items;
							for(var i=0;i<10;i++){
								var o = new Object();
								grid.store.insert(items.length, o);
								items[items.length-1]['index'] = items.length-1;
							}
						}
					}	
				},
				columns: [{
					cls : "x-grid-header-1",
					text: '取值字段',
					xtype: 'combocolumn',
					dataIndex: 'ds_findtoui_f',
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
					},
					flex: 1,
					format:"",
					editor: {
						format:'',
						xtype: 'combo',
						listConfig:{
							maxHeight:180
						},
						store: {
							fields: ['display', 'value'],
							data :[]
						},
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						onTriggerClick:function(trigger){
							var me=this;
							this.getStore().loadData(findData);
							if (!me.readOnly && !me.disabled) {
								if (me.isExpanded) {
									me.collapse();
								} else {
									me.expand();
								}
								me.inputEl.focus();
							}    
						}
					}
				},{
					cls : "x-grid-header-1",
					text: '赋值字段',
					xtype: 'combocolumn',
					dataIndex: 'ds_findtoui_i',
					flex: 1,
					renderer: function(val, meta, record){
						return val;
					},
					editor: {
						format:"",
						xtype: 'combo',
						listConfig:{
							maxHeight:180
						},
						store: {
							fields: ['display', 'value'],
							data:arr
						},
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local'
					}
				},{
					cls : "x-grid-header-1",
					text: '描述',
					dataIndex: 'ds_dbcaption',
					flex: 1,	
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
					},
					editor:{
						xtype:'textfield',
						format:""						
					}
				},{
					cls : "x-grid-header-1",
					text: '列宽',
					dataIndex: 'ds_dbwidth',
					//format: '0',
					flex:1,
					editor: {
						xtype: 'textfield'
							///format: '0'
					}
				},{
					cls : "x-grid-header-1",
					text: '类型',
					dataIndex: 'ds_type',
					flex: 1,	
					editor:{
						xtype:'combo',
						editable : false,
						store: {
							fields:['display','value'],
							data:[{'display':'字符串','value':'S'},
								{'display':'数字','value':'N'},
								{'display':'日期','value':'D'},
								{'display':'时间','value':'DT'},
								{'display':'是否(-1,0)','value':'YN'},
								{'display':'下拉框','value':'C'},
								{'display':'字符串（忽略大小写）','value':'CS'}]},
   						queryMode: 'local',
    					displayField: 'display',
    					valueField: 'value'
					}
				},{
					xtype:'actioncolumn',
					cls : "x-grid-header-1",
					header:'操作',
					//width:50,
					align:'center',
					items: [{
		                icon : basePath+'resource/images/delete.png',
		                handler: function(grid, rowIndex, colIndex) {
						    grid.getStore().removeAt(rowIndex);
		                }
		            }],
					flex:0.3
				}]
			}]
		});
		if(formData!=null){
			if(formData['ds_caller']=="" || formData['ds_caller']==null || formData['ds_caller']!=caller){
				delete formData['ds_caller'];
				delete formData['ds_id'];
			}
			Ext.getCmp('dbform').getForm().setValues(formData);
		}
		Ext.getCmp('dbgrid').getStore().loadData(gridData);
	},
	dbfindSetGrid:function(caller,grid,field){
		var FindFields=new Array(),me=this,SetFields=new Array(),gridData=new Array(),DBCaller=null,DBKey=null,DBtable=null;
		var me=this;
		function showButton(value,cellmeta){
			var returnStr = "<INPUT align='center' type='button' value='删除' onclick='DeleteGrid();'>";
			return returnStr;
		};
		var localJson=me.getDBGridFields(caller,field);
		if(localJson==null||!localJson.data||!localJson.data.dbcaller){
			//说明无数据
			for(var i=0;i<10;i++){
				var o = new Object();
				o.ds_caller=caller;
				o.ds_triggerfield=field;
				gridData.push(o);
			}Ext.Array.each(grid.data,function(item){
				var obj=new Object();
				obj.display=item.dg_caption;
				obj.value=item.dg_field;
				SetFields.push(obj);
			});
		}else {
				DBCaller=localJson.data.dbcaller;
				DBKey=localJson.data.linkkey;
				DBtable=localJson.data.dbtablename;
				Ext.Array.each(localJson.data.details,function(field){
					var o=new Object();
					o.display=field.dd_fieldcaption;
					o.value=field.dd_fieldname;
					FindFields.push(o);
				});
				Ext.Array.each(grid.data,function(item){
					var obj=new Object();
					obj.display=item.dg_caption;
					obj.value=item.dg_field;
					SetFields.push(obj);
				});
				gridData=localJson.data.dbfindsetgrid;
				if(gridData.length==0) {
					gridData=[{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},
								{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field},{ds_caller:caller,ds_triggerfield:field}];	
				}
					
		}
		Ext.create('Ext.Window', {
			width: '90%',
			height: '100%',
			autoShow: true,
			layout: 'border',
			title:'<h1>Grid查找配置</h1>',
			items: [{
				region:'center',
				xtype: 'grid',
				id:'dbGridgrid',
				columnLines:true,
				store:Ext.create('Ext.data.Store',{
					fields:['ds_id','ds_caller','ds_gridfield','ds_dbfindfield','ds_triggerfield']
				}),
				emptyText : $I18N.common.grid.emptyText,
				bodyStyle: 'background-color:#f1f1f1;',
				plugins: Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1
				}),
				tbar:[{
					xtype:'button',
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
					cls: 'x-btn-gray',
					formBind: true,//form.isValid() == false时,按钮disabled
					width: 60,
					style: {
						margin: '10 5 0 5'
					},
					handler:function(btn){
						me.saveDBFindSetGrid(btn,caller,field);
					}
				},'-',{
					xtype:'button',
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					formBind: true,//form.isValid() == false时,按钮disabled
					width: 60,
					handler:function(btn){
						btn.ownerCt.ownerCt.ownerCt.close();
					} 
				},'-',{
					xtype:'button',
					text: '维护DBFindSet',
					iconCls: 'x-button-icon-submit',
					cls: 'x-btn-gray',				
					width: 120,
					handler:function(btn){
						var value=btn.ownerCt.items.items[6].lastValue;
						if(value){
							openGridUrl(value,"ds_caller", "dd_caller", "jsps/ma/dbFindSet.jsp", "DBFind维护");
						}else{
							me.FormUtil.onAdd('DBfindSet', 'DBFIND设置', 'jsps/ma/dbFindSet.jsp');
						}
					}
				},'-',{
					xtype:'dbfindtrigger',
					fieldLabel:'<h3>DBFindSetCaller</h3>',
					labelWidth:110,
					labelSeparator:"",
					name:'dbcaller',
					id:'dbcaller',
					allowBlank:false,
					value:DBCaller,
					fieldStyle:'background:#fffac0;color:#515151;',
					listeners:{
						change:function(field,newValue){
							Ext.getCmp('commitchange').setDisabled(false);
						},
						blur:function(){

						}
					}
				},{
					xtype:'hidden',
					name:'dbtable',
					id:'dbtablename',
					value:DBtable,
					listeners:{
						change:function(field,newValue){
							var  link=Ext.getCmp('linkkey');
							var table=newValue.toUpperCase().split('LEFT JOIN')[0].replace(/(^\s*)|(\s*$)/g, "");
							link.setDisabled(false);
							Ext.getCmp('linkkey').setValue(null);
						}
					}
				},{
					xtype:'dbfindtrigger',
					labelAlign:'right',
					fieldLabel:'<h3>关联字段名</h3>',
					labelWidth:80,
					labelSeparator:"",
					name:'linkkey',
					id:'linkkey',
					value:DBKey,
					disabled:true,
					allowBlank:false,
					fieldStyle:'background:#fffac0;color:#515151;',
					listeners:{
						afterrender:function(field){
							if(field.value){
								field.setDisabled(false);
							}			
						}	
					}

				},'-',{
					xtype:'button',
					text: '确认',
					id:'commitchange',
					iconCls: 'x-button-icon-submit',
					cls: 'x-btn-gray',				
					width: 60,
					disabled:true,
					handler:function(btn){
						var field=Ext.getCmp('dbcaller');
						var table=Ext.getCmp('dbtablename').value;
						if(field.originalValue==field.value||table==undefined){
						    showMessage('提示','未做任何修改!');
							btn.setDisabled(true);
							return;
						}else{
							//更改下拉store  同时 reset 关联字段
							var json=me.getDBGridFields(field.value,null);
							FindFields=new Array();
							Ext.Array.each(json.data.details,function(field){
								var o=new Object();						
								o.display=field.dd_fieldcaption;
								o.value=field.dd_fieldname;
								FindFields.push(o);
							});
						}
					}
				}],
				listeners:{
					itemclick:function(selModel, record,e,index){			    
						var grid=selModel.ownerCt;
						if(index.toString() == 'NaN'){
							index = '';
						}
						if(index == grid.store.data.items.length-1){//如果选择了最后一行
							var items=grid.store.data.items;
							for(var i=0;i<10;i++){
								var o = new Object();
								o.ds_caller=caller;
								o.ds_triggerfield=field;
								grid.store.insert(items.length, o);
								items[items.length-1]['index'] = items.length-1;
							}
						}
					}	
				},
				columns: [{
					text: 'ID',
					dataIndex: 'ds_id',
					xtype:'numbercolumn',
					width:0	

				},{
					cls : "x-grid-header-1",
					text: 'Caller',
					dataIndex: 'ds_caller',
					flex:1,
					readOnly:true				
				},{
					cls : "x-grid-header-1",
					text:'Grid赋值字段',
					dataIndex: 'ds_gridfield',
					flex:1,
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
					},
					editor: {
						format:"",
						xtype: 'combobox',
						listConfig:{
							maxHeight:180
						},
						typeAhead: true,
						triggerAction: 'all',
						selectOnTab: true,
						store: {
							fields: ['display', 'value'],
							data:SetFields
						},
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local'
					}
				},{
					cls : "x-grid-header-1",
					text:'查找字段',
					dataIndex: 'ds_dbfindfield',
					flex:1,
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
						'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';						  
					},
					editor: {
						format:"",
						xtype: 'combo',
						listConfig:{
							maxHeight:180
						},
						store: {
							fields: ['display', 'value'],
							data:[]
						},
					 	onTriggerClick:function(trigger){
							var me=this;
							this.getStore().loadData(FindFields);
							if (!me.readOnly && !me.disabled) {
								if (me.isExpanded) {
									me.collapse();
								} else {
									me.expand();
								}
								me.inputEl.focus();
							}    
						},
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local'
					}
				},{
					cls : "x-grid-header-1",
					text: '触发DBFIND字段',
					dataIndex:'ds_triggerfield',
					flex:1,
					renderer: function(val, meta, record){
						if(!val){
							val="";
						}
						return val;						  
					},
					editor: {
						format:"",
						xtype: 'combobox',
						listConfig:{
							maxHeight:180
						},
						typeAhead: true,
						triggerAction: 'all',
						selectOnTab: true,
						store: {
							fields: ['display', 'value'],
							data:SetFields
						},
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local'
					}
				},{
					xtype:'actioncolumn',
					cls : "x-grid-header-1",
					header:'操作',
					align:'center',
					items: [{
		                icon : basePath+'resource/images/delete.png',
		                handler: function(grid, rowIndex, colIndex) {
		                	var lastselected=grid.getStore().getAt(rowIndex);
							 var id=lastselected.data.ds_id;
							 if(id!=null){
								 //存在ID 则后台删除
								 Ext.Ajax.request({
										url : basePath + 'common/deleteDbFindSetGrid.action',
										params : {
											id:id
										},
										method : 'post',
										callback : function(options,success,response){
											var res=new Ext.decode(response.responseText);
											if(res.exceptionInfo != null){
												showError(res.exceptionInfo);return;
											}
											if(res.success){
												Ext.Msg.alert('提示','删除成功!');				                 
											}
										}
									});
							 }
						    grid.getStore().removeAt(rowIndex);
		                }
		            }],
					flex:0.3
				
				}]
			}]
		});
		Ext.getCmp('dbGridgrid').getStore().loadData(gridData);
	},
	save:function(me,caller,field){
		var grid=Ext.getCmp('dbgrid');
		var jsonGridData = new Array();
		var dd;
		var s = grid.getStore().data.items,allowsave=true;
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			dd = new Object();
			allowsave=true;
			if(grid.necessaryFields){
				Ext.Array.each(grid.necessaryFields,function(f){
					if(data[f] == null || data[f]==""){
						allowsave=false;
						return false;
					}
				});
				if(allowsave){
					if(data['ds_findtoui_i']==null || data['ds_findtoui_i']=="")data['ds_findtoui_i']="ignore";
					jsonGridData.push(Ext.JSON.encode(data));
				}
			}
		}
		var param=jsonGridData;
		if(param.length==0){
			showError($I18N.common.grid.emptyDetail);
		}else {
			var r = Ext.getCmp('dbform').getForm().getValues();
			var params = new Object();
			params.formStore = unescape(Ext.JSON.encode(r));
			params.gridStore = unescape(param.toString());
			params.caller=caller;
			me.FormUtil.setLoading(true);
			Ext.Ajax.request({
				url : basePath + 'common/saveDbfindSetUI.action',
				params : params,
				method : 'post',
				callback : function(options,success,response){
					var res=new Ext.decode(response.responseText);
					me.FormUtil.setLoading(false);
					if(res.exceptionInfo != null){
						showError(res.exceptionInfo);return;
					}
					if(res.success){
						showMessage('提示','保存成功');
						var jsondata=me.getDbFindSetUI(caller,field);
						Ext.getCmp('dbform').getForm().setValues(jsondata.formdata);
						Ext.getCmp('dbgrid').getStore().loadData(jsondata.griddata);
					}
				}
			});
		}

	},
	saveDBFindSetGrid:function(btn,pagecaller,dgfield){
		var bar=btn.ownerCt,grid=btn.ownerCt.ownerCt;
		var caller=bar.items.items[6].value;
		var table=bar.items.items[7].value;
		var field=bar.items.items[8].value;
		var me=this;
		var jsonGridData = new Array();
		grid.necessaryField='ds_gridfield';
		var dd;
		var s = grid.getStore().data.items;
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			dd = new Object();
			if(s[i].dirty&&data[grid.necessaryField] != null && data[grid.necessaryField] != ""){
				Ext.each(grid.columns, function(c){
					if(c.dataIndex){
						dd[c.dataIndex] = s[i].data[c.dataIndex];
					}
				});
				jsonGridData.push(Ext.JSON.encode(dd));
			}
		}
		if(jsonGridData == null || jsonGridData == '' || jsonGridData.length==0)
			showError($I18N.common.grid.emptyDetail);
		if(caller&&caller!=""&&field&&field!=""){
			Ext.Ajax.request({
				url : basePath +'common/saveDbFindSetGrid.action',
				params: {
					caller:caller,
					field:field,
					table:table,
					dgfield:dgfield,
					gridStore:unescape(jsonGridData.toString())
				},
				method : 'post',
				callback : function(options,success,response){
					var re = new Ext.decode(response.responseText);
					if(re.success){	
						var json=me.getDBGridFields(pagecaller,dgfield);
						grid.getStore().loadData(json.data.dbfindsetgrid);
						showMessage('提示','保存成功!');
					}
					else if(re.exceptionInfo == null){
						showError(res.exceptionInfo);return ;
					}
				} 
			});
		}
	},
	getDBGridFields:function(caller,field){
		var localJson=null;
		Ext.Ajax.request({
			url : basePath +'common/getDbFindSetGridFields.action',
			params: {
				caller:caller,
				field:field
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var re = new Ext.decode(response.responseText);
				if(re.exceptionInfo == null){
					localJson=re;
				}
			} 
		});
		return localJson;
	},
	getDbFindSetUI:function(caller,field){
		var localJson=null;
		Ext.Ajax.request({
			url : basePath +'common/getDbFindSetUI.action',
			params: {
				caller:caller,
				field:field
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);

					return;
				}
				if(res.success){
					localJson=res;			
				}

			} 

		});
		return localJson;
	}
});