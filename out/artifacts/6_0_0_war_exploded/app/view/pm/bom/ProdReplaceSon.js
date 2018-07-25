Ext.define('erp.view.pm.bom.ProdReplaceSon',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'pm/bom/saveProdReplaceSon.action',
				deleteUrl: 'pm/bom/deleteProdReplaceSon.action',
				updateUrl: 'pm/bom/updateProdReplaceSon.action',
				auditUrl: 'pm/bom/auditProdReplaceSon.action',
				resAuditUrl: 'pm/bom/resAuditProdReplaceSon.action',
				submitUrl: 'pm/bom/submitProdReplaceSon.action',
				resSubmitUrl: 'pm/bom/resSubmitProdReplaceSon.action',
				getIdUrl: 'common/getId.action?seq=PRODREPLACE_SEQ',
				keyField: 'bo_id',
				codeField: 'bo_code',
				statusField: 'bo_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'pre_detno',
				keyField: 'pre_id',
				mainField: 'pre_bomid',
				necessaryField: 'pre_prodcode'
			}]
		}); 
		me.callParent(arguments); 
	} 
});