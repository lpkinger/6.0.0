Ext.define('erp.view.scm.sale.SaleClash',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/sale/saveSaleClash.action',
				deleteUrl: 'scm/sale/deleteSaleClash.action',
				updateUrl: 'scm/sale/updateSaleClash.action',
				auditUrl: 'scm/sale/auditSaleClash.action',
				resAuditUrl: 'scm/sale/resAuditSaleClash.action',
				submitUrl: 'scm/sale/submitSaleClash.action',
				resSubmitUrl: 'scm/sale/resSubmitSaleClash.action',
				getIdUrl: 'common/getId.action?seq=SALECLASH_SEQ',
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