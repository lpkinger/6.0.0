Ext.define('erp.view.scm.sale.ReturnApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/sale/saveReturnApply.action',
				deleteUrl: 'scm/sale/deleteReturnApply.action',
				updateUrl: 'scm/sale/updateReturnApply.action',
				auditUrl: 'scm/sale/auditReturnApply.action',
				printUrl: 'scm/sale/printReturnApply.action',
				resAuditUrl: 'scm/sale/resAuditReturnApply.action',
				submitUrl: 'scm/sale/submitReturnApply.action',
				resSubmitUrl: 'scm/sale/resSubmitReturnApply.action',
				getIdUrl: 'common/getId.action?seq=RETURNAPPLY_SEQ',
				keyField: 'ra_id',
				codeField: 'ra_code',
				statusField: 'ra_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'rad_detno',
				necessaryField: 'rad_prodcode',
				keyField: 'rad_id',
				mainField: 'rad_raid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});