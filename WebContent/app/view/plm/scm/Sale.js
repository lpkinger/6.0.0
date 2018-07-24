Ext.define('erp.view.plm.scm.Sale',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'saleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'plm/sale/saveSale.action',
					deleteUrl: 'plm/sale/deleteSale.action',
					updateUrl: 'plm/sale/updateSale.action',
					auditUrl: 'plm/sale/auditSale.action',
					resAuditUrl: 'plm/sale/resAuditSale.action',
					submitUrl: 'plm/sale/submitSale.action?caller='+caller,
					resSubmitUrl: 'plm/sale/resSubmitSale.action?caller='+caller,
					endUrl: 'plm/sale/endSale.action',
					resEndUrl: 'plm/sale/resEndSale.action',
					getIdUrl: 'common/getId.action?seq=SALE_SEQ',
					keyField: 'sa_id',
					codeField: 'sa_code',
					statuscodeField: 'sa_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'sd_detno',
					necessaryField: 'sd_prodcode',
					keyField: 'sd_id',
					mainField: 'sd_said',
					allowExtraButtons : true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});