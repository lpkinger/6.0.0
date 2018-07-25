Ext.define('erp.view.scm.purchase.SampleInquiry',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 45%',
				saveUrl: 'scm/purchase/saveInquiry.action',
				updateUrl: 'scm/purchase/updateInquiry.action',
				deleteUrl: 'scm/purchase/deleteInquiry.action',
				auditUrl: 'scm/purchase/auditInquiry.action',
				resAuditUrl: 'scm/purchase/resAuditInquiry.action',
				submitUrl: 'scm/purchase/submitInquiry.action',
				resSubmitUrl: 'scm/purchase/resSubmitInquiry.action',
				getIdUrl: 'common/getId.action?seq=INQUIRY_SEQ',
				keyField: 'in_id',
				codeField: 'in_code',
				statusField: 'in_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 55%', 
				detno: 'id_detno',
				necessaryField: 'id_vendcode',
				keyField: 'id_id',
				mainField: 'id_inid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});