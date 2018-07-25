Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AttentionManageDetail', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.attention.AttentionManageDetail','oa.attention.AttentionGrid','oa.attention.Form','core.form.ColorField','core.button.Save','core.button.Close',
    		'core.form.ScopeField','oa.attention.AttentionSubGrid','core.trigger.MultiDbfindTrigger','oa.attention.AttentionManageGrid','core.form.PhotoField'
    	],
    init:function(){

      var me=this;
    	this.control({ 
    	});
    },

});