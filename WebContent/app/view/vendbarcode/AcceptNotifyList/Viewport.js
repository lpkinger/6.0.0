Ext.define('erp.view.vendbarcode.AcceptNotifyList.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
		items: [{
	    	  xtype:'acceptNotifyListGrid',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});