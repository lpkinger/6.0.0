Ext.define('erp.view.crm.marketCompete.CompetitorActivityplan',{ 
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
					saveUrl: 'crm/marketCompete/saveCompetitorActivityplan.action',
					deleteUrl: 'crm/marketCompete/deleteCompetitorActivityplan.action',
					updateUrl: 'crm/marketCompete/updateCompetitorActivityplan.action',
					getIdUrl: 'common/getId.action?seq=COMPETITORACTIVITYPLAN_SEQ',
					keyField: 'cap_id',
					codeField: 'cap_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});