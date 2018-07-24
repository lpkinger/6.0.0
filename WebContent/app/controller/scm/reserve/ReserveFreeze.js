Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ReserveFreeze', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.ReserveFreezeForm','scm.reserve.ReserveFreeze',
    		'core.button.Confirm','core.button.Close', 		
    		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.ConDateField'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.beforeClose(me);
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	}
    });