Ext.define('erp.view.ma.DocumentSetup',{ 
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
					saveUrl: 'ma/saveDocumentSetup.action',
					updateUrl: 'ma/updateDocumentSetup.action',
					getIdUrl: 'common/getId.action?seq=DOCUMENTSETUP_SEQ',
					keyField: 'ds_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});