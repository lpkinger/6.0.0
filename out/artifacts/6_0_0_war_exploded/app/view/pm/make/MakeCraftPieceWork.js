Ext.define('erp.view.pm.make.MakeCraftPieceWork',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',		
				updateUrl: 'pm/make/updateMakeCraftPieceWork.action',				
				keyField : 'ma_id',
				statusField : 'ma_status',
				codeField : 'ma_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				keyField : 'mcp_id',
				mainField : 'mcp_maid'
			}]
		}); 
		me.callParent(arguments); 
	}
});