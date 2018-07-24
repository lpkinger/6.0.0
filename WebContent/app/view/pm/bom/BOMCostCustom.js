Ext.define('erp.view.pm.bom.BOMCostCustom',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
		    		region: 'center',
		    		xtype: 'erpFormPanel',
		    		printUrl: 'pm/bom/printBOM.action',
		    		//enableTools: false
		    }]
		}); 
		me.callParent(arguments); 
	} 
});