Ext.define('erp.view.hr.emplmana.Trainplan',{ 
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
					anchor: '100% 35%',
					saveUrl: 'hr/emplmana/saveTrainingPlan.action',
					deleteUrl: 'hr/emplmana/deleteTrainingPlan.action',
					updateUrl: 'hr/emplmana/updateTrainingPlan.action',		
					auditUrl: 'hr/emplmana/auditTrainingPlan.action',
					resAuditUrl: 'hr/emplmana/resAuditTrainingPlan.action',
					submitUrl: 'hr/emplmana/submitTrainingPlan.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTrainingPlan.action',
					getIdUrl: 'common/getId.action?seq=TrainingPlan_SEQ',
					keyField: 'tp_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					necessaryField: 'ti_tccode',
					keyField: 'ti_id',
					detno: 'ti_detno',
					mainField: 'ti_tpid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});