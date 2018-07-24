Ext.QuickTips.init();
Ext.define('erp.controller.common.JprocessCommunicate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.JProcess.JprocessCommunicate','core.button.Add',
    		'core.button.Save','core.button.Close','core.button.Delete',
    		
    	],
    init:function(){
    	this.control({ 
    	});
    }
    
                                    
});