Ext.define('erp.view.scm.sale.SaleClose',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'scm/sale/auditSaleClose.action',
				resAuditUrl: 'scm/sale/resAuditSaleClose.action',
				submitUrl: 'scm/sale/submitSaleClose.action',
				resSubmitUrl: 'scm/sale/resSubmitSaleClose.action',
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
				keyField: 'sc_id',
				codeField: 'sc_code',
				statusField: 'sc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'scd_detno',
				necessaryField: 'scd_ordercode',
				keyField: 'scd_id',
				mainField: 'scd_scid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});