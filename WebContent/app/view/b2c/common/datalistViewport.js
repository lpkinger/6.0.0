Ext.define('erp.view.b2c.common.datalistViewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
			items: [{
				region : 'center',
				xtype: "erpb2cDatalistPanel" ,
		    	anchor: '100% 100%'
		    }]
		});
		me.callParent(arguments); 
	}
});