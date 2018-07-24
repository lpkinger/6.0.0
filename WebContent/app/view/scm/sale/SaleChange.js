Ext.define('erp.view.scm.sale.SaleChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/sale/saveSaleChange.action?caller=' + caller,
				deleteUrl: 'scm/sale/deleteSaleChange.action?caller=' + caller,
				updateUrl: 'scm/sale/updateSaleChange.action?caller=' + caller,
				auditUrl: 'scm/sale/auditSaleChange.action?caller=' + caller,
				printUrl: 'scm/sale/printSaleChange.action?caller=' + caller,
				resAuditUrl: 'scm/sale/resAuditSaleChange.action?caller=' + caller,
				submitUrl: 'scm/sale/submitSaleChange.action?caller=' + caller,
				resSubmitUrl: 'scm/sale/resSubmitSaleChange.action?caller=' + caller,
				getIdUrl: 'common/getId.action?seq=SALECHANGE_SEQ',
				keyField: 'sc_id',
				codeField: 'sc_code',
				statusField: 'sc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'scd_detno',
				necessaryField: 'scd_prodcode',
				keyField: 'scd_id',
				mainField: 'scd_scid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});