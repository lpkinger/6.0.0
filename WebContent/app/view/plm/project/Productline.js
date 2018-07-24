Ext.define('erp.view.plm.project.Productline',{ 
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
					anchor: '100% 100%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=Productline_SEQ',
					keyField: 'pl_id',
					codeField:'pl_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});