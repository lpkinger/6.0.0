Ext.define('erp.view.scm.sale.WorkCenter',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				saveUrl: 'scm/sale/saveWorkCenter.action',
				deleteUrl: 'scm/sale/deleteWorkCenter.action',
				updateUrl: 'scm/sale/updateWorkCenter.action',
				getIdUrl: 'common/getId.action?seq=WORKCENTER_SEQ',
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				keyField: 'wc_id',
			    codeField: 'wc_code'
			},{
				xtype : 'erpGridPanel2',
				anchor : '100% 70%',
				detno : 'wm_detno',
				keyField : 'wm_id',
				mainField : 'wm_wcid'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});