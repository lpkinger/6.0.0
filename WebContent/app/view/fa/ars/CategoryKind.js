Ext.define('erp.view.fa.ars.CategoryKind',{ 
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
					saveUrl: 'fa/ars/saveCategoryKind.action',
					deleteUrl: 'fa/ars/deleteCategoryKind.action',
					updateUrl: 'fa/ars/updateCategoryKind.action',
					auditUrl: 'fa/ars/auditCategoryKind.action',
					resAuditUrl: 'fa/ars/resAuditCategoryKind.action',
					submitUrl: 'fa/ars/submitCategoryKind.action',
					resSubmitUrl: 'fa/ars/resSubmitCategoryKind.action',
					getIdUrl: 'common/getId.action?seq=CATEGORYKIND_SEQ',
					keyField: 'ck_id',	
					codefield:'ck_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});