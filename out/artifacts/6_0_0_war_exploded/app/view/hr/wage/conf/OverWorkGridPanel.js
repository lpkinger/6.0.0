Ext.define('erp.view.hr.wage.conf.OverWorkGridPanel', {
	extend:'Ext.grid.Panel',
	alias:'widget.erpOverWorkGridPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region:'south',
	layout:'fit',
	id:'overworkgrid',
	emptyText:$I18N.common.grid.emptyText,
	columnLines:true,
	autoScroll:true,
	bodyStyle: 'background-color:#f1f1f1;',
	bbar: {xtype: 'erpToolbar'},
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	store: Ext.create('Ext.data.Store', {
			    fields:[
			    'WO_ID',
			    'WO_TYPE',
			    'WO_EXPRESSION',
			    'WO_EXPRESSIONTEXT']
	}),
    columns: [{
    	header:'ID',
    	text:'ID',
    	dataIndex:'WO_ID',
    	cls:'x-grid-header-1',
    	align:'right',
    	xtype:'numbercolumn',
    	width:0
    },
	{
	    header: "加班类型",
	    text: "加班类型",
	    dataIndex: "WO_TYPE",
	    fullName: null,
	    editor: {
	        xtype: "combo",
	        hideTrigger: false,
	        store: {
	            fields: [
	                "display",
	                "value"
	            ],
	            data: [
	                {
	                    "display": "工作日加班",
	                    "value": "workDay"
	                },
	                {
	                    "display": "节假日加班",
	                    "value": "holiday"
	                },
	                {
	                    "display": "双休日加班",
	                    "value": "weekend"
	                }	           
	            ]
	        },
	        queryMode: "local",
	        displayField: "display",
	        valueField: "value",
	        editable: false
	    },
	    filter: {
	        dataIndex: "WO_TYPE",
	        xtype: "combo",
	        hideTrigger: false,
	        queryMode: "local",
	        displayField: "display",
	        valueField: "value",
	        store: {
	            fields: [
	                "display",
	                "value"
	            ],
	            data: [
	                {
	                    "display": "工作日加班",
	                    "value": "workDay"
	                },
	                {
	                    "display": "节假日加班",
	                    "value": "holiday"
	                },
	                {
	                    "display": "双休日加班",
	                    "value": "weekend"
	                }	           
	            ]
	        }
	    },
	    align: "left",
	    xtype: "combocolumn"
	},  
    {
    	header:'公式表达式',
    	text:'公式表达式',
    	dataIndex:'WO_EXPRESSION',
    	name:'WO_EXPRESSION',
    	id:'WO_EXPRESSION',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		field:{
			xtype:'textfield',
			allowBlank:false
		},
    	width:0
    },{
    	header:'公式表达式文本',
    	text:'公式表达式文本',
    	dataIndex:'WO_EXPRESSIONTEXT',
    	name:'WO_EXPRESSIONTEXT',
    	id:'WO_EXPRESSIONTEXT',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		editor:{
			xtype:'formulaTrigger'
		},
/*		field:{
			xtype:'textfield',
			allowBlank:false
		},*/
    	width:400
    }],
	initComponent : function(){
		this.callParent(arguments);
	}
});