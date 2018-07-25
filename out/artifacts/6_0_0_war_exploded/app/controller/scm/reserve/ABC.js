Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ABC', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.ABCForm','scm.reserve.ABC',
    		'core.button.Confirm','core.button.Close', 		
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.ConDateField',
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
    });