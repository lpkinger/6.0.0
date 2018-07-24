Ext.define('erp.view.hr.emplmana.Questionset',{ 
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
					saveUrl: 'hr/emplmana/saveQuestionset.action',
					deleteUrl: 'hr/emplmana/deleteQuestionset.action',
					updateUrl: 'hr/emplmana/updateQuestionset.action',		
					getIdUrl: 'common/getId.action?seq=Questionset_SEQ',
					keyField: 'qs_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});