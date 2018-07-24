Ext.QuickTips.init();
Ext.define('erp.controller.oa.vehicle.Vehiclemaintain', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.vehicle.Vehiclemaintain','core.form.Panel','core.form.FileField','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.AutoCodeTrigger','core.button.TurnFYBX',
    			'core.form.YnField','core.trigger.DbfindTrigger','core.button.Scan'
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
    		'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('vd_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('vd_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vd_statuscode');
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
					me.FormUtil.onAdd('addVehiclemaintain', '新增车辆维修单', 'jsps/oa/vehicle/vehiclemaintain.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vd_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('vd_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vd_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('vd_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vd_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('vd_id').value);
				}
			},'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('vd_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('vd_id').value);
				}
			},
			'erpTurnFYBXButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('vd_statuscode');
    				var turnstatus=Ext.getCmp('vd_turnstatus');
    				if((status && status.value != 'AUDITED')||turnstatus.value=='已转'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入费用报销单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'oa/vehiclemaintain/turnFYBX.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			id: Ext.getCmp('vd_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/oa/fee/feePleaseFYBX.jsp?whoami=FeePlease!FYBX&formCondition=fp_id=" + id + "&gridCondition=fpd_fpid=" + id;
    	    		    					me.FormUtil.onAdd('FeePlease' + id, '费用报销单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});