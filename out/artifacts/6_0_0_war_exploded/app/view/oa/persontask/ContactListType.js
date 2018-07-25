Ext.define('erp.view.oa.persontask.ContactListType',{ 
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
					saveUrl: 'oa/contactlisttype/saveContactListType.action',
//					deleteUrl: 'oa/contactlisttype/saveContactListType.action',
//					updateUrl: 'oa/contactlisttype/saveContactListType.action',
					getIdUrl: 'common/getId.action?seq=CONTACTLISTTYPE_SEQ',
					keyField: 'clt_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});