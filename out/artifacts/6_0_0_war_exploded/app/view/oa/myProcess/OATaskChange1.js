Ext.define('erp.view.oa.myProcess.OATaskChange1',{ 
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
					getIdUrl: 'common/getId.action?seq=ProjectTaskChange_SEQ',
					auditUrl: 'oa/myProcess/auditOATaskChange.action',
					resAuditUrl: 'oa/myProcess/resAuditOATaskChange.action',
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'ptc_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});