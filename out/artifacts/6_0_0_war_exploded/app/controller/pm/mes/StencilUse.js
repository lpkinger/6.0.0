Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.StencilUse', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.Device','core.form.Panel','core.button.BackStencil',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('su_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addStencilUse', '新增钢网登记', 'jsps/pm/mes/stencilU.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('su_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('su_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('su_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('su_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('su_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('su_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('su_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('su_id').value);
    			}
    		},
    		'erpBackStencilButton':{//钢网归还
    			click:function(btn){   				
					var win = new Ext.window.Window({  
			    		  modal : true,
			        	  id : 'win',
			        	  height : '35%',
			        	  width : '30%',       	 
			        	  layout : 'anchor',   
			        	  bodyStyle: 'background: #f1f1f1;',
						  bodyPadding:5,			  
			        	  items : [{
			        	  	anchor: '100% 100%',
			                xtype: 'form',
			                bodyStyle: 'background: #f1f1f1;',
			                defaults:{
			        	  	  fieldStyle : "background:rgb(224, 224, 255);",    
							  labelStyle:"color:red;",
							  allowBlank:false  
			        	    },
				            items:[{
				        		  xtype:'textareatrigger',
				        		  name:'record',
				        		  fieldLabel:'归还外观检测',
				        		  id:'record'
				        	  },{
				        	  	  xtype:'datefield',
				        		  name:'date',
				        		  fieldLabel:'归还日期',
				        		  id:'date'
				        	  },{
				        	      xtype:'dbfindtrigger',
				        		  name:'pr_location',
				        		  fieldLabel:'归还储位',
				        		  id:'pr_location'
				        	  }],
			                buttonAlign : 'center',
				            buttons: [{
								text: '确定'	,
								cls: 'x-btn-gray',
								iconCls: 'x-button-icon-save',
								id:'confirmBtn',
								formBind: true, //only enabled once the form is valid
			                    handler: function(btn) {      			                    	
			    					me.backStencil(me.getForm(btn).getValues());   			 		    					
								  }
							  },{
							    text: '取消'	,
								cls: 'x-btn-gray',
								iconCls: 'x-button-icon-close',
			                    handler: function(btn) {                   	                  				    			 
			    					win.close();
								}
							  }]
			    	       }]
			    		});
    	           win.show(); 				
    			},
    			afterrender: function(btn){
    				var status = Ext.getCmp('su_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	backStencil:function(data){//归还钢网
		 Ext.getCmp('win').close();
		 var su_id = Ext.getCmp("su_id").value,record = data['record'],
		 date = data['date'],location = data['pr_location'];
		 Ext.Ajax.request({
			url: basePath + 'pm/mes/backStencil.action',
			params: {
				caller   : caller,
				id       : su_id,
				record   : record,
				location : location,
				date     : date
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}else{
					showMessage('提示','归还成功'); 		
					window.location.href = window.location.href + '&formCondition=' + 
					formCondition + '&gridCondition=' + gridCondition;
				}
			}
		});
	}
});