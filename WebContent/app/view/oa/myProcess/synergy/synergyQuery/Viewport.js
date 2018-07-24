Ext.define('erp.view.oa.myProcess.synergy.synergyQuery.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpSynergyQueryFormPanel',  
	    	  anchor: '100% 30%'
	    },{
	    	  region: 'south',         
	    	  xtype:'erpSynergyQueryGridPanel',  
	    	  anchor: '100% 70%'
	    }]
		});
		me.callParent(arguments); 
	}
});