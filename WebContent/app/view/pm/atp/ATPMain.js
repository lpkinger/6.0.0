Ext.define('erp.view.pm.atp.ATPMain',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'ATPViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 48%',
					saveUrl: 'pm/atp/saveATPMain.action',
					deleteUrl: 'pm/atp/deleteATPMain.action',
					updateUrl: 'pm/atp/updateATPMain.action',
					auditUrl: 'pm/atp/auditATPMain.action',
					resAuditUrl: 'pm/atp/resAuditATPMain.action',
					submitUrl: 'pm/atp/submitATPMain.action',
					resSubmitUrl: 'pm/atp/resSubmitATPMain.action',
					executeOperation: 'pm/atp/executeOperation.action',
					getIdUrl: 'common/getId.action?seq=ATPMain_SEQ',
					keyField: 'am_id',
					codeField: 'am_code',
					statusField: 'am_status',
					statuscodeField: 'am_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 52%', 
					detno: 'ad_detno',
					keyField: 'ad_id',
					mainField: 'ad_amid',
					necessaryField: 'ad_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});