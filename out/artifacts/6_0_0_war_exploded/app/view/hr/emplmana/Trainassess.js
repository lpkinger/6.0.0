Ext.define('erp.view.hr.emplmana.Trainassess',{ 
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
					saveUrl: 'hr/emplmana/saveTrainassess.action',
					deleteUrl: 'hr/emplmana/deleteTrainassess.action',
					updateUrl: 'hr/emplmana/updateTrainassess.action',		
					auditUrl: 'hr/emplmana/auditTrainassess.action',
					resauditUrl: 'hr/emplmana/resAuditTrainassess.action',
					submitUrl: 'hr/emplmana/submitTrainassess.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTrainassess.action',
					getIdUrl: 'common/getId.action?seq=TrainingCourseassess_SEQ',
					keyField: 'ta_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});