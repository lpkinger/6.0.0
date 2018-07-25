Ext.define('erp.view.pm.craft.WorkTime',{ 
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
					saveUrl: 'pm/craft/saveWorkTime.action',
					deleteUrl: 'pm/craft/deleteWorkTime.action',
					updateUrl: 'pm/craft/updateWorkTime.action',
					getIdUrl: 'common/getId.action?seq=WORKTIME_SEQ',
					keyField: 'wt_id',
					codeField: 'wt_code'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});