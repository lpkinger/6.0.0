Ext.define('erp.view.core.form.DyConditionField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.dyconfield',
    layout: 'hbox',
    type:'',
    caption:'',
    relation:'',
    layout: 'column',
    height: 22,
    name:'',
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
    	me.insert(0, {
    		xtype: 'displayfield',
    		columnWidth:0.4,
    		value: me.caption,
    		align:'right',
    	    name:  me.name
    	});
    	me.insert(1,{
    		xtype: 'displayfield',
    		value: me.relation,
    		columnWidth:0.2,
    	    name:  me.name+"_relation"
		});
    	me.insert(2, {			
			xtype:me.type,
			columnWidth:0.4,
			hideLabel:true,
			name:me.name+"_condition"
    	});
	},	
	reset: function(){
		var me = this;
		me.items.items[0].reset();
		me.items.items[1].reset();
	},
	
	listeners: {
    	afterrender: function(){
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    	}
    },
    getValue: function(){//以;隔开{类型;值}
    	var me = this;
    	return me.items.items[0].value + ';' + me.items.items[1].value+";"+me.items.items[2].value;
    },
    isValid: function(){
    	return true;
    }
});