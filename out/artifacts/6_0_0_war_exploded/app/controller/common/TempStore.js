Ext.QuickTips.init();
Ext.define('erp.controller.common.TempStore', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[	'common.tempStore.Viewport','common.tempStore.TempStoreGridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton','core.button.CheckVendorUU',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
	    	'erpTempStoreGridPanel': {
	    		afterrender: function(grid){
					setTimeout(function(){
							grid.summary();//计算合计
					}, 1000);
            	}
    		}
    	});
    }
});