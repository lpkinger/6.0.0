Ext.define('erp.view.hr.attendance.WorkDate',{
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
					saveUrl: 'hr/attendance/saveWorkDate.action',
					deleteUrl: 'hr/attendance/deleteWorkDate.action',
					updateUrl: 'hr/attendance/updateWorkDate.action',
					getIdUrl: 'common/getId.action?seq=WORKDATE_SEQ',
					keyField: 'wd_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});