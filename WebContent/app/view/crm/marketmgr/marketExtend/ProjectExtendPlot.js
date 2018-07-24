Ext.define('erp.view.crm.marketmgr.marketExtend.ProjectExtendPlot',{ 
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
//					saveUrl: 'crm/marketmgr/saveProjectExtendPlot.action',
//					deleteUrl: 'crm/marketmgr/deleteProjectExtendPlot.action',
//					updateUrl: 'crm/marketmgr/updateProjectExtendPlot.action',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl:  'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PROJECTEXTENDPLOT_SEQ',
					keyField: 'pep_id',
					codeField: 'pep_code',
					statusField: 'pep_status',
					statusCodeField: 'pep_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});