Ext.define('erp.view.common.sysinit.SysHome', {
	extend: 'Ext.container.Viewport',
	getTools: function(){
		return [{
			xtype: 'tool',
			type: 'gear',
			handler: function(e, target, header, tool){
				var portlet = header.ownerCt;
				portlet.setLoading('Loading...');
				Ext.defer(function() {
					portlet.setLoading(false);
				}, 2000);
			}
		}];
	},
	initComponent: function(){
		Ext.apply(this, {
			layout: {
				type: 'fit',
				padding: '0 5 5 5'
			},
			items: [{
				id: 'portal',
				xtype: 'portalpanel',
				items: [{
					id: 'col-1',
					items: [{
						id: 'portlet-1',
						title: '功能模块',
						//tools: this.getTools(),
					    height:0.4*Height,
						items: {
							xtype:'moudleconportlet'
						}
					},{
						id: 'portlet-2',
						title:'基础配置项',
						height:0.6*Height,
						items:{
							xtype:'basicconportlet'
						},
						listeners: {
							//'close': Ext.bind(this.onPortletClose, this)
							beforerender:function(panel){
								/*var e=Ext.getCmp('portal').getEl().dom,
								h = Number(e.style.height.replace('px', ''));
								panel.height=h*panel.anchorHeight-1;*/
							}
						}
					}]
				},{
					id: 'col-2',
					items: [{
						id: 'portlet-3',
						title: '初始化进度',
						height:Height,
						//tools: this.getTools(),
						//html: '<div class="portlet-content">'+Ext.example.bogusMarkup+'</div>',
						listeners: {
							//'close': Ext.bind(this.onPortletClose, this)
						}
					}]
				}]
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
