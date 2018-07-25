Ext.define('erp.view.scm.product.ProdInnerRelation',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				//与其它页面不同，必须传一个caller
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				printUrl: 'common/printCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				bannedUrl: 'common/bannedCommon.action?caller='+caller,
				resBannedUrl: 'common/resBannedCommon.action?caller='+caller,
				endUrl: 'common/endCommon.action?caller='+caller,
				resEndUrl: 'common/resEndCommon.action?caller='+caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				getIdUrl: 'common/getCommonId.action?caller=' +caller,
				onConfirmUrl: 'common/ConfirmCommon.action?caller=' +caller,
	    	},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				directImport:true//支持直接将Excel数据插入从表
			}]
		}); 
		me.callParent(arguments); 
	} 
});