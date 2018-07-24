Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProductWarehouse', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.ProductWarehouse','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    			'core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'
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
	   				me.FormUtil.onAdd('addProductWarehouse', '物料仓库管理', 'jsps/scm/reserve/productWarehouse.jsp');
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