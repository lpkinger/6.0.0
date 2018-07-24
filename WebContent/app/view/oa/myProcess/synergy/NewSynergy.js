Ext.define('erp.view.oa.myProcess.synergy.NewSynergy',{ 
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
					saveUrl: 'oa/myProcess/synergy/saveSynergy.action',
					updateUrl: 'oa/myProcess/synergy/updateSynergy.action',
					deleteUrl: 'oa/myProcess/synergy/deleteSynergy.action',
					getIdUrl: 'common/getId.action?seq=SYNERGY_SEQ',
					keyField: 'sy_id'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});