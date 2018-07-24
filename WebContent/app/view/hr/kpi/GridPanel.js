Ext.define('erp.view.hr.kpi.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpKpiQueryGridPanel',
	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'querygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    bodyStyle:'background-color:#f1f1f1;',
    cls: 'custom-grid',
    GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	RenderUtil: Ext.create('erp.util.RenderUtil'),
    store: Ext.create('Ext.data.Store', {
        fields:[{
        	name: 'kdb_title',
        	type: 'string'
        },{
        	name: 'kd_startkind',
        	type: 'string'
        },{
        	name: 'kdb_period',
        	type: 'string'
        },{
        	name: 'kt_beman',
        	type: 'string'
        },{
        	name: 'kt_score',
        	type: 'string'
        },{
        	name: 'kt_level',
        	type: 'string'
        },{
        	name: 'ktd_description',
        	type: 'string'
        },{
        	name: 'ktd_score_from',
        	type: 'string'
        },{
        	name: 'ktd_score_to',
        	type: 'number'
        },{
        	name: 'ktd_score',
        	type: 'number'
        },{
        	name: 'kt_bemanid',
        	type: 'number'
        },{
        	name: 'kt_id',
        	type: 'number'
        },{
        	name: 'kt_kdbid',
        	type: 'number'
        }],
        data: []
    }),
    defaultColumns: [{
    	dataIndex: 'kdb_title',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	width: 120,
    	text: '考核模板'
    },{
    	dataIndex: 'kd_startkind',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '考核类型',
    	width: 120,
    	filter: {
			dataIndex: 'kd_startkind',
			displayField: 'display',
			queryMode: 'local',
			store: {data: [{display: "周考核", value: "week"},
						   {display: "月度考核", value: "month"},
                           {display: "季度考核", value: "season"},
                           {display: "手动考核", value: "manual"}],
                           fields: ["display", "value"]
					},
			valueField: "value",
			xtype: "combo"
			},
		xtype: 'combocolumn'
    },{
    	dataIndex: 'kdb_period',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '考核期间',
    	width: 150
    },{
    	dataIndex: 'kt_beman',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '受评人',
    	width: 80
    },{
    	dataIndex: 'kt_score',
    	cls: 'x-grid-header-1',
    	sortable: false,
    	text: '总分',
    	width: 70
    },{
		dataIndex: 'kt_level',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '等级',
		width: 70
	},{
		dataIndex: 'ktd_description',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '考核项目',
		width: 200
	},{
		dataIndex: 'ktd_score_from',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '最低分',
		width: 70,
		align: 'right'
	},{
		dataIndex: 'ktd_score_to',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '最高分',
		width: 70,
		xtype: 'numbercolumn',
		align: 'right'
	},{
		dataIndex: 'ktd_score',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '得分',
		width: 70,
		xtype: 'numbercolumn',
		align: 'right'
	},{
		dataIndex: 'kt_bemanid',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '受评人ID',
		hidden: true,
		width: 0,
		xtype: 'numbercolumn',
		align: 'right'
	},{
		dataIndex: 'kt_id',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '考核结果ID',
		hidden: true,
		width: 120
	},{
		dataIndex: 'kt_kdbid',
		cls: 'x-grid-header-1',
		sortable: false,
		text: '考核历史id',
		hidden: true,
		width: 120
	}],
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = Ext.create('erp.view.core.plugin.CopyPasteMenu');
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){
		this.columns = this.defaultColumns;
		this.GridUtil.add10EmptyItems(this);
		this.callParent(arguments);
	},
    viewConfig: {       
        getRowClass: function(record,index) { 
            return record.get('index')%2 == 1 ? (!Ext.isEmpty(record.get('kdb_title')) ? 'custom-first' : 'custom') : 
            	(!Ext.isEmpty(record.get('kdb_title')) ? 'custom-alt-first' : 'custom-alt');
        } 
    
    }
});