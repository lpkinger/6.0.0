Ext.define('erp.view.ma.SysNavigationCopy',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpSysTreeGrid',
				anchor: '100% 100%'
			},{
				xtype: 'grid',
				anchor: '100% 25%',
				plugins: Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }),
				store: Ext.create('Ext.data.Store', {
					fields: ['sn_id', 'sn_displayname', {name: 'sn_detno', type: 'int'}]
				}),
				columnLines: true,
				columns: [{
					hidden: true,
					dataIndex: 'sn_id'
				},{
					flex: 1,
					text: '描述',
					cls: 'x-grid-header-1',
					dataIndex: 'sn_displayname',
					editor: {
						xtype: 'textfield'
					},
					align: 'center',
					renderer: function(val, meta, record, x, y, store, view){
						var tree = view.ownerCt.ownerCt.down('erpSysTreeGrid'),
							node = tree.store.getNodeById(record.data.sn_id);
						if(node) {
							node.set('text', val);
						}
						return val;
					}
				},{
					flex: 0.3,
					text: '序号',
					cls: 'x-grid-header-1',
					dataIndex: 'sn_detno',
					align: 'center',
					editor: {
						xtype: 'numberfield',
						hideTrigger: true
					}
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});