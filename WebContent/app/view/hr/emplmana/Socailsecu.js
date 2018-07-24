Ext.define('erp.view.hr.emplmana.Socailsecu',{ 
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
					saveUrl: 'hr/emplmana/saveSocailSecurity.action',
					deleteUrl: 'hr/emplmana/deleteSocailSecurity.action',
					updateUrl: 'hr/emplmana/updateSocailSecurity.action',		
					getIdUrl: 'common/getId.action?seq=employee_SEQ',
					keyField: 'em_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});