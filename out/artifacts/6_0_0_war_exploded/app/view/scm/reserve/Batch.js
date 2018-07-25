Ext.define('erp.view.scm.reserve.Batch',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=BATCH_SEQ',
				keyField: 'ba_id', 
				codeField: 'ba_code'
			}]
		}); 
		me.callParent(arguments); 
	} 
});