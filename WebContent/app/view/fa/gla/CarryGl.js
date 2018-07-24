Ext.define('erp.view.fa.gla.CarryGl',{ 
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
				title: '结转损益',
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				layout: {
					type : 'vbox',
					pack : 'center'
				},
				fieldDefaults: {
					margin: '5 3 5 20',
					labelWidth: 150
				},
				items: [{
					xtype: 'displayfield',
					fieldLabel: '总账期间',
					name: 'yearmonth',
					id: 'yearmonth'
				},{
					xtype: 'cateTreeDbfindTrigger',
					fieldLabel: '结转损益科目',
					name: 'ca_code',
					id: 'ca_code'
				},{
					xtype: 'checkbox',
					boxLabel: '将结转损益产生的凭证立即登账',
					name: 'account',
					id: 'account'
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