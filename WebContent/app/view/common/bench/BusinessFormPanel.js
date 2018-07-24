Ext.QuickTips.init();

Ext.define('erp.view.common.bench.BusinessFormPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.erpBusinessFormPanel',
	region:'north',
	bodyCls : 'x-panel-body-gray',
	margin: '0',
	padding: '0',
	cls: 'form',
	scenes: null,
	initComponent : function(){ 
		var me = this;
		Ext.apply(this, { 
			items:[{
				xtype:'container',
				layout:'table',
				flex:1,
				items:[{
					margin:'0 0 0 4',
					xtype:'displayfield',
					value:'<b>场景:</b>'
				},{
					xtype: 'erpSwitchButton',
					items: me.scenes
				}]
			}]
		});
		this.callParent(arguments);
	}
});
