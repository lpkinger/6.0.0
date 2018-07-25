Ext.define('erp.view.pm.make.MakeScrapmake',{ 
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
					saveUrl: 'pm/make/saveMakeScrapmake.action' ,
					deleteUrl: 'pm/make/deleteMakeScrapmake.action' ,
					updateUrl: 'pm/make/updateMakeScrapmake.action' ,
					auditUrl: 'pm/make/auditMakeScrapmake.action' ,
					resAuditUrl: 'pm/make/resAuditMakeScrapmake.action' ,
					submitUrl: 'pm/make/submitMakeScrapmake.action' ,
					resSubmitUrl: 'pm/make/resSubmitMakeScrapmake.action' ,
					printUrl: 'pm/make/printMakeScrap.action' ,
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