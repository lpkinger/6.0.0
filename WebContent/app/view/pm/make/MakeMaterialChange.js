Ext.define('erp.view.pm.make.MakeMaterialChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveMakeMaterialChange.action?caller=' +caller,
					deleteUrl: 'pm/make/deleteMakeMaterialChange.action?caller=' +caller,
					updateUrl: 'pm/make/updateMakeMaterialChange.action?caller=' +caller,
					auditUrl: 'pm/make/auditMakeMaterialChange.action?caller=' +caller,
					resAuditUrl: 'pm/make/resAuditMakeMaterialChange.action?caller=' +caller,
					submitUrl: 'pm/make/submitMakeMaterialChange.action?caller=' +caller,
					resSubmitUrl: 'pm/make/resSubmitMakeMaterialChange.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=MAKEMATERIALCHANGE_SEQ',
					keyField: 'mc_id',
					codeField: 'mc_code',
					statusField: 'mc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					allowExtraButtons: true,
					detno: 'md_detno',
					necessaryField: 'md_prodcode',
					keyField: 'md_id',
					mainField: 'md_mcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});