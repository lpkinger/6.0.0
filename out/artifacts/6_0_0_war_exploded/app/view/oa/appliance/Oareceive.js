Ext.define('erp.view.oa.appliance.Oareceive',{ 
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
					saveUrl: 'oa/appliance/saveOareceive.action',
					deleteUrl: 'oa/appliance/deleteOareceive.action',
					updateUrl: 'oa/appliance/updateOareceive.action',
					printUrl: 'oa/appliance/printOareceive.action',
					getIdUrl: 'common/getId.action?seq=Oareceive_SEQ',
					auditUrl: 'oa/appliance/auditOareceive.action',
					resAuditUrl: 'oa/appliance/resAuditOareceive.action',
					submitUrl: 'oa/appliance/submitOareceive.action',
					resSubmitUrl: 'oa/appliance/resSubmitOareceive.action',
					keyField: 'or_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'od_procode',
					keyField: 'od_id',
					detno: 'od_detno',
					mainField: 'od_osid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});