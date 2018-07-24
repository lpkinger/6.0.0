Ext.define('erp.view.hr.attendance.EmpWorkDateSet',{
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
					saveUrl: 'hr/attendance/saveEmpWorkDateSet.action'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});