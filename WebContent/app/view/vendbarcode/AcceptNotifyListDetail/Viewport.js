Ext.define('erp.view.vendbarcode.AcceptNotifyListDetail.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
		items: [{
	    	  xtype:'acceptNotifyListDetailGrid',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});