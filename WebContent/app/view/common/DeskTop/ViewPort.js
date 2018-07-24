Ext.define('erp.view.common.DeskTop.ViewPort', {
	extend: 'Ext.container.Viewport',
	uses: ['erp.view.common.DeskTop.InfoRemindPortal','erp.view.common.DeskTop.NewFlowPortal','erp.view.common.DeskTop.FlowPortal','erp.view.common.DeskTop.PortalColumn','erp.view.common.DeskTop.Portlet','erp.view.common.DeskTop.CommonUsePortal','erp.view.common.DeskTop.TaskPortal','erp.view.common.DeskTop.InfoPortal','erp.view.common.DeskTop.CallPortal','erp.view.common.DeskTop.FeedbackPortal','erp.view.common.DeskTop.SubsPortal','erp.view.common.DeskTop.InvitePortal'],
	initComponent: function(){
		Ext.apply(this, {
			id: 'app-viewport',
			layout: {
				type: 'fit',
				padding: '0 2 2 2'
			},
			items: [{
				id: 'app-portal',
				layout:'column',
				xtype:'deskportal'
			}]
		});
		this.callParent(arguments);
	},

	onPortletClose: function(portlet) {
		this.showMsg('"' + portlet.title + '" was removed');
	},

	showMsg: function(msg) {
		var el = Ext.get('app-msg'),
		msgId = Ext.id();

		this.msgId = msgId;
		el.update(msg).show();

		Ext.defer(this.clearMsg, 3000, this, [msgId]);
	},

	clearMsg: function(msgId) {
		if (msgId === this.msgId) {
			Ext.get('app-msg').hide();
		}
	}
});

