Ext.define('erp.view.oa.fee.FeeAccount',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},  
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
	    		xtype: 'FeeAccount'
			}]
		}); 
		me.callParent(arguments); 
	} 
});