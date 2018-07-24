Ext.define('erp.view.hr.attendance.Workovertime',{ 
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
					anchor: '100% 40%',
					saveUrl: 'hr/attendance/saveWorkovertime.action',
					deleteUrl: 'hr/attendance/deleteWorkovertime.action',
					updateUrl: 'hr/attendance/updateWorkovertime.action',
					auditUrl: 'hr/attendance/auditWorkovertime.action',
					resAuditUrl: 'hr/attendance/resAuditWorkovertime.action',
					submitUrl: 'hr/attendance/submitWorkovertime.action',
					confirmUrl:'hr/attendance/confirmWorkovertime.action',
					resSubmitUrl: 'hr/attendance/resSubmitWorkovertime.action',
					endUrl: 'common/endCommon.action?caller='+caller,
					resEndUrl: 'common/resEndCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Workovertime_SEQ',
					keyField: 'wo_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					//necessaryField: 'ppd_costname',
					keyField: 'wod_id',
					detno: 'wod_detno',
					mainField: 'wod_woid',
					allowExtraButtons:true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});
/*Ext.define('erp.view.hr.attendance.Workovertime',{
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
					saveUrl: 'hr/attendance/saveWorkovertime.action',
					deleteUrl: 'hr/attendance/deleteWorkovertime.action',
					updateUrl: 'hr/attendance/updateWorkovertime.action',
					getIdUrl: 'common/getId.action?seq=WORKOVERTIME_SEQ',
					keyField: 'wo_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});*/