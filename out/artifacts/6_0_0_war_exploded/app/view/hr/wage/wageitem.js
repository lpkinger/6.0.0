Ext.define('erp.view.hr.wage.wageitem',{
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
					saveUrl: 'hr/wage/saveWageItem.action',
					deleteUrl: 'hr/wage/deleteWageItem.action',
					updateUrl: 'hr/wage/updateWageItem.action',
					auditUrl: 'hr/wage/auditWageItem.action',
					resAuditUrl: 'hr/wage/resAuditWageItem.action',
					submitUrl: 'hr/wage/submitWageItem.action',
					resSubmitUrl: 'hr/wage/resSubmitWageItem.action',
					getIdUrl: 'common/getId.action?seq=WAGEITEM_SEQ',
					keyField: 'wi_id',
					codeField: 'wi_code',
					statusField: 'wi_status',
					statuscodeField: 'wi_statuscode',
					voucherConfig:true					
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});