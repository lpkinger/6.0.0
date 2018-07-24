Ext.define('erp.view.fa.ars.CategoryDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'categoryViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'fa/ars/saveCategoryDoc.action',
					deleteUrl: 'fa/ars/deleteCategoryDoc.action',
					updateUrl: 'fa/ars/updateCategoryDoc.action',
					auditUrl: 'fa/ars/auditCategoryDoc.action',
					resAuditUrl: 'fa/ars/resAuditCategoryDoc.action',
					submitUrl: 'fa/ars/submitCategoryDoc.action',
					resSubmitUrl: 'fa/ars/resSubmitCategoryDoc.action',
					getIdUrl: 'common/getId.action?seq=CATEGORYDOC_SEQ',
					keyField: 'cd_id',	
					//codefield:'ca_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});