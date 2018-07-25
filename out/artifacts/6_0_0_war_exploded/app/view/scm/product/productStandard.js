Ext.define('erp.view.scm.product.productStandard',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'scm/product/saveProductStandard.action',
					deleteUrl: 'scm/product/deleteProductStandard.action',
					updateUrl: 'scm/product/updateProductStandard.action',
					auditUrl: 'scm/product/auditProductStandard.action',
					resAuditUrl: 'scm/product/resAuditProductStandard.action',
					submitUrl: 'scm/product/submitProductStandard.action',
					resSubmitUrl: 'scm/product/resSubmitProductStandard.action',
					getIdUrl: 'common/getId.action?seq=PRODUCTSTANDARDRATE_SEQ',
					keyField: 'psr_id',
					codeField:'psr_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					detno: 'pd_detno',
					keyField:'pd_id',
					mainField: 'pd_psrid'
				 }
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});