Ext.define('erp.view.hr.attendance.CardApply',{
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
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=CardApply_SEQ',
					auditUrl: 'oa/check/auditCardApply.action',
					confirmUrl:'common/confirmCommon.action?caller='+caller,
					resAuditUrl: 'common/CRMCommonResAudit.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'ca_id',
					codeField: 'ca_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});