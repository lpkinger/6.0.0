Ext.define('erp.view.oa.storage.Propertyrepair',{ 
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
					anchor: '100% 50%',
					saveUrl: 'oa/storage/savePropertyrepair.action',
					deleteUrl: 'oa/storage/deletePropertyrepair.action',
					updateUrl: 'oa/storage/updatePropertyrepair.action',		
					getIdUrl: 'common/getId.action?seq=Propertyrepair_SEQ',
					auditUrl: 'oa/storage/auditPropertyrepair.action',
					resAuditUrl: 'oa/storage/resAuditPropertyrepair.action',
					submitUrl: 'oa/storage/submitPropertyrepair.action',
					resSubmitUrl: 'oa/storage/resSubmitPropertyrepair.action',
					keyField: 'pr_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'pd_code',
					keyField: 'pd_id',
					detno: 'pd_detno',
					mainField: 'pd_prid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});