Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.VendorContact', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.VendorContact','core.form.Panel',
    		'core.button.Save','core.button.Close','core.button.Add',
    			'core.button.Upload','core.button.Update','core.button.Scan',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
		this.control({
			'erpSaveButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addVendorContact', '新增供应商联系人资料', 'jsps/scm/purchase/vendorContact.jsp');
				}
			},
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