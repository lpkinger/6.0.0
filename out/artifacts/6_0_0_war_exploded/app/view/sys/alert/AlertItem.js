Ext.define('erp.view.sys.alert.AlertItem',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'sys/alert/saveAlertItem.action',
				deleteUrl: 'sys/alert/deleteAlertItem.action',
				updateUrl: 'sys/alert/updateAlertItem.action',
				submitUrl: 'sys/alert/submitAlertItem.action',
				resSubmitUrl:'sys/alert/resSubmitAlertItem.action',
				auditUrl: 'sys/alert/auditAlertItem.action',
				resAuditUrl:'sys/alert/resAuditAlertItem.action',
				bannedUrl:'sys/alert/bannedAlertItem.action',
				resBannedUrl:'sys/alert/resBannedAlertItem.action',
				getIdUrl: 'common/getId.action?seq=ALERT_ITEM_SEQ',
				keyField: 'ai_id',
				statusField:'ai_statuscode'
			},{
				xtype:'tabpanel',
				anchor: '100% 50%',
				items:[{
					id: 'grid',
					xtype: 'erpGridPanel2',
					detno: 'aa_detno',
					keyField: 'aa_id',
					mainField: 'aa_aiid',
					allowExtraButtons: true,
					title:'项目参数'
				},{
					id: 'alertOutput',
					xtype: 'AlertItemGrid',
					detno: 'ao_detno',
					keyField: 'ao_id',
					mainField: 'ao_aiid',
					caller: 'AlertOutput',
					allowExtraButtons: true,
					title:'输出结果'
				}]
			}]
		});
		me.callParent(arguments); 
	}
});