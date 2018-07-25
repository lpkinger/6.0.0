Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.customerBatchDeal', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['scm.sale.customerBatchDeal.Viewport', 'scm.sale.customerBatchDeal.PassedUU','common.batchDeal.Form','common.batchDeal.GridPanel','common.datalist.Toolbar','core.form.ConDateField',
	    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.DetailTextField','scm.sale.customerBatchDeal.noPassedUU','common.batchDeal.Viewport','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
	     		'core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','core.button.CheckVendorUU',
	     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
	     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');		
		this.control({
		});	
	}	
});