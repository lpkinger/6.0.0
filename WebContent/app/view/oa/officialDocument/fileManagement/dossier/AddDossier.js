Ext.define('erp.view.oa.officialDocument.fileManagement.dossier.AddDossier',{ 
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
					saveUrl: 'oa/officialDocument/fileManagement/saveDossier.action',
					deleteUrl: 'oa/officialDocument/fileManagement/deleteDossier.action',
					updateUrl: 'oa/officialDocument/fileManagement/updateDossier.action',
					getIdUrl: 'common/getId.action?seq=DOSSIER_SEQ',
					keyField: 'do_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});