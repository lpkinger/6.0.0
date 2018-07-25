Ext.define('erp.view.pm.bom.ProdReplaceMother',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'pm/bom/saveProdReplaceMother.action',
				deleteUrl: 'pm/bom/deleteProdReplaceMother.action',
				updateUrl: 'pm/bom/updateProdReplaceMother.action',
				auditUrl: 'pm/bom/auditProdReplaceMother.action',
				resAuditUrl: 'pm/bom/resAuditProdReplaceMother.action',
				submitUrl: 'pm/bom/submitProdReplaceMother.action',
				resSubmitUrl: 'pm/bom/resSubmitProdReplaceMother.action',
				getIdUrl: 'common/getId.action?seq=PRODREPLACE_SEQ',
				keyField: 'pr_id',
				codeField: 'pr_code',
				statusField: 'pr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'pre_detno',
				keyField: 'pre_id',
				mainField: 'pre_itemid',
				necessaryField: 'pre_prodcode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});