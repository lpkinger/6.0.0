Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.myContactList.NewContactList', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.myContactList.NewContactList','core.form.Panel','core.button.Save','core.button.Close',
    		'core.form.YnField','core.trigger.DbfindTrigger'
    	],
    init:function(){
//    	var me = this;
    	this.control({ 
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
    		'textfield[id=cl_emname]': {
    			afterrender: function(field){
    				field.setValue(em_name);
    			}
    		},
    		'textfield[id=cl_emid]': {
    			afterrender: function(field){
    				field.setValue(em_uu);
    			}
    		},
    		'htmleditor[id=cl_context]': {
    			afterrender: function(f){
    				f.setHeight(300);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});