Ext.define('erp.view.scm.product.NewProductCon',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 75%',
				saveUrl: 'scm/product/saveNewProductCon.action',
				deleteUrl:'scm/product/deleteNewProductCon.action',
				updateUrl:'scm/product/updateNewProductCon.action',
				submitUrl:'scm/product/submitNewProductCon.action',
				resSubmitUrl:'scm/product/resSubmitNewProductCon.action',
				auditUrl:'scm/product/auditNewProductCon.action',
				resAuditUrl:'scm/product/resAuditNewProductCon.action',
				getIdUrl:'common/getId.action?seq=NewProductCon_SEQ',
			}]
		}); 
		me.callParent(arguments); 
	} 
});