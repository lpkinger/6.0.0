Ext.define('erp.view.crm.chance.b2bBusinessChance.B2BBusinessChance',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
		items: [{
	    	  xtype:'BBCGridPanel'
	    }]
		});
		me.callParent(arguments); 
	}
});