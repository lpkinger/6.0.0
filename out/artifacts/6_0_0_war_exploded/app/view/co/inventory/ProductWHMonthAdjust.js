Ext.define('erp.view.co.inventory.ProductWHMonthAdjust',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'co/inventory/saveProductWHMonthAdjust.action',
				deleteUrl: 'co/inventory/deleteProductWHMonthAdjust.action',
				updateUrl: 'co/inventory/updateProductWHMonthAdjust.action',
				auditUrl: 'co/inventory/auditProductWHMonthAdjust.action',
				resAuditUrl: 'co/inventory/resAuditProductWHMonthAdjust.action',
				submitUrl: 'co/inventory/submitProductWHMonthAdjust.action',
				resSubmitUrl: 'co/inventory/resSubmitProductWHMonthAdjust.action',
				postUrl: 'co/inventory/postProductWHMonthAdjust.action',
				resPostUrl: 'co/inventory/resPostProductWHMonthAdjust.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTWHMONTHADJUST_SEQ',
				keyField: 'pwa_id',
				codeField: 'pwa_code',
				statusField: 'pwa_status',
				statuscodeField: 'pwa_statuscode',
				voucherConfig: {
					voucherField: 'pwa_vouchercode',
					vs_code: 'ProductWHMonthAdjust',
					yearmonth: 'pwa_date',
					datas: 'pwa_code',
					status: 'pwa_statuscode',
					mode: 'single',
					kind: '期初调整单',
					vomode: 'ST'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'pwd_detno',
				necessaryField: 'pwd_prodcode',
				keyField: 'pwd_id',
				mainField: 'pwd_pwaid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});