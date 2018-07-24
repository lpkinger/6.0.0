Ext.define('erp.view.scm.reserve.ProductWarehouse',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/reserve/saveProductWarehouse.action',
				updateUrl: 'scm/reserve/updateProductWarehouse.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id', 
				codeField: 'pr_code',
				statusField: 'pr_statuscode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});