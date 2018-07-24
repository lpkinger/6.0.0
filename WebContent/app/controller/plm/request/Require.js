Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.Require', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
	'plm.request.Require','core.form.Panel','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.form.MultiField',
	'core.button.Add','core.button.Save','core.button.Close','core.button.Banned','core.button.ResBanned',
	'core.button.Update','core.button.Delete','core.form.YnField','core.button.Sync',
	'core.button.ResAudit','core.button.Audit','core.button.Submit','core.button.ResSubmit',
	'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.TurnProject','core.button.TurnPrepProject'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpTurnPrepProject':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pr_statuscode');
    				var auditstatus = Ext.getCmp('pr_auditstatus');
    				if((status && status.value != 'AUDITED')){
    					btn.hide();
    				}
    				if(auditstatus && auditstatus.value != ''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转预立项吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var form = Ext.getCmp('form');
    	    				Ext.Ajax.request({
    	    			   		url : basePath + form.turnPrepProjectUrl,
    	    			   		params: {
    	    			   			id: Ext.getCmp('pr_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(response.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success){
    	    		    				window.location.reload();
    	    		    				showMessage($I18N.common.msg.title_prompt, $I18N.common.msg.success_turn,3000);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}, 	
    		'erpTurnProject':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('pr_statuscode');
    				var auditstatus = Ext.getCmp('pr_auditstatus');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(auditstatus && auditstatus.value !=''){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转立项吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var form = Ext.getCmp('form');
    	    				Ext.Ajax.request({
    	    			   		url : basePath + form.turnProjectUrl,
    	    			   		params: {
    	    			   			id: Ext.getCmp('pr_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(response.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success){
    	    		    				window.location.reload();	
    	    		    				showMessage($I18N.common.msg.title_prompt, $I18N.common.msg.success_turn,3000);
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}, 	
    		'erpSaveButton': {
 			   click: function(btn){
 				  var form = me.getForm(btn);
 				  if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
  						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
  					me.BaseUtil.getRandomNumber(caller);//自动添加编号
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
    				me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
    			},
    		},
    		'erpAddButton': {
    			afterrender: function(btn){
   				   var statu = Ext.getCmp('pr_statuscode');
   				   if(statu && statu.value != 'ENTERING'){
   					   btn.hide();
   				   }
   			   },
    			click: function(){
    				me.FormUtil.onAdd('addRequire', '新增需求单', 'jsps/plm/request/require.jsp');
    			}
    		},
    		 'erpSubmitButton': {
  			   afterrender: function(btn){
  				   var statu = Ext.getCmp('pr_statuscode');
  				   if(statu && statu.value != 'ENTERING'){
  					   btn.hide();
  				   }
  			   },
  			   click: function(btn){
  				   me.FormUtil.onSubmit(Ext.getCmp('pr_id').value);
  			   }
  		   },
  		 'erpResSubmitButton': {
			   afterrender: function(btn){
				   var statu = Ext.getCmp('pr_statuscode');
				   if(statu && statu.value != 'COMMITED'){
					   btn.hide();
				   }
			   },
			   click: function(btn){
				   me.FormUtil.onResSubmit(Ext.getCmp('pr_id').value);
			   }
		   },
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pr_id').value);
				}
			},
			'erpResAuditButton': {
 			   afterrender: function(btn){
 				   var statu = Ext.getCmp('pr_statuscode');
 				   if(statu && statu.value != 'AUDITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onResAudit(Ext.getCmp('pr_id').value);
 			   }
 		   },
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});