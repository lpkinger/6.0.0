Ext.define('erp.view.crm.marketCompete.Competitor',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'crm/marketCompete/saveCompetitor.action',
					deleteUrl: 'crm/marketCompete/deleteCompetitor.action',
					updateUrl: 'crm/marketCompete/updateCompetitor.action',
//					auditUrl: 'crm/marketCompete/auditCompetitor.action',
//					resAuditUrl: 'crm/marketCompete/resAuditCompetitor.action',
//					submitUrl: 'crm/marketCompete/submitCompetitor.action',
//					resSubmitUrl:  'crm/marketCompete/resSubmitCompetitor.action',
					getIdUrl: 'common/getId.action?seq=COMPETITOR_SEQ',
					keyField: 'co_id',
					codeField: 'co_code',
//					statusField: 'co_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					detno: 'cd_detno',
					necessaryField: 'cd_name',
					keyField: 'cd_id',
					mainField: 'cd_coid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});