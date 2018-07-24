Ext.define('erp.view.fa.gla.CategoryDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'categoryDocViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveCategoryDoc.action',
					deleteUrl: 'fa/ars/deleteCategoryDoc.action',
					updateUrl: 'fa/ars/updateCategoryDoc.action',
					auditUrl: 'fa/ars/auditCategoryDoc.action',
					resAuditUrl: 'fa/ars/resAuditCategoryDoc.action',
					submitUrl: 'fa/ars/submitCategoryDoc.action',
					resSubmitUrl: 'fa/ars/resSubmitCategoryDoc.action',
					getIdUrl: 'common/getId.action?seq=CategoryDoc_SEQ',
					keyField: 'cd_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});