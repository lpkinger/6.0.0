Ext.define('erp.view.WisdomPark.ActivityType',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				saveUrl: 'common/saveCommon.action?caller=' +caller,
				deleteUrl: 'wisdomPark/activityCenter/deleteActivityType.action?caller=' +caller,
				updateUrl: 'common/updateCommon.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=ACTIVITYTYPE_SEQ'
	    	}]
		}); 
		me.callParent(arguments); 
	} 
});