Ext.define('erp.view.scm.product.VendProductLossSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/product/saveVendProductLossSet.action',
				updateUrl: 'scm/product/updateVendProductLossSet.action',
				deleteUrl: 'scm/product/deleteVendProductLossSet.action',
				auditUrl: 'scm/product/auditVendProductLossSet.action',
				resAuditUrl: 'scm/product/resAuditVendProductLossSet.action',
				submitUrl: 'scm/product/submitVendProductLossSet.action',
				resSubmitUrl: 'scm/product/resSubmitVendProductLossSet.action',
				getIdUrl: 'common/getId.action?seq=VendProdLossSet_SEQ',
				keyField: 'vps_id',
				codeField: 'vps_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%',
				detno: 'vpd_detno',
				title : '委外损耗明细',
				necessaryField: 'vpd_prodcode',
				keyField: 'vpd_id',
				mainField: 'vpd_vpsid',
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