Ext.define('erp.view.crm.chance.BusinessChanceBasis',{ 
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
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,				
					getIdUrl: 'common/getId.action?seq=BUSINESSBASIS_SEQ',
					keyField: 'bb_id',
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});