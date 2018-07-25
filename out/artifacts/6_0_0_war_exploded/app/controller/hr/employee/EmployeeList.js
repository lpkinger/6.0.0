Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.EmployeeList', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.employee.EmployeeList','core.form.Panel',
    		'core.button.Close','core.button.Upload','core.button.Update',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField',
    		'core.form.FileField'
    	],
    init:function(){
    	this.control({ 
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});