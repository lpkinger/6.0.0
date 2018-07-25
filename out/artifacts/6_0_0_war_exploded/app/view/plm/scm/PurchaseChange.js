Ext.define('erp.view.plm.scm.PurchaseChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'purchaseViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'plm/purchasechange/savePurchaseChange.action',
					deleteUrl: 'plm/purchasechange/deletePurchaseChange.action',
					updateUrl: 'plm/purchasechange/updatePurchaseChange.action',
					auditUrl: 'plm/purchasechange/auditPurchaseChange.action',
					resAuditUrl: 'plm/purchasechange/resAuditPurchaseChange.action',
					submitUrl: 'plm/purchasechange/submitPurchaseChange.action',
					resSubmitUrl: 'plm/purchasechange/resSubmitPurchaseChange.action',
					getIdUrl: 'common/getId.action?seq=PurchaseChange_SEQ',
					keyField: 'pc_id',
					codeField: 'pc_code',
					statusField: 'pc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'pcd_detno',
					necessaryField: 'pcd_prodcode',
					keyField: 'pcd_id',
					mainField: 'pcd_pcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});