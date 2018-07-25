Ext.define('erp.view.co.cost.MonthAccount',{ 
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
			    		var grid = Ext.getCmp('cogrid');
			    		Ext.create('erp.util.BaseUtil').exportGrid(grid, '成本对账检查');
			    	}
				},{
					name: 'alldiffer',
					id: 'alldiffer',
					text: $I18N.common.button.erpAllDifferButton,
			    	cls: 'x-btn-gray'
				},'->',{
					margin:0,
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
				id:'cogrid',
				region: 'center',
				columnLines: true,
				plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
				columns: [{
					text: '期间',
					cls: 'x-grid-header-1',
					dataIndex: 'cm_yearmonth',
					xtype: 'numbercolumn',
					locked: true,
					format: '0',
					width: 80
				},{
					text: '类型',
					cls: 'x-grid-header-1',
					dataIndex: 'cm_code',
					hidden:true,
					width: 70
				},{
					text: '科目',
					cls: 'x-grid-header-1',
					dataIndex: 'cm_name',
					locked: true,
					width: 140
				},{
					text: '期初余额',
					cls: 'x-grid-header-1',
					columns: [{
						text: '成本系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cd_beginamount',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_beginbalance',
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
						text: '成本系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cd_nowdebit',
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
						dataIndex: 'nowdebit',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '本期贷方发生',
					cls: 'x-grid-header-1',
					columns: [{
						text: '成本系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cd_nowcredit',
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
						dataIndex: 'nowcredit',
						align: 'right',
						width: 120,
						renderer: warnRenderer
					}]
				},{
					text: '期末余额',
					cls: 'x-grid-header-1',
					columns: [{
						text: '成本系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cd_endamount',
						align: 'right',
						width: 120,
						renderer: defaultRenderer
					},{
						text: '总账系统',
						cls: 'x-grid-header-1',
						dataIndex: 'cm_endbalance',
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
						name: 'cm_yearmonth', type: 'number'
					},{
						name: 'cm_code', type: 'string'
					},{
						name: 'cm_name', type: 'string'
					},{
						name: 'cd_beginamount', type: 'number'
					},{
						name: 'cd_nowdebit', type: 'number'
					},{
						name: 'cd_nowcredit', type: 'number'
					},{
						name: 'cd_endamount', type: 'number'
					},{
						name: 'cm_beginbalance', type: 'number'
					},{
						name: 'cm_nowdebit', type: 'number'
					},{
						name: 'cm_nowcredit', type: 'number'
					},{
						name: 'cm_endbalance', type: 'number'
					},{
						name: 'beginbalance', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cd_beginamount') - record.get('cm_beginbalance')).toFixed(3));
						}
					},{
						name: 'nowdebit', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cd_nowdebit') - record.get('cm_nowdebit')).toFixed(3));
						}
					},{
						name: 'nowcredit', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cd_nowcredit') - record.get('cm_nowcredit')).toFixed(3));
						}
					},{
						name: 'endbalance', type: 'number',
						convert : function(value, record) {
							return Number((record.get('cd_endamount') - record.get('cm_endbalance')).toFixed(3));
						}
					}],
					viewConfig: { 
				        getRowClass: function(record) { 
				            return record.get('isCount') ? 'isCount' : null; 
				        } 
				    }
				})
			}]
		});
		me.callParent(arguments);
	}
});