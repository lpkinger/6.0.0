Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.ContactListType', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.ContactListType','core.form.Panel','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    			'core.button.Update','core.button.Delete',
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
    		}
//    		,
//    		'erpAddButton': {
//    			click: function(btn){
//    				me.FormUtil.onAdd('addContactListType', '新建联络单类型', 'jsps/oa/persontask/contactListType.jsp');
//    			}
//    		},
//    		'erpUpdateButton': {
//    			click: function(btn){
//    				this.FormUtil.onUpdate(this);
//    			}
//    		},
//    		'erpDeleteButton': {
//    			click: function(btn){
//    				me.FormUtil.onDelete((Ext.getCmp('clt_id').value));
//    			}
//    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});