Ext.define('erp.view.fa.ars.CategoryAssKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 60%',
					saveUrl: 'fa/ars/saveCategoryAssKind.action',
					deleteUrl: 'fa/ars/deleteCategoryAssKind.action',
					updateUrl: 'fa/ars/updateCategoryAssKind.action',
					getIdUrl: 'common/getId.action?seq=CATEGORY_SEQ',
					keyField: 'ca_id',
					codeField: 'ca_code',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 40%', 
					detno: 'cak_detno',
					necessaryField: 'cak_akid',
					keyField: 'cak_id',
					mainField: 'cak_cateid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});