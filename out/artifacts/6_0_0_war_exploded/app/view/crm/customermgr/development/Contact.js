Ext.define('erp.view.crm.customermgr.development.Contact',{ 
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
					anchor: '100% 30%',
					saveUrl: 'crm/customermgr/saveContact.action',
					deleteUrl: 'crm/customermgr/deleteContact.action',
					updateUrl: 'crm/customermgr/updateContact.action',
					getIdUrl: 'common/getId.action?seq=CONTACT_SEQ',
					keyField: 'ct_id'
				},
				{
					xtype : 'erpGridPanel2',
					anchor : '100% 70%',
					keyField : 'ct_id',
					mainField : 'ct_cuid',
					detno : 'ct_detno',
					autoSetSequence : true
				} ]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});