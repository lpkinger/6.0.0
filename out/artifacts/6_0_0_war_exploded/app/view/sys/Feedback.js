Ext.define('erp.view.sys.Feedback',{ 
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
					autoScroll: true,
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
			}] 
		}); 
		me.callParent(arguments); 
	} 
});