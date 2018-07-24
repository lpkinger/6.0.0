Ext.define('erp.view.scm.product.ProductMatch',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '120% 100%',
				autoScroll: true,
				saveUrl: 'scm/product/saveProductMatch.action',
				deleteUrl: 'scm/product/deleteProductMatch.action',
				updateUrl: 'scm/product/updateProductMatch.action',
				getIdUrl: 'common/getId.action?seq=ProductMatch_SEQ',
				keyField: 'pm_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});