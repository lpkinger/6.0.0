Ext.define('erp.view.crm.chance.StageChange',{ 
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
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=ChangeProject_SEQ',
					auditUrl: 'crm/chance/auditStageChange.action',
					resAuditUrl: 'crm/chance/resAuditStageChange.action',
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'sc_id',
					codeField: 'sc_code',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});