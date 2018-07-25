Ext.define('erp.view.pm.mould.MouldQuote',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MouldQuoteViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					printUrl: 'common/printCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getCommonId.action?caller=' +caller,
					keyField: 'mq_id',
					codeField: 'mq_code',
					statusField: 'mq_status',
					statuscodeField: 'mq_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'mqd_detno',
					necessaryField: 'mqd_pscode',
					keyField: 'mqd_id',
					mainField: 'mqd_mqid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});