Ext.define('erp.view.pm.make.MRPData',{ 
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
					saveUrl: 'pm/make/saveMRPData.action',
					deleteUrl: 'pm/make/deleteMRPData.action',
					updateUrl: 'pm/make/updateMRPData.action',
					submitUrl: 'pm/make/submitMRPData.action',
					auditUrl: 'pm/make/auditMRPData.action',
					resAuditUrl: 'pm/make/resAuditMRPData.action',					
					resSubmitUrl: 'pm/make/resSubmitMRPData.action',
					getIdUrl: 'common/getId.action?seq=MRPDATA_SEQ',
					keyField: 'md_id',
					statusField: 'md_status',
					codeField: 'md_statuscode'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});