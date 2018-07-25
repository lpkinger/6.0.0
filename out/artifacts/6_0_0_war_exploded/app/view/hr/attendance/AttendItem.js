Ext.define('erp.view.hr.attendance.AttendItem',{
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
					saveUrl: 'hr/attendance/saveAttendItem.action',
					deleteUrl: 'hr/attendance/deleteAttendItem.action',
					updateUrl: 'hr/attendance/updateAttendItem.action',
					getIdUrl: 'common/getId.action?seq=ATTENDITEM_SEQ',
					keyField: 'ai_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});