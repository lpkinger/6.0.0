Ext.define('erp.view.pm.bom.BOMStepChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BOMStepChangeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 45%',
					saveUrl: 'pm/bom/saveBOMStepChange.action',
					deleteUrl: 'pm/bom/deleteBOMStepChange.action',
					updateUrl: 'pm/bom/updateBOMStepChange.action',
					auditUrl: 'pm/bom/auditBOMStepChange.action',
					submitUrl: 'pm/bom/submitBOMStepChange.action',
					resSubmitUrl: 'pm/bom/resSubmitBOMStepChange.action',
					getIdUrl: 'common/getId.action?seq=BOMSTEPCHANGE_SEQ',
					keyField: 'bc_id',
					statusField: 'bc_status',
					statuscodeField: 'bc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 55%', 
					detno: 'bcd_detno',
					keyField: 'bcd_id',
					mainField: 'bcd_bcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});