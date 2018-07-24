Ext.define('erp.view.drp.aftersale.repair2order.Viewport',{
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  xtype:'erpRepair2OrderGridPanel',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});