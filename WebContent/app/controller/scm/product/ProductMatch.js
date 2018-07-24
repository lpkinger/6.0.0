Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductMatch', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','scm.product.Product','core.form.MultiField',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
			'core.button.ResForBidden',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger'
	],
	init:function(){
		var me = this;
		this.control({ 
			'erpFormPanel': {
				afterrender: function(){
					var tree = parent.Ext.getCmp('tree-panel');
					if(!tree.collapsed) {
						parent.Ext.getCmp('tree-panel').toggleCollapse();
					}
				}
			},
			
			'erpSaveButton': {
				click: function(btn){					
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pm_id').value);
				}
			},
			'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addProductMatch', '新增物料对照', 'jsps/scm/product/ProductMatch.jsp');
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