Ext.define('erp.view.hr.check.Vacationset',{ 
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
					saveUrl: 'hr/check/saveVacationset.action',
					deleteUrl: 'hr/check/deleteVacationset.action',
					updateUrl: 'hr/check/updateVacationset.action',		
					getIdUrl: 'common/getId.action?seq=Vacationset_SEQ',
					keyField: 'vs_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});