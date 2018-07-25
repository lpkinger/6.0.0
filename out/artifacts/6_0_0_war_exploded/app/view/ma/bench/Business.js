Ext.define('erp.view.ma.bench.Business',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				saveUrl: 'bench/ma/saveBusiness.action?caller=' +caller,
				deleteUrl: 'bench/ma/deleteBusiness.action?caller=' +caller,
				updateUrl: 'bench/ma/updateBusiness.action?caller=' +caller,
				auditUrl: 'common/auditCommon.action?caller=' +caller,
				resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
				submitUrl: 'common/submitCommon.action?caller=' +caller,
				resSubmitUrl: 'common/resSubmitCommon.action?caller=' +caller,
				bannedUrl: 'bench/ma/bannedBusiness.action?caller='+caller,
				resBannedUrl: 'bench/ma/resBannedBusiness.action?caller='+caller,
				getIdUrl: 'common/getId.action?seq=BENCHBUSINESS_SEQ'
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});