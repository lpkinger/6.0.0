Ext.QuickTips.init();
Ext.define('erp.controller.ma.specialPowerDataList', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['ma.specialPowerDataList.specialPowerDataList','ma.specialPowerDataList.specialPowerDataListView','ma.powerDataList.powerDataListToolBar'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');		
		this.control({
			
			'erppowerdatalist':{
				beforeshow:function(t,o){					
					t.store.load();	
				}
			}			
		});	
	}	
});