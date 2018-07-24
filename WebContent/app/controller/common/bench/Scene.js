Ext.QuickTips.init();
Ext.define('erp.controller.common.bench.Scene', {
	extend: 'Ext.app.Controller',
	views:['common.bench.Scene','common.bench.SceneFormPanel','common.bench.SceneGridPanel','common.bench.Toolbar','common.bench.SwitchButton',
			'common.bench.ResultGridPanel','core.plugin.NewRowNumberer','core.form.BtnDateField','core.grid.TfColumn','core.grid.YnColumn',
			'core.form.FtField','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField','core.form.FtNumberField', 'core.form.MonthDateField'],
  	init:function(){
   		this.control({
	   		'erpResultGridPanel': {
	   			afterrender:function(grid){
	   				store.on('datachanged',function(store){
		            	selectRecord(grid);
		          	});
	   				grid.reconfigure(store,columns);
	   				grid.selModel.selectAll();
	   			}
	   		}
		});
   	}
});