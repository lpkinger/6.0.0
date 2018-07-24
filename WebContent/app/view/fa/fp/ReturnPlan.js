Ext.define('erp.view.fa.fp.ReturnPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				anchor: '100% 20%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/fp/saveReturnPlan.action',
				deleteUrl: 'fa/fp/deleteReturnPlan.action',
				updateUrl: 'fa/fp/updateReturnPlan.action',
				auditUrl: 'fa/fp/auditReturnPlan.action',
				resAuditUrl: 'fa/fp/resAuditReturnPlan.action',
				submitUrl: 'fa/fp/submitReturnPlan.action',
				resSubmitUrl: 'fa/fp/resSubmitReturnPlan.action',
				getIdUrl: 'common/getId.action?seq=CreditContractRegister_SEQ',
				keyField: 'ccr_id',	
				statusField: 'ccr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%',
				detno: 'ccrd_detno',
				keyFeild:'ccrd_id',
				mainField:'ccrd_ccrid',
			}]
		}); 
		me.callParent(arguments); 
	} 
});
