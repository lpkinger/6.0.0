Ext.define('erp.view.scm.reserve.Periodsdetailfreeze',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack : 'center',
	},
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 350,
				height: 200,
				bodyStyle: 'background: #f1f1f1;',
				margin: '-30 0 0 0',
	    		xtype: 'Periodsdetailfreeze'
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});