Ext.define('erp.view.ma.Grid', {
	extend : 'Ext.Viewport',
	layout : 'border',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				region: 'east',
				xtype: 'container',
				width: '25%',
				height: '100%',
				layout: 'border',
				items: [{
					region: 'north',
					title: '基本信息',
					height: 280,
					maxHeight: 320,
					split: true,
					xtype: 'form',
					collapsible: true,
					cls: 'custom',
					layout: 'column',
					defaults: {
						xtype: 'textfield',
						labelAlign: 'top',
						columnWidth: 1,
						margin: '3 3 3 3'
					},
					items: [{
						fieldLabel: 'Caller',
						readOnly: true,
						value: whoami
					},{
						fieldLabel: '表名',
						name: 'fo_detailtable'
					},{
						fieldLabel: '默认条件',
						name: 'fo_detailcondition',
						xtype: 'textarea',
						height: 80
					},{
						fieldLabel: '默认排序',
						name: 'fo_detailgridorderby'
					}]
				},{
					xtype: 'form',
					id: 'detailset',
					title: '列信息',
					cls: 'custom',
					region: 'center',
					width: '100%',
					layout: 'column',
					defaults: {
						xtype: 'textfield',
						labelAlign: 'top',
						columnWidth: 1,
						margin: '3 3 3 3'
					},
					items: [{
						fieldLabel: '描述',
						name: 'dg_caption'
					},{
						fieldLabel: '序号',
						name: 'dg_sequence',
						columnWidth: .3
					},{
						fieldLabel: '字段',
						name: 'dg_field',
						columnWidth: .7
					},{
						fieldLabel: '宽度',
						name: 'dg_width',
						columnWidth: .3
					},{
						fieldLabel: '类型',
						name: 'dg_type',
						columnWidth: .7
					},{
						fieldLabel: '逻辑',
						name: 'dg_logictype'
					},{
						fieldLabel: 'Render',
						name: 'dg_renderer'
					},{
						fieldLabel: '合计类型',
						name: 'dg_summarytype',
						xtype: 'combo',
						displayField: 'display',
						valueField: 'value',
						queryMode: 'local',
						store: {
							fields: ['display', 'value'],
							data: [{
								display: '求和', value: 'sum'
							},{
								display: '平均值', value: 'average'
							},{
								display: 'Count', value: 'count'
							}]
						}
					}],
					buttonAlign: 'center',
					buttons: [{
						text: '提交到Grid',
						height: 30
					}]
				}]
			}, {
				region: 'center',
				xtype: 'container',
				id: 'grid',
				layout: 'fit'
			}]
		});
		me.callParent(arguments);
		me.getFormSet(function(formParam){
			
		});
		me.getGridSet(function(gridParam){
			me.down('#grid').add(me.createGrid(gridParam));
		});
	},
	getFormSet: function(fn) {
		var me = this;
		Ext.Ajax.request({
        	url : basePath + 'common/singleFormItems.action',
        	params: {
        		caller: 'Form',
        		condition: 'fo_caller=\'' + whoami + '\''
        	},
        	method : 'post',
        	callback : function(opt, s, r){
        		var res = new Ext.decode(r.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			fn.call(me, res);
        		}
        	}
		});
	},
	getGridSet: function(fn) {
		var me = this;
		Ext.Ajax.request({
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: 'DetailGrid',
        		condition: 'dg_caller=\'' + whoami + '\''
        	},
        	method : 'post',
        	callback : function(opt, s, r){
        		var res = new Ext.decode(r.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(res.columns){
        			fn.call(me, res);
        		}
        	}
		});
	},
	createGrid: function(param) {
		var data = (param.data && Ext.decode(param.data.replace(/,}/g, '}').replace(/,]/g, ']'))) 
			|| [], store = Ext.create('Ext.data.Store', {
				fields: param.fields,
				data: data
		    });
		return Ext.create('Ext.grid.Panel', {
			width: '100%',
			height: '100%',
			BaseUtil: Ext.create('erp.util.BaseUtil'),
			RenderUtil: Ext.create('erp.util.RenderUtil'),
			columns: param.columns,
			store: store,
			columnLines: true,
			buttonAlign: 'center',
			buttons: [{
				text: $I18N.common.button.erpSaveButton,
				height: 30
			}, {
				text: $I18N.common.button.erpCloseButton,
				height: 30
			}],
			listeners: {
                selectionchange: function(model, records) {
                    if (records[0]) {
                        this.ownerCt.ownerCt.down('#detailset').getForm().loadRecord(records[0]);
                    }
                }
            }
		});
	}
});