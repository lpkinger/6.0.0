Ext.define('erp.view.ma.jprocess.RefreshSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'check-grid',
				anchor: '100% 100%',
				tbar: ['->',{
					cls: 'x-btn-blue',
					id: 'check',
					text: '全部刷新',
					width: 80,
					margin: '0 0 0 50'
				},{
					cls: 'x-btn-blue',
					id: 'close',
					text: $I18N.common.button.erpCloseButton,
					width: 80,
					margin: '0 0 0 5'
				},'->'],
				columns: [{
					text: '刷新项',
					dataIndex: 'value',
					flex: 10
				},{
					text: '',
					dataIndex: 'check',
					flex: 1,
					renderer: function(val, meta, record) {
						meta.tdCls = val;
						return '';
					}
				},{
					text: '',
					dataIndex: 'link',
					flex: 1,
					renderer: function(val, meta, record, x, y, store) {
						var idx = store.indexOf(record);
						meta.style = 'color:blue;text-decoration: underline;cursor: pointer;';
						return '<a href="javascript:Ext.getCmp(\'check-grid\').check(' + idx + ')">优化</a>';
					}
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'action', type: 'string'}, {name: 'value', type: 'string'}, {name: 'detail', type: 'string'}],
					data: [{
						action: 'hr/refreshOrgLevel.action',
						value: '刷新组织层级'
					},{
						action: 'hr/refreshOrgEmployees.action',
						value: '刷新组织人员对照关系'
					},{
						action: 'hr/refreshJobEmployees.action',
						value: '刷新下属岗位人员对照关系'
					},{
						action: 'hr/refreshOrgJobEmployeeTree.action#hr/employee/getHrOrgsTreeAndEmployees.action',
						value: '刷新组织架构树'
					},{
						action: 'common/vastDeployProcess.action',
						value: '流程批量保存——批量保存已启用且包含task节点的流程,请不要重复点击'
					},{
						action: 'common/vastRefreshJnode.action',
						value: '流程节点处理人批量刷新'
					},{
						action: 'hr/employee/vastRefreshPower.action',
						value: '刷新岗位权限、个人权限'
					}]
				}),
				plugins: [{
 		            ptype: 'rowexpander',
 		            rowBodyTpl : [
 		                '<ul>',          
 		                '<li style="margin-left:30px;color:gray;">{detail}</li>',
 		                '</ul>'
 		            ]
 		        }],
 		        selModel: new Ext.selection.CellModel(),
 		        toggleRow: function(record) {
 		        	var rp = this.plugins[0];
 		        	if(rp)
 		        		rp.toggleRow(this.store.indexOf(record));
 		        }
			}] 
		}); 
		me.callParent(arguments); 
	} 
});