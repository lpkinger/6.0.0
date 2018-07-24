Ext.define('erp.view.pm.make.lossworktime',{
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
					anchor: '100% 35%',
					saveUrl: 'pm/make/saveLossWorkTime.action',
					deleteUrl: 'pm/make/deleteLossWorkTime.action',
					updateUrl: 'pm/make/updateLossWorkTime.action',
					auditUrl: 'pm/make/auditLossWorkTime.action',
					resAuditUrl: 'pm/make/resAuditLossWorkTime.action',
					submitUrl: 'pm/make/submitLossWorkTime.action',
					resSubmitUrl: 'pm/make/resSubmitLossWorkTime.action',
					getIdUrl: 'common/getId.action?seq=LOSSWORKTIME_SEQ',
					keyField: 'lw_id',
					codeField: 'lw_code',
					statusField: 'lw_status',
					statuscodeField: 'lw_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					keyField: 'lwd_id',
					detno: 'lwd_detno',
					mainField: 'lwd_lwid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});