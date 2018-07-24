Ext.define('erp.view.hr.check.Legalholiday',{ 
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
					saveUrl: 'hr/check/saveLegalholiday.action',
					deleteUrl: 'hr/check/deleteLegalholiday.action',
					updateUrl: 'hr/check/updateLegalholiday.action',		
					getIdUrl: 'common/getId.action?seq=Legalholiday_SEQ',
					keyField: 'lh_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});