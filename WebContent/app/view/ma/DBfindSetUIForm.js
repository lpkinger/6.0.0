 Ext.define('erp.view.ma.DBfindSetUIForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.DBfindSetUIForm',
	requires: ['erp.view.core.button.Save','erp.view.core.button.Close','erp.view.core.trigger.DbfindTrigger','erp.view.core.form.YnField'],
	id: 'DBfindSetUIForm',
    FormUtil: Ext.create('erp.util.FormUtil'),	
	region:'north',						
	layout: 'column',
	autoScroll:true,
	buttonAlign:'left',//'center',
	bodyStyle: 'background:#f1f1f1;',
	fieldDefaults: {
					labelWidth: 80,
					fieldStyle:'background:#FFFAFA;color:#515151;'
				},
	
	
	
    items: [
    		{
				xtype: 'textfield',
				fieldLabel: '描述',				
				allowBlank:false,
				margin:'5,0,0,0',
				//margin_right:'5px',
				fieldStyle:'background:#fffac0;color:#515151;',
				id:'ds_caption',
				name: 'ds_caption',
				columnWidth: 0.25
			},
			{
					xtype: 'dbfindtrigger',//'textfield',
					fieldLabel: '查找表名',
					name: 'ds_whichdbfind',
					id:'ds_whichdbfind',
					margin:'5,0,0,0',
					allowBlank:false,
					fieldStyle:'background:#fffac0;color:#515151;',
					columnWidth: 0.5
				},{
					xtype: 'dbfindtrigger',
					fieldLabel: '关联字段',
					name: 'ds_likefield',
					margin:'5,0,0,0',
					id: 'ds_likefield',
					allowBlank:false,
					fieldStyle:'background:#fffac0;color:#515151;',
					columnWidth: 0.25
				},
    			{
					xtype:'adddbfindtrigger',//'multidbfindtrigger',
					fieldLabel:'所需表',
					name: 'ds_tables',
					id:'ds_tables',
					margin:'5,0,0,0',
					allowBlank:false,
					fieldStyle:'background:#fffac0;color:#515151;',
					columnWidth: 0.5,
					listeners:{
						change:function(){
							Ext.getCmp('submitchange').setDisabled(false);			
						}						
					}
				},{
					xtype:'textfield',
					fieldLabel: 'dbfind字段',
					allowBlank:false,
					margin:'5,0,0,0',
					name:'ds_whichui',
					fieldStyle:'background:#fffac0;color:#515151;',
					columnWidth: 0.5
					//value:field
				},
				{
					xtype:'textfield',
					fieldLabel: '页面caller',
					name:'ds_caller',
					margin:'5,0,0,0',
					columnWidth: 0.5
					//value:caller
				},											
				{
					xtype: 'textfield',
					fieldLabel: '条件',
					margin:'5,0,0,0',
					name: 'ds_uifixedcondition',
					columnWidth: 0.5
				},{
					xtype: 'textfield',
					fieldLabel: '排序',
					margin:'5,0,0,0',
					name: 'ds_orderby',
					columnWidth: 0.5
				},{
					xtype: 'textfield',
					fieldLabel: '关联下拉项',
					name: 'ds_dlccaller',
					margin:'5,0,0,0',
					id:'ds_dlccaller',
					columnWidth: 0.47
				},
				{
					xtype: 'button',
			        iconCls: 'dlccaller',
			        margin:'5,0,0,0',
			        cls: 'x-btn-tb',			        
			        columnWidth: 0.02,
			        tooltip: '获取关联下拉项',
			        hidden: false,
			    	handler: function(){
			    		var table=Ext.getCmp('ds_tables').value;
			    		var fields='';
			    		var items=Ext.getCmp('DBfindSetUIGrid').store.data.items;
			    		
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
					xtype:'erpYnField',
					fieldLabel: '快速查找',
					name:'ds_isfast',
					columnWidth: 0.5
				},{
					xtype: 'textfield',
					fieldLabel: '容错提示',
					margin:'5,0,0,0',
					name: 'ds_error',
					columnWidth: 0.5
				},{
					xtype:'hidden',
					id:'ds_id',
					name:'ds_id'
				}
				
	     	
		],
	bbar:['->',{
					xtype : 'tbtext',
					text:'<font color=gray>*点击【确认】根据所需表载入取值字段</font>' //'*点击【确认】根据所需表载入取值字段'
				},'-',{
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
									showMessage('提示','所需表载入取值字段成功');	
									var DBfindSetUIGrid=Ext.getCmp("DBfindSetUIGrid");									
									DBfindSetUIGrid.findData=Ext.decode(res.findfields);
									
								}

							} 

						});
					}
				},'-',{
					xtype:'button',
					text: $I18N.common.button.erpSaveButton,
					iconCls: 'x-button-icon-save',
					id:'saveButton',
					cls: 'x-btn-gray',
					formBind: true,//form.isValid() == false时,按钮disabled
					width: 60,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){
						var _copyConf=getUrlParam('_copyConf');
	    				if (_copyConf) {
	    					Ext.getCmp('ds_id').setValue(null);
	    				}
							me=this.ownerCt.ownerCt;
							me.save(btn);
					},
					listeners:{
							beforerender:function(){								
								formCondition = getUrlParam('formCondition');								
								if(formCondition!=''&&formCondition!=null){
									Ext.getCmp('saveButton').text="更新";									
								}
								
							}
					}
				},{
					xtype:'button',
					text: $I18N.common.button.erpCopyByConfigsButton,
					iconCls: 'x-button-icon-copy',
			    	cls: 'x-btn-gray',
					id:'copyButton',
					formBind: true,//form.isValid() == false时,按钮disabled
					width: 60,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){
						me=this.ownerCt.ownerCt;
						me.copyData(btn);
					},
					listeners:{
							beforerender:function(){								
								formCondition = getUrlParam('formCondition');								
								if(formCondition==''||formCondition==null){								
									Ext.getCmp('copyButton').hide();	
								}
								
							}
					}
				},{
					xtype:'button',
					text: $I18N.common.button.erpDeleteButton,
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray',
					id:'deleteButton',
					formBind: true,//form.isValid() == false时,按钮disabled
					width: 60,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){
						
						me=this.ownerCt.ownerCt;
						
						me.deleteData(btn);
					},
					listeners:{
							beforerender:function(){								
								formCondition = getUrlParam('formCondition');								
								if(formCondition==''||formCondition==null){								
								Ext.getCmp('deleteButton').hide();	
								}
								
							}
					}
				},
				'-',{
					xtype:'button',
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					cls: 'x-btn-gray',
					width: 60,
					style: {
						marginLeft: '10px'
					},
					handler:function(btn){
						
						var main = parent.Ext.getCmp("content-panel");
						if (main) {
							main.getActiveTab().close();
							} 
					} 
				},'->','->'],
			
    
	initComponent : function(){
		me=this;
		
		this.callParent(arguments);		
	},
	
	
	save:function(btn){
		var grid=Ext.getCmp('DBfindSetUIGrid');
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
					jsonGridData.push(Ext.JSON.encode(data));
				}
			}
		}
		var param=jsonGridData;

		if(param.length==0){
			showError($I18N.common.grid.emptyDetail);
		}else {
			var r = Ext.getCmp('DBfindSetUIForm').getForm().getValues();
			var params = new Object();
			params.formStore = unescape(Ext.JSON.encode(r));
			params.gridStore = unescape(param.toString());
			
		
			Ext.Ajax.request({
				url : basePath + 'ma/dbfindsetui/saveDbfindSetUI.action',
				params : params,
				method : 'post',
				callback : function(options,success,response){
					var res=new Ext.decode(response.responseText);													
					if(res.exceptionInfo != null){
						showError(res.exceptionInfo);return;
					}
					if(res.success){															
						me.reLoadData(res.id);
						showMessage('提示','保存成功');	
					}
				}
			});
		}

	},
	reLoadData:function(Id){
    		var _copyConf=getUrlParam('_copyConf');
			if (_copyConf) {
				id=Ext.decode(_copyConf).keyValue;
				ds_id=id;
			}else{
				formCondition = getUrlParam('formCondition');		
				id = formCondition != null ? formCondition.replace(/IS/g, '=') : null;					
				ds_id = (id != null && id !="")? id.split('=')[1].replace(/'/g, ''):"";	    					
			}
			
			if(Id != null && Id !="")			
				ds_id=Id;	
			if(ds_id!=null&&ds_id!='')
				{
					Ext.getCmp('saveButton').setText("更新");
					var deletebtn = Ext.getCmp('deleteButton');
					deletebtn.show();	
					var localJson= this.getDbFindSetUI(ds_id);
					if(localJson!=null && localJson.griddata!=null){
						gridData=localJson.griddata;
						formData=localJson.formdata;
						findData=localJson.fields;
					}
					

					Ext.getCmp('DBfindSetUIForm').getForm().setValues(formData);
					Ext.getCmp('DBfindSetUIGrid').getStore().loadData(gridData);
					
					var arr=new Array();
					grid =Ext.getCmp('DBfindSetUIGrid');	
					deletebtn.dsid = ds_id;
					Ext.Array.each(grid.store.data.items,function(item){
						var obj=new Object();
						
						obj.display=item.data.ds_findtoui_f;
						obj.value=item.data.ds_findtoui_i;
						arr.push(obj);
					});		
					
					var combo=Ext.getCmp('combocolumn');
					combo.editor.store.data=arr;
				}else {
					gridData=new Array();
					for(var i=0;i<10;i++){
						var o = new Object();
						gridData.push(o);
					}
												
					Ext.getCmp('DBfindSetUIGrid').getStore().loadData(gridData);					
				}
					
	},
	 getDbFindSetUI:function(id){
		var localJson=null;
		
		Ext.Ajax.request({
			url : basePath +'ma/dbfindsetui/getData.action',
			params: {	
				id:id
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
	},
	deleteData:function(btn){
		formCondition = getUrlParam('formCondition');		
		id = formCondition != null ? formCondition.replace(/IS/g, '=') : null;					
		ds_id = (id != null && id !="")? id.split('=')[1].replace(/'/g, ''):btn.dsid;
		if(ds_id==''||ds_id==null){
			showError('系统错误');
		}else{
			
		Ext.Ajax.request({
			url : basePath +'ma/dbfindsetui/deleteData.action',
			params: {	
				id:ds_id
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);

				if(res.success){
					btn.dsid=null;
					Ext.Msg.show({ 
						title : '系统提示', 
						msg : '删除成功', 
						buttons: Ext.Msg.OK, 
						fn:function(){ 
							var main = parent.Ext.getCmp("content-panel");
							if (main) {
								main.getActiveTab().close();
							} 
						},
						closable: false 
					}); 			
				}else{
					showError(res.exceptionInfo);
					return;
				}

			} 

		});
		}
		
	},
	copyData:function(btn){
		formCondition = getUrlParam('formCondition');		
		id = formCondition != null ? formCondition.replace(/IS/g, '=') : null;					
		ds_id = (id != null && id !="")? id.split('=')[1].replace(/'/g, ''):btn.dsid;
		if(ds_id==''||ds_id==null){
			showError('系统错误');
		}else{
			var me=this;
			var form= btn.ownerCt.ownerCt;
			var keyValue=form.getForm().findField("ds_id").value
			var _copyConf="%7BkeyValue:"+keyValue+"%7D";
			var url=window.location.href;
			var copyUrl=url.substring(url.indexOf('jsps/'),url.indexOf('?'))+"?whoami="+ caller+"&_copyConf="+_copyConf;
			var main = parent.Ext.getCmp("content-panel");
			me.FormUtil.onAdd('copy' + caller, '单据复制', copyUrl);
		
		}
	}
	
    
	
	
	

});