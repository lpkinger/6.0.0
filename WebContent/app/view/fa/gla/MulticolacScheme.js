Ext.define('erp.view.fa.gla.MulticolacScheme',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				deleteUrl: 'fa/gla/deleteMulticolacScheme.action',
				updateUrl: 'fa/gla/updateMulticolacScheme.action',
				auditUrl: 'fa/gla/auditMulticolacScheme.action',
				saveUrl: 'fa/gla/saveMulticolacScheme.action',
				resAuditUrl: 'fa/gla/resAuditMulticolacScheme.action',
				submitUrl: 'fa/gla/submitMulticolacScheme.action',
				resSubmitUrl: 'fa/gla/resSubmitMulticolacScheme.action',
				getIdUrl: 'common/getId.action?seq=MulticolacScheme_SEQ',
				keyField: 'mas_id',			
				statusField: 'mas_status',
				statuscodeField: 'mas_statuscode'			
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'masd_detno',				
				keyField: 'masd_id',
				mainField: 'masd_masid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});