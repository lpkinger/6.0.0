Ext.QuickTips.init();
Ext.define('erp.controller.b2b.product.ProductApprovalDown', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),   
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','b2b.product.ProductApprovalDown','core.button.Close',
			'core.form.MultiField',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.form.FileField'			
	],
	init:function(){
		var me = this;
		this.control({
			'erpCloseButton': {
				afterrender:function(btn){
					
				},
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