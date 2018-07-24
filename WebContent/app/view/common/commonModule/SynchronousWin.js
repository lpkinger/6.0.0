Ext.define('erp.view.common.commonModule.SynchronousWin', {
	extend: 'Ext.window.Window',
	alias: 'widget.synchronousWin',
	title: '同步到其他账套',
	width: 500,
	height: 300,
	layout: 'fit',
	id: 'synchronousWin',
	closeAction: 'hide',
	items: [{
		xtype: 'grid',
		autoScroll: true,
		selModel: Ext.create('Ext.selection.CheckboxModel',{
			mode:"SIMPLE"
		}),
		columns: [{
			text:'账套名称',
			flex:1,
			dataIndex:'ma_function'
		}, {
			text:'账套编号',
			flex:1,
			dataIndex:'ma_user'
		}],
		store: Ext.create('Ext.data.Store', {
			fields: ['ma_id', 'ma_name', 'ma_user', 'ma_function'],
			proxy: {
				type: 'ajax',
				url: basePath + 'common/getAbleMasters.action',
				method: 'GET',
				extraParams  : {                           
		            isOwnerMaster: true 
		        }, 
				reader: {
					type: 'json',
					root: 'masters'
				}
			},
			autoLoad: true,
			listeners: {
				load: function(store, records, successful, operation, eOpts ) {
					Ext.Array.each(records, function(record) {
						if(record) {
							
						}
					});
					for(var i = 0; i < records.length; i++) {
						var record = records[i];
						if(record.get('ma_user') == sob) {
							store.removeAt(i);
						}
					}
				}
			}
		})
	}],
	initComponent : function(){
		var me = this;
		Ext.apply(me, {
			buttonAlign: 'center',
			buttons: [{
				xtype: 'button',
				text: '确定',
				handler: function() {
					var grid = me.down('grid');
					var selectedData = grid.getSelectionModel().getSelection();
					if(selectedData.length > 0) {
						me.fireEvent('onSynchronous', selectedData);
						me.close();
					}else {
						showMessage('提示', '请先勾选账套.', 1000);
					}
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
	}
})