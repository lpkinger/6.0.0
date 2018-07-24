Ext.define('erp.view.oa.officialDocument.sendODManagement.SODDetail',{ 
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
					xtype: 'erpSODDetailFormPanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});