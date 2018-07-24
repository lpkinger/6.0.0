Ext.define('erp.view.fs.analysis.AccountStatistics',{ 
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
						fieldLabel: '保理转让款',
						xtype: 'numberfield',
						name: 'AA_TRANSFERAMOUNT',
						id: 'AA_TRANSFERAMOUNT',
						columnWidth: 0.25,
						hideTrigger:true,
						readOnly: true
					},{
						fieldLabel: '保理首付款',
						xtype: 'numberfield',
						name: 'AA_DUEAMOUNT',
						id: 'AA_DUEAMOUNT',
						columnWidth: 0.25,
						hideTrigger:true,
						readOnly: true
					},{
						fieldLabel: '借据余额',
						xtype: 'numberfield',
						name: 'AA_LEFTAMOUNT',
						id: 'AA_LEFTAMOUNT',
						columnWidth: 0.25,
						hideTrigger:true,
						readOnly: true
					}]
				}]
		    },{
		    	anchor: '100% 40%',
				title:'已结清业务',
				id: 'close',
				xtype : 'erpGridPanel2',
				caller: 'AccountApply!Analysis',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')]
			},{
				anchor: '100% 45%',
				title:'未结清业务',
				id: 'unclose',
				xtype : 'erpGridPanel2',
				caller: 'AccountApply!Analysis',
				condition:'1=2',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				bbar: {xtype: 'erpToolbar',id:'toolbar1'},
				onExport: function() {
					this.BaseUtil.exportGrid(this);
				}
			}]
		});
		this.callParent(arguments); 
	}
});