Ext.define('erp.view.pm.make.MakeScrap',{ 
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
					saveUrl: 'pm/make/saveMakeScrap.action' ,
					deleteUrl: 'pm/make/deleteMakeScrap.action' ,
					updateUrl: 'pm/make/updateMakeScrap.action' ,
					auditUrl: 'pm/make/auditMakeScrap.action' ,
					resAuditUrl: 'pm/make/resAuditMakeScrap.action' ,
					submitUrl: 'pm/make/submitMakeScrap.action' ,
					printUrl: 'pm/make/printMakeScrap.action' ,
					resSubmitUrl: 'pm/make/resSubmitMakeScrap.action' ,
					getIdUrl: 'common/getId.action?seq=MakeScrap_SEQ',
					keyField: 'ms_id',
					codeField: 'ms_code',
					statusField: 'ms_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'md_detno',
					necessaryField: 'md_prodcode',
					keyField: 'md_id',
					mainField: 'md_msid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});