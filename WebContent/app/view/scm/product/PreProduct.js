Ext.define('erp.view.scm.product.PreProduct',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/savePreProduct.action',
				deleteUrl: 'scm/product/deletePreProduct.action',
				updateUrl: 'scm/product/updatePreProduct.action',
				auditUrl: 'scm/product/auditPreProduct.action',
				resAuditUrl: 'scm/product/resAuditPreProduct.action',
				submitUrl: 'scm/product/submitPreProduct.action',
				resSubmitUrl: 'scm/product/resSubmitPreProduct.action',
				getIdUrl: 'common/getId.action?seq=PREPRODUCT_SEQ',
				keyField: 'pre_id',
				statusField: 'pre_status',
				statuscodeField: 'pre_statuscode',
				codeField: 'pre_thisid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});