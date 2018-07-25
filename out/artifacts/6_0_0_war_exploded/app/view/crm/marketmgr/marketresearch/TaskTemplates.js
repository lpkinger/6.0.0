Ext.define('erp.view.crm.marketmgr.marketresearch.TaskTemplates',{ 
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
					saveUrl: 'crm/marketmgr/saveTaskTemplates.action',
					deleteUrl: 'crm/marketmgr/deleteTaskTemplates.action',
					updateUrl: 'crm/marketmgr/updateTaskTemplates.action',
					getIdUrl: 'common/getId.action?seq=TaskTemplates_SEQ',
					keyField: 'tt_id'
				}/*,{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'ttd_name',
					keyField: 'ttd_id',
					detno: 'ttd_detno',
					mainField: 'ttd_ttid'
				}*/]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});