Ext.define('erp.view.core.button.UUListener', {
	extend : 'Ext.Button',
	alias : 'widget.erpUUListenerButton',
	iconCls : 'x-btn-uu-medium',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpUUListenerButton,
	style : {
		marginLeft : '10px'
	},
	width : 80,
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function(btn) {
		btn.showUUWin();
	},
    showUUWin: function() {
    	var me = this;
    	if(!me.uulistener) {
			Ext.Ajax.request({
				url: basePath + 'ma/uu/getUU.action',
				params: {
					formCondition: formCondition
				},
				async: false,
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.success) {
						me.uulistener = rs.uulistener;
					}
				}
			});
		}
		var data = me.uulistener;
		var cal = Ext.getCmp('fo_caller').value;
		if(!data || data.length == 0) {
			data = new Array();
			for(var i = 0;i < 10; i++ ) {
				data.push({uu_caller: cal, uu_ftype: 2});
			}
		}
		var g = Ext.getCmp('grid'),fields = new Array();
		g.store.each(function(item){
			if(item.get('deploy') == true) {
				fields.push({display: item.get('fd_field'), value: item.get('fd_field')});
			}
		});
		Ext.create('Ext.Window', {
			height: 500, 
			width: 600,
			title: 'UU字段',
			layout: 'anchor',
			modal: true,
			items: [{
				anchor: '100% 100%',
				xtype: 'gridpanel',
				store: new Ext.data.Store({
					fields: [{name: 'uu_id', type: 'number'}, 'uu_caller', 'uu_field', 'uu_ftype'],
					data: data
				}),
				columnLines: true,
				columns: [{
					dataIndex: 'uu_id',
					hidden: true
				},{
					dataIndex: 'uu_caller',
					hidden: true
				},{
					dataIndex: 'uu_field',
					flex: 1,
					text: '字段',
					xtype: 'combocolumn',
					editor: {
						xtype: 'combo',
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						editable: false,
						store: {
							fields: ['display', 'value'],
							data: fields
						}
					},
					renderer: function(val, meta, record, x, y, store, view) {
						var grid = view.ownerCt, cn = grid.columns[y], r = val;
						if(cn.editor) {
							Ext.Array.each(cn.editor.store.data, function(){
								if(val == this.value)
									r = this.display;
							});
						}
						return r;
					}
				},{
					dataIndex: 'uu_ftype',
					flex: 0.6,
					text: '类型',
					xtype: 'combocolumn',
					editor: {
						xtype: 'combo',
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						editable: false,
						value: 2,
						store: {
							fields: ['display', 'value'],
							data: me.uuset
						}
					},
					renderer: function(val, meta, record, x, y, store, view) {
						var r = val;
						Ext.Array.each(me.uuset, function(){
							if(val == this.value)
								r = this.display;
						});
						return r;
					}
				},{
					dataIndex: 'deleted',
					flex: 0.3,
					renderer: function(val, meta, record, x, y, store, view) {
						var grid = view.ownerCt,
							idx = store.indexOf(record);
						return '<a href="javascript:Ext.getCmp(\'' + grid.id + '\').store.getAt(' + 
							idx + ').set(\'deleted\', true);">删除</a>';
					}
				}],
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    })],
				listeners: {
					itemclick: function(selModel, record) {
						var grid = selModel.ownerCt,
							store = grid.store,
							len = store.data.length;
						if(store.indexOf(record) == len - 1) {
							var d = new Array();
							for(var i = 0;i < 5; i++ ) {
								d.push({uu_caller: cal, uu_ftype: 2});
							}
							store.add(d);
						}
					}
				},
				viewConfig: { 
			        getRowClass: function(record) { 
			            return record.get('deleted') ? 'deleted' : '';
			        } 
			    }
			}],
			buttonAlign: 'center',
			buttons: [{
				text: $I18N.common.button.erpSaveButton,
				cls: 'x-btn-blue',
				handler: function(btn) {
					me.saveUUListener(btn.ownerCt.ownerCt.down('gridpanel'));
				}
			},{
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-blue',
				handler: function(btn) {
					btn.ownerCt.ownerCt.close();
				}
			}]
		}).show();
    },
    uuset: [{
		display: '员工ID',
		value: 0
	},{
		display: '员工编号',
		value: 1
	},{
		display: '员工姓名',
		value: 2
	},{
		display: '员工UU',
		value: 3
	},{
		display: '供应商ID',
		value: 4
	},{
		display: '供应商编号',
		value: 5
	},{
		display: '供应商名称',
		value: 6
	},{
		display: '供应商UU',
		value: 7
	},{
		display: '客户ID',
		value: 8
	},{
		display: '客户编号',
		value: 9
	},{
		display: '客户名称',
		value: 10
	},{
		display: '客户UU',
		value: 11
	}],
	saveUUListener: function(grid) {
		var me = this, data = new Array();
		grid.store.each(function(item){
			if(item.get('deleted')) {
				if(item.get('uu_id') > 0)
					data.push({uu_id: -item.get('uu_id'), uu_caller: item.get('uu_caller')});
			} else if (item.dirty && !Ext.isEmpty(item.get('uu_field'))) {
				data.push(item.data);
			}
		});
		if(data.length > 0) {
			Ext.Ajax.request({
				url: basePath + 'ma/uu/saveUU.action',
				params: {
					data: Ext.encode(data)
				},
				callback: function(opt, s, r) {
					var rs = Ext.decode(r.responseText);
					if(rs.success) {
						grid.ownerCt.close();
						me.uulistener = null;
					}
				}
			});
		}
	}
});