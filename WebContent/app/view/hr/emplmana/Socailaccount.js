Ext.define('erp.view.hr.emplmana.Socailaccount',{ 
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
					saveUrl: 'hr/emplmana/saveSocailAccount.action',
					deleteUrl: 'hr/emplmana/deleteSocailAccount.action',
					updateUrl: 'hr/emplmana/updateSocailAccount.action',		
					getIdUrl: 'common/getId.action?seq=employee_SEQ',
					keyField: 'em_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});