Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MrpReplace', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.form.ConMonthDateField','core.form.YnField',
     		'core.button.VastDeal','core.button.VastPrint','core.button.VastAnalyse','core.button.GetVendor','core.form.FtDateField','pm.mps.MrpReplace','pm.mps.MrpReplaceGrid',
     		'core.trigger.TextAreaTrigger','core.form.YnField','core.button.DealMake',
     		'core.button.MakeOccur','core.button.SaleOccur','core.button.AllThrow','core.button.SelectThrow','core.form.MonthDateField'
     	],
    init:function(){   
    	this.control({
    		
    	});
     }
    });


		 