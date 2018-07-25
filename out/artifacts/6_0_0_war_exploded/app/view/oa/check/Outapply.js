Ext.define('erp.view.oa.check.Outapply',{ 
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
					saveUrl: 'oa/check/saveOutapply.action',
					deleteUrl: 'oa/check/deleteOutapply.action',
					updateUrl: 'oa/check/updateOutapply.action',
					auditUrl: 'oa/check/auditOutapply.action',
					resAuditUrl: 'oa/check/resAuditOutapply.action',
					submitUrl: 'oa/check/submitOutapply.action',
					resSubmitUrl: 'oa/check/resSubmitOutapply.action',
					getIdUrl: 'common/getId.action?seq=Outapply_SEQ',
					keyField: 'oa_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});