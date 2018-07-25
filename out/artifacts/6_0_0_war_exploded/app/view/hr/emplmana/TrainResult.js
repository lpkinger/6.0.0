Ext.define('erp.view.hr.emplmana.TrainResult',{ 
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
					saveUrl: 'hr/emplmana/saveTrainingResult.action',
					deleteUrl: 'hr/emplmana/deleteTrainingResult.action',
					updateUrl: 'hr/emplmana/updateTrainingResult.action',		
					auditUrl: 'hr/emplmana/auditTrainingResult.action',
					submitUrl: 'hr/emplmana/submitTrainingResult.action',
					resSubmitUrl: 'hr/emplmana/resSubmitTrainingResult.action',
					getIdUrl: 'common/getId.action?seq=TrainingCourseResult_SEQ',
					keyField: 'tr_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});