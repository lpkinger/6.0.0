Ext.define('erp.view.oa.storage.Propertyhandle',{ 
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
					saveUrl: 'oa/storage/savePropertyhandle.action',
					deleteUrl: 'oa/storage/deletePropertyhandle.action',
					updateUrl: 'oa/storage/updatePropertyhandle.action',		
					getIdUrl: 'common/getId.action?seq=Propertyhandle_SEQ',
					auditUrl: 'oa/storage/auditPropertyhandle.action',
					resAuditUrl: 'oa/storage/resAuditPropertyhandle.action',
					submitUrl: 'oa/storage/submitPropertyhandle.action',
					resSubmitUrl: 'oa/storage/resSubmitPropertyhandle.action',
					keyField: 'ph_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'pd_code',
					keyField: 'pd_id',
					detno: 'pd_detno',
					mainField: 'pd_phid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});