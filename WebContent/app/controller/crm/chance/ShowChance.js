Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.ShowChance', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.chance.ShowChance','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.button.Submit',
    		'core.button.ResAudit','core.button.ResSubmit','core.button.Audit','core.button.TurnOaacceptance',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.HolidayDatePicker',
    		'common.datalist.GridPanel2','common.datalist.Toolbar','crm.customercare.ShowForm'
    	],
    init:function(){
    	
    }			
});