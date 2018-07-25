Ext.define('erp.view.hr.attendance.fingerprint',{
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
					saveUrl: 'hr/attendance/saveIdCard.action',
					deleteUrl: 'hr/attendance/deleteIdCard.action',
					updateUrl: 'hr/attendance/updateIdCard.action',
					getIdUrl: 'common/getId.action?seq=IDCARD_SEQ',
					keyField: 'ic_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});