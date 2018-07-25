Ext.QuickTips.init();
Ext.define('erp.controller.oa.attention.AccreditAttention', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
    		'oa.attention.AccreditAttention','oa.attention.AccreditAttentionGridPanel'
    	],
    init:function(){
      var me=this;
    	this.control({ 
    	});
    },
  
});