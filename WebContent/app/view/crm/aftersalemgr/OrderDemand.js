Ext.define('erp.view.crm.aftersalemgr.OrderDemand',{ 
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
					saveUrl: 'crm/aftersalemgr/saveOrderDemand.action?',
					deleteUrl: 'crm/aftersalemgr/deleteOrderDemand.action',
					updateUrl: 'crm/aftersalemgr/updateOrderDemand.action',
					auditUrl: 'crm/aftersalemgr/auditOrderDemand.action',
					resAuditUrl: 'crm/aftersalemgr/resAuditOrderDemand.action',
					submitUrl: 'crm/aftersalemgr/submitOrderDemand.action',
					resSubmitUrl: 'crm/aftersalemgr/resSubmitOrderDemand.action',
					getIdUrl: 'common/getId.action?seq=CURORDERDEMAND_SEQ',
					keyField: 'cd_id',
					codeField: 'cd_code'
		 		}]
			}]
		});
		me.callParent(arguments); 
	}
});