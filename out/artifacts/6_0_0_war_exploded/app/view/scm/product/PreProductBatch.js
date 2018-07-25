Ext.define('erp.view.scm.product.PreProductBatch',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 35%',
				saveUrl: 'scm/product/savePreProductBatch.action',
				updateUrl: 'scm/product/updatePreProductBatch.action',
				deleteUrl: 'scm/product/deletePreProductBatch.action',
				auditUrl: 'scm/product/auditPreProductBatch.action',
				resAuditUrl: 'scm/product/resAuditPreProductBatch.action',
				submitUrl: 'scm/product/submitPreProductBatch.action',
				resSubmitUrl: 'scm/product/resSubmitPreProductBatch.action',
				getIdUrl: 'common/getId.action?seq=PREPRODUCTBATCH_SEQ',
				keyField: 'pb_id',
				codeField: 'pb_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 65%',
				detno: 'pbd_detno',
				title : '新物料明细',
				necessaryField: 'pbd_prodcode',
				keyField: 'pbd_id',
				mainField: 'pbd_pbid',
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