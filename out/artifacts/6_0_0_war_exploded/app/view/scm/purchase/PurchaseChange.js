Ext.define('erp.view.scm.purchase.PurchaseChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/purchase/savePurchaseChange.action',
				deleteUrl: 'scm/purchase/deletePurchaseChange.action',
				updateUrl: 'scm/purchase/updatePurchaseChange.action',
				auditUrl: 'scm/purchase/auditPurchaseChange.action',
				printUrl: 'scm/purchase/printPurchaseChange.action',
				resAuditUrl: 'scm/purchase/resAuditPurchaseChange.action',
				submitUrl: 'scm/purchase/submitPurchaseChange.action',
				resSubmitUrl: 'scm/purchase/resSubmitPurchaseChange.action',
				getIdUrl: 'common/getId.action?seq=PurchaseChange_SEQ',
				keyField: 'pc_id',
				codeField: 'pc_code',
				statusField: 'pc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'pcd_detno',
				keyField: 'pcd_id',
				mainField: 'pcd_pcid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});