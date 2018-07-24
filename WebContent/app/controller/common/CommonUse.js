Ext.QuickTips.init();
Ext.define('erp.controller.common.CommonUse', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'common.commonUse.Viewport','common.commonUse.CommonUseGrid','core.trigger.SearchField'  		
    ],
    init:function(){
    	var me = this;
    	this.control({
    		
    	});
    }    		
});