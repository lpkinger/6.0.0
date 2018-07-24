Ext.define('erp.view.fa.wg.Wageitem',{ 
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
					saveUrl: 'fa/wg/saveWageItem.action',
					deleteUrl: 'fa/wg/deleteWageItem.action',
					updateUrl: 'fa/wg/updateWageItem.action',
					auditUrl: 'fa/wg/auditWageItem.action',
					resAuditUrl: 'fa/wg/resAuditWageItem.action',
					submitUrl: 'fa/wg/submitWageItem.action',
					resSubmitUrl: 'fa/wg/resSubmitWageItem.action',
					getIdUrl: 'common/getId.action?seq=Wageitem_SEQ',
					keyField: 'wi_id',
					statusField: 'wi_status',
					statuscodeField: 'wi_statuscode',
					codeField: 'wi_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});