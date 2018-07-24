Ext.define('erp.view.scm.purchase.PurchaseSend',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/purchase/savePurchase.action',
				deleteUrl: 'scm/purchase/deletePurchase.action',
				updateUrl: 'scm/purchase/udpatePurchase.action',
				getIdUrl: 'common/getId.action?seq=PURCHASE_SEQ',
				keyField: 'pu_id',
				codeField: 'pu_code',
				statusField: 'pu_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});