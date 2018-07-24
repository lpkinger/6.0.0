Ext.define('erp.view.crm.aftersalemgr.PrototypeDemand',{ 
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
					saveUrl: 'crm/aftersalemgr/savePrototypeDemand.action',
					deleteUrl: 'crm/aftersalemgr/deletePrototypeDemand.action',
					updateUrl: 'crm/aftersalemgr/updatePrototypeDemand.action',
					auditUrl: 'crm/aftersalemgr/auditPrototypeDemand.action',
					resAuditUrl: 'crm/aftersalemgr/resAuditPrototypeDemand.action',
					submitUrl: 'crm/aftersalemgr/submitPrototypeDemand.action',
					resSubmitUrl: 'crm/aftersalemgr/resSubmitPrototypeDemand.action',
					getIdUrl: 'common/getId.action?seq=CURPrototypeDemand_SEQ',
					keyField: 'cd_id'
		 		}]
			}]
		});
		me.callParent(arguments); 
	}
});