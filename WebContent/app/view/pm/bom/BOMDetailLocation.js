Ext.define('erp.view.pm.bom.BOMDetailLocation',{ 
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
					saveUrl: 'pm/bom/saveBOMDetailLocation.action',
					deleteUrl: 'pm/bom/deleteBOMDetailLocation.action',
					updateUrl: 'pm/bom/updateBOMDetailLocation.action',
					getIdUrl: 'common/getId.action?seq=BOM_SEQ',
					keyField: 'bd_id',
					codeField: 'bd_soncode',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					detno: 'bdl_detno',
					keyField: 'bdl_id',
					mainField: 'bdl_bdid',
					necessaryField: 'bdl_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});