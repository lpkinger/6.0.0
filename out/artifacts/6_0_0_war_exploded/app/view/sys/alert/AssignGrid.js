Ext.QuickTips.init();

Ext.define('erp.view.sys.alert.AssignGrid',{
	extend : 'Ext.grid.Panel',
	alias : 'widget.assigngrid',
	id:'assignGrid',
	columnLines : true,
    requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit : 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	initComponent : function(){
		var me = this;
		Ext.apply(me, {
			columns: [{
				header: 'ID',
				hidden: true,
				dataIndex: 'aia_id'
			}, {
				header: '实例ID',
				hidden: true,
				dataIndex: 'aia_aiiid'
			}, {
				header: '序号',
				width: 40,
				dataIndex: 'aia_detno'
			}, {
				header: '推送策略',
				dataIndex: 'aia_condition',
				width: 200,
				renderer: function(val,meta,record) {
					conditionSqlWindow = function(index){
						var itemId = Ext.getCmp('aii_itemid').value;
						if(!itemId) {
							showMessage('提示', '请先选择预警项目!', 1000);
							return;
						}
						var win = Ext.create('erp.view.sys.alert.ConditionSqlWindow',{
							storeIndex: index
						});
						win.show();
					}
					return '<span style="display:inline-block;padding-left:2px; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;width: 80%;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="conditionSqlWindow(\''+record.get('aia_detno')+'\')"></span>';
				}
			}, {
				header: '指定推送人',
				dataIndex: 'aia_mans',
				width: 200,
				renderer: function(val,meta,record) {
					assignSelectWindow = function(index){
						var win = Ext.create('erp.view.sys.alert.AssignSelectWindow',{
							storeIndex: index
						});
						win.show();
					}
					return '<span style="display:inline-block;padding-left:2px; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;width: 80%;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="assignSelectWindow(\''+record.get('aia_detno')+'\')"></span>';
				}
			}, {
				header: '指定推送人编码',
				dataIndex: 'aia_mancode',
				hidden: true
			}, {
				header: '条件推送人',
				dataIndex: 'aia_mansql',
				width: 200,
				renderer: function(val,meta,record) {
					assignSqlWindow = function(index){
						var win = Ext.create('erp.view.sys.alert.AssignSqlWindow',{
							storeIndex: index
						});
						win.show();
					}
					return '<span style="display:inline-block;padding-left:2px; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;width: 80%;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="assignSqlWindow(\''+record.get('aia_detno')+'\')"></span>';
				}
			}],
			store : Ext.create('Ext.data.Store', {
				fields: ['aia_id', 'aia_detno', 'aia_mans', 'aia_mancode', 'aia_mansql', 'aia_condition'],
				data:[]
			})
		});
		me.callParent(arguments);
	},
	detno: 'aia_detno',
	keyField: 'aia_id',
	bbar: {xtype: 'erpToolbar'},
	loadData: function(instanceId) {
		var me = this;
		Ext.Ajax.request({
			url : basePath + 'sys/alert/getAssign.action',
			params : {
				instanceId: instanceId
			},
			method : 'post',
			callback : function(options,success,response){
				if(success) {
					var result = JSON.parse(response.responseText);
					if(result.success) {
						var grid = Ext.getCmp('assignGrid');
						grid.getStore().loadData(result.data);
					}
				}
			}
		});
	}
});