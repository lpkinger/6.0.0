Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.SafeReserve', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.SafeReserveForm','scm.product.SafeReserve',
    		'core.button.Confirm','core.button.Close','core.button.Print',
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