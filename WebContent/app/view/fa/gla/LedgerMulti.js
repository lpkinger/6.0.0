/**
 * 
 */
Ext.define('erp.view.fa.gla.LedgerMulti',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ledgerMulti',
	layout : 'fit',
	id: 'ledgerMulti', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'ca_code',
        	type: 'string'
        },{
        	name: 'ca_name',
        	type: 'string'
        },{
        	name: 'amm_acid',
        	type: 'string'
        },{
        	name: 'amm_assmulti',
        	type: 'string'
        },{
        	name: 'cm_yearmonth',
        	type: 'string'
        },{
        	name: 'cmc_currency',
        	type: 'string'
        },{
        	name: 'cm_begindebit',
        	type: 'number'
        },{
        	name: 'cm_begincredit',
        	type: 'number'
        },{
        	name: 'cmc_begindoubledebit',
        	type: 'number'
        },{
        	name: 'cmc_begindoublecredit',
        	type: 'number'
        },{
        	name: 'cm_nowdebit',
        	type: 'number'
        },{
        	name: 'cm_nowcredit',
        	type: 'number'
        },{
        	name: 'cmc_nowdoubledebit',
        	type: 'number'
        },{
        	name: 'cmc_nowdoublecredit',
        	type: 'number'
        },{
        	name: 'cm_yearenddebit',
        	type: 'number'
        },{
        	name: 'cm_yearendcredit',
        	type: 'number'
        },{
        	name: 'cmc_yearenddoubledebit',
        	type: 'number'
        },{
        	name: 'cmc_yearenddoublecredit',
        	type: 'number'
        },{
        	name: 'cm_enddebit',
        	type: 'number'
        },{
        	name: 'cm_endcredit',
        	type: 'number'
        },{
        	name: 'cmc_enddoubledebit',
        	type: 'number'
        },{
        	name: 'cmc_enddoublecredit',
        	type: 'number'
        },{
        	name: 'index',
        	type: 'number'
        }],
        data: []
    }),
    defaultColumns: [{
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 130,
    	text: '科目编号'
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 200
    },{
    	dataIndex: 'amm_acid',
    	cls: 'x-grid-header-1',
    	text: '核算组合ID',
    	width: 0
    },{
    	dataIndex: 'amm_assmulti',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	hidden: true,
    	text: '核算名称',
    	width: 250
    },{
    	dataIndex: 'cm_yearmonth',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '期间',
    	width: 70
    },{
		cls: 'x-grid-header-1',
		text: '期初余额',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_begindebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_begincredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本期发生',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_nowdebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_nowcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本年累计',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_yearenddebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_yearendcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '期末余额',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_enddebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_endcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	}],
	doubleColumns: [{
    	dataIndex: 'ca_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 120,
    	text: '科目编号'
    },{
    	dataIndex: 'ca_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '科目名称',
    	width: 180
    },{
    	dataIndex: 'amm_acid',
    	cls: 'x-grid-header-1',
    	text: '核算组合ID',
    	width: 0
    },{
    	dataIndex: 'amm_assmulti',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	hidden: true,
    	text: '核算名称',
    	width: 250
    },{
    	dataIndex: 'cm_yearmonth',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '期间',
    	width: 70
    },{
    	dataIndex: 'cmc_currency',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '币别',
		width: 70
    },{
		cls: 'x-grid-header-1',
		text: '期初余额（本币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_begindebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_begincredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '期初余额（原币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cmc_begindoubledebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cmc_begindoublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本期发生（本币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_nowdebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_nowcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本期发生（原币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cmc_nowdoubledebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cmc_nowdoublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本年累计（本币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_yearenddebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_yearendcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '本年累计（原币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cmc_yearenddoubledebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cmc_yearenddoublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '期末余额（本币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cm_enddebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cm_endcredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	},{
		cls: 'x-grid-header-1',
		text: '期末余额（原币）',
		sortable: false,
		columns: [{
			text: '借方',
			cls: 'x-grid-header-1',
			dataIndex: 'cmc_enddoubledebit',
			sortable: false,
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		},{
			dataIndex: 'cmc_enddoublecredit',
			cls: 'x-grid-header-1',
			sortable: false,
			text: '贷方',
			width: 120,
			xtype: 'numbercolumn',
			align: 'right'
		}]
	}],
    bodyStyle:'background-color:#f1f1f1;',
    cls: 'custom-grid',
    GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.callParent(arguments);
	},
	plugins : [ Ext.create('erp.view.core.plugin.CopyPasteMenu') ],
	viewConfig: { 
        getRowClass: function(record) { 
            return record.get('index')%2 == 1 ? (!Ext.isEmpty(record.get('ca_code')) ? 'custom-first' : 'custom') : 
            	(!Ext.isEmpty(record.get('ca_code')) ? 'custom-alt-first' : 'custom-alt');
        } 
    }
});