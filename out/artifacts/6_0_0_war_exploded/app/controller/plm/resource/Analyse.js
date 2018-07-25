Ext.QuickTips.init();
Ext.define('erp.controller.plm.resource.Analyse', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    RenderUtil:Ext.create('erp.util.RenderUtil'),
    views:[
     		'plm.resource.Analyse','core.trigger.DbfindTrigger',
     		'plm.resource.BarChart','erp.view.plm.resource.AnalyseGrid','plm.resource.AnalyseForm',
     	],
    init:function(){
    	this.control({
    	
		});
    }
});