
Ext.define('erp.view.sysmng.message.messagedetail.MessageForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.MessageForm',
	id: 'form', 
	autoScroll:true,
	closeAction:'hide',
	layout: "column",
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	
	defaults:{
			focusCls: 'x-form-field-cir-focus',
			labelAlign: "left" ,
			margin:'8 0 0 10',
			labelWidth:70
	},
	items:[{
			name:'MM_ID',
			fieldLabel:'ID号',
			id : 'id',
			hidden:true,
			xtype:'numberfield'
		},
		{
			name:'MM_CODE',
			fieldLabel:'编号',		
			id : 'code',			
			xtype:'textfield',
			readOnly:true,
			style: {
				color:'#989898',
			}
		},
		{
			name:'MM_DETNO',
			fieldLabel:'序号',
			id : 'detno',
			minValue:0,
			allowDecimals:false , 
			allowBlank:false,
			xtype:'numberfield',
			style: {
				color:'#fd0000',
			}
		},
		{
			name:'MM_CALLER',
			fieldLabel:'Caller',
			id : 'MM_CALLER',			
			xtype:'dbfindtrigger',
			hidden:false,
			allowBlank:false,
			editable:false,
			style: {
				color:'#fd0000',
			}
		},
		{

			name:'MM_NAME',
			fieldLabel:'单据名称',
			id : 'MM_NAME',			
			xtype:'textfield',
			readOnly:true,
			style: {
				color:'#989898',
			}
		},
		
		{
			name:'MM_ISUSED',
			fieldLabel:'启用状态',
			editable:false,
			id : 'isused',
			allowBlank:false,
			xtype:'combo',
			value:'-1',
			displayField: 'display',
	    	valueField: 'value',
	    	style: {
				color:'#fd0000',
			},
			store:Ext.create('Ext.data.Store', {
	    					    fields: ['value', 'display'],
	    					    data : [{value:"-1", display:"是"},
	    					           {value:"0", display:"否"}]
	    					            			 })
		},
		
		{
			name:'MM_OPERATE',
			fieldLabel:'执行操作',
			id : 'operate',
			xtype:'combo',
			editable:false,
			displayField: 'display',
	    	valueField: 'value',
	    	allowBlank:false,
	    	style: {
				color:'#fd0000',
			},
			store:Ext.create('Ext.data.Store', {
	    					    fields: ['value', 'display'],
	    					    data : [
	    					    		{value:"save", display:"保存"},
	    					    		{value:"update", display:"更新"},
	    					    		{value:"commit", display:"提交"},
	    					    		{value:"resCommit", display:"反提交"},
	    					            {value:"audit", display:"审核"},
	    					            {value:"resAudit", display:"反审核"},
	    					            {value:"post", display:"过账"},
	    					            {value:"resPost", display:"反过账"},
	    					            {value:"print", display:"打印"},
	    					            {value:"delete", display:"删除"},
	    					            {value:"deletedetail", display:"删除明细"},	    					         	    					           	    					            
	    					            {value:"batchDeal", display:"批处理"},
	    					           ]
	    					            }),
	    	 listeners: {
	    	 	change:function(t,d){	    	 		
	    	 			    	 
	    	 		if(d=='batchDeal'){	    	 			
	    	 			Ext.getCmp('action').show();	    	 			
	    	 		}else{    	 	
	    	 			Ext.getCmp('action').hide();
	    	 		}
	    	 		
	    	 	}
	    	 
	    	 }			            
		},
		{
			name:'MM_ACTION',
			hidden:true,
			fieldLabel:'调用action',
			id : 'action',			
			xtype:'textareatrigger',
			style: {
				color:'#fd0000',
			},
			listeners:{
				beforeshow:function(v){							
					var action=Ext.getCmp('action');		
					action.allowBlank=false;
					var form=Ext.getCmp('form');	
					form.isValid=false;
					var save=Ext.getCmp('save');
					save.setDisabled(true);					
				},
				beforehide:function(){					
					var action=Ext.getCmp('action');
					action.allowBlank=true;					
					var form=Ext.getCmp('form');
					form.isValid=true;
					var save=Ext.getCmp('save');
					save.setDisabled(false);
				},
				change:function(v,b){
					var save=Ext.getCmp('save');					
					if(b!=""&&save.disabled==false){
						var save=Ext.getCmp('save');
						save.setDisabled(false);
					}
				}
				
			}
			
		},
		{
			name:'MM_OPERATEDESC',
			fieldLabel:'操作描述',
			id : 'operatedesc',			
			xtype:'textareatrigger',
			allowBlank:false,
			style: {
				color:'#fd0000',
			}
		},
		
		{
			name:'MM_MODULE',
			fieldLabel:'所属模块',
			allowBlank:false,
			id : 'module',			
			xtype:'combo',
			editable:false,
			displayField: 'display',
	    	valueField: 'value',
	    	style: {
				color:'#fd0000',
			},
			store:Ext.create('Ext.data.Store', {
	    					    fields: ['value', 'display'],
	    					    data : [{value:"客户关系管理", display:"客户关系管理"},
	    					           {value:"产品生命周期管理", display:"产品生命周期管理"},
	    					           {value:"供应链管理", display:"供应链管理"},
	    					           {value:"生产制造管理", display:"生产制造管理"},
	    					           {value:"行政办公管理", display:"行政办公管理"},
	    					           {value:"人力资源管理", display:"人力资源管理"},
	    					           {value:"风险控制管理", display:"风险控制管理"},
	    					           {value:"财务会计管理", display:"财务会计管理"},
	    					           {value:"成本会计管理", display:"成本会计管理"},
	    					           {value:"售后服务管理", display:"售后服务管理"},
	    					           ]
	      })
			
		},
		{
			name:'MM_URL',
			fieldLabel:'链接URL',
			id : 'url',			
			xtype:'textareatrigger'
		},
		{
			name:'MM_APPURL',
			fieldLabel:'移动端URL',
			id : 'appurl',
			xtype:'textareatrigger'
		},
		{
			name:'MM_STATUS',
			fieldLabel:'单据状态',
			id : 'status',
			xtype:'textfield',
			value:'在录入',
			readOnly: true
		},
		{
			name:'MM_RECORDER',
			fieldLabel:'录入人',
			id : 'recorder',			
			xtype:'textfield',
			readOnly: true
		},
		{
			name:'MM_RECORDDATE',
			fieldLabel:'单据日期',
			id : 'recorddate',		
			xtype:'datefield',
			format:'Y-m-d',  
            value:new Date(),
            readOnly: true
            
		},
		
		{
			name:'MM_STATUSCODE',
			hidden:true,
			fieldLabel:'单据状态码',
			id : 'statuscode',			
			xtype:'textfield',
			value:'ENTERING'
		},
		{
			name:'MM_TABLE',			
			fieldLabel:'表名',
			id : 'table',			
			xtype:'textareatrigger'
		},
		{
			name:'MM_CODEFIELD',
			fieldLabel:'编号字段',			
			id : 'codefield',
			xtype:'textfield'
		},
		{
			name:'MM_KEYFIELD',			
			fieldLabel:'主键字段',
			id : 'keyfield',
			xtype:'textfield'
		},
		{
			name:'MM_PFFIELD',
			fieldLabel:'明细外键',
			id : 'pffield',			
			xtype:'textfield'
		},
		
		],
	bbar: ['->',
			{
				xtype: 'erpAddButton',
				height:30,
				width: 70,
			},
			{
				xtype: 'erpSaveButton',
				height:30,
				width: 70,
			},
			{
				xtype: 'erpUpdateButton',
				height:30,
				width: 70,
			},
			
			{
				xtype: 'erpDeleteButton',
				height:30,
				width: 70,
			},			
			{
				text:'关闭',
				height:30,
				width: 70,
				cls: 'x-btn-gray',
				iconCls: 'x-button-icon-close',
				listeners: {
						click: function(btn){														
							var main = parent.Ext.getCmp("content-panel");
							if (main) {
								main.getActiveTab().close();
							} 
							
							},
						afterrender:function(fn){
							formCondition = getUrlParam('formCondition');							
							if(formCondition == null || formCondition == ''){
									fn.hide();
							}
						}
						}
				
					},'->'],
	initComponent : function(){ 	
		this.getFormData();
		this.callParent(arguments);
		
		
	},
	getFormData:function(){
		var me = this;
		//从url解析参数
		 formCondition = getUrlParam('formCondition');
		
		if(formCondition != null && formCondition != ''){
			formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=").replace(/\'/g,"");
			this.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'sysmng/getMessageFormData.action',
	        	params: {	        		 
	        		id: formCondition.split("=")[1]	       
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){	        		
	        		me.setLoading(false);
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else{
	        			//me.setFormValues(res.data);
	        			var form=Ext.getCmp('form');	        
	        			form.getForm().setValues(res.data);
	        		
	        			parent.Ext.getCmp('content-panel').activeTab.setTitle(res.data.MM_NAME);
	        			
	        		}
	        	}
	        });
	        
	}
	},
	
	
	
	
});