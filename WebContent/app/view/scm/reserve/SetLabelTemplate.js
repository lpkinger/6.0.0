Ext.define('erp.view.scm.reserve.SetLabelTemplate',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/reserve/saveLabelT.action?caller=' +caller,
				deleteUrl: 'scm/reserve/deleteLabelT.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateLabelT.action?caller=' +caller,
				auditUrl: 'scm/reserve/auditLabelT.action?caller=' +caller,
				resAuditUrl: 'scm/reserve/resAuditLabelT.action?caller=' +caller,
				submitUrl: 'scm/reserve/submitLabelT.action?caller=' +caller,
				resSubmitUrl: 'scm/reserve/resSubmitLabelT.action?caller=' +caller,
				bannedUrl:'scm/reserve/bannedLabelT.action?caller=' +caller,
				resBannedUrl:'scm/reserve/resBannedLabelT.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=Label_SEQ',
				keyField: 'la_id',
				codeField: 'la_code',
				statusField: 'la_status',
				statuscodeField: 'la_statuscode'
			},{			
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'lp_detno',
				keyField: 'lp_id',
				mainField: 'lp_laid',
				allowExtraButtons : true			
			}]
		}); 
		me.callParent(arguments); 
	} 
});