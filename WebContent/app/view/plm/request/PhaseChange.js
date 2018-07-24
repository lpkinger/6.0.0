Ext.define('erp.view.plm.request.PhaseChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', //fit
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 45%',
					saveUrl: 'plm/request/savePhaseChange.action',
					deleteUrl: 'plm/request/deletePhaseChange.action',
					updateUrl: 'plm/request/updatePhaseChange.action',
					auditUrl: 'plm/request/auditPhaseChange.action',
					submitUrl: 'plm/request/submitPhaseChange.action',
					resSubmitUrl: 'plm/request/resSubmitPhaseChange.action',
					getIdUrl: 'common/getId.action?seq=PRJPHASECHANGE_SEQ',				
					keyField: 'pc_id',
					codeField: 'pc_code',
					statusField: 'pc_status',
					statuscodeField: 'pc_statuscode'
				},{							
					xtype: 'erpGridPanel2',
					anchor: '100% 55%',				
					detno: 'pcd_detno',
					necessaryField: '',
					keyField: 'pcd_id',
					allowExtraButtons:false,
					mainField: 'pcd_pcid'
				}]		
		}); 
		this.callParent(arguments); 
	}
});