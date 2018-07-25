Ext.define('erp.view.opensys.demand.PrototypeDemand',{ 
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
					saveUrl: 'opensys/demand/savePrototypeDemand.action',
					deleteUrl: 'opensys/demand/deletePrototypeDemand.action',
					updateUrl: 'opensys/demand/updatePrototypeDemand.action',
					auditUrl: 'opensys/demand/auditPrototypeDemand.action',
					resAuditUrl: 'opensys/demand/resAuditPrototypeDemand.action',
					submitUrl: 'opensys/demand/submitPrototypeDemand.action',
					resSubmitUrl: 'opensys/demand/resSubmitPrototypeDemand.action',
					getIdUrl: 'common/getId.action?seq=CURPrototypeDemand_SEQ',
					keyField: 'cd_id'
		 		}]
			}]
		});
		me.callParent(arguments); 
	}
});