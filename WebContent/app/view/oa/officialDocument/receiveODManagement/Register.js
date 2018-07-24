Ext.define('erp.view.oa.officialDocument.receiveODManagement.Register',{ 
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
					saveUrl: 'oa/officialDocument/receiveODManagement/saveRegister.action',
					deleteUrl: 'oa/officialDocument/receiveODManagement/deleteRegister.action',
					updateUrl: 'oa/officialDocument/receiveODManagement/updateRegister.action',
					getIdUrl: 'common/getId.action?seq=RECEIVEOFFICIALDOCUENT_SEQ',
					submitUrl: 'oa/officialDocument/receiveODManagement/submitROD.action',
					keyField: 'rod_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});