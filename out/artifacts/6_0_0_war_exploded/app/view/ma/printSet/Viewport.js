Ext.define('erp.view.ma.printSet.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype:'erpPrintSetGridPanel',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});