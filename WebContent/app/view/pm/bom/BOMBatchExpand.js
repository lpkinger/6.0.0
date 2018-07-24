Ext.define('erp.view.pm.bom.BOMBatchExpand',{ 
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
					updateUrl: 'pm/bom/updateBOMBatchExpand.action',
					printUrl: 'pm/bom/printBOMSet.action',
					keyField: 'em_id',
					codeField: 'em_code',
					enableTools: false
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 80%', 
					detno: 'bb_detno',
					keyField: 'bb_id',
					mainField: 'bb_emid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});