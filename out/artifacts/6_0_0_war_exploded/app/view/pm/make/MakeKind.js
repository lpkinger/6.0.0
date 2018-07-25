Ext.define('erp.view.pm.make.MakeKind',{ 
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
					saveUrl: 'pm/make/saveMakeKind.action',
					deleteUrl: 'pm/make/deleteMakeKind.action',
					updateUrl: 'pm/make/updateMakeKind.action',
					getIdUrl: 'common/getId.action?seq=makekind_SEQ',
					keyField: 'mk_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});