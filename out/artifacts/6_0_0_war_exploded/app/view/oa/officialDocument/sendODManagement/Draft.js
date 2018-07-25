Ext.define('erp.view.oa.officialDocument.sendODManagement.Draft',{ 
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
					saveUrl: 'oa/officialDocument/sendODManagement/saveDraft.action',
					deleteUrl: 'oa/officialDocument/sendODManagement/deleteDraft.action',
					updateUrl: 'oa/officialDocument/sendODManagement/updateDraft.action',
					getIdUrl: 'common/getId.action?seq=SENDOFFICIALDOCUMENT_SEQ',
					keyField: 'sod_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});