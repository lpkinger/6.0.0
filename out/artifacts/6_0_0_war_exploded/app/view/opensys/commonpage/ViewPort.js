Ext.define('erp.view.opensys.commonpage.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		 items:[{
			 xtype:'erpFormPanel2',
			 autoScroll: true,
			 anchor:'100% 100%',
			 saveUrl: 'opensys/saveCommon.action?caller=' +caller,
			 deleteUrl: 'opensys/deleteCommon.action?caller=' +caller,
			 updateUrl: 'opensys/updateCommon.action?caller=' +caller,
			 auditUrl: 'opensys/auditCommon.action?caller=' +caller,
			 resAuditUrl: 'opensys/resAuditCommon.action?caller=' +caller,
			 submitUrl: 'opensys/submitCommon.action?caller=' +caller,
			 resSubmitUrl: 'opensys/resSubmitCommon.action?caller=' +caller,
			 getIdUrl: 'opensys/getCommonId.action?caller=' +caller
		 }]
		});
		me.callParent(arguments); 
	}
});