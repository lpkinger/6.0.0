Ext.define('erp.view.fa.ars.CategoryDepartment',{ 
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
					anchor: '100% 55%',
					saveUrl: 'fa/ars/saveCategoryDepartment.action',
					deleteUrl: 'fa/ars/deleteCategoryDepartment.action',
					updateUrl: 'fa/ars/updateCategoryDepartment.action',
					getIdUrl: 'common/getId.action?seq=CATEGORY_SEQ',
					keyField: 'ca_id',
					codeField: 'ca_code',
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 45%', 
					detno: 'cd_detno',
					necessaryField: 'cd_departmentid',
					keyField: 'cd_id',
					mainField: 'cd_cateid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});