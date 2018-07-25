Ext.define('erp.view.pm.mould.ModAlter',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ModAlterViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/saveModAlter.action',
					deleteUrl: 'pm/mould/deleteModAlter.action',
					updateUrl: 'pm/mould/updateModAlter.action',
					auditUrl: 'pm/mould/auditModAlter.action',
					resAuditUrl: 'pm/mould/resAuditModAlter.action',
					submitUrl: 'pm/mould/submitModAlter.action',
					resSubmitUrl: 'pm/mould/resSubmitModAlter.action',
					bannedUrl: 'pm/mould/bannedModAlter.action',
					resBannedUrl: 'pm/mould/resBannedModAlter.action',
					getIdUrl: 'common/getId.action?seq=ModAlter_SEQ',
					keyField: 'app_id',
					codeField: 'app_code',
					statusField: 'app_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'ald_detno',
					necessaryField: 'ald_pscode',
					keyField: 'ald_id',
					mainField: 'ald_alid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});