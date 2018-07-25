Ext.define('erp.view.fa.fix.mQuery.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpMQueryGridPanel',  
	    	  anchor: '100% 100%'
	      }]
		});
		me.callParent(arguments); 
	}
});