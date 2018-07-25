Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductPurchase', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.product.ProductPurchase','core.form.Panel',
    		'core.button.Audit','core.button.Save','core.button.Close',
    			'core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
    		'core.form.FileField'
    	],
    init:function(){
    	this.control({ 
    		'#pr_leadtime':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_purcmergedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_purchasedays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_ltinstock':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'#pr_ltwarndays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
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
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});