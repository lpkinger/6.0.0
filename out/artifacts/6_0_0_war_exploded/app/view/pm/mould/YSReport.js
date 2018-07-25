Ext.define('erp.view.pm.mould.YSReport',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'YSReportViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'pm/mould/deleteYSReport.action',
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'pm/mould/auditYSReport.action',
					printUrl: 'common/printCommon.action?caller=' +caller,
					resAuditUrl: 'pm/mould/resAuditYSReport.action',
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					postUrl: 'pm/mould/postYSReport.action',
					resPostUrl: 'pm/mould/resPostYSReport.action',
					getIdUrl: 'common/getCommonId.action?caller=' +caller,
					keyField: 'mo_id',
					codeField: 'mo_code',
					statusField: 'mo_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'yd_detno',
					necessaryField: 'yd_mjhtcod',
					keyField: 'yd_id',
					mainField: 'yd_moid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});