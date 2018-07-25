Ext.require(['Gnt.plugin.TaskContextMenu',
             'Sch.plugin.TreeCellEditing',
             'Sch.plugin.Pan',
             'Gnt.panel.Gantt',
             'Gnt.column.PercentDone',
             'Gnt.column.StartDate',
             'Gnt.column.EndDate',
             'Gnt.plugin.Printable',
             'Gnt.widget.AssignmentCellEditor',
             'Gnt.column.ResourceAssignment',
             'Gnt.model.Assignment',
             'erp.util.BaseUtil',
             'Gnt.widget.Calendar'
             ]);
Ext.define('erp.view.plm.task.gantt', {
	extend : 'Ext.Viewport',
	layout  : 'border',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
				region      : 'north',
				contentEl   : 'north',
				bodyStyle   : 'padding:0px'
			},{
				xtype:'ganttpanel',
				ganttConf:!hideToolBar,
				dockedItems: [{
					xtype: 'gantt_toolbar'
				}]
			}]
		});
		me.callParent(arguments);
	}
});