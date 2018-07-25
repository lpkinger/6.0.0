Ext.define('erp.view.plm.test.TestPost',{ 
	extend: 'Ext.Viewport', 
	layout: 'border',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'panel',
				bodyStyle: 'background: #f1f1f1;padding-top: 60px',
				height: '100%',
				region: 'west',
				width: '33%',
				flex: 1,
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{
					xtype: 'button',
					cls: 'x-btn-blue btn-test-post',
					text: '采购验收单',
					style: {
						marginTop: 20
					},
					caller: 'ProdInOut!PurcCheckin'
				},{
					xtype: 'displayfield',
					value: '<img src=' + basePath + 'resource/images/32/down.png>',
					labelSeparator: '',
					style: {
						marginTop: 20
					}
				},{
					xtype: 'button',
					cls: 'x-btn-blue btn-test-post',
					text: '采购验退单',
					style: {
						marginTop: 35
					},
					caller: 'ProdInOut!PurcCheckout'
				}]
			},{
				xtype: 'panel',
				bodyStyle: 'background: #f1f1f1;padding-top: 60px',
				region: 'center',
				width: '34%',
				height: '100%',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{
					xtype: 'button',
					cls: 'x-btn-blue btn-test-post',
					text: '发货单',
					style: {
						marginTop: 20
					},
					caller: 'ProdInOut!Sale'
				},{
					xtype: 'displayfield',
					value: '<img src=' + basePath + 'resource/images/32/down.png>',
					labelSeparator: '',
					style: {
						marginTop: 20
					}
				},{
					xtype: 'button',
					cls: 'x-btn-blue btn-test-post',
					text: '退货单',
					style: {
						marginTop: 35
					},
					caller: 'ProdInOut!SaleReturn'
				}]
			},{
				xtype: 'panel',
				bodyStyle: 'background: #f1f1f1;padding-top: 60px',
				region: 'east',
				width: '33%',
				height: '100%',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{
					xtype: 'button',
					cls: 'x-btn-blue btn-test-post',
					text: '生产领料单',
					style: {
						marginTop: 20
					},
					caller: 'ProdInOut!Picking'
				},{
					xtype: 'displayfield',
					value: '<img src=' + basePath + 'resource/images/32/down.png>',
					labelSeparator: '',
					style: {
						marginTop: 20
					}
				},{
					xtype: 'button',
					cls: 'x-btn-blue btn-test-post',
					text: '生产退料单',
					style: {
						marginTop: 35
					},
					caller: 'ProdInOut!Make!Return'
				}]
			}]
		});
		me.callParent(arguments); 
	}
});