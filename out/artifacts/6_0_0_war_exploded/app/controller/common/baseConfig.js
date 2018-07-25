Ext.QuickTips.init();
Ext.define('erp.controller.common.baseConfig', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'common.baseConfig.viewport','common.baseConfig.baseConfigForm'     		
    ],
    init:function(){
    	var me = this;
    	this.control({
    		
    	});
    }    		
});