Ext.QuickTips.init();
Ext.define('erp.controller.oa.knowledge.KnowledgeMapLink', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.knowledge.KnowledgeMapLink','core.form.Panel','common.datalist.Toolbar','core.form.FileField','oa.knowledge.KnowledgeMapLinkGrid',
    		'oa.knowledge.KnowledgeMappingGrid','core.grid.Panel5',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    	    'filefield':{
    	     afterrender:function(field){
    	       var scantimes=Ext.getCmp('kl_scantimes').getValue();
    	       var rectimes=Ext.getCmp('kl_recommonedtimes').getValue();
    	       var commenttimes=Ext.getCmp('kl_commenttimes').getValue();
    	       var point=Ext.getCmp('kl_point').getValue();
    	       Ext.getCmp('knowledgedetails').setValue('知识阅读'+scantimes+'次     '+'知识推荐'+rectimes+'次    '+'知识评论'+commenttimes+'次     '+'知识分数'+point+'分');
    	       field.hide();
    	       Ext.getCmp('knowledgeGridPanel').setReadOnly(true);
    	     }   	    
    	    },
    	    'erpKnowledgeMapLinkGrid':{
    	     itemmousedown:function(){
    	     
    	     }
    	    }
    	});
    }
});