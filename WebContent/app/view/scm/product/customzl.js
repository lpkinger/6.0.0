Ext.define('erp.view.scm.product.customzl',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/product/saveCustomzl.action',
				updateUrl: 'scm/product/updateCustomzl.action',
				getIdUrl: 'common/getId.action?seq=customzlb_SEQ',
				deleteUrl: 'scm/product/deleteCustomzl.action',
				keyField: 'cz_id', 
				codeField: '',
				statusField: 'cz_statuscode',
				fixedlayout:true
			}]
		}); 
		me.callParent(arguments); 
	} 
});