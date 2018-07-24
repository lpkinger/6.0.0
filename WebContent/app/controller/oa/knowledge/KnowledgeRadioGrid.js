Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeRadioGrid', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil', 'erp.util.GridUtil'],
    views:[
     		'oa.knowledge.KnowledgeRadioGrid',
     		'common.editorColumn.Viewport'
     	],
    init:function(){
    	this.control({ 
    		
    	});
    }
});