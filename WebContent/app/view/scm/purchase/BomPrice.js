Ext.define('erp.view.scm.purchase.BomPrice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/purchase/saveBomPrice.action',
				deleteUrl: 'scm/purchase/deleteBomPrice.action',
				updateUrl: 'scm/purchase/updateBomPrice.action',
				auditUrl: 'scm/purchase/auditBomPricee.action',
				resAuditUrl: 'scm/purchase/resAuditBomPrice.action',
				submitUrl: 'scm/purchase/submitBomPrice.action',
				resSubmitUrl: 'scm/purchase/resSubmitBomPrice.action',
				bannedUrl: '',
				resBannedUrl: '',
				getIdUrl: 'common/getId.action?seq=BOMPRICE_SEQ',
				keyField: 'bp_id',
				codeField: 'bp_code',
				statusField: 'bp_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				//detno: 'bpd_detno',
				necessaryField: 'bpd_prodcode',
				keyField: 'bpd_id',
				mainField: 'bpd_bpid',
				//forceFit: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }),
			     Ext.create('erp.view.core.plugin.CopyPasteMenu')]
			}]
		}); 
		me.callParent(arguments); 
	} 
});