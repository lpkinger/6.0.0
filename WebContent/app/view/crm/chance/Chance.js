Ext.define('erp.view.crm.chance.Chance',{ 
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
					saveUrl: 'crm/Chance/saveChance.action',
					deleteUrl: 'crm/Chance/deleteChance.action',
					updateUrl: 'crm/Chance/updateChance.action',
					getIdUrl: 'common/getId.action?seq=CHANCE_SEQ',
					auditUrl: 'crm/Chance/auditChance.action',
					resAuditUrl: 'crm/Chance/resAuditChance.action',
					submitUrl: 'crm/Chance/submitChance.action',
					resSubmitUrl: 'crm/Chance/resSubmitChance.action',
					keyField: 'ch_id',
					codeField: 'ch_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});