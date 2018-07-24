Ext.define('erp.view.scm.product.ProductDescription',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',									
				updateUrl: 'scm/product/updateProductDescription.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code',
				statusField: 'pr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'pd_detno',
				necessaryField: 'pd_description',
				keyField: 'pd_id',
				mainField: 'pd_prid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});