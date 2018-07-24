Ext.define('erp.view.common.print.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: {
		type: isPrintWindow_?'fit':'vbox',
		align: 'center'
	},
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpPrintFormPanel',
				minMode: true,
				width: isPrintWindow_?'auto':550,
				height: isPrintWindow_?'auto':400,
				bodyStyle: 'padding: 10px',
				margin: isPrintWindow_?'0':'40 0 0 0'
		    }]
		});
		me.callParent(arguments); 
	}
});