Ext.define('erp.view.scm.purchase.PurchaseForecast',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/purchase/savePurchaseForecast.action',
				deleteUrl: 'scm/purchase/deletePurchaseForecast.action',
				updateUrl: 'scm/purchase/updatePurchaseForecast.action',
				auditUrl: 'scm/purchase/auditPurchaseForecast.action',
				resAuditUrl: 'scm/purchase/resAuditPurchaseForecast.action',
				submitUrl: 'scm/purchase/submitPurchaseForecast.action',
				resSubmitUrl: 'scm/purchase/resSubmitPurchaseForecast.action',
				getIdUrl: 'common/getId.action?seq=PURCHASEFORECAST_SEQ',
				codeField: 'pf_code',
				keyField: 'pf_id',
				statusField: 'pf_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pfd_detno',
				necessaryField: 'pfd_prodcode',
				keyField: 'pfd_id',
				mainField: 'pfd_pfid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});