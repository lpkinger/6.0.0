Ext.define('erp.view.fa.fp.Receivable',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 30%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveReceivable.action',
				deleteUrl: 'fa/fp/deleteReceivable.action',
				updateUrl: 'fa/fp/updateReceivable.action',
				auditUrl: 'fa/fp/auditReceivable.action',
				resAuditUrl: 'fa/fp/resAuditReceivable.action',
				submitUrl: 'fa/fp/submitReceivable.action',
				resSubmitUrl: 'fa/fp/resSubmitReceivable.action',
				getIdUrl: 'common/getId.action?seq=DebitContractRegister_SEQ',
				keyField: 'dcr_id',	
				statusField: 'dcr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%',
				detno:'dcrd_detno',
				keyField:'dcrd_id',
				mainField:'dcrd_dcrid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
