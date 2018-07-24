Ext.define('erp.view.scm.product.ProductGroup',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				updateUrl: 'scm/product/updateProductGroup.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				keyField: 'pg_id',
				detno: 'pg_detno',
				mainField: 'pg_prid',
				necessaryField:'pg_name'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});