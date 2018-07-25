Ext.define('erp.view.pm.make.MakeNotice',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeNoticeViewport',
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveMakeNotice.action?caller=' +caller,
					deleteUrl: 'pm/make/deleteMakeNotice.action?caller=' +caller,
					updateUrl: 'pm/make/updateMakeNotice.action?caller=' +caller,
					submitUrl: 'pm/make/submitMakeNotice.action?caller=' +caller,
					auditUrl: 'pm/make/auditMakeNotice.action?caller=' +caller,
					resAuditUrl: 'pm/make/resAuditMakeNotice.action?caller=' +caller,				
					resSubmitUrl: 'pm/make/resSubmitMakeNotice.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=MAKENOTICE_SEQ',
					keyField: 'mn_id',
					statusField: 'mn_statuscode',
					codefield:'mn_code',
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'mnd_detno',
					necessaryField: 'mnd_prodcode',
					keyField: 'mnd_id',
					mainField: 'mnd_mnid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});