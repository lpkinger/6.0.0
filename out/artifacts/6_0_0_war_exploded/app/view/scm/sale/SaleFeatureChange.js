Ext.define('erp.view.scm.sale.SaleFeatureChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/sale/saveSaleFeatureChange.action?caller=' + caller,
				deleteUrl: 'scm/sale/deleteSaleFeatureChange.action?caller=' + caller,
				updateUrl: 'scm/sale/updateSaleFeatureChange.action?caller=' + caller,
				auditUrl: 'scm/sale/auditSaleFeatureChange.action?caller=' + caller,
				printUrl: 'scm/sale/printSaleFeatureChange.action?caller=' + caller,
				resAuditUrl: 'scm/sale/resAuditSaleFeatureChange.action?caller=' + caller,
				submitUrl: 'scm/sale/submitSaleFeatureChange.action?caller=' + caller,
				resSubmitUrl: 'scm/sale/resSubmitSaleFeatureChange.action?caller=' + caller,
				getIdUrl: 'common/getId.action?seq=SaleFeatureChange_SEQ',
				keyField: 'sfc_id',
				codeField: 'sfc_code',
				statusField: 'sfc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'sfcd_detno',
				necessaryField: 'sfcd_oldfecode',
				keyField: 'sfcd_id',
				mainField: 'sfcd_scid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});