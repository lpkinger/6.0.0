Ext.define('erp.view.crm.marketmgr.marketExtend.ProjectExtend',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ProjectExtend', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 65%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					auditUrl: 'common/auditCommon.action?caller=' +caller,
					resAuditUrl: 'common/resAuditCommon.action?caller=' +caller,
					submitUrl: 'common/submitCommon.action?caller=' +caller,
					resSubmitUrl:  'common/resSubmitCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PROJECTEXTEND_SEQ',
					keyField: 'pe_id',
					codeField: 'pe_code',
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%', 
					detno: 'ped_detno',
					necessaryField: 'ped_costname',
					keyField: 'ped_id',
					mainField: 'ped_peid',
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});