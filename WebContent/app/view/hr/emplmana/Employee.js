Ext.define('erp.view.hr.emplmana.Employee', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 40%',
				saveUrl : 'hr/emplmana/saveEmployee.action',
				deleteUrl : 'hr/emplmana/deleteEmployee.action',
				updateUrl : 'hr/emplmana/updateEmployee.action',
				auditUrl : 'hr/employee/auditEmployee.action',
				submitUrl : 'hr/employee/submitEmployee.action',
				resSubmitUrl : 'hr/employee/resSubmitEmployee.action',
				resAuditUrl : 'hr/employee/resAuditEmployee.action',
				getIdUrl : 'common/getId.action?seq=Employee_SEQ',
				keyField : 'em_id',
				statusField : 'em_statuscode'
			}, {
				xtype: 'gridpanel',
				id: 'grid',
				anchor : '100% 60%',
				title: '兼职信息设置',
				columns: [{
					text: '岗位名称',
					width: 160,
					dataIndex: 'jo_name',
					editor: {
						xtype: 'dbfindtrigger'
					},
					dbfind: 'Job|jo_name',
					ignore: true
				},{
					text: '岗位描述',
					width: 200,
					dataIndex: 'jo_description',
					ignore: true
				},{
					text: '组织ID',
					width: 0,
					dataIndex: 'or_id',
					ignore: true
				},{
					text: '组织信息',
					width: 140,
					dataIndex: 'or_name',
					editor: {
						xtype: 'dbfindtrigger'
					},
					dbfind: 'HrOrg|or_name',
					ignore: true
				},{
					xtype: 'actioncolumn',
					text: '操作', 
					width: 100,
					align: 'center',
					items: [{
						icon: basePath + 'resource/images/16/delete.png',
						tooltip: '删除',
						handler: function(view, rowIndex, colIndex) {
							var rec = view.getStore().getAt(rowIndex);
							view.ownerCt.deleteRecord(rec);
						}
					}]
				},{
					text: '&nbsp;',
					hidden: true,
					dataIndex: 'jo_id'
				}],
				necessaryField: 'jo_id',
				GridUtil: Ext.create('erp.util.GridUtil'),
				dbfinds: [{
					field: 'or_id',
					dbGridField: 'or_id'
				},{
					field: 'or_name',
					dbGridField: 'or_name'
				},{
					field: 'or_id',
					dbGridField: 'jo_orgid'
				},{
					field: 'or_name',
					dbGridField: 'jo_orgname'
				},{
					field: 'jo_name',
					dbGridField: 'jo_name'
				},{
					field: 'jo_description',
					dbGridField: 'jo_description'
				},{
					field: 'jo_id',
					dbGridField: 'jo_id'
				}],
				columnLines: true,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    })],
				store: new Ext.data.Store({
					fields: ['jo_name', 'jo_description', 'jo_id','or_id', 'or_name'],
					data: [{},{},{},{},{}]
				}),
				deleteRecord: function(record) {
					var empId = Ext.getCmp('em_id').getValue();
					if(empId != null && empId > 0 && record.get('jo_id') > 0) {
						var grid = this, url = "hr/emplmana/deleteExtraJob.action";
						grid.setLoading(true);
						Ext.Ajax.request({
							url : basePath + url,
							params: {
							   	caller: caller,
							   	empId: empId,
							   	jobId: record.get('jo_id')
							},
							method : 'post',
							callback : function(opt, success, response){
								grid.setLoading(false);
								success && grid.store.remove(record);
							}
						});
					} else {
						this.store.remove(record);
					}
				}
			}]
		});
		me.callParent(arguments);
	}
});