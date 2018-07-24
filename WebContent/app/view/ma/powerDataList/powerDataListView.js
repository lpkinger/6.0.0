Ext.define('erp.view.ma.powerDataList.powerDataListView',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype:'erppowerdatalist',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});