Ext.define('erp.view.common.datalist.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
		items: [{
	    	  xtype:'erpDatalistGridPanel',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});