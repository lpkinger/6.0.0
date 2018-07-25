Ext.define('erp.view.crm.marketmgr.marketExtend.ProjBudget',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ProjBudgeti', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl:  'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PROJBUDGETI_SEQ',
					keyField: 'pb_id',
					codeField: 'pb_code',
					statusField: 'pb_statuscode'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'pbd_detno',
					necessaryField: 'pbd_costname',
					keyField: 'pbd_id',
					mainField: 'pbd_pbid',
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});