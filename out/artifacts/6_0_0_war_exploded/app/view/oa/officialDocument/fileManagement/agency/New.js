Ext.define('erp.view.oa.officialDocument.fileManagement.agency.New',{ 
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
					saveUrl: 'oa/officialDocument/fileManagement/agency/saveAgency.action',
					deleteUrl: 'oa/officialDocument/fileManagement/agency/deleteAgency.action',
					updateUrl: 'oa/officialDocument/fileManagement/agency/updateAgency.action',
					getIdUrl: 'common/getId.action?seq=AGENCY_SEQ',
					keyField: 'ay_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});