Ext.define('erp.view.oa.custom.CustomForm',{ 
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
					anchor: '100% 40%',
					saveUrl: 'ma/saveForm.action',
					deleteUrl: 'ma/deleteForm.action',
					updateUrl: 'ma/updateForm.action',
					getIdUrl: 'common/getId.action?seq=FORM_SEQ',
					keyField: 'fo_id'
				},{
					xtype: 'customgrid', 
					anchor: '100% 60%',
					detno: 'fd_detno',
					necessaryField: 'fd_field',
					keyField: 'fd_id',
					mainField: 'fd_foid'
				}]
			}] 
		}); 
		me.callParent(arguments);
	} 
});