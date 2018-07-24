Ext.define('erp.view.crm.aftersalemgr.ComplaintType',{ 
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
					anchor: '100% 100%',
					saveUrl: 'crm/aftersalemgr/saveComplaintType.action',
					deleteUrl: 'crm/aftersalemgr/deleteComplaintType.action',
					updateUrl: 'crm/aftersalemgr/updateComplaintType.action',		
					getIdUrl: 'common/getId.action?seq=ComplaintType_SEQ',
					keyField: 'ct_id',
					codeField: 'ct_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});