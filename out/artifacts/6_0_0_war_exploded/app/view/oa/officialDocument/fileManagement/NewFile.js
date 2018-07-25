Ext.define('erp.view.oa.officialDocument.fileManagement.NewFile',{ 
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
					saveUrl: 'oa/officialDocument/fileManagement/saveFile.action',
					deleteUrl: 'oa/officialDocument/fileManagement/deleteFile.action',
					updateUrl: 'oa/officialDocument/fileManagement/updateFile.action',
					getIdUrl: 'common/getId.action?seq=FILE_SEQ',
					keyField: 'fi_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});