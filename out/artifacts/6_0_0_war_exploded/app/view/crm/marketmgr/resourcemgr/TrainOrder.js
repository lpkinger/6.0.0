Ext.define('erp.view.crm.marketmgr.resourcemgr.TrainOrder',{ 
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
					anchor: '100% 60%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					auditUrl: 'crm/marketmgr/auditTrainOrder.action?caller='+caller,
					resAuditUrl: 'crm/marketmgr/resAuditTrainOrder.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=TrainOrder_SEQ',
					keyField: 'to_id',
					codeField: 'to_code',
					statusField: 'to_status'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 40%', 
					detno: 'td_detno',
					//necessaryField: '',
					keyField: 'td_id',
					mainField: 'td_toid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});