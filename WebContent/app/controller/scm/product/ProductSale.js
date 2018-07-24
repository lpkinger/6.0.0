Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductSale', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'scm.product.ProductSale','core.form.Panel',
      		'core.button.Save','core.button.Close',
  				'core.button.Upload','core.button.Update','core.button.Delete',
  			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger', 'core.form.FileField'
      	],
      	init:function(){
      		var me = this;
      		this.control({ 
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