Ext.QuickTips.init();
Ext.define('erp.controller.pm.craft.WorkTime', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.craft.WorkTime','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.button.Scan',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
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
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('wt_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addWorkTime', '新增班次', 'jsps/pm/craft/workTime.jsp');
    			}
    		}
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
});