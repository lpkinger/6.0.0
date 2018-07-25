Ext.define('erp.view.opensys.feedback.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel2',
				autoScroll: true,
				anchor:'100% 100%',
				saveUrl: 'sys/feedback/saveFeedback.action',
				replyUrl: 'sys/feedback/reply.action',
				turnBuglistUrl:'sys/feedback/turnBuglist.action',
				turnProjectUrl:'sys/feedback/turnProject.action',
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'sys/feedback/auditFeedback.action',
				resAuditUrl: 'sys/feedback/resAudit.action',
				submitUrl: 'sys/feedback/submitFeedback.action',
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=FEEDBACK_SEQ',
				keyField: 'fb_id', 
				codeField: 'fb_code',
				statusField: 'fb_statuscode'
			}]
		});
		me.callParent(arguments); 
	}
});