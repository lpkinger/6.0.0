Ext.define('erp.view.pm.bom.ProdRelation',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'pm/bom/saveProdRelation.action',
				updateUrl: 'pm/bom/updateProdRelation.action',
				deleteUrl: 'pm/bom/deleteProdRelation.action',
				auditUrl: 'pm/bom/auditProdRelation.action',					
				resAuditUrl: 'pm/bom/resAuditProdRelation.action',
				submitUrl: 'pm/bom/submitProdRelation.action',
				resSubmitUrl: 'pm/bom/resSubmitProdRelation.action',
				getIdUrl: 'common/getId.action?seq=PRODRELATION1_SEQ',
				keyField: 'prr_thisid',
				codeField: 'prr_thiscode',
				statusField: 'prr_usestatus',
				statuscodeField: 'prr_usestatuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				keyField: 'prr_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});