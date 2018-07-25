Ext.define('erp.view.scm.reserve.InventoryByCondition',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	hideBorders: true, 
	initComponent : function(){ 
		var me=this;
		Ext.apply(me, { 
			items: [{
	    		xtype: 'erpFormPanel'
		    }]
		}); 
		me.callParent(arguments); 
	} 
});