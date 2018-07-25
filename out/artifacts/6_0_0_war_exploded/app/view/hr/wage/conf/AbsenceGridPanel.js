Ext.define('erp.view.hr.wage.conf.AbsenceGridPanel', {
	extend:'Ext.grid.Panel',
	alias:'widget.erpAbsenceGridPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region:'south',
	layout:'fit',
	id:'absencegrid',
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
			    'WAC_ID',
			    'WAC_TYPE',
			    'WAC_CONDEXPRESSION',
			    'WAC_CONDEXPRESSIONTEXT',
			    'WAC_EXPRESSION',
			    'WAC_EXPRESSIONTEXT']
	}),
    columns: [{
    	header:'ID',
    	text:'ID',
    	dataIndex:'WAC_ID',
    	cls:'x-grid-header-1',
    	align:'right',
    	xtype:'numbercolumn',
    	width:0
    },
	{
	    header: "缺勤类型",
	    text: "缺勤类型",
	    dataIndex: "WAC_TYPE",
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
	                    "display": "事假",
	                    "value": "personleave"
	                },
	                {
	                    "display": "病假",
	                    "value": "sickleave"
	                },
	                {
	                    "display": "产假",
	                    "value": "maternityleave"
	                },
	                {
	                    "display": "旷工",
	                    "value": "absent"
	                },	                
	                {
	                    "display": "其他假",
	                    "value": "otherleave"
	                }		                
	            ]
	        },
	        queryMode: "local",
	        displayField: "display",
	        valueField: "value",
	        editable: false
	    },
	    filter: {
	        dataIndex: "WAC_TYPE",
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
	                    "display": "事假",
	                    "value": "personleave"
	                },
	                {
	                    "display": "病假",
	                    "value": "sickleave"
	                },
	                {
	                    "display": "产假",
	                    "value": "maternityleave"
	                },
	                {
	                    "display": "旷工",
	                    "value": "absent"
	                },	                
	                {
	                    "display": "其他假",
	                    "value": "otherleave"
	                }		                
	            ]
	        }
	    },
	    align: "left",
	    xtype: "combocolumn"
	},    	
   	{
    	header:'条件表达式',
    	text:'公式表达式',
    	dataIndex:'WAC_CONDEXPRESSION',
    	name:'WAC_CONDEXPRESSION',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		field:{
			xtype:'textfield',
			allowBlank:false
		},
    	width:0
    },{
    	header:'条件表达式文本',
    	text:'公式表达式文本',
    	dataIndex:'WAC_CONDEXPRESSIONTEXT',
    	name:'WAC_CONDEXPRESSIONTEXT',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		editor:{
			xtype:'formulaTrigger'
		},
    	width:400
    },{
    	header:'公式表达式',
    	text:'公式表达式',
    	dataIndex:'WAC_EXPRESSION',
    	name:'WAC_EXPRESSION',
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
    	dataIndex:'WAC_EXPRESSIONTEXT',
    	name:'WAC_EXPRESSIONTEXT',
    	cls:'x-grid-header-1',
    	editable:true,
    	align:'left',
		editor:{
			xtype:'formulaTrigger'
		},
    	width:400
    }],
	initComponent : function(){
		this.callParent(arguments);
	}
});