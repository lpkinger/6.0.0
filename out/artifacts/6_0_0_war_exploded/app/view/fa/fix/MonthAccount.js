Ext.define('erp.view.fa.fix.MonthAccount',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var warnRenderer = function(val, meta, record) {
			if (val != 0 ) {
				meta.tdCls = 'x-grid-cell-warn';
				return val;
			} else {
				return '';
			}
		}
		var defaultRenderer = function(val, meta, record) {
			return val != 0 ? val:'';
		}
		Ext.apply(me, { 
			items: [{
				xtype: 'form',
				width: '100%',
				region: 'north',
				layout: 'hbox',
				bodyStyle: 'background:#f1f1f1',
				fieldDefaults: {
					labelAlign : 'right',
					width: 120,
					margin: '10 8 10 0'
				},
				items: [{
					xtype : 'displayfield',
					labelWith : 60,
					fieldLabel : '期间',
					id : 'info_ym',
					value : Ext.Date.format(new Date(), 'Ym')
				},{
					xtype: 'container',
					flex: 10
				},{
					xtype : 'checkbox',
					id : 'chkun',
					boxLabel : '包括未记账凭证',
					checked : true
				},{
					xtype : 'checkbox',
					id : 'chkbalance',
					boxLabel : '只显示有差额科目',
					checked : true
				}],
				buttonAlign: 'center',
				tbar: {margin:'0 0 5 0',style:{background:'#fff'},defaults:{margin:'0 10 0 0'},items:[{
					name: 'query',
					id: 'query',
					text: $I18N.common.button.erpRefreshButton,
					iconCls: 'x-button-icon-reset',
			    	cls: 'x-btn-gray'
				},{
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray',
			    	handler: function(btn){
			    		var grid = Ext.getCmp('asGrid');
			    		Ext.create('erp.util.BaseUtil').exportGrid(grid, '固定资产对账检查');
			    	}
				},{
					name: 'alldiffer',
					id: 'alldiffer',
					text: $I18N.common.button.erpAllDifferButton,
			    	cls: 'x-btn-gray'
				},'->',{
					margin:'0',
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]}
			},{
				xtype: 'grid',
				id:'asGrid',
				region: 'center',
				columnLines: true,
				columns: [{
					text: '类型',
					dataIndex: 'type',
					hidden: true,
					width: 0
				},{
					text: '期间',
					cls: 'x-grid-header-1',
					dataIndex: 'yearmonth',
					xtype: 'numbercolumn',
					locked: true,
					format: '0',
					width: 80
				},{
					text: '科目',
					cls: 'x-grid-header-1',
					dataIndex: 'catecode',
					locked: true,
					width: 110
				},{
					text: '期初余额',
					cls: 'x-grid-header-1',
					columns: [{
						text: '固定资产',
						cls: 'x-grid-header-1',
						dataIndex: 'beginamount',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_beginamount',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'begindiff',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '本期借方发生',
					cls: 'x-grid-header-1',
					columns: [{
						text: '固定资产',
						cls: 'x-grid-header-1',
						dataIndex: 'nowdebit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_nowdebit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'debitdiff',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '本期贷方发生',
					cls: 'x-grid-header-1',
					columns: [{
						text: '固定资产',
						cls: 'x-grid-header-1',
						dataIndex: 'nowcredit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_nowcredit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'creditdiff',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '期末余额',
					cls: 'x-grid-header-1',
					columns: [{
						text: '固定资产',
						cls: 'x-grid-header-1',
						dataIndex: 'endamount',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_endamount',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'enddiff',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				}],
				store: Ext.create('Ext.data.Store', {
					fields: [{
						name: 'type'
					},{
						name: 'isCount', 
						type: 'bool'
					},{
						name: 'yearmonth', 
						type: 'number'
					},{
						name: 'catecode', 
						type: 'string'
					},{
						name: 'beginamount',
						type: 'number'
					},{
						name: 'nowdebit',
						type: 'number'
					},{
						name: 'nowcredit',
						type: 'number'
					},{
						name: 'endamount',
						type: 'number'
					},{
						name: 'cm_beginamount',
						type: 'number'
					},{
						name: 'cm_nowdebit',
						type: 'number'
					},{
						name: 'cm_nowcredit',
						type: 'number'
					},{
						name: 'cm_endamount',
						type: 'number'
					},{
						name: 'begindiff', 
						type: 'number',
						convert : function(value, record) {
							return Number((record.get('beginamount') - record.get('cm_beginamount')).toFixed(3));
						}
					},{
						name: 'debitdiff', 
						type: 'number',
						convert : function(value, record) {
							return Number((record.get('nowdebit') - record.get('cm_nowdebit')).toFixed(3));
						}
					},{
						name: 'creditdiff', 
						type: 'number',
						convert : function(value, record) {
							return Number((record.get('nowcredit') - record.get('cm_nowcredit')).toFixed(3));
						}
					},{
						name: 'enddiff', 
						type: 'number',
						convert : function(value, record) {
							return Number((record.get('endamount') - record.get('cm_endamount')).toFixed(3));
						}
					}],
					groupers: [{
			        	property: 'type',
			        	transform: function(value) {
			        		switch(value){
			        		case '固定资产':
			        			return 1;
			        		case '累计折旧':
			        			return 2;
			        		}
			        	}
			        }],
					groupField: 'type'
				}),
				viewConfig : {
					getRowClass : function(record) {
						return record.get('isCount') ? 'isCount' : null;
					}
				},
				plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				features : [ Ext.create('Ext.grid.feature.Grouping', {
					groupHeaderTpl : '{name} (共:{rows.length}条)'
				}) ]
			}]
		});
		me.callParent(arguments);
	}
});