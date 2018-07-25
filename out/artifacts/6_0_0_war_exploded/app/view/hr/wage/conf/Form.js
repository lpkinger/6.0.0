Ext.define('erp.view.hr.wage.conf.Form', {
	extend:'Ext.form.Panel',
	alias:'widget.erpWageConfFormPanel',
	id:'form',
    title: '基本设置',
	region:'north',
	frame:true,
    layout: {
        type: 'vbox',
        align: 'left'
    },
    autoScroll:true,
    defaultType: 'textfield',
	labeSeparator:':',//默认：
	buttonAlign:'center',
	cls:'u-form-default',
   	fieldDefaults:{
   		fieldStyle:'background:#FFFAFA;color:#515151;',
   		focusCls:'x-form-field-cir-focus',
   		labelAlign:'left',
   		msgTarget:'side',
   		labelWidth:150,
   		blankText:$I18N.common.form.blankText
   	},
    items: [{
        fieldLabel: 'ID',
        name: 'WC_ID',
        xtype:'numberfield',
        readOnly:false,
        hidden:true,
        maxLength:100,
        cls:'form-field-allowBlank',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    },{
        fieldLabel: '月平均工作天数',
        name: 'WC_MONTHWORKDAYS',
        xtype:'numberfield',
        readOnly:false,
        hideTrigger:true,
        maxValue:31,
        minValue:0,        
        maxLength:100,
        cls:'form-field-allowBlank',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    },{
        fieldLabel: '是否固定工作天数',
        id:'isfixedmonthworkdays',
        name: 'WC_ISFIXEDMONTHWORKDAYS',
        xtype:'combo',
        store: Ext.create('Ext.data.Store', {
		    fields: ['name', 'value'],
		    data : [
		        {"name":"是", "value":1},
		        {"name":"否", "value":0}
		    ]
		}),
	    queryMode: 'local',
	    displayField: 'name',
	    valueField: 'value',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    },{
        fieldLabel: '基本工资比率',
        name: 'WC_BASEWAGERATE',
        xtype:'numberfield',
        readOnly:false,
        hideTrigger:true,
        maxValue:1,
        minValue:0,        
        maxLength:100,
        cls:'form-field-allowBlank',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    },{
        fieldLabel: '月度绩效比率',
        name: 'WC_MONTHPERFRATE',
        xtype:'numberfield',
        readOnly:false,
        hideTrigger:true,
        maxValue:1,
        minValue:0,
        maxLength:100,
        cls:'form-field-allowBlank',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    },
    {
        fieldLabel: '应税所得表达式',
        name: 'WC_TAXINEXPRESSION',
        id:'WC_TAXINEXPRESSION',
        xtype:'textfield',
        readOnly:false,
        hidden:true,
        width:500,
        cls:'form-field-allowBlank',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    },
    {
        fieldLabel: '应税所得表达式文字',
        name: 'WC_TAXINEXPRESSIONTEXT',
        id:'WC_TAXINEXPRESSIONTEXT',
        xtype:'formulaTrigger',
        readOnly:false,
        width:1000,
        cls:'form-field-allowBlank',
        fieldStyle:'background:#FFFAFA;color:#515151;',
        allowBlank: false
    }
    ],
	initComponent : function(){
		this.callParent(arguments);
	},
    dockedItems:[{
    	xtype:'toolbar',
    	dock:'bottom',
    	defaults:{
    		styple:{
    			marginLeft:'10px'
    		}
    	},
    	items:["->",
    		{
    			xtype:'erpUpdateButton',
    			height:26
    		},{
    			xtype:'erpCloseButton',
    			height:26
    		},"->"]
    }]
});