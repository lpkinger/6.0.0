Ext.define('erp.view.pm.bom.ProductReview',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'pm/bom/saveProductReview.action',
				deleteUrl: 'pm/bom/deleteProductReview.action',
				updateUrl: 'pm/bom/updateProductReview.action',
				getIdUrl: 'common/getId.action?seq=ProductReview_SEQ',
				submitUrl: 'pm/bom/submitProductReview.action',
				auditUrl: 'pm/bom/auditProductReview.action',
				resAuditUrl: 'pm/bom/resAuditProductReview.action',			
				resSubmitUrl: 'pm/bom/resSubmitProductReview.action',
				keyField: 'pv_id',
				codeField: 'pv_code', 
				statusField: 'pv_status',
				statuscodeField: 'pv_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pvd_detno',
				keyField: 'pvd_id',
				mainField: 'pvd_pvid',
				allowExtraButtons : true
			}]
		}); 
		me.callParent(arguments); 
	} 
});