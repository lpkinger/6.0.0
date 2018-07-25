Ext.define('erp.view.oa.meeting.StandMeeting',{ 
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
					saveUrl: 'oa/meeting/saveStandMeeting.action',
					deleteUrl: 'oa/meeting/deleteStandMeeting.action',
					updateUrl: 'oa/meeting/updateStandMeeting.action',
					auditUrl: 'oa/meeting/auditStandMeeting.action',
					resAuditUrl: 'oa/meeting/resAuditStandMeeting.action',
					submitUrl: 'oa/meeting/submitStandMeeting.action',
					resSubmitUrl: 'oa/meeting/resSubmitStandMeeting.action',
					bannedUrl: 'oa/meeting/banStandMeeting.action',
					resBannedUrl: 'oa/meeting/resBanStandMeeting.action',
					getIdUrl: 'common/getId.action?seq=StandMeeting_SEQ',					
					keyField: 'sm_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});