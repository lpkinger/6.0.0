Ext.define('erp.view.pm.mps.SaleForecastChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [
				{
					xtype: 'erpFormPanel',
					anchor: '100% 25%',
					saveUrl: 'scm/sale/saveSaleForeCastChange.action',
					deleteUrl: 'scm/sale/deleteSaleForeCastChange.action',
					updateUrl: 'scm/sale/updateSaleForeCastChange.action',
					submitUrl:'scm/sale/submitSaleForeCastChange.action',
					resSubmitUrl:'scm/sale/resSubmitSaleForeCastChange.action',
					auditUrl:'scm/sale/auditSaleForeCastChange.action',
					resAuditUrl:'scm/sale/resAuditSaleForeCastChange.action',
					getIdUrl: 'common/getId.action?seq=SALEFORECASTCHANGE_SEQ',
					keyField: 'sc_id',
					codeField:'sc_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%',
					detno: 'sd_detno',
					necessaryField: 'sd_code',
					keyField: 'scd_id',
					mainField: 'scd_mainid'
				  }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});