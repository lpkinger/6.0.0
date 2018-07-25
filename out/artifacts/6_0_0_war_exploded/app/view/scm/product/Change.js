Ext.define('erp.view.scm.product.Change',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/product/saveChange.action?caller='+caller,
				deleteUrl: 'scm/product/deleteChange.action?caller='+caller,
				updateUrl: 'scm/product/updateChange.action?caller='+caller,		
				getIdUrl: 'common/getId.action?seq=changestatus_SEQ',
				auditUrl: 'scm/product/auditChange.action?caller='+caller,
				resAuditUrl: 'scm/product/resAuditChange.action?caller='+caller,
				submitUrl: 'scm/product/submitChange.action?caller='+caller,
				resSubmitUrl: 'scm/product/resSubmitChange.action?caller='+caller,
				keyField: 'cs_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				keyField: 'csd_id',
				detno: 'csd_detno',
				mainField: 'csd_csid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});