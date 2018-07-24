Ext.define('erp.view.pm.bom.BOMDetailDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BOMTestViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'pm/bom/saveBOMDetailDoc.action',
					deleteUrl: 'pm/bom/deleteBOMDetailDoc.action',
					updateUrl: 'pm/bom/updateBOMDetailDoc.action',
					getIdUrl: 'common/getId.action?seq=BOM_SEQ',
					keyField: 'bd_id',
					codeField: 'bd_soncode',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					detno: 'bdd_detno',
					keyField: 'bdd_id',
					mainField: 'bdd_bdid',
					necessaryField: 'bdd_doc'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});