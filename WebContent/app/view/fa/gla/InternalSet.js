Ext.define('erp.view.fa.gla.InternalSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl : 'common/saveCommon.action?caller=' + caller,
				deleteUrl : 'common/deleteCommon.action?caller=' + caller,
				updateUrl : 'common/updateCommon.action?caller=' + caller,
				getIdUrl: 'common/getId.action?seq=INTERNALSET_SEQ',
				keyField: 'is_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});