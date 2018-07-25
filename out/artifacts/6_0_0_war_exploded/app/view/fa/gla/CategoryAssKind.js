Ext.define('erp.view.fa.gla.CategoryAssKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'categoryAssKindViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'fa/ars/saveCategoryAssKind.action',
					deleteUrl: 'fa/ars/deleteCategoryAssKind.action',
					updateUrl: 'fa/ars/updateCategoryAssKind.action',
					auditUrl: 'fa/ars/auditCategoryAssKind.action',
					resAuditUrl: 'fa/ars/resAuditCategoryAssKind.action',
					submitUrl: 'fa/ars/submitCategoryAssKind.action',
					resSubmitUrl: 'fa/ars/resSubmitCategoryAssKind.action',
					getIdUrl: 'common/getId.action?seq=CategoryAssKind_SEQ',
					keyField: 'cak_id',
				/*	codeField: 'abb_code',*/
					/*statusField: ''*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});