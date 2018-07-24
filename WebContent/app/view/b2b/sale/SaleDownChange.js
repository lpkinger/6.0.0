Ext.define('erp.view.b2b.sale.SaleDownChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',	
				auditUrl: 'b2b/sale/auditSaleDownChange.action',
				updateUrl: 'b2b/sale/updateSaleDownChange.action',
				resAuditUrl: 'b2b/sale/resSaleDownChange.action',
				confirmUrl:'b2b/sale/confirmSaleDownChange.action',
				submitUrl: 'b2b/sale/submitSaleDownChange.action',
				resSubmitUrl: 'b2b/sale/resSubmitSaleDownChange.action',
				keyField: 'sc_id',
				codeField: 'sc_code',
				statusField: 'sc_status',
				statuscodeField: 'sc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'scd_detno',
				keyField: 'scd_id',
				mainField: 'scd_scid'				
			}]
		}); 
		me.callParent(arguments); 
	} 
});