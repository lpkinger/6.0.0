Ext.define('erp.view.hr.employee.HrJob',{ 
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
					saveUrl: 'hr/employee/saveHrJob.action',
					deleteUrl: 'hr/employee/deleteHrJob.action',
					updateUrl: 'hr/employee/updateHrJob.action',		
					getIdUrl: 'common/getId.action?seq=HrJob_SEQ',
					keyField: 'jo_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});