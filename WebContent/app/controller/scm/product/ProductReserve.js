Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductReserve', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'scm.product.ProductReserve','core.form.Panel','core.form.MultiField',
   		'core.button.Save','core.button.Close','core.button.Upload','core.button.Update','core.button.Delete', 			
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
   	],
   	init:function(){
   		var me = this;
   		this.control({ 
   			'#pr_validdays':{
    			afterrender: function(f) {
    				f.isInteger = true;
    			}
    		},
   			'erpSaveButton': {
   				click: function(btn){
   					this.FormUtil.beforeSave(this);
   				}
   			},
	   		'erpUpdateButton': {
	   			click: function(btn){
	   				this.FormUtil.onUpdate(this);
	   			}
	   		},
	   		'erpAddButton': {
	   			click: function(){
	   				me.FormUtil.onAdd('addProductReserve', '新增物料(库存资料)', 'jsps/scm/product/productReserve.jsp');
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