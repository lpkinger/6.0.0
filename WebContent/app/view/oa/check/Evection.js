Ext.define('erp.view.oa.check.Evection',{ 
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
					saveUrl: 'oa/check/saveEvection.action',
					deleteUrl: 'oa/check/deleteEvection.action',
					updateUrl: 'oa/check/updateEvection.action',
					auditUrl: 'oa/check/auditEvection.action',
					resAuditUrl: 'oa/check/resAuditEvection.action',
					submitUrl: 'oa/check/submitEvection.action',
					resSubmitUrl: 'oa/check/resSubmitEvection.action',
					getIdUrl: 'common/getId.action?seq=Evection_SEQ',
					keyField: 'ec_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});