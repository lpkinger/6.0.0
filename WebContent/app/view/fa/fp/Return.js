Ext.define('erp.view.fa.fp.Return',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 30%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveReturn.action',
				deleteUrl: 'fa/fp/deleteReturn.action',
				updateUrl: 'fa/fp/updateReturn.action',
				auditUrl: 'fa/fp/auditReturn.action',
				resAuditUrl: 'fa/fp/resAuditReturn.action',
				submitUrl: 'fa/fp/submitReturn.action',
				resSubmitUrl: 'fa/fp/resSubmitReturn.action',
				getIdUrl: 'common/getId.action?seq=CreditContractRegister_SEQ',
				keyField: 'ccr_id',	
				statusField: 'ccr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%',
				detno:'ccrd_detno',
				keyField:'ccrd_id',
				mainField:'ccrd_ccrid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});
