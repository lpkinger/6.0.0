Ext.define('erp.view.scm.purchase.Application',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/purchase/saveApplication.action',
				deleteUrl: 'scm/purchase/deleteApplication.action',
				updateUrl: 'scm/purchase/updateApplication.action',
				auditUrl: 'scm/purchase/auditApplication.action',
				resAuditUrl: 'scm/purchase/resAuditApplication.action',
				submitUrl: 'scm/purchase/submitApplication.action',
				printUrl: 'scm/purchase/printApplication.action',
				resSubmitUrl: 'scm/purchase/resSubmitApplication.action',
				getIdUrl: 'common/getId.action?seq=APPLICATION_SEQ',
				keyField: 'ap_id',
				codeField: 'ap_code',
				statusField: 'ap_statuscode',
				voucherConfig:true
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'ad_detno',
				necessaryField: 'ad_prodcode',
				keyField: 'ad_id',
				allowExtraButtons:true,
				mainField: 'ad_apid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});