Ext.define('erp.view.common.subs.SubsApply',{ 
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
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'common/charts/auditSubsApply.action?caller=' +caller,
				resAuditUrl: 'common/charts/resAuditSubsApply.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});