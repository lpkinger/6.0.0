Ext.define('erp.view.ma.specialPowerDataList.specialPowerDataListView',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype:'erpspecialpowerdatalist',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});