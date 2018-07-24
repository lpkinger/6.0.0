Ext.define('erp.view.scm.reserve.LabelParameter',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
				saveUrl: 'scm/reserve/saveLabelP.action?caller=' +caller,
				deleteUrl: 'scm/reserve/deleteLabelP.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateLabelP.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=LABELPARAMETER_SEQ',
				keyField: 'lp_id'
			}]
		}); 
		me.callParent(arguments); 
	} 
});