Ext.QuickTips.init();
Ext.define('erp.controller.common.reportsQuery', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'common.reportsQuery.viewport','common.reportsQuery.reportsGrid','core.trigger.SearchField'     		
    ],
    init:function(){
    	var me = this;
    	this.control({
    		
    	});
    }    		
});