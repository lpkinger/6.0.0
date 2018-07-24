Ext.QuickTips.init();
Ext.define('erp.controller.common.SysInit', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.sysinit.ViewPort','common.sysinit.SysPanel','common.sysinit.TabPanel','common.sysinit.MoudleConPortal'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    	});
    }
});
