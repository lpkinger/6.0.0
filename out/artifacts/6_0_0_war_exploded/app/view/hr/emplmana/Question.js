Ext.define('erp.view.hr.emplmana.Question',{ 
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
					saveUrl: 'hr/emplmana/saveQuestion.action',
					deleteUrl: 'hr/emplmana/deleteQuestion.action',
					updateUrl: 'hr/emplmana/updateQuestion.action',		
					getIdUrl: 'common/getId.action?seq=Question_SEQ',
					keyField: 'qu_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});