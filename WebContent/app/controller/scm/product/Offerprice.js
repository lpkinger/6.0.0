Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.Offerprice', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    	'core.form.Panel','scm.product.Offerprice','core.button.Close',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
	],
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