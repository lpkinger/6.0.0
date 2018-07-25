Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.MouldSale', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.MouldSale','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.TurnDeliveryOrder','core.form.FileField','core.button.UpdateChargeStatus',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
			},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('msa_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('msa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMouldSale', '新增模具销售单', 'jsps/pm/mould/mouldSale.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('msa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('msa_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('msa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('msa_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('msa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('msa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
     				var status = Ext.getCmp('msa_statuscode'), 
    					turnstatus = Ext.getCmp('msa_turnstatuscode'),
    					chargestatus = Ext.getCmp('msa_chargestatus');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNOUT'){
    					btn.hide();
    				}
    				if(chargestatus && chargestatus.value != '未收款'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('msa_id').value);
    			}
    		},
			'erpPrintButton': {
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('msa_id').value);
				}
			},
			'erpTurnDeliveryOrderButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('msa_statuscode'), turnstatus = Ext.getCmp('msa_turnstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(turnstatus && turnstatus.value == 'TURNOUT'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入模具出货单吗?", function(btn){
    					if(btn == 'yes'){
    						var id = Ext.getCmp('msa_id').value;
    						me.FormUtil.setLoading(true);
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mould/turnDeliveryOrder.action',
    	    			   		params: {
    	    			   			id: id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
						   			me.FormUtil.setLoading(false);
						   			var localJson = new Ext.decode(response.responseText);
						   			if(localJson.exceptionInfo){
						   				showError(localJson.exceptionInfo);
						   				return "";
						   			}
					    			if(localJson.success){
					    				if(localJson.log){
					    					showMessage("提示", localJson.log);
					    				}
					    				window.location.reload();
						   			}
			   					}
    	    				});
    					}
    				});
    			}
			}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});