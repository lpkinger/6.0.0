Ext.define('erp.view.pm.make.ApsMain',{
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
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveAps.action',
					deleteUrl: 'pm/make/deleteAps.action',
					updateUrl: 'pm/make/updateAps.action',
					submitUrl:'pm/make/submitAps.action',
					resSubmitUrl:'pm/make/resSubmitAps.action',
					auditUrl:'pm/make/auditAps.action',
					resAuditUrl:'pm/make/resAuditAps.action',
					deleteAllDetailsUrl:'pm/make/deleteAllDetails.action',
					getIdUrl: 'common/getId.action?seq=APSMAIN_SEQ',
					keyField: 'am_id',
					codeField:'am_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					detno: 'ad_detno',
					keyField: 'ad_id',
					mainField: 'ad_amid'
				  }] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});