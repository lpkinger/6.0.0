Ext.define('erp.view.core.form.ColorTypeField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.colortypefield',
    layout: 'column',
    items: [],
    value: " '' @ ''",
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
    	me.insert(0, {
	        xtype: 'colorfield',
	        id: me.name+'color',
	        name:me.name+'color',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,

	    });
    	me.insert(1, {
	        xtype: 'combo',
	        height:5,
	        id: me.name + 'type',
	        name: me.name + 'type',
	        columnWidth: 0.5,
	       fieldStyle: me.fieldStyle,
	       store: Ext.create('Ext.data.Store', {
    		    fields: ['display', 'value'],
    		    data : [
    		        {"display":"-选择类型-", "value": 0},
    		        {"display":"普通", "value": "普通"},
    		        {"display":"进行", "value": "进行"},
    		        {"display":"延迟", "value": "延迟"},
    		        {"display":"完成", "value":"完成"},  		     
    		    ]
    		}),
    	    queryMode: 'local',
    	    displayField: 'display',
    	    valueField: 'value',
	    });
	},
	getValue: function(){
    	return this.value;
    },
    listeners: {
    	afterrender: function(){
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    	}
    }
});