Ext.define('erp.view.scm.sale.NonPreSale',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	width:1058,
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		console.log(window.innerWidth);
		Ext.apply(me, { 
			items: [{ 
				layout: 'fit', 
				autoScroll: true,
				items: [{
					xtype: 'erpFormPanel',
					width:2052,
					autoScroll: true,
					layout:'column',
					saveUrl: 'scm/sale/savePreSale.action?caller='+caller,
					deleteUrl: 'scm/sale/deletePreSale.action?caller='+caller,
					updateUrl: 'scm/sale/updatePreSale.action?caller='+caller,
					auditUrl: 'scm/sale/auditPreSale.action?caller='+caller,
					resAuditUrl: 'scm/sale/resAuditPreSale.action?caller='+caller,
					submitUrl: 'scm/sale/submitPreSale.action?caller='+caller,
					resSubmitUrl: 'scm/sale/resSubmitPreSale.action?caller='+caller,
					getOtherPreSaleValues:'scm/sale/getOtherPreSaleValues.action',
					getIdUrl: 'common/getId.action?seq=PreSale_SEQ',
					keyField: 'ps_id',
					codeField: 'ps_code',
					statuscodeField: 'ps_statuscode',
					statusField: 'ps_status'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});