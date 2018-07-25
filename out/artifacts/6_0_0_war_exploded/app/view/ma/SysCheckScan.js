Ext.define('erp.view.ma.SysCheckScan', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				layout : 'border',
				items : [ {
					region : 'west',
					width : '25%',
					xtype : 'erpSysCheckTreePanel',
					layout : 'fit',
				}, {
					layout : 'anchor',
					region : 'center',
					id:'mainplace',
					items : [{
						//style : 'background:#CDCDB4',
						//bodyStyle : 'background:#CDCDB4;',
						anchor : '100%  7%',
						region:'north',
						xtype : 'SearChForm',
					},{
						anchor:'100% 93%',
						layout:'fit',
						autoScroll:true,
						region:'center',
						xtype : 'erpSysCheckGrid',				
						condition:'1=1'
					},{
						anchor:'100% 93%',
						layout:'fit',
						autoScroll:true,
						hidden:true,
						id:'gridpanel',
						xtype : 'erpGridPanel2',
						caller : 'SysCheckData',
						bbar:{
						 xtype:'toolbar',
						 items:['-', {
								text : '合计栏',
								xtype : 'tbtext',
							}, {
								xtype:'splitter',
								width:10,
							},{
								id:'scd_count',
								itemId:'allcount',
								xtype:'tbtext',
								text:'记录总数:0'
								
							},{
								xtype : 'splitter',
								width : 10
							}, {
								id : 'scd_warn_sum',
								itemId : 'scdwarncount',
								xtype : 'tbtext',
								text : '提醒:0',
								margin : '0 0 10 0'
							}, {
								xtype : 'splitter',
								width : 10
							},{
								id:'scd_publish_sum',
								itemId:'scdpublish',
								xtype:'tbtext',
								text:'处罚:0'
							},{
								xtype:'splitter',
								width:10
							},{
								id:'scd_publishamount_sum',
								itemId:'publishamount',
								xtype:'tbtext',
								text:'处罚分数:0'
							},]
						}
					}]
				} ]
			} ]
		});
		me.callParent(arguments);
	}
});