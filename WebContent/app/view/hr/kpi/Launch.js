Ext.define('erp.view.hr.kpi.Launch',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "erpBatchDealFormPanel",  
	    	anchor: '100% 25%'
	    },{
			region: 'center',         
			xtype: "erpBatchDealGridPanel",  
	    	anchor: '100% 75%',
	    }]
		});
		me.callParent(arguments); 
	}
});