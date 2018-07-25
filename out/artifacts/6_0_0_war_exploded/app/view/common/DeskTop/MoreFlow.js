Ext.define('erp.view.common.DeskTop.MoreFlow', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				xtype : 'erpDeskTabPanel',				
				bodyStyle : {
					border : 'none'
				},
				bodyBorder : false,
				items : [{
					title : '待办流程',
					anchor : '100% 100%',
					xtype : 'erpDatalistGridPanel',
					id : 'toDo',
					url:'jsps/common/jprocessDeal.jsp',
					caller : 'JProcess!Me',
					firstPage:true,
					keyField:'jp_nodeId',
					pfField:null,					
					defaultCondition : 'JP_NODEDEALMAN=\''
						+ em_code + '\' and jp_status=\''
						+ "待审批" + '\'',
						//dockedItems : [{}],
						showRowNum : false,
						selModel : Ext.create(
								'Ext.selection.CheckboxModel', {
									checkOnly : true,
									headerWidth : 0
								})
				}, {
					tbar : ['->',{
						xtype:'checkbox',
						name:'only_todo',
						boxLabel:'仅显示待审批流程',
						labelAlign:'left',
						inputValue:1							
					},{
						xtype : 'erpProcessRemindButton',
						style: {
        		    		marginRight: '10px',
        		    		marginLeft: '10px'
        		        }
					}],
					title : '已发起流程',
					anchor : '100% 100%',
					xtype : 'erpDatalistGridPanel',
					id : 'alreadyLaunch',
					caller : 'JProcess!Launch',
					firstPage:true,
					keyField:'jp_nodeId',
					pfField:null,	
					defaultCondition : 'jp_launcherid=\''
						+ em_code + '\'',
						dockedItems : [{
							id : 'pagingtoolbar4',
							xtype: 'erpDatalistToolbar',
							dock: 'bottom',
							displayInfo: true
						}],
						showRowNum : false,
						selModel : Ext.create(
								'Ext.selection.CheckboxModel', {
									checkOnly : true,
									headerWidth : 30
								}),
								plugins : [Ext.create(
										'erp.view.core.grid.HeaderFilter'											
								), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				}, {
					title : '已处理流程',
					anchor : '100% 100%',
					xtype : 'erpDatalistGridPanel',
					id : 'alreadyDo',
					caller : 'JProcess!Done',
					url:'jsps/common/jprocessDeal.jsp?_do=1',
					firstPage:true,
					keyField:'jp_nodeId',
					pfField:null,
					defaultCondition : 'JP_NODEDEALMAN=\''
						+ em_code + '\' and jp_status<>\''
						+ "待审批" + '\'',
						dockedItems : [{
							id : 'pagingtoolbar5',
							xtype: 'erpDatalistToolbar',
							dock: 'bottom',
							displayInfo: true
						}],
						showRowNum : false,
						selModel : Ext.create(
								'Ext.selection.CheckboxModel', {
									checkOnly : true,
									headerWidth : 0
								}),
								plugins : [Ext.create(
								'erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				}]
			}]
		});
		me.callParent(arguments);
	}

});