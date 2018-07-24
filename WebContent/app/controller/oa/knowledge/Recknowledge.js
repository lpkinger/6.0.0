Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.Recknowledge', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.knowledge.Recknowledge','common.datalist.GridPanel','common.datalist.Toolbar','common.batchDeal.Form',
            'core.form.ConDateField',
     		'core.button.VastDeal','core.button.VastPrint','core.button.VastAnalyse','core.button.GetVendor',
     		'core.button.VastTurnPurc','core.trigger.TextAreaTrigger','core.form.YnField','core.button.DealMake',
     		'core.button.MakeOccur','core.button.SaleOccur','core.button.AllThrow','core.button.SelectThrow'
    	],
    init:function(){
    	this.control({ 
    		
    	});
    },

});