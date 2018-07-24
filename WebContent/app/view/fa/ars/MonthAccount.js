Ext.define('erp.view.fa.ars.MonthAccount',{ 
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
					xtype : 'displayfield',
					labelWith : 120,
					fieldLabel: '核算类别',
					value: '客户',
					width: 140
				},{
					xtype: 'container',
					flex: 10
				},{
					xtype: 'checkbox',
					id: 'chkdetail',
					boxLabel: '显示客户明细',
					checked: true
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
			    		var grid = Ext.getCmp('arMonthAccountGrid');
			    		Ext.create('erp.util.BaseUtil').exportGrid(grid,'应收对账检查');
			    	}
				},{
					name: 'alldiffer',
					id: 'alldiffer',
					text: $I18N.common.button.erpAllDifferButton,
			    	cls: 'x-btn-gray'
				},'->',{
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
				id:'arMonthAccountGrid',
				region: 'center',
				columnLines: true,
				columns: [{
					text: '期间',
					cls: 'x-grid-header-1',
					dataIndex: 'am_yearmonth',
					xtype: 'numbercolumn',
					locked: true,
					format: '0',
					width: 80
				},{
					text: '类型',
					cls: 'x-grid-header-1',
					dataIndex: 'am_catecode',
					locked: true,
					width: 110
				},{
					text: '客户编号',
					cls: 'x-grid-header-1',
					dataIndex: 'am_asscode',
					locked: true,
					width: 120
				},{
					text: '客户名称',
					cls: 'x-grid-header-1',
					dataIndex: 'am_assname',
					locked: true,
					width: 180
				},{
					text: '币别',
					cls: 'x-grid-header-1',
					dataIndex: 'am_currency',
					locked: true,
					width: 60
				},{
					text: '期初余额',
					cls: 'x-grid-header-1',
					columns: [{
						text: '应收系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_beginbalance',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'am_beginbalance',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'beginbalance',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '本期借方发生',
					cls: 'x-grid-header-1',
					columns: [{
						text: '应收系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_nowdebit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'am_nowdebit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'nowdebit',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '本期贷方发生',
					cls: 'x-grid-header-1',
					columns: [{
						text: '应收系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_nowcredit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'am_nowcredit',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'nowcredit',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '期末余额',
					cls: 'x-grid-header-1',
					columns: [{
						text: '应收系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_endbalance',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'am_endbalance',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '差额',
						cls: 'x-grid-header-1',
						dataIndex: 'endbalance',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				}],
				store: Ext.create('Ext.data.Store', {
					fields: [{
						name: 'isCount', type: 'bool'
					},{
						name: 'am_yearmonth', type: 'number'
					},{
						name: 'am_catecode', type: 'string'
					},{
						name: 'am_asscode', type: 'string'
					},{
						name: 'am_assname', type: 'string'
					},{
						name: 'am_currency', type: 'string'
					},{
						name: 'am_beginbalance', type: 'number'
					},{
						name: 'cm_beginbalance', type: 'number'
					},{
						name: 'beginbalance', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cm_beginbalance') - record.get('am_beginbalance')).toFixed(3));
						}
					},{
						name: 'am_nowdebit', type: 'number'
					},{
						name: 'cm_nowdebit', type: 'number'
					},{
						name: 'nowdebit', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cm_nowdebit') - record.get('am_nowdebit')).toFixed(3));
						}
					},{
						name: 'am_nowcredit', type: 'number'
					},{
						name: 'cm_nowcredit', type: 'number'
					},{
						name: 'nowcredit', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cm_nowcredit') - record.get('am_nowcredit')).toFixed(3));
						}
					},{
						name: 'am_endbalance', type: 'number'
					},{
						name: 'cm_endbalance', type: 'number'
					},{
						name: 'endbalance', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cm_endbalance') - record.get('am_endbalance')).toFixed(3));
						}
					}],
					groupers: [{
			        	property: 'am_catecode',
			        	transform: function(value) {
			        		switch(value){
			        		case '应收':
			        			return 1;
			        		case '预收':
			        			return 2;
			        		case '发出商品':
			        			return 3;
			        		}
			        	}
			        }],
					groupField : 'am_catecode'
				}),
				viewConfig: { 
			        getRowClass: function(record) { 
			            return record.get('isCount') ? 'isCount' : null; 
			        } 
			    },
			    plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				features : [ Ext.create('erp.view.core.feature.FloatingGrouping', {
					groupHeaderTpl : '{name} (共:{rows.length}条)'
				}) ]
			}]
		});
		me.callParent(arguments);
	}
});