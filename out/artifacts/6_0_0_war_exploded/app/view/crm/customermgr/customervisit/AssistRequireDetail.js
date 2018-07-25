Ext.define('erp.view.crm.customermgr.customervisit.AssistRequireDetail',{ 
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
					saveUrl: 'crm/customermgr/updateAssistRequireDetail.action',
					deleteUrl: 'crm/customermgr/deleteAssistRequireDetail.action',
					updateUrl: 'crm/customermgr/updateAssistRequireDetail.action',
					getIdUrl: 'common/getId.action?seq=AssistRequireDetail_SEQ',
					keyField: 'ard_id',
					codeField: '',
//					statusField: 'ch_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});