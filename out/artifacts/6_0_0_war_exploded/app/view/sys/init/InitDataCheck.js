Ext.define('erp.view.sys.init.InitDataCheck', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.initdatacheck',
	columnLines : true,
	forceFit : true,
	/*
	 * viewConfig : { stripeRows : true, enableTextSelection : true },
	 */
	autoScroll : true,
	columns : [ {
		text : '检测项',
		dataIndex : 'desc',
		flex : 10,
		renderer : function(val, meta, record) {
			if (record.get('check') == 'error') {
				meta.style = 'color: gray';
			}
			return val;
		}
	}, {
		text : '',
		dataIndex : 'check',
		style : 'text-align:center',
		width : 150,
		renderer : function(val, meta, record) {
			meta.tdCls = val;
			return '';
		}
	}, {
		text : '',
		dataIndex : 'link',
		width : 0,
		renderer : function(val, meta, record) {
			if (record.get('check') == 'error') {
				meta.tdCls = 'detail';
				return '详细情况';
			}
			return '';
		}
	}, {
		menuDisabled : true,
		sortable : false,
		xtype : 'actioncolumn',
		width : 50,
		items : [ {
			iconCls : 'refresh',
			tooltip : '开账',
			handler : function(grid, rowIndex, colIndex) {
				grid.ownerCt.refreshItem(this, grid.ownerCt, rowIndex);
			}
		} ]
	} ],
	store : Ext.create('Ext.data.Store', {
		fields : [ {
			name : 'link',
			type : 'string'
		}, {
			name : 'desc',
			type : 'string'
		}, {
			name : 'groupName',
			type : 'string'
		}, {
			name : 'detail'
		} ],
		groupField : 'groupName',
		data : [ {
			link : 'common/GL/refreshLedger.action',
			desc : '总账开帐',
			groupName : '科目余额初始化'
		}, {
			link : 'common/GL/refreshAR.action',
			desc : '应收确认开帐',
			groupName : '应收应付初始化'
		}, {
			link : 'common/GL/refreshAP.action',
			desc : '应付确认开帐',
			groupName : '应收应付初始化'
		} ]
	}),
	features : [ {
		id : 'group',
		ftype : 'grouping',
		groupHeaderTpl : Ext.create('Ext.XTemplate', '{rows:this.formatName}', {
			formatName : function(f) {
				return f[0].data.groupName;
			}
		}),
		enableGroupingMenu : false
	} ],
	plugins : [ {
		ptype : 'rowexpander',
		rowBodyTpl : [ '<div style="padding: 1em;color: #f30">{detail}</div>' ]
	} ],
	initComponent : function() {
		var me = this;
		this.callParent();
	},
	refreshItem : function(btn, grid, idx) {
		var me = this, r;
		if (Ext.isNumber(idx)) {
			r = grid.store.getAt(idx);
		}
		r.set('check', 'loading');
		var action = r.get('link');
		Ext.Ajax.request({
			url : basePath + action,
			//async : false,
			method : 'GET',
			//timeout : 600000,
			callback : function(opt, s, re) {
				r.set('check', 'checked');
				var rs = Ext.decode(re.responseText);
				if(rs.error) {
					r.set('check', 'error');
				}
				if(rs.result) {
					r.set('detail', rs.result);
				}		
				me.toggleRowbody(idx, rs.error);
				if (!Ext.isNumber(idx)) {
					btn.setDisabled(false);
				}
			}
		});
	},
	/**
	 * 展开收拢RowBody
	 * @param rowIdx 行号
	 * @param expand {Boolean} 展开/收拢
	 */
	toggleRowbody : function(rowIdx, expand) {
		var p = this.plugins[0], rowNode = this.view.getNode(rowIdx), row = Ext.get(rowNode), nextBd = Ext.get(row)
				.down(p.rowBodyTrSelector), record = this.view.getRecord(rowNode), isCollapsed = row
				.hasCls(p.rowCollapsedCls);
		if (expand && isCollapsed) {
			row.removeCls(p.rowCollapsedCls);
			nextBd.removeCls(p.rowBodyHiddenCls);
			p.recordsExpanded[record.internalId] = true;
			this.view.fireEvent('expandbody', rowNode, record, nextBd.dom);
		} else if (!expand && !isCollapsed) {
			row.addCls(p.rowCollapsedCls);
			nextBd.addCls(p.rowBodyHiddenCls);
			p.recordsExpanded[record.internalId] = false;
			this.view.fireEvent('collapsebody', rowNode, record, nextBd.dom);
		}
		this.view.up('gridpanel').invalidateScroller();
	}
});