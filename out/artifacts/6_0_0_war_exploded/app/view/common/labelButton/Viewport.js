Ext.define('erp.view.common.labelButton.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},  
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
	    		xtype: 'erpLabelButtonFormPanel'
	    	}]
		});
		me.callParent(arguments); 
	}
});