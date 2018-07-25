Ext.define('erp.view.scm.product.Productlevel',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/product/saveProductlevel.action',
				deleteUrl: 'scm/product/deleteProductlevel.action',
				updateUrl: 'scm/product/updateProductlevel.action',		
				getIdUrl: 'common/getId.action?seq=Productlevel_SEQ',
				auditUrl: 'scm/product/auditProductlevel.action',
				resAuditUrl: 'scm/product/resAuditProductlevel.action',
				submitUrl: 'scm/product/submitProductlevel.action',
				resSubmitUrl: 'scm/product/resSubmitProductlevel.action',
				keyField: 'pl_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				necessaryField: 'pd_billtype',
				keyField: 'pd_id',
				detno: 'pd_detno',
				mainField: 'pd_plid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});