Ext.define('erp.view.hr.attendance.Workreason',{
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
					saveUrl: 'hr/attendance/saveWorkreason.action',
					deleteUrl: 'hr/attendance/deleteWorkreason.action',
					updateUrl: 'hr/attendance/updateWorkreason.action',
					getIdUrl: 'common/getId.action?seq=WORKREASON_SEQ',
					keyField: 'wr_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});