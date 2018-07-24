Ext.define('erp.view.fs.analysis.QuotaStatistics',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				anchor: '100% 15%',
				xtype: "erpStatisticsForm", 
				extraItems : [{
					id: 'amount',
					layout: 'column',
					columnWidth: 1,
					xtype: 'fieldcontainer',
					allowBlank: false,
					defaults: {
						margin : '2 2 2 2',
						labelWidth: 105,
				       	fieldStyle : "background:#FFFAFA;color:#515151;",
				       	labelAlign : "right",
				       	blankText : $I18N.common.form.blankText
					},
					items: [{
						fieldLabel: '总额度金额',
						xtype: 'numberfield',
						name: 'CQ_QUOTA',
						id: 'CQ_QUOTA',
						columnWidth: 0.25,
						hideTrigger:true,
						readOnly: true
					},{
						fieldLabel: '已使用额度金额',
						xtype: 'numberfield',
						name: 'CQ_DUEAMOUNT',
						id: 'CQ_DUEAMOUNT',
						columnWidth: 0.25,
						hideTrigger:true,
						readOnly: true
					},{
						fieldLabel: '可用额度金额',
						xtype: 'numberfield',
						name: 'CQ_SPAMOUNT',
						id: 'CQ_SPAMOUNT',
						columnWidth: 0.25,
						hideTrigger:true,
						readOnly: true
					}]
				}]
		    },{
		    	anchor: '100% 85%',
				title:'卖方额度管理',
				id: 'seller',
				xtype : 'erpGridPanel2',
				caller: 'Quota!Seller',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				bbar: {
					xtype: 'erpToolbar',
					id:'toolbar',
					enableAdd : false,
					enableDelete : false,
					enableCopy : false,
					enablePaste : false,
					enableUp : false,
					enableDown : false
				}
			}]
		});
		this.callParent(arguments); 
	}
});