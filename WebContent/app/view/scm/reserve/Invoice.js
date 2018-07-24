Ext.define('erp.view.scm.reserve.Invoice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/reserve/saveInvoice.action',
				deleteUrl: 'scm/reserve/deleteInvoice.action',
				updateUrl: 'scm/reserve/updateInvoice.action',
				submitUrl: 'scm/reserve/submitInvoice.action',
				resSubmitUrl: 'scm/reserve/resSubmitInvoice.action',
				auditUrl: 'scm/reserve/auditInvoice.action',
				resAuditUrl: 'scm/reserve/resAuditInvoice.action',
				postUrl: 'scm/reserve/postInvoice.action',
				printUrl: 'scm/reserve/printInvoice.action',
				printDeliveryUrl: 'scm/reserve/printInvoice.action',
				resPostUrl: 'scm/reserve/resPostInvoice.action',
				getIdUrl: 'common/getId.action?seq=Invoice_SEQ',
				keyField: 'in_id',
				codeField: 'in_code',
				statuscodeField: 'in_statuscode',
				statusField: 'in_status'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'id_detno',
				keyField: 'id_id',
				mainField: 'id_inid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});