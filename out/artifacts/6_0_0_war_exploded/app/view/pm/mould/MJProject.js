Ext.define('erp.view.pm.mould.MJProject',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MJProjectViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'pm/mould/auditMJProject.action',
					printUrl: 'common/printCommon.action?caller=' +caller,
					resAuditUrl: 'pm/mould/resAuditMJProject.action',
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getCommonId.action?caller=' +caller,
					keyField: 'ws_id',
					codeField: 'ws_code',
					statusField: 'ws_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'wd_detno',
					keyField: 'wd_id',
					mainField: 'wd_wsid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});