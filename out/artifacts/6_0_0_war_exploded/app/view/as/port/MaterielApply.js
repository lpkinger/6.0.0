Ext.define('erp.view.as.port.MaterielApply',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	id:'materielapply', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'as/port/saveMaterielApply.action',
					deleteUrl: 'as/port/deleteMaterielApply.action',
					updateUrl: 'as/port/updateMaterielApply.action',
					auditUrl: 'as/port/auditMaterielApply.action',
					resAuditUrl: 'as/port/resAuditMaterielApply.action',
					submitUrl: 'as/port/submitMaterielApply.action',
					resSubmitUrl: 'as/port/resSubmitMaterielApply.action',
					getIdUrl: 'common/getId.action?seq=AS_MAKEAPPLY_SEQ',
					keyField: 'ama_id',
					codeField:'ama_code'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					detno: 'amad_detno',
					keyField: 'amad_id',
					mainField: 'amad_amaid'
				 }]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});