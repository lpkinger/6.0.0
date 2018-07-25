Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.WCScheduleAnalyse', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.make.WCScheduleAnalyse','common.query.Form','pm.make.WCPlanTreeGrid','core.form.YnField',
    		'core.trigger.DbfindTrigger','core.grid.YnColumn'
    	],
    init:function(){
    	this.control({
    		'WCPlanTreeGrid':{
 			   itemmousedown: function(selModel, record){  
    					if(record.data['wa_iftop'] == -1 && record.childNodes.length == 0 ){
    						Ext.getCmp('querygrid').loadChildNodes(record);
    					}
    			}
 		   },
    	});
    }
});