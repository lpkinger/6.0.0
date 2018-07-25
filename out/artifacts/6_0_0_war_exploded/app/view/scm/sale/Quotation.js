Ext.define('erp.view.scm.sale.Quotation',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/sale/saveQuotation.action',
				deleteUrl: 'scm/sale/deleteQuotation.action',
				updateUrl: 'scm/sale/updateQuotation.action',
				auditUrl: 'scm/sale/auditQuotation.action',
				printUrl: 'scm/sale/printQuotation.action',
				resAuditUrl: 'scm/sale/resAuditQuotation.action',
				submitUrl: 'scm/sale/submitQuotation.action',
				resSubmitUrl: 'scm/sale/resSubmitQuotation.action',
				bannedUrl: 'scm/sale/bannedQuotation.action',
				resBannedUrl: 'scm/sale/resBannedQuotation.action',
				getIdUrl: 'common/getId.action?seq=QUOTATION_SEQ',
				keyField: 'qu_id',
				codeField: 'qu_code',
				statusField: 'qu_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'qd_detno',
				necessaryField: 'qd_prodcode',
				allowExtraButtons: true,
				keyField: 'qd_id',
				mainField: 'qd_quid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});