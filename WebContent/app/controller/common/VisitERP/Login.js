Ext.QuickTips.init();
Ext.define('erp.controller.common.VisitERP.Login', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','common.VisitERP.Login','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField', 
     		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
     		'core.button.Add','core.button.DeleteDetail','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
   ],
   init:function(){
	   	var me = this;
		this.control({});
	}
});