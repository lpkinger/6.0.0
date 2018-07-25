Ext.define('erp.view.pm.make.MakeChange',{ 
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
					saveUrl: 'pm/make/saveMakeChange.action?caller='+caller ,
					deleteUrl: 'pm/make/deleteMakeChange.action?caller='+caller ,
					updateUrl: 'pm/make/updateMakeChange.action?caller='+caller ,
					auditUrl: 'pm/make/auditMakeChange.action?caller='+caller ,
					resAuditUrl: 'pm/make/resAuditMakeChange.action?caller='+caller ,
					submitUrl: 'pm/make/submitMakeChange.action?caller='+caller ,
					resSubmitUrl: 'pm/make/resSubmitMakeChange.action?caller='+caller ,
					getIdUrl: 'common/getId.action?seq=MAKECHANGE_SEQ',
					keyField: 'mc_id',
					codeField: 'mc_code',
					statusField: 'mc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
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