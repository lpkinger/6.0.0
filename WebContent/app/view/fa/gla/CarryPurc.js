Ext.define('erp.view.fa.gla.CarryPurc',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	}, 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				xtype: 'form',
				height: 260,
				width: 440,
				bodyStyle: 'background: #f1f1f1;',
				title: '结转采购费用',
				layout: {
					type : 'vbox',
					pack : 'center'
				},
				fieldDefaults: {
					margin: '5 3 5 20',
					labelWidth: 150,
					width: 440
				},
				items: [{
					xtype: 'displayfield',
					fieldLabel: '总账期间',
					name: 'yearmonth',
					id: 'yearmonth'
				},{
					xtype: 'checkbox',
					boxLabel: '将结转产生的凭证立即登账',
					name: 'account',
					id: 'account'
				},{
					xtype: 'displayfield',
					fieldLabel: '采购费用凭证',
					name: 'vocode',
					id: 'vocode'
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '执行',
					id: 'deal',
					cls: 'x-btn-gray',
					iconCls: 'x-button-icon-submit'
				}, {
					xtype: 'erpCloseButton'
				}]
			}] 
		}); 
		this.callParent(arguments); 
	} 
});