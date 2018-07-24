Ext.define('erp.view.crm.customermgr.development.Solution',{ 
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
					saveUrl: 'crm/customermgr/saveSolution.action',
					deleteUrl: 'crm/customermgr/deleteSolution.action',
					updateUrl: 'crm/customermgr/updateSolution.action',
					getIdUrl: 'common/getId.action?seq=Solution_SEQ',
					keyField: 'so_id',
					codeField: 'so_code',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});