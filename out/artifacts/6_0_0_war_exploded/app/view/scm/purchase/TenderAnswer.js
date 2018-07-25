Ext.define('erp.view.scm.purchase.TenderAnswer',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/purchase/saveTenderAnswer.action',
				deleteUrl: 'scm/purchase/deleteTenderAnswer.action',
				updateUrl: 'scm/purchase/updateTenderAnswer.action',
				auditUrl: 'scm/purchase/auditTenderAnswer.action',
				resAuditUrl: 'scm/purchase/resAuditTenderAnswer.action',
				printUrl: 'scm/purchase/printTenderAnswer.action',
				submitUrl: 'scm/purchase/submitTenderAnswer.action',
				resSubmitUrl: 'scm/purchase/resSubmitTenderAnswer.action',
				keyField: 'id',
				codeField: 'code',
				statusField: 'auditstatus',
				statuscodeField: 'auditstatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'detno',
				needUpdate: false,
				keyField: 'id',
				mainField: 'tsid',
				necessaryField: 'topic'
			}]
		}); 
		me.callParent(arguments); 
	} 
});