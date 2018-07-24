Ext.QuickTips.init();
Ext.define('erp.controller.ma.DocumentPowerSet', {
    extend: 'Ext.app.Controller',
    views:[
    		'ma.DocumentPowerSet','core.grid.GroupDocumentPower'
    	],
    init:function(){
    	this.control({ 
    		'groupdocumentpower': {
    			
    		}
    	});
    }
});