Ext.define('erp.view.hr.kpi.KpiScheme',{ 
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
					anchor: '100% 40%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=KpiScheme_SEQ',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'ks_id',
					codeField: 'ks_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					keyField: 'ksd_id',
					detno: 'ksd_detno',
					mainField: 'ksd_ksid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});