Ext.define('erp.view.scm.reserve.profit.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
				items: [{
					xtype: "erpProfitGridPanel",  
			    	anchor: '100% 100%',
			    	keyField:'bdd_bsid',
			    	mainField:'bdd_bsdid',
			     }]  
		});
		me.callParent(arguments); 
	}
});