Ext.define('erp.view.scm.sale.ProductRate',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/sale/saveProductRate.action',
				deleteUrl: 'scm/sale/deleteProductRate.action',
				updateUrl: 'scm/sale/updateProductRate.action',
				auditUrl: 'scm/sale/auditProductRate.action',
				resAuditUrl: 'scm/sale/resAuditProductRate.action',
				submitUrl: 'scm/sale/submitProductRate.action',
				resSubmitUrl: 'scm/sale/resSubmitProductRate.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTRATE_SEQ',
				keyField: 'pdr_id',
				codeField: 'pdr_code',
				statusField: 'pdr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%', 
				detno: 'pdrd_detno',
				necessaryField: 'pdrd_prodcode',
				keyField: 'pdrd_id',
				mainField: 'pdrd_pdrid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});