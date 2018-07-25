Ext.define('erp.view.plm.record.RecordLog',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	autoScroll:true,
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'plm/record/saveWorkRecord.action',
					updateUrl:'plm/record/updateWorkRecord.action',
					getIdUrl: 'common/getId.action?seq=WORKRECORD_SEQ',
					resSubmitUrl:'plm/record/resSubmitWorkRecord.action',
					keyField: 'wr_id',
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});