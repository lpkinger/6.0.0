Ext.define('erp.view.opensys.demand.OrderDemand',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel2',
					anchor: '100% 100%',
					saveUrl: 'opensys/demand/saveOrderDemand.action',
					deleteUrl: 'opensys/demand/deleteOrderDemand.action',
					updateUrl: 'opensys/demand/updateOrderDemand.action',
					auditUrl: 'opensys/demand/auditOrderDemand.action',
					resAuditUrl: 'opensys/demand/resAuditOrderDemand.action',
					submitUrl: 'opensys/demand/submitOrderDemand.action',
					resSubmitUrl: 'opensys/demand/resSubmitOrderDemand.action',
					getIdUrl: 'common/getId.action?seq=CURORDERDEMAND_SEQ',
					keyField: 'cd_id',
					codeField: 'cd_code'
		 		}]
			}]
		});
		me.callParent(arguments); 
	}
});