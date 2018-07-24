Ext.define('erp.view.pm.mould.DeliveryOrder',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'DeliveryOrderViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'pm/mould/saveDeliveryOrder.action',
					deleteUrl: 'pm/mould/deleteDeliveryOrder.action',
					updateUrl: 'pm/mould/updateDeliveryOrder.action',
					auditUrl: 'pm/mould/auditDeliveryOrder.action',
					resAuditUrl: 'pm/mould/resAuditDeliveryOrder.action',
					submitUrl: 'pm/mould/submitDeliveryOrder.action',
					resSubmitUrl: 'pm/mould/resSubmitDeliveryOrder.action',
					postUrl: 'pm/mould/postDeliveryOrder.action',
					resPostUrl: 'pm/mould/resPostDeliveryOrder.action',
					getIdUrl: 'common/getId.action?seq=MOD_DELIVERYORDER_SEQ',
					keyField: 'md_id',
					codeField: 'md_code',
					statusField: 'md_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'mdd_detno',
					necessaryField: 'mdd_pscode',
					keyField: 'mdd_id',
					mainField: 'mdd_mdid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});