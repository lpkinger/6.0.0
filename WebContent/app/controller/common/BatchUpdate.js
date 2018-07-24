Ext.QuickTips.init();
Ext.define('erp.controller.common.BatchUpdate', {
    extend: 'Ext.app.Controller',
    views:[
     		'common.batchUpdate.Viewport','common.batchUpdate.Form','core.toolbar.Toolbar','core.grid.Panel2'
     		 ],
    init:function(){
    	//var me = this;
    	this.control({ 
    	});
    }
});