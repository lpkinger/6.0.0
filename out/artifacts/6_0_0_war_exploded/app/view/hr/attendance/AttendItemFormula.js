Ext.define('erp.view.hr.attendance.AttendItemFormula',{
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
					saveUrl: 'hr/attendance/saveAttendItemFormula.action',
					deleteUrl: 'hr/attendance/deleteAttendItemFormula.action',
					updateUrl: 'hr/attendance/updateAttendItemFormula.action',
					getIdUrl: 'common/getId.action?seq=ATTENDITEMFORMULA_SEQ',
					keyField: 'aif_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});