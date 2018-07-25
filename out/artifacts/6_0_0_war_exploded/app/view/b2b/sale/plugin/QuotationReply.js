/**
 * grid，tip显示多次回复信息
 */
Ext.define('erp.view.b2b.sale.plugin.QuotationReply', {
	ptype : 'quotationreply',
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
				if(column && ['qd_lapqty', 'qd_price'].indexOf(column.dataIndex) > -1) {
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
			maxWidth : 250,
			showDelay : 100,
			items : [ {
				xtype : 'grid',
				width : 250,
				columns : [{
					text: '分段数量',
					cls : 'x-grid-header-1',
					align: 'right',
					dataIndex : 'qdd_lapqty',
					width : 110
				} ,{
					text: '单价',
					cls : 'x-grid-header-1',
					xtype : 'numbercolumn',
					align: 'right',
					dataIndex : 'qdd_price',
					width : 110
				}],
				columnLines : true,
				store : new Ext.data.Store({
					fields : ['qdd_lapqty',  'qdd_price' ],
					data : [ {} ]
				})
			} ]
		});
	},
	updateTipBody : function(view, tip) {
		var me = this, record = me.grid.store.getAt(me.activeIndex.y);
		if (record && me.tipStore) {
			var c = record.get('qd_id'), data = new Array();
			Ext.each(me.tipStore, function(d) {
				if (d.qdd_qdid == c) {
					data.push(d);
				}
			});
			tip.down('grid').store.loadData(data);
			tip.down('grid').setTitle('行：' + record.get('qd_detno'));
		}
	},
	getReply : function(grid) {
		var me = this;
		me.tipStore = [];
		Ext.defer(function(){
			var idField = Ext.getCmp('qu_id');
			if(idField && idField.getValue() > 0) {
				Ext.Ajax.request({
					url : basePath + 'b2b/sale/zdquotation/getReply.action',
					params : {
						id: idField.getValue()
					},
					callback : function(opt, s, r) {
						if (s) {
							var datas = Ext.decode(r.responseText);								
							me.tipStore = datas;
						}
					}
				});
			}
		}, 200);
	}
});