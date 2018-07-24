Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.deskTop', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil'],
    views: [
    		'vendbarcode.main.viewPort'],
    init: function() {  
    	var me=this;
        this.control({
        });
    }
});