Ext.define('erp.view.scm.reserve.AvgCostAccount',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
				bodyStyle: 'background: #f1f1f1;',
				margin: '-20 0 0 0',
				xtype: 'form',
				title: '加权平均成本计算作业',
				cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				items: [{
					xtype: 'container',
					contentEl: 'desc'
				},{		
					margin : '20 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '库存期间',
			    	allowBlank: false,
			    	labelWidth: 40,
			    	readOnly: true,
			    	id: 'date',
			    	name: 'date'
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'erpBOMCostButton'
				},{
					xtype: 'erpResultScanButton'
				},{
					xtype: 'erpTurnCostChangeButton'
				},{
					xtype:'erpCloseButton'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});