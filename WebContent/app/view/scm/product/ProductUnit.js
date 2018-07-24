Ext.define('erp.view.scm.product.ProductUnit',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				updateUrl: 'scm/product/updateProductUnit.action',			
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				keyField: 'pu_id',
				mainField: 'pu_prid',
				detno: 'pu_detno',
				necessaryField:'pu_otherunit'
			}]
		}); 
		me.callParent(arguments); 
	} 
});