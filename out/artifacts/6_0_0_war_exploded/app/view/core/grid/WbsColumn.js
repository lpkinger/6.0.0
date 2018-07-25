Ext.define("erp.view.core.grid.WbsColumn", {
			extend : "Ext.grid.column.Column",
			alias : "widget.wbscolumn",
			width : 40,
			align : "left",
			text:'编号',
			dataIndex : "tt_index",
			renderer : function(f, g, b, h, d, e) { 
				var a = e.getRootNode(), c = [];
				while (b !== a) {
					c.push(b.data.index + 1);
					b = b.parentNode
				}
				return c.reverse().join(".")
			},
		});