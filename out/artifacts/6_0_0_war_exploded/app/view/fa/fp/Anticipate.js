Ext.define('erp.view.fa.fp.Anticipate',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					anchor: '100% 40%',
					xtype: 'erpFormPanel',
					saveUrl: 'fa/fp/saveAnticipate.action',
					deleteUrl: 'fa/fp/deleteAnticipate.action',
					updateUrl: 'fa/fp/updateAnticipate.action',
					auditUrl: 'fa/fp/auditAnticipate.action',
					resAuditUrl: 'fa/fp/resAuditAnticipate.action',
					submitUrl: 'fa/fp/submitAnticipate.action',
					resSubmitUrl: 'fa/fp/resSubmitAnticipate.action',
					printUrl:'fa/fp/printAnticipate.action',
					getIdUrl: 'common/getId.action?seq=ANTICIPATE_SEQ',
					keyField: 'an_id',	
					codeField: 'an_code',
					statusField: 'an_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					keyField: 'and_id',
					mainField: 'and_anid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});