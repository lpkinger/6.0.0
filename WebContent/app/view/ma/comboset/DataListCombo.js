//问题反馈编号：2016120061
Ext.define('erp.view.ma.comboset.DataListCombo',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.applyIf(me, { 
		items: [{
	    	  xtype:'erpCombolistGridPanel',
	    	  anchor: '100% 100%'
	    }]
		});
		me.callParent(arguments); 
	}
});