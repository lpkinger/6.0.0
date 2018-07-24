Ext.define('erp.view.oa.check.Checktype',{ 
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
					saveUrl: 'oa/check/saveChecktype.action',
					deleteUrl: 'oa/check/deleteChecktype.action',
					updateUrl: 'oa/check/updateChecktype.action',
					getIdUrl: 'common/getId.action?seq=Checktype_SEQ',
					keyField: 'ct_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});