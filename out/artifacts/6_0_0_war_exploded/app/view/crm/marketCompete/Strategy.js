Ext.define('erp.view.crm.marketCompete.Strategy',{ 
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
					anchor: '100% 100%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl:  'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=STRATEGY_SEQ',
					keyField: 'st_id',
					codeField: 'st_code',					
					statusField: 'st_status',
					statusCodeField: 'st_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});