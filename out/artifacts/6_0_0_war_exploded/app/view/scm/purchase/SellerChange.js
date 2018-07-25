Ext.define('erp.view.scm.purchase.SellerChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'scm/purchase/saveSellerChange.action',
					deleteUrl: 'scm/purchase/deleteSellerChange.action',
					updateUrl: 'scm/purchase/updateSellerChange.action',
					auditUrl: 'scm/purchase/auditSellerChange.action',
					resAuditUrl: 'plm/sale/resAuditSale.action',
					submitUrl: 'scm/purchase/submitSellerChange.action?caller='+caller,
					resSubmitUrl: 'scm/purchase/resSubmitSellerChange.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=SELLERCHANGE_SEQ'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					allowExtraButtons : true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});