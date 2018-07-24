Ext.define('erp.view.hr.attendance.BookAirTicket',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 70%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=BookAirTicket_SEQ',
					auditUrl: 'oa/check/auditBookAirTicket.action',
					confirmUrl:'common/confirmCommon.action?caller='+caller,
					resAuditUrl: 'common/CRMCommonResAudit.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'bt_id',
					codeField: 'bt_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					//necessaryField: 'ppd_costname',
					keyField: 'btd_id',
					detno: 'btd_detno',
					mainField: 'btd_btid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});