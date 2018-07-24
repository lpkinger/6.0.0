/**
 * grid，tip显示多次回复信息
 */
Ext.define('erp.view.scm.purchase.plugin.InquiryReply', {
	ptype : 'inquiryreply',
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
		var flag=true;
		Ext.each(this.grid.columns,function(c){
			if(c.dataIndex=='id_price'&&c.hidden){//单价隐藏时不显示分段数量窗口
				flag=false;
			}
		});
		this.activeIndex = {x: cellIndex, y: recordIndex};
		if(view.tip) {
			if(!flag){//单价隐藏时但系统时间大于报价截止时间时显示分段数量窗口
				var record = this.grid.store.getAt(this.activeIndex.y);
				if(record.data.vendquoted!=undefined &&record.data.vendquoted!='未报价'&&record.data.vendquoted!='已报价'){
					flag=true;
				}
			}
			if(this.tipStore && this.tipStore.length > 0) {
				var column = view.headerCt.getGridColumns()[this.activeIndex.x];
				if(column && ['id_lapqty', 'id_price'].indexOf(column.dataIndex) > -1) {
					if(flag){
						view.tip._visible = true;
						this.updateTipBody(view, view.tip);
						view.tip.show();
					}else{
						view.tip._visible = false;
						view.tip.hide();
					}
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
			width : 250,
			showDelay : 100,
			items : [ {
				xtype : 'grid',
				width : 250,
				columns : [ {
					text : '分段数量',
					cls : 'x-grid-header-1',
					dataIndex : 'idd_lapqty',
					xtype : 'numbercolumn',
					align: 'right',
					width : 130
				},{
					text: '单价',
					cls : 'x-grid-header-1',
					xtype : 'numbercolumn',
					align: 'right',
					format : '0,000.0000',
					dataIndex : 'idd_price',
					width : 110
				} ],
				columnLines : true,
				store : new Ext.data.Store({
					fields : ['idd_lapqty',  'idd_price' ],
					data : [ {} ]
				})
			} ]
		});
	},
	updateTipBody : function(view, tip) {
		var me = this, record = me.grid.store.getAt(me.activeIndex.y);
		if (record && me.tipStore && me.grid.readOnly) {
			var c = record.get('id_id'), data = new Array();
			Ext.each(me.tipStore, function(d) {
				if (d.idd_idid == c) {
					data.push(d);
				}
			});
			tip.down('grid').store.loadData(data);
			tip.down('grid').setTitle('行：' + record.get('id_detno'));
		}
	},
	getReply : function(grid) {
		var me = this;
		me.tipStore = [];
		Ext.defer(function(){
			var idField = Ext.getCmp('in_id'), statusField = Ext.getCmp('in_statuscode');
			if(statusField && statusField.getValue() == 'AUDITED') {
				Ext.Ajax.request({
					url : basePath + 'scm/inquiry/getReply.action',
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