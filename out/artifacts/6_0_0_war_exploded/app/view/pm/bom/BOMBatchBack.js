Ext.define('erp.view.pm.bom.BOMBatchBack',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 20%',
					updateUrl: 'pm/bom/updateBOMBatchBack.action',
					keyField: 'em_id',
					codeField: 'em_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					detno:'bm_detno',
					keyField:'bm_id',
					mainField:'bm_emid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});