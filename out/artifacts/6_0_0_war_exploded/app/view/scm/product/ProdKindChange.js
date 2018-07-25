Ext.define('erp.view.scm.product.ProdKindChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/product/saveProdKindChange.action?caller=' + caller,
				deleteUrl: 'scm/product/deleteProdKindChange.action?caller=' + caller,
				updateUrl: 'scm/product/updateProdKindChange.action?caller=' + caller,
				auditUrl: 'scm/product/auditProdKindChange.action?caller=' + caller,
				printUrl: 'scm/product/printProdKindChange.action?caller=' + caller,
				resAuditUrl: 'scm/product/resAuditProdKindChange.action?caller=' + caller,
				submitUrl: 'scm/product/submitProdKindChange.action?caller=' + caller,
				resSubmitUrl: 'scm/product/resSubmitProdKindChange.action?caller=' + caller,
				getIdUrl: 'common/getId.action?seq=ProdKindChange_SEQ',
				keyField: 'pc_id',
				codeField: 'pc_code',
				statusField: 'pc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pcd_detno',
				necessaryField: 'pcd_pkcode',
				keyField: 'pcd_id',
				mainField: 'pcd_pcid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});