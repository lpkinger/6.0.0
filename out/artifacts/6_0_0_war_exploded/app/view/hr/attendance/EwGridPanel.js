Ext.require([
    'erp.util.*'
]);
Ext.define('erp.view.hr.attendance.EwGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.erpEwGridPanel',
	layout : 'fit',
	id: 'querygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'em_depart',
        	type: 'string'
        },{
        	name: 'em_code',
        	type: 'string'
        },{
        	name: 'em_name',
        	type: 'string'
        },{
        	name: '01',
        	type: 'string'
        },{
        	name: '02',
        	type: 'string'
        },{
        	name: '03',
        	type: 'string'
        },{
        	name: '04',
        	type: 'string'
        },{
        	name: '05',
        	type: 'string'
        },{
        	name: '06',
        	type: 'string'
        },{
        	name: '07',
        	type: 'string'
        },{
        	name: '08',
        	type: 'string'
        },{
        	name: '09',
        	type: 'string'
        },{
        	name: '10',
        	type: 'string'
        },{
        	name: '11',
        	type: 'string'
        },{
        	name: '12',
        	type: 'string'
        },{
        	name: '13',
        	type: 'string'
        },{
        	name: '14',
        	type: 'string'
        },{
        	name: '15',
        	type: 'string'
        },{
        	name: '16',
        	type: 'string'
        },{
        	name: '17',
        	type: 'string'
        },{
        	name: '18',
        	type: 'string'
        },{
        	name: '19',
        	type: 'string'
        },{
        	name: '20',
        	type: 'string'
        },{
        	name: '21',
        	type: 'string'
        },{
        	name: '22',
        	type: 'string'
        },{
        	name: '23',
        	type: 'string'
        },{
        	name: '24',
        	type: 'string'
        },{
        	name: '25',
        	type: 'string'
        },{
        	name: '26',
        	type: 'string'
        },{
        	name: '27',
        	type: 'string'
        },{
        	name: '28',
        	type: 'string'
        },{
        	name: '29',
        	type: 'string'
        },{
        	name: '30',
        	type: 'string'
        },{
        	name: '31',
        	type: 'string'
        }],
        data: []
    }),
    columns: [{
    	dataIndex: 'em_depart',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 100,
    	text: '部门'
    },{
    	dataIndex: 'em_code',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 100,
    	text: '员工编号'
    },{
    	dataIndex: 'em_name',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '姓名',
    	width: 100
    },{
    	dataIndex: '01',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '01',
    	width: 50
    },{
    	dataIndex: '02',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '02',
    	width: 50
    },{
    	dataIndex: '03',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '03',
    	width: 50
    },{
    	dataIndex: '04',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '04',
    	width: 50
    },{
    	dataIndex: '05',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '05',
    	width: 50
    },{
    	dataIndex: '06',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '06',
    	width: 50
    },{
    	dataIndex: '07',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '07',
    	width: 50
    },{
    	dataIndex: '08',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '08',
    	width: 50
    },{
    	dataIndex: '09',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '09',
    	width: 50
    },{
    	dataIndex: '10',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '10',
    	width: 50
    },{
    	dataIndex: '11',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '11',
    	width: 50
    },{
    	dataIndex: '12',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '12',
    	width: 50
    },{
    	dataIndex: '13',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '13',
    	width: 50
    },{
    	dataIndex: '14',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '14',
    	width: 50
    },{
    	dataIndex: '15',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '15',
    	width: 50
    },{
    	dataIndex: '16',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '16',
    	width: 50
    },{
    	dataIndex: '17',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '17',
    	width: 50
    },{
    	dataIndex: '18',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '18',
    	width: 50
    },{
    	dataIndex: '19',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '19',
    	width: 50
    },{
    	dataIndex: '20',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '20',
    	width: 50
    },{
    	dataIndex: '21',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '21',
    	width: 50
    },{
    	dataIndex: '22',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '22',
    	width: 50
    },{
    	dataIndex: '23',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '23',
    	width: 50
    },{
    	dataIndex: '24',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '24',
    	width: 50
    },{
    	dataIndex: '25',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '25',
    	width: 50
    },{
    	dataIndex: '26',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '26',
    	width: 50
    },{
    	dataIndex: '27',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '27',
    	width: 50
    },{
    	dataIndex: '28',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '28',
    	width: 50
    },{
    	dataIndex: '29',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '29',
    	width: 50
    },{
    	dataIndex: '30',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '30',
    	width: 50
    },{
    	dataIndex: '31',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '31',
    	width: 50
    }],
    bodyStyle:'background-color:#f1f1f1;',
    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	headerWidth: 0
	}),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	RenderUtil:Ext.create('erp.util.RenderUtil'),
	showRowNum:true,
	autoQuery: true,
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = Ext.create('erp.view.core.plugin.CopyPasteMenu');
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){
		this.GridUtil.add10EmptyItems(this);
        this.callParent(arguments);
    }
});