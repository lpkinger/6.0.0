Ext.define('erp.view.fa.gla.ResearchFee',{ 
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
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				bodyStyle: 'background: #f1f1f1;',
				title: '研发费用结转',
				layout: {
					type : 'vbox',
					pack : 'center'
				},
				fieldDefaults: {
					margin: '5 3 5 20',
					labelWidth: 150
				},
				items: [{
					width: 300,
					xtype: 'displayfield',
					fieldLabel: '总账期间',
					name: 'yearmonth',
					id: 'yearmonth'
				},{
					xtype: 'checkbox',
					boxLabel: '将结转的研发费用凭证立即登账',
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