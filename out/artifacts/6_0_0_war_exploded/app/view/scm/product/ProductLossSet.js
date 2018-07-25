Ext.define('erp.view.scm.product.ProductLossSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/product/saveProductLossSet.action',
				updateUrl: 'scm/product/updateProductLossSet.action',
				deleteUrl: 'scm/product/deleteProductLossSet.action',
				auditUrl: 'scm/product/auditProductLossSet.action',
				resAuditUrl: 'scm/product/resAuditProductLossSet.action',
				submitUrl: 'scm/product/submitProductLossSet.action',
				resSubmitUrl: 'scm/product/resSubmitProductLossSet.action',
				getIdUrl: 'common/getId.action?seq=ProductLossSet_SEQ',
				keyField: 'ps_id',
				codeField: 'ps_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%',
				detno: 'psd_detno',
				title : '损耗明细',
				necessaryField: 'psd_plcode',
				keyField: 'psd_id',
				mainField: 'psd_psid',
				allowExtraButtons: true,
				bbar: {xtype: 'erpToolbar',id:'toolbar2'},
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
			}]
		}); 
		me.callParent(arguments); 
	} 
});