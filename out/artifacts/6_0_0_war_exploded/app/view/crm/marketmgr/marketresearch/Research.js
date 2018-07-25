Ext.define('erp.view.crm.marketmgr.marketresearch.Research',{ 
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
					saveUrl: 'crm/marketmgr/saveResearch.action',
					deleteUrl: 'crm/marketmgr/deleteResearch.action',
					updateUrl: 'crm/marketmgr/updateResearch.action',
					getIdUrl: 'common/getId.action?seq=Research_SEQ',
					keyField: 're_id',
					codeField: 're_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});