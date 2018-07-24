Ext.define('erp.view.ma.PostStyle',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 25%',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'common/deleteCommon.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,		
				getIdUrl: 'common/getId.action?seq=POSTSTYLE_SEQ',
				keyField: 'ps_id'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 75%', 
				allowExtraButtons: true,
				keyField: 'pss_id',
				detno: 'pss_detno',
				mainField: 'pss_psid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});