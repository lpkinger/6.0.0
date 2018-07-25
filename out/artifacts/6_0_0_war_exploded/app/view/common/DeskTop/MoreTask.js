Ext.define('erp.view.common.DeskTop.MoreTask', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{xtype: 'toolbar',  
				region: 'north',  
				anchor: '100% 5%', 
				items : ['->',{
					xtype : 'button',
					text:'发起任务',
					id:'addTask',
					iconCls: 'main-task',
					cls: 'x-btn-gray',
					style: {
						marginRight: '10px'
					},
					width: 100
				}]},{
					xtype : 'erpDeskTabPanel',
					anchor: '100% 95%', 
					bodyStyle : {
						border : 'none'
					},
					bodyBorder : false,
					items : [{
						title : '待办任务',
						anchor : '100% 100%',
						xtype : 'erpDatalistGridPanel',
						id : 'TasktoDo',
						url:'jsps/plm/record/billrecord.jsp?_noc=1',
						keyField:'ra_id',
						pfField:null,	
						firstPage:true,
						caller : 'ResourceAssignment!Bill',
						defaultCondition : 'ra_emid='+ em_id+'  AND ra_taskpercentdone<100 ',
						//dockedItems : [{}],
						showRowNum : false,
						selModel : Ext.create(
								'Ext.selection.CheckboxModel', {
									checkOnly : true,
									headerWidth : 0
								})
					}, {
						title : '已发起任务',
						anchor : '100% 100%',
						xtype : 'erpDatalistGridPanel',
						id : 'TaskDoing',
						url:'jsps/plm/task/task.jsp?_noc=1',
						keyField:'id',	
						pfField:'ra_taskid',
						caller : 'ProjectTask!Bill',
						firstPage:true,
						defaultCondition :'recorder=\'' + em_name + '\'',
						dockedItems : [{
							id : 'pagingtoolbar6',
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
										'erp.view.core.grid.HeaderFilter'											
								), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
					}, {
						title : '已处理任务',
						anchor : '100% 100%',
						xtype : 'erpDatalistGridPanel',
						id : 'TaskDone',
						url:'jsps/plm/record/billrecord.jsp?_noc=1',
						keyField:'ra_id',
						pfField:null,
						caller : 'ResourceAssignment!Bill',
						firstPage:true,
						defaultCondition : 'ra_emid=' + em_id + ' AND ra_taskpercentdone=100 ',									
						dockedItems : [{
							id : 'pagingtoolbar7',
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