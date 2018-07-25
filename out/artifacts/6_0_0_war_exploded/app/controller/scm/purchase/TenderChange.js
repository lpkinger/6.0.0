Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.TenderChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'scm.purchase.TenderChange','core.form.Panel','core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit','core.button.Update',
    		'core.button.Delete','core.form.YnField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				me.beforeSave();
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeSave(true);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('tc_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('TenderChange', '开标变更单', 'jsps/scm/purchase/tenderchange.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('tc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('tc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('tc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('tc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('tc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('tc_id').value);
    			}
    		},
    		'datefield[id=tc_newendtime]':{
    			change:function(field){
    				var type = Ext.getCmp('tc_type');
    				var oldtime = Ext.getCmp('tc_oldendtime').value;
    				if(type&&oldtime != null&&oldtime != ''){
    					newend = Date.parse(field.value);
    					oldend = Date.parse(oldtime);
    					if(newend==oldend){
    						showError("请重新选择截止时间！");
    						field.setValue(null);
    					}
    					if(type&&type.value == '提前开标'&&oldend<newend){
    						showError("提前开标：新截止时间不能大于原截止时间！");
    						field.setValue(null);
    					}else if(type&&type.value == '延迟开标'&&newend < oldend){
    						showError("延迟开标：新截止时间不能小于原截止时间！");
    						field.setValue(null);
						}
					}
				}
			},
			'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    	});
    },
	beforeSave : function(update){
		var me = this;
		var type = Ext.getCmp('tc_type');
		var oldtime = Ext.getCmp('tc_oldendtime');
		var newtime = Ext.getCmp('tc_newendtime');
		if(type&&oldtime&&newtime){
			newend = Date.parse(newtime.value);
			oldend = Date.parse(oldtime.value);
			if(newend==oldend){
				showError("请重新选择截止时间！");
				return false;
			}
			if(type&&type.value == '提前开标'&&oldend<newend){
				showError("提前开标：新截止时间不能大于原截止时间！");
				return false;
			}else if(type&&type.value == '延迟开标'&&newend < oldend){
				showError("延迟开标：新截止时间不能小于原截止时间！");
				return false;
			}
		}
		if(update){
			me.FormUtil.onUpdate(me);
		}else{
			me.FormUtil.beforeSave(me);
		}
	}
});