Ext.define('erp.view.hr.emplmana.Trainset',{ 
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
					saveUrl: 'hr/emplmana/saveTrainset.action',
					deleteUrl: 'hr/emplmana/deleteTrainset.action',
					updateUrl: 'hr/emplmana/updateTrainset.action',		
					getIdUrl: 'common/getId.action?seq=Trainset_SEQ',
					keyField: 'ts_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});