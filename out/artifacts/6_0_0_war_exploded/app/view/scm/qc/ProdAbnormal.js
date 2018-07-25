Ext.define('erp.view.scm.qc.ProdAbnormal',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/qc/saveProdAbnormal.action',
				deleteUrl: 'scm/qc/deleteProdAbnormal.action',
				updateUrl: 'scm/qc/updateProdAbnormal.action',
				auditUrl: 'scm/qc/auditProdAbnormal.action',
				resAuditUrl: 'scm/qc/resAuditProdAbnormal.action',
				submitUrl: 'scm/qc/submitProdAbnormal.action',
				resSubmitUrl: 'scm/qc/resSubmitProdAbnormal.action',
				checkUrl: 'scm/qc/checkProdAbnormal.action',
				resCheckUrl: 'scm/qc/resCheckProdAbnormal.action',
				getIdUrl: 'common/getId.action?seq=PRODABNORMAL_SEQ',
				codeField: 'pa_code',
				keyField: 'pa_id',
				statusField: 'pa_status',
				statuscodeField: 'pa_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});