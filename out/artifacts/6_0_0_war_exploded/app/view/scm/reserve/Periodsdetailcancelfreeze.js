Ext.define('erp.view.scm.reserve.Periodsdetailcancelfreeze', {
	extend : 'Ext.Viewport',
	layout : {
		type : 'vbox',
		align : 'center',
		pack : 'center',
	},
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'form',
				width: 350,
				height: 200,
				margin: '-30 0 0 0',
				layout: {
					type: 'vbox',
					pack: 'center'
				},
				title : '库存取消冻结',
				bodyStyle : 'background:#f1f2f5;',
				defaults : {
					labelAlign : "right"
				},
				items : [{
					xtype : 'displayfield',
					fieldLabel : '已冻结期间',
					fieldStyle: 'font-size: 14px;',
					width: 350,
					margin : '4 2 4 2',
					id : 'date',
					name : 'date'
				}],
				buttonAlign : 'center',
				buttons : [{
					xtype : 'erpCancelFreezeButton'
				},{
					xtype : 'erpCloseButton'
				}]
			} ]
		});
		me.callParent(arguments);
	}
});