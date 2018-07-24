Ext.define('erp.view.oa.officialDocument.fileManagement.documentRoom.NewDocumentRoom',{ 
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
					saveUrl: 'oa/officialDocument/fileManagement/saveDocumentRoom.action',
					deleteUrl: 'oa/officialDocument/fileManagement/deleteDocumentRoom.action',
					updateUrl: 'oa/officialDocument/fileManagement/updateDocumentRoom.action',
					getIdUrl: 'common/getId.action?seq=DOCUMENTROOM_SEQ',
					keyField: 'dr_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});