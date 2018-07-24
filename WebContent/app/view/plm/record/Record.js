Ext.define('erp.view.plm.record.Record',{ 
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
					xtype:'erpRecordTreePanel',
					region:'west'
				},{
				    xtype:'erpRecordGridPanel',
				    region:'center',
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});