Ext.define('erp.view.pm.mould.MouldFeePlease',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MouldFeePleaseViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/saveMouldFeePlease.action',
					deleteUrl: 'pm/mould/deleteMouldFeePlease.action',
					updateUrl: 'pm/mould/updateMouldFeePlease.action',
					auditUrl: 'pm/mould/auditMouldFeePlease.action',
					//printUrl: 'pm/mould/printModAlter.action',
					printUrl: 'pm/mould/printMouldFeePlease.action',
					resAuditUrl: 'pm/mould/resAuditMouldFeePlease.action',
					submitUrl: 'pm/mould/submitMouldFeePlease.action',
					resSubmitUrl: 'pm/mould/resSubmitMouldFeePlease.action',
					endUrl: 'pm/mould/endMouldFeePlease.action',
					resEndUrl: 'pm/mould/resEndMouldFeePlease.action',
					getIdUrl: 'common/getId.action?seq=MouldFeePlease_SEQ',
					keyField: 'mp_id',
					codeField: 'mp_code',
					statusField: 'mp_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'mfd_detno',
					keyField: 'mfd_id',
					mainField: 'mfd_mpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});