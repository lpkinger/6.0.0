Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.BOMTree', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.bom.BOMTree','common.query.Form','pm.bom.BOMTreeGrid','core.form.YnField',
    		'core.trigger.DbfindTrigger'
    	],
    init:function(){
    	this.control({ 
    		'bomTreeGrid': {
    			itemmousedown: function(selModel, record){
    				if(!record.data['leaf'] && record.data['bd_sonbomid'] > 0 && record.childNodes.length == 0){
    					Ext.getCmp('querygrid').loadChildNodes(record);
    				}
    			}
    		}
    	});
    }
});