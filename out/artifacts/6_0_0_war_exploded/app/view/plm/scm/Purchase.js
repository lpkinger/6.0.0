Ext.define('erp.view.plm.scm.Purchase',{ 
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
					saveUrl: 'plm/purchase/savePurchase.action',
					deleteUrl: 'plm/purchase/deletePurchase.action',
					updateUrl: 'plm/purchase/updatePurchase.action',
					auditUrl: 'plm/purchase/auditPurchase.action',
					printUrl: 'scm/purchase/printPurchase.action',
					resAuditUrl: 'plm/purchase/resAuditPurchase.action',
					submitUrl: 'plm/purchase/submitPurchase.action',
					resSubmitUrl: 'plm/purchase/resSubmitPurchase.action',
					endUrl: 'plm/purchase/endPurchase.action',
					resEndUrl: 'plm/purchase/resEndPurchase.action',
					getIdUrl: 'common/getId.action?seq=PURCHASE_SEQ',
					keyField: 'pu_id',
					codeField: 'pu_code',
					statusField: 'pu_status',
					statuscodeField: 'pu_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'pd_detno',
					necessaryField: 'pd_prodcode',
					keyField: 'pd_id',
					mainField: 'pd_puid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});