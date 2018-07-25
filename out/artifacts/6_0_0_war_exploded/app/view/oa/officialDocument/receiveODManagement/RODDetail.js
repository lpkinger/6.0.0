Ext.define('erp.view.oa.officialDocument.receiveODManagement.RODDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype: 'erpRODDetailFormPanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});