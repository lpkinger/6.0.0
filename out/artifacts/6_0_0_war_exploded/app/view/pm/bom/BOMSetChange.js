Ext.define('erp.view.pm.bom.BOMSetChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BomSetViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 45%',
					saveUrl: 'pm/bom/saveBOMSetChange.action',
					deleteUrl: 'pm/bom/deleteBOMSetChange.action',
					updateUrl: 'pm/bom/updateBOMSetChange.action',
					auditUrl: 'pm/bom/auditBOMSetChange.action',
					submitUrl: 'pm/bom/submitBOMSetChange.action',
					resSubmitUrl: 'pm/bom/resSubmitBOMSetChange.action',
					getIdUrl: 'common/getId.action?seq=BOMSETCHANGE_SEQ',
					keyField: 'bc_id',
					codeField: 'bc_code',
					statusField: 'bc_status',
					statuscodeField: 'bc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 55%', 
					detno: 'bcd_detno',
					keyField: 'bcd_id',
					mainField: 'bcd_bcid',
					necessaryField: 'bcd_prodcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});