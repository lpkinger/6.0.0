Ext.define('erp.view.common.tempStore.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			region: 'north',         
			xtype: "panel",   
	    	anchor: '100% 100%',
	    	layout : 'fit',
	    	items : [{xtype: 'erpTempStoreGridPanel'}]
	    }]
		});
		me.callParent(arguments); 
	}
});