Ext.define('erp.view.fa.ars.cmCopQuery.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpCmCopQueryGridPanel',  
	    	  anchor: '100% 100%'
	      }]
		});
		me.callParent(arguments); 
	}
});