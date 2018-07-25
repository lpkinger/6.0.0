Ext.QuickTips.init();
Ext.define('erp.controller.common.SysHome', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['common.sysinit.SysHome','common.sysinit.PortalPanel','common.sysinit.BasicConPortal','common.sysinit.MoudleConPortal'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    	});
    }
});
