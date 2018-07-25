/**
 * grid，tip显示多次回复信息
 */
Ext.define('erp.view.scm.purchase.plugin.Reply', {
	ptype : 'purchasereply',
	constructor : function(cfg) {
		if (cfg) {
			Ext.apply(this, cfg);
		}
	},
	init : function(grid) {
		this.grid = grid;
		var me = this, view = grid.view.normalView || grid.view;
		if (view) {
			view.on({
				scope: me,
				render: me.renderTip,
				uievent: me.currentUI
			});
			grid.on({
				scope: me,
				reconfigure: me.getReply
			});
		}
	},
	renderTip: function(view) {
		if (!view.tip) {
			var me = this;
			view.tip = me.createTip(view);
			view.tip.on({
				beforeshow : function() {
					return view.tip._visible;
				}
			});
		}
	},
	currentUI : function(type, view, cell, recordIndex, cellIndex, e) {
		this.activeIndex = {x: cellIndex, y: recordIndex};
		if(view.tip) {
			if(this.tipStore && this.tipStore.length > 0) {
				var column = view.headerCt.getGridColumns()[this.activeIndex.x];
				if(column && ['pd_qtyreply', 'pd_deliveryreply', 'pd_replydetail'].indexOf(column.dataIndex) > -1) {
					view.tip._visible = true;
					this.updateTipBody(view, view.tip);
					view.tip.show();
				} else {
					view.tip._visible = false;
					view.tip.hide();
				}
			} else {
				view.tip._visible = false;
				view.tip.hide();
			}
		}
	},
	createTip : function(view) {
		return Ext.create('Ext.tip.ToolTip', {
			target : view.el,
			delegate : view.itemSelector,
			trackMouse : true,
			renderTo : Ext.getBody(),
			maxWidth : 580,
			showDelay : 100,
			items : [ {
				xtype : 'grid',
				width : 580,
				columns : [ {
					text : '类型',
					cls : 'x-grid-header-1',
					dataIndex : 'pr_type',
					width : 90
				}, {
					text : '时间',
					cls : 'x-grid-header-1',
					xtype : 'datecolumn',
					dataIndex : 'pr_date',
					format:'Y-m-d',
					width : 90
				}, {
					text : '回复人',
					cls : 'x-grid-header-1',
					dataIndex : 'pr_recorder',
					width : 70
				},{
					text: '数量',
					cls : 'x-grid-header-1',
					xtype : 'numbercolumn',
					align: 'right',
					dataIndex : 'pr_qty',
					width : 70
				}, {
					text : '回复交期',
					cls : 'x-grid-header-1',
					xtype : 'datecolumn',
					dataIndex : 'pr_delivery',
					width : 90
				},{
					text: '备注',
					cls : 'x-grid-header-1',
					dataIndex: 'pr_remark',
					width: 140
				} ],
				columnLines : true,
				store : new Ext.data.Store({
					fields : ['pr_type', { name: 'pr_date', type: 'date', dateFormat: 'Y-m-d'}, 'pr_recorder', 'pr_qty', 'pr_delivery', 'pr_reamrk', 'pr_pucode', 'pr_pddetno' ],
					data : [ {} ]
				})
			} ]
		});
	},
	updateTipBody : function(view, tip) {
		var me = this, record = me.grid.store.getAt(me.activeIndex.y);
		if (record && me.tipStore && me.grid.readOnly) {
			var c = record.get('pd_detno'), data = new Array();
			Ext.each(me.tipStore, function(d) {
				if (d.pr_pddetno == c) {
					data.push(d);
				}
			});
			tip.down('grid').store.loadData(data);
			tip.down('grid').setTitle('行：' + record.get('pd_detno'));
		}
	},
	getReply : function(grid) {
		var me = this;
		me.tipStore = [];
		Ext.defer(function(){
			var idField = Ext.getCmp('pu_id'), statusField = Ext.getCmp('pu_statuscode');
			if(statusField && statusField.getValue() == 'AUDITED') {
				Ext.Ajax.request({
					url : basePath + 'scm/purchase/getReply.action',
					params : {
						id: idField.getValue()
					},
					callback : function(opt, s, r) {
						if (s) {
							var datas = Ext.decode(r.responseText);
							Ext.Array.each(datas, function(d){
								d.pr_date = Ext.Date.format(new Date(d.pr_date), 'Y-m-d');
								d.pr_delivery = Ext.Date.format(new Date(d.pr_delivery), 'Y-m-d');
							});
							me.tipStore = datas;
						}
					}
				});
			}
		}, 200);
	}
});