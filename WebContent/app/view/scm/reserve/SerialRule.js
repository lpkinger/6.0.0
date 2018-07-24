Ext.define('erp.view.scm.reserve.SerialRule',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/reserve/saveSerail.action?caller=' +caller,
				deleteUrl: 'scm/reserve/deleteSerail.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateSerail.action?caller=' +caller,
				auditUrl: 'scm/reserve/auditSerail.action?caller=' +caller,
				resAuditUrl: 'scm/reserve/resAuditSerail.action?caller=' +caller,
				submitUrl: 'scm/reserve/submitSerail.action?caller=' +caller,
				resSubmitUrl: 'scm/reserve/resSubmitSerail.action?caller=' +caller,
				bannedUrl:'scm/reserve/bannedSerail.action?caller=' +caller,
				resBannedUrl:'scm/reserve/resBannedSerail.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=BARCODESET_SEQ',
				keyField: 'bs_id',
				codeField: 'bs_code',
				statusField: 'bs_status',
				statuscodeField: 'bs_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				detno: 'bsd_detno',
				keyField: 'bsd_id',
				mainField: 'bsd_bsid',
				allowExtraButtons : true
			}]
		}); 
		me.callParent(arguments); 
	} 
});