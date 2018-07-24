Ext.define('erp.view.crm.chance.BusinessChanceStage',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'crm/chance/saveBusinessChanceStage.action',
					deleteUrl: 'crm/chance/deleteBusinessChanceStage.action',
					updateUrl: 'crm/chance/updateBusinessChanceStage.action',
					getIdUrl: 'common/getId.action?seq=BusinessChanceStage_SEQ',
					auditUrl: 'crm/chance/auditBusinessChanceStage.action',
					resAuditUrl: 'crm/chance/resAuditBusinessChanceStage.action',
					submitUrl: 'crm/chance/submitBusinessChanceStage.action',
					resSubmitUrl: 'crm/chance/resSubmitBusinessChanceStage.action',
					keyField: 'bs_id',
					codeField: 'bs_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});