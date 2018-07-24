Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.PriceUpdate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.PriceUpdateForm','scm.purchase.PriceUpdate',
    		'core.button.Confirm','core.button.Close',
    		'core.form.ConDateField'
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