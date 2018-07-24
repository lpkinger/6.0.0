Ext.define('erp.view.scm.purchase.PurchasePrice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/purchase/savePurchasePrice.action',
				deleteUrl: 'scm/purchase/deletePurchasePrice.action',
				updateUrl: 'scm/purchase/updatePurchasePrice.action',
				auditUrl: 'scm/purchase/auditPurchasePrice.action',
				resAuditUrl: 'scm/purchase/resAuditPurchasePrice.action',
				submitUrl: 'scm/purchase/submitPurchasePrice.action',
				resSubmitUrl: 'scm/purchase/resSubmitPurchasePrice.action',
				bannedUrl: 'scm/purchase/bannedPurchasePrice.action',
				resBannedUrl: 'scm/purchase/resBannedPurchasePrice.action',
				getIdUrl: 'common/getId.action?seq=PURCHASEPRICE_SEQ',
				codeField: 'pp_code',
				keyField: 'pp_id',
				statusField: 'pp_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'ppd_detno',
				necessaryField: 'ppd_prodcode',
				allowExtraButtons: true,
				keyField: 'ppd_id',
				mainField: 'ppd_ppid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});