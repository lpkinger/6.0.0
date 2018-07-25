Ext.define('erp.view.oa.meeting.New',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'oa/meeting/saveMeetingRoom.action',
					deleteUrl: 'oa/meeting/deleteMeetingRoom.action',
					updateUrl: 'oa/meeting/updateMeetingRoom.action',
					auditUrl: 'oa/meeting/auditMeetingRoom.action',
					resAuditUrl: 'oa/meeting/resAuditMeetingRoom.action',
					submitUrl: 'oa/meeting/submitMeetingRoom.action',
					resSubmitUrl: 'oa/meeting/resSubmitMeetingRoom.action',
					getIdUrl: 'common/getId.action?seq=MEETINGROOM_SEQ',
					keyField: 'mr_id',
					statusField: 'mr_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'eq_detno',
					necessaryField: '',
					keyField: 'eq_id',
					allowExtraButtons:true,
					mainField: 'eq_mrid'
				}]
		}); 
		me.callParent(arguments); 
	} 
});