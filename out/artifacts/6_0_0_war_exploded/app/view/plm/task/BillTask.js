Ext.define('erp.view.plm.task.BillTask', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 100%',
				saveUrl : 'plm/task/saveTask.action',
				updateUrl : 'plm/task/updateTask.action',
				submitUrl : 'plm/task/submitTask.action',
				resSubmitUrl : 'plm/task/resSubmitTask.action',
				auditUrl : 'plm/task/auditTask.action',
				getIdUrl : 'common/getId.action?seq=PROJECTTASK_SEQ',
				keyField : 'id',
				codeField : 'taskcode'
			} ]
		});
		me.callParent(arguments);
	}
});