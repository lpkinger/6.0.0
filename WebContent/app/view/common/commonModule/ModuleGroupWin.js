Ext.define('erp.view.common.commonModule.ModuleGroupWin', {
	extend: 'Ext.window.Window',
	alias: 'widget.modulegroupwin',
	title: '常用功能分组',
	width: 680,
	height: 365,
	layout: 'fit',
	modal: true,
	initComponent: function() {
		var me = this;
		me.centerpanel = Ext.getCmp('centerpanel');
		deleteGroup = function(rowIdx) {
			Ext.Msg.confirm('提示', '确认删除?', function(yes) {
				if(yes === 'yes') {
					var grid = Ext.getCmp('modulegroupgrid');
					var store = grid.getStore();
					store.removeAt(rowIdx);
					store.data.each(function(d, i) {
						d.set('index', i+1);
						d.dirty=false;
						d.commit();
					});
				}
			});
		};
		var expandedStore = Ext.create('Ext.data.Store', {
            fields: ['value', 'name'],
            data: [
                { "value": true, "name": "是" },
                { "value": false, "name": "否" }
            ]
        });
        var expandedCombo = new Ext.form.ComboBox({
            store: expandedStore,
            valueField: 'value',
            displayField: 'name',
            mode: 'local',
            forceSelection: true,
            editable: false,
            triggerAction: 'all',
            selectOnFocus: true
        });
        changeExpanded = function(rowIdx, colIdx) {
        	var grid = Ext.getCmp('modulegroupgrid');
        	var record = grid.getStore().data.items[rowIdx];
        	record.set('expanded', !record.get('expanded'));
        	record.dirty=false; 
			record.commit();
        }
		Ext.apply(me, {
			items: [{
				xtype: 'grid',
				id: 'modulegroupgrid',
				sortableColumns:false,
				store: Ext.create('Ext.data.Store',{
					fields: ['cuid', 'id', 'index', 'text', 'expanded'],
					data: me.groupData
				}),
				columns: [{
					header: '序号',
					dataIndex: 'index',
					hidden: true,
					flex: 1
				}, {
					header: '分组名称',
					dataIndex: 'text',
					editor: {
						xtype: 'textfield',
						allowBlank: false,
						blankText: '分组名称不能为空'
					},
					flex: 3
				}, {
					header: '默认展开',
					dataIndex: 'expanded',
					renderer: function(value, meta, record, rowIdx, colIdx) {
	                    return '<div class="x-checker-'+(value?'on':'off')+'" style="margin: 0 auto;margin-top:6px;" onClick="changeExpanded('+rowIdx+','+colIdx+')">&nbsp;</div>'
	                },
					flex: 1
				}, {
					header: '操作',
					align: 'center',
					renderer: function(value, el, record, rowIdx, colIdx) {
						if(record.get('id') != '-1') {
							return '<a onClick="deleteGroup(\''+rowIdx+'\')" style="cursor:pointer;color:red;text-decoration:underline;">删除</a>'
						}
					}
				}],
				enableDragDrop:  true,
				listeners: {
					beforeedit: function(e, eOpts) {
						return e.record.get('id')!='-1';
					},
					edit: function(editor, e, eOpts) {
						var rowIdx = e.rowIdx;
						var rc = e.record;
						rc.dirty=false; 
						rc.commit();
					}
				},
				viewConfig:{
					plugins:{
						ptype:'gridviewdragdrop',
						dragText:'请移动到表格'
					},
					listeners:{
						beforedrop: function( node, data, overModel, dropPosition, dropFunction, eOpts) {
							// 控制[未分组]组别始终在最后
							if(data.records[0].get('id') == -1) {
								return false;
							}
							if(overModel.get('id') == '-1' && dropPosition === 'after') {
								return false;
							}else {
								return true;
							}
						},
						
						drop:function( node, data, overModel, dropPosition, eOpts ){
							var grid = Ext.getCmp('modulegroupgrid');
							var store = grid.getStore();
							for(var i = 0; i < store.getCount()-1; i++) {
								store.getAt(i).set('index',i);
								store.getAt(i).dirty=false;
								store.getAt(i).commit();
							}
						}
					}
				},
				plugins: [
			        Ext.create('Ext.grid.plugin.CellEditing', {
			            clicksToEdit: 1
			        })
			    ],
				tbar: [{
					xtype: 'button',
					text: '+添加分组',
					handler: function() {
						var grid = Ext.getCmp('modulegroupgrid');
						var store = grid.getStore();
						store.insert(0,{
							index: 0,
							text: '新分组'
						});
						for(var i = 0; i < store.getCount(); i++) {
							store.getAt(i).set('index',i);
							store.getAt(i).dirty=false;
							store.getAt(i).commit();
						}
						grid.getPlugin().startEditByPosition({row: 0, column: 1});
					}
				}, '->', '<a style="font-size:12px;color:red;">[未分组]<a>项将始终排在最后&nbsp;&nbsp;']
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'button',
				text: '确定',
				handler: function() {
					var groupData = me.getGroupData();
					me.centerpanel.importGroupData(groupData);
					me.close();
				}
			}, {
				xtype: 'button',
				text: '取消',
				handler: function() {
					me.close();
				}
			}]
		});
		me.callParent(arguments);
	},
	getGroupData: function() {
		var me = this;
		var grid = me.down('grid');
		var gridData = grid.getStore().data.items.map(function(d){return d.data;});
		var groupData = [];
		Ext.Array.each(gridData, function(s, i) {
			var g = me.centerpanel.getGroupByID(s.id);
			Ext.id();
			var d = new Date().getTime()+'';
			var id = Ext.idSeed+Number(d.substr(d.length-3));
			var group = {
				group: true,
				id: id,
				index: i,
				items: [],
				text: s.text,
				expanded: s.expanded
			};
			if(g) {
				group = g;
				group.expanded = s.expanded;
				group.text = s.text;
			}
			groupData.push(group);
		});
		return groupData;
	}
});