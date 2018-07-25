Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.KpiResult', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'hr.kpi.KpiResult','core.form.Panel','core.grid.Panel2','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup','core.trigger.MultiDbfindTrigger',
    		'core.button.Close','core.grid.YnColumn','common.datalist.Toolbar','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
    		'core.trigger.AutoCodeTrigger','core.trigger.AddDbfindTrigger'],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
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