Ext.define('erp.view.pm.bom.BomSet',{ 
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
					anchor: '100% 48%',
					saveUrl: 'pm/bom/saveBomSet.action',
					deleteUrl: 'pm/bom/deleteBomSet.action',
					updateUrl: 'pm/bom/updateBomSet.action',
					auditUrl: 'pm/bom/auditBomSet.action',
					resAuditUrl: 'pm/bom/resAuditBomSet.action',
					submitUrl: 'pm/bom/submitBomSet.action',
					resSubmitUrl: 'pm/bom/resSubmitBomSet.action',
					getIdUrl: 'common/getId.action?seq=BomSet_SEQ',
					keyField: 'bs_id',
					codeField: 'bs_code',
					statusField: 'bs_status',
					statuscodeField: 'bs_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 52%', 
					detno: 'bsd_detno',
					keyField: 'bsd_id',
					mainField: 'bsd_bsid',
					necessaryField: 'bsd_prodcode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});