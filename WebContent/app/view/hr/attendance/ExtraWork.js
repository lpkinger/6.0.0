Ext.define('erp.view.hr.attendance.ExtraWork',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor',
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'hr/attendance/saveExtraWork.action',
					deleteUrl: 'hr/attendance/deleteExtraWork.action',
					updateUrl: 'hr/attendance/updateExtraWork.action',
					auditUrl: 'hr/attendance/auditExtraWork.action',
					resAuditUrl: 'hr/attendance/resAuditExtraWork.action',
					submitUrl: 'hr/attendance/submitExtraWork.action',
					//confirmUrl:'hr/attendance/confirmWorkovertime.action',
					resSubmitUrl: 'hr/attendance/resSubmitExtraWork.action',
					getIdUrl: 'common/getId.action?seq=WORKOVERTIMEDET_SEQ',
					keyField: 'wod_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});