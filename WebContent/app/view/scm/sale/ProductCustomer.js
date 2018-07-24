Ext.define('erp.view.scm.sale.ProductCustomer',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',					
				updateUrl: 'scm/sale/updateProductCustomer.action',
				getIdUrl: 'common/getId.action?seq=PRODUCT_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'pc_detno',
				necessaryField: 'pc_custid',
				keyField: 'pc_id',
				mainField: 'pc_prodid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});