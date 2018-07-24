

Ext.define('erp.view.WisdomPark.ActivityCenter', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		Ext.apply(this, {
			items : [{
				xtype: 'erpFormPanel',
				saveUrl: 'wisdomPark/activityCenter/saveActivity.action',
				deleteUrl: 'wisdomPark/activityCenter/deleteActivity.action',
				updateUrl: 'wisdomPark/activityCenter/updateActivity.action',
				publishUrl: 'wisdomPark/activityCenter/publishActivity.action',
				cancelUrl: 'wisdomPark/activityCenter/cancelActivity.action',
				advanceEndUrl: 'wisdomPark/activityCenter/advanceEndActivity.action',
				getIdUrl: 'common/getId.action?seq=ACTIVITYCENTER_SEQ',
				keyField:'ac_id'
			}]
		});

		this.callParent(arguments);
	}
});