Ext.define('erp.view.oa.appliance.OapurchaseChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'erpFormPanel', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'oa/appliance/saveOapurchaseChange.action',
					deleteUrl: 'oa/appliance/deleteOapurchaseChange.action',
					updateUrl: 'oa/appliance/updateOapurchaseChange.action',
					getIdUrl: 'common/getId.action?seq=OapurchaseChange_SEQ',
					auditUrl: 'oa/appliance/auditOapurchaseChange.action',
					submitUrl: 'oa/appliance/submitOapurchaseChange.action',
					resSubmitUrl: 'oa/appliance/resSubmitOapurchaseChange.action',
					keyField: 'oc_id',
					codeField: 'oc_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					keyField: 'ocd_id',
					detno: 'ocd_detno',
					mainField: 'ocd_kbid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});