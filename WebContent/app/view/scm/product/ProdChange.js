Ext.define('erp.view.scm.product.ProdChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/product/saveProdChange.action?caller=' + caller,
				deleteUrl: 'scm/product/deleteProdChange.action?caller=' + caller,
				updateUrl: 'scm/product/updateProdChange.action?caller=' + caller,
				auditUrl: 'scm/product/auditProdChange.action?caller=' + caller,
				printUrl: 'scm/product/printProdChange.action?caller=' + caller,
				resAuditUrl: 'scm/product/resAuditProdChange.action?caller=' + caller,
				submitUrl: 'scm/product/submitProdChange.action?caller=' + caller,
				resSubmitUrl: 'scm/product/resSubmitProdChange.action?caller=' + caller,
				getIdUrl: 'common/getId.action?seq=ProdChange_SEQ',
				keyField: 'pc_id',
				codeField: 'pc_code',
				statusField: 'pc_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pcd_detno',
				necessaryField: 'pcd_prcode',
				keyField: 'pcd_id',
				mainField: 'pcd_pcid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});