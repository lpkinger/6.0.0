Ext.define('erp.view.scm.purchase.PurchaseClose',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'scm/purchase/auditPurchaseClose.action',
				resAuditUrl: 'scm/purchase/resAuditPurchaseClose.action',
				submitUrl: 'scm/purchase/submitPurchaseClose.action',
				resSubmitUrl: 'scm/purchase/resSubmitPurchaseClose.action',
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
				keyField: 'pc_id',
				codeField: 'pc_code',
				statusField: 'pc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pcd_detno',
				necessaryField: 'pcd_ordercode',
				keyField: 'pcd_id',
				mainField: 'pcd_pcid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});