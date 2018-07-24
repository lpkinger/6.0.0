Ext.define('erp.view.scm.purchase.MCApplication',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/purchase/saveMCApplication.action',
				deleteUrl: 'scm/purchase/deleteMCApplication.action',
				updateUrl: 'scm/purchase/updateMCApplication.action',
				auditUrl: 'scm/purchase/auditMCApplication.action',
				resAuditUrl: 'scm/purchase/resAuditMCApplication.action',
				submitUrl: 'scm/purchase/submitMCApplication.action',
				printUrl: 'scm/purchase/printMCApplication.action',
				resSubmitUrl: 'scm/purchase/resSubmitMCApplication.action',
				getIdUrl: 'common/getId.action?seq=APPLICATION_SEQ',
				keyField: 'ap_id',
				codeField: 'ap_code',
				statusField: 'ap_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'ad_detno',
				keyField: 'ad_id',
				allowExtraButtons:true,
				mainField: 'ad_apid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});