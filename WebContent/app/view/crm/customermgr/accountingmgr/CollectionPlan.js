Ext.define('erp.view.crm.customermgr.accountingmgr.CollectionPlan',{ 
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
					saveUrl: 'crm/customermgr/saveCollectionPlan.action',
					deleteUrl: 'crm/customermgr/deleteCollectionPlan.action',
					updateUrl: 'crm/customermgr/updateCollectionPlan.action',
					getIdUrl: 'common/getId.action?seq=CollectionPlan_SEQ',
					keyField: 'cp_id',
					codeField: '',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});