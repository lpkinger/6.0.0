Ext.define('erp.view.oa.storage.Propertyapply',{ 
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
					saveUrl: 'oa/storage/savePropertyapply.action',
					deleteUrl: 'oa/storage/deletePropertyapply.action',
					updateUrl: 'oa/storage/updatePropertyapply.action',		
					getIdUrl: 'common/getId.action?seq=Propertyapply_SEQ',
					auditUrl: 'oa/storage/auditPropertyapply.action',
					resAuditUrl: 'oa/storage/resAuditPropertyapply.action',
					submitUrl: 'oa/storage/submitPropertyapply.action',
					resSubmitUrl: 'oa/storage/resSubmitPropertyapply.action',
					keyField: 'pa_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'pd_code',
					keyField: 'pd_id',
					detno: 'pd_detno',
					mainField: 'pd_paid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});