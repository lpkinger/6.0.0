Ext.QuickTips.init();
Ext.define('erp.controller.ma.Grid', {
	extend : 'Ext.app.Controller',
	views : [ 'ma.Grid',
	          'core.grid.TfColumn', 'core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger' ],
	init : function() {
		this.control({
			
		});
	}
});