Ext.define('erp.view.scm.sale.SalePrice',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/sale/saveSalePrice.action',
				deleteUrl: 'scm/sale/deleteSalePrice.action',
				updateUrl: 'scm/sale/updateSalePrice.action',
				auditUrl: 'scm/sale/auditSalePrice.action',
				resAuditUrl: 'scm/sale/resAuditSalePrice.action',
				submitUrl: 'scm/sale/submitSalePrice.action',
				resSubmitUrl: 'scm/sale/resSubmitSalePrice.action',
				getIdUrl: 'common/getId.action?seq=SALEPRICE_SEQ',
				keyField: 'sp_id',
				codeField: 'sp_code',
				statusField: 'sp_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'spd_detno',
				necessaryField: 'spd_prodcode',
				keyField: 'spd_id',
				mainField: 'spd_spid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});