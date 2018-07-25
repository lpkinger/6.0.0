Ext.define('erp.view.hr.emplmana.Trainexam',{ 
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
					saveUrl: 'hr/emplmana/saveTrainexam.action',
					deleteUrl: 'hr/emplmana/deleteTrainexam.action',
					updateUrl: 'hr/emplmana/updateTrainexam.action',		
					getIdUrl: 'common/getId.action?seq=Trainexam_SEQ',
					keyField: 'te_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});