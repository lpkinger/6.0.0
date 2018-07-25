Ext.define('erp.view.crm.chance.BusinessDataBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'crm/chance/deleteBusinessDataBase.action',
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
				keyField: 'bd_id'
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});