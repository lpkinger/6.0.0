Ext.define('erp.view.scm.purchase.VendorPerformanceAssess',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/saveVPA.action',
				updateUrl: 'scm/purchase/updateVPA.action',
				deleteUrl: 'scm/purchase/deleteVPA.action',
				auditUrl: 'scm/purchase/auditVPA.action',
				resAuditUrl: 'scm/purchase/resAuditVPA.action',
				submitUrl: 'scm/purchase/submitVPA.action',
				resSubmitUrl: 'scm/purchase/resSubmitVPA.action',
				getIdUrl: 'common/getId.action?seq=VENDORPERFORMANCEASSESS_SEQ',
				keyField: 'vpa_id',
				codeField: 'vpa_code',
				statusField: 'vpa_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});