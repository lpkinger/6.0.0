Ext.define('erp.view.drp.distribution.SaleAsk',{ 
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
					saveUrl: 'drp/distribution/saveSaleAsk.action',
					deleteUrl: 'drp/distribution/deleteSaleAsk.action',
					updateUrl: 'drp/distribution/updateSaleAsk.action',
					auditUrl: 'drp/distribution/auditSaleAsk.action',
					resAuditUrl: 'drp/distribution/resAuditSaleAsk.action',
					submitUrl: 'drp/distribution/submitSaleAsk.action',
					resSubmitUrl: 'drp/distribution/resSubmitSaleAsk.action',
					endUrl: 'drp/distribution/endSaleAsk.action',
					resEndUrl: 'drp/distribution/resEndSaleAsk.action',
					getIdUrl: 'common/getId.action?seq=SALEASK_SEQ',
				    printUrl:'drp/distribution/printSaleAsk.action',
					keyField: 'sa_id',
					codeField: 'sa_code',
					statuscodeField: 'sa_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'sd_detno',
					necessaryField: 'sd_prodcode',
					keyField: 'sd_id',
					mainField: 'sd_said'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});