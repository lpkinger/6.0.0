Ext.define('erp.view.crm.marketmgr.resourcemgr.ResourceDemand',{ 
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
					anchor: '100% 70%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=ResourceDemand_SEQ',
					auditUrl: 'common/auditCommon.action?caller='+caller,
					resAuditUrl: 'common/resAuditCommon.action?caller='+caller,
					submitUrl: 'common/submitCommon.action?caller='+caller,
					resSubmitUrl: 'common/resSubmitCommon.action?caller='+caller,
					keyField: 'rd_id',
					codeField: 'rd_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 30%', 
					//necessaryField: 'ppd_costname',
					keyField: 'rdd_id',
					detno: 'rdd_detno',
					mainField: 'rdd_ppid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});