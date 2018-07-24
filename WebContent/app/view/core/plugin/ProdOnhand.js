/**
 * grid，tip显示物料分仓库存
 */
Ext.define('erp.view.core.plugin.ProdOnhand', {
	ptype : 'prodonhand',
	constructor : function(cfg) {
		if (cfg) {
			Ext.apply(this, cfg);
		}
	},
	prodKey: 'mm_prodcode',
	init : function(grid) {
		this.grid = grid;
		var me = this, view = grid.view.normalView || grid.view;
		if (view) {
			view.on({
				scope: me,
				render: me.renderTip
			})
			var views = grid.view.lockedView;
			if(views){
				views.on({
					scope: me,
					render: me.renderTip
				})
			}
			grid.store.on({
				scope: me,
				load: me.getProductWh
			});
		}
	},
	renderTip: function(view) {
		if (!view.tip) {
			var me = this;
			view.tip = me.createTip(view);
			view.tip.on({
				beforeshow : function() {
					me.updateTipBody(view, view.tip);
				}
			});
		}
	},
	createTip : function(view) {
		return Ext.create('Ext.tip.ToolTip', {
			target : view.el,
			delegate : view.itemSelector,
			trackMouse : true,
			renderTo : Ext.getBody(),
			maxWidth :500,
			items : [{
				xtype : 'grid',
				width : 382,
				columns : [ {
					text : '仓库编号',
					cls : 'x-grid-header-1',
					dataIndex : 'PW_WHCODE',
					width : 80
				},{
					text : '仓库名称',
					cls : 'x-grid-header-1',
					dataIndex : 'WH_DESCRIPTION',
					width : 120
				}, {
					text : '库存',
					cls : 'x-grid-header-1',
					xtype : 'numbercolumn',
					dataIndex : 'PW_ONHAND',
					width : 90
				}, {
					text : '可用库存',
					cls : 'x-grid-header-1',
					xtype : 'numbercolumn',
					dataIndex : 'FREEONHAND',
					width : 90
				} ],
				columnLines : true,
				title : '物料分仓2库存',
				store : new Ext.data.Store({
					fields : [ 'PW_WHCODE', 'WH_DESCRIPTION', 'PW_ONHAND', 'FREEONHAND' ],
					data : [ {} ]
				})
			} ]
		});
	},
	updateTipBody : function(view, tip) {
		var me = this, record = view.getRecord(tip.triggerElement);
		if (record && me.productWh) {
			var c = record.get(me.prodKey), pws = new Array();
			Ext.each(me.productWh, function(d) {
				if (d.PW_PRODCODE == c) {
					pws.push(d);
				}
			});
			tip.down('grid').setTitle(c);
			tip.down('grid').store.loadData(pws);
		}
	},
	getProductWh : function() {
		var me = this, codes = [];
		me.grid.store.each(function(d) {
			var p = d.get(me.prodKey);
			if(!Ext.isEmpty(p))
				codes.push("'" + p + "'");
		});
		if(codes.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'scm/product/getProductwh.action',
				params : {
					codes : codes.join(','),
					//useFactory:me.grid.ifOnlyShowUserFactoryWh||false,//根据登录用户所属工厂获取对应仓库分仓库存
					caller:caller
				},
				callback : function(opt, s, r) {
					if (s) {
						var rs = Ext.decode(r.responseText);
						if (rs.data) {
							me.productWh = rs.data;
						}
					}
				}
			});
		} else {
			me.productWh = [];
		}
	}
});