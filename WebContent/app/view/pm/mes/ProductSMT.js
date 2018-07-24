Ext.define('erp.view.pm.mes.ProductSMT',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ProductSMTViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'pm/mes/saveProductSMT.action',
					deleteUrl: 'pm/mes/deleteProductSMT.action',
					updateUrl: 'pm/mes/updateProductSMT.action',
					getIdUrl: 'common/getId.action?seq=ProductSMT_SEQ',
					submitUrl: 'pm/mes/submitProductSMT.action',
					auditUrl: 'pm/mes/auditProductSMT.action',
					resAuditUrl: 'pm/mes/resAuditProductSMT.action',			
					resSubmitUrl: 'pm/mes/resSubmitProductSMT.action',
					keyField: 'ps_id',
					codeField: 'ps_code', 
					statusField: 'ps_status',
					statuscodeField: 'ps_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'psl_detno',
					keyField: 'psl_id',
					mainField: 'psl_psid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});