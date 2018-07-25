Ext.define('erp.view.fa.fp.ReceivablePlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 20%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveReceivablePlan.action',
				deleteUrl: 'fa/fp/deleteReceivablePlan.action',
				updateUrl: 'fa/fp/updateReceivablePlan.action',
				auditUrl: 'fa/fp/auditReceivablePlan.action',
				resAuditUrl: 'fa/fp/resAuditReceivablePlan.action',
				submitUrl: 'fa/fp/submitReceivablePlan.action',
				resSubmitUrl: 'fa/fp/resSubmitReceivablePlan.action',
				getIdUrl: 'common/getId.action?seq=DebitContractRegister_SEQ',
				keyField: 'dcr_id',	
				statusField: 'dcr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%',
				detno:'dcrd_detno',
				keyField:'dcrd_id',
				mainField:'dcrd_dcrid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
