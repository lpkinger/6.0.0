Ext.QuickTips.init();
Ext.define('erp.controller.scm.product.ProductPlan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'scm.product.ProductPlan','core.form.Panel',
      		'core.button.Audit','core.button.Save','core.button.Close',
      			'core.button.Upload','core.button.Update','core.button.Delete',
      		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
      		'core.form.FileField'
      	],
      	init:function(){
      		var me = this;
      		this.control({ 
      			'#pr_gdtqq':{
        			afterrender: function(f) {
        				f.isInteger = true;
        			}
        		},
        		'#pr_precision':{
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