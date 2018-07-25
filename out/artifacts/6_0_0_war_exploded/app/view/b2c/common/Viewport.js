Ext.define('erp.view.b2c.common.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'center',      
				xtype: "erpb2cPanel" ,
		    	anchor: '100% 100%'
		    }]
		});
		me.callParent(arguments);
	}
});