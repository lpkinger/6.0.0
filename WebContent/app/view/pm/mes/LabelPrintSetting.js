Ext.define('erp.view.pm.mes.LabelPrintSetting',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'LPSettingViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'pm/mes/saveLPSetting.action',
					deleteUrl: 'pm/mes/deleteLPSetting.action',
					updateUrl: 'pm/mes/updateLPSetting.action',
					getIdUrl: 'common/getId.action?seq=LabelPrintSetting_SEQ',
					submitUrl: 'pm/mes/submitLPSetting.action',
					auditUrl: 'pm/mes/auditLPSetting.action',
					resAuditUrl: 'pm/mes/resAuditLPSetting.action',			
					resSubmitUrl: 'pm/mes/resSubmitLPSetting.action',
					keyField: 'lps_id',
					codeField: 'lps_code', 
					statusField: 'lps_status',
					statuscodeField: 'lps_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});