Ext.define('erp.view.pm.mps.MpsOption',{ 
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
					saveUrl: 'pm/mps/saveMRPOption.action',
					deleteUrl: 'pm/mps/deleteMRPOption.action',
					updateUrl: 'pm/mps/updateMRPOption.action',
					submitUrl:'pm/mps/submitMRPOption.action',
					resSubmitUrl:'pm/mps/resSubmitMRPOption.action',
					auditUrl:'pm/mps/auditMRPOption.action',
					resAuditUrl:'pm/mps/resAuditMRPOption.action',
					getIdUrl: 'common/getId.action?seq=MPSOPTION_SEQ',
					keyField: 'mo_id',
					codeField:'mo_code'
				}] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});