Ext.define('erp.view.crm.marketCompete.CompetitorActivityReport',{ 
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
					saveUrl: 'crm/marketCompete/saveCompetitorActivityReport.action',
					deleteUrl: 'crm/marketCompete/deleteCompetitorActivityReport.action',
					updateUrl: 'crm/marketCompete/updateCompetitorActivityReport.action',
					getIdUrl: 'common/getId.action?seq=COMPETITORACTIVITYREPORT_SEQ',
					keyField: 'car_id',
//					codeField: 'car_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});