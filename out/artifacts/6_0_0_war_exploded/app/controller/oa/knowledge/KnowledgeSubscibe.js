Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeSubscibe', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.knowledge.Recknowledge','common.datalist.GridPanel','common.datalist.Toolbar','common.batchDeal.Form',
             'oa.knowledge.KnowledgeRankForm','core.form.ConDateField',
    	],
    init:function(){
    	this.control({ 

    	});
    },

});