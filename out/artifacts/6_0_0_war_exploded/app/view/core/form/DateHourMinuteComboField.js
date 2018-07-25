Ext.define('erp.view.core.form.DateHourMinuteComboField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.datetimecombofield',
    layout: 'column',
    items: [],
    height: 27,
	dateregex:/^(d{4})-(d{2})-(d{2})$/,
    initComponent : function(){
    	this.callParent(arguments);
    	var me = this;
    	if(me.value){
			me.Date=me.value.substring(0,10);
			me.Time=me.value.substring(11,16);
		}
    	me.insert(0, Ext.create('Ext.form.field.Date', {
			columnWidth: 0.6,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			groupName:me.groupName,
			height: 22,
			value:me.Date,
			allowBlank: me.allowBlank,
			listeners: {
				change: function(field){
					var date = me.items.items[0].value;
					var time = me.items.items[1].value;
					if(date!=null && date !='' && time!=null && time !=''){
						me.value=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						this.value=me.firstValue;
						me.setValue(me.value);
					}
				}
			}
		}));
		me.insert(1, Ext.create('Ext.form.TimeField', {
			columnWidth: 0.4,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			groupName:me.groupName,
			height: 22,
			value:me.Time,
			allowBlank: me.allowBlank,
			listeners: {
				change: function(){
					var date = me.items.items[0].value;
					var time = me.items.items[1].value;
					if(date!=null && date !='' && time!=null && time !=''){
						me.value=Ext.Date.format(date,'Y-m-d')+" "+Ext.Date.format(time,'H:i')+":00";
						me.setValue(me.value);
					}
				}
			}
		}));
		me.insert(2,{
			xtype:'hidden',
			name:me.name,
			value:me.value
		});
	},
	getValue: function(){
		return this.value;
	},
	setValue: function(v){
		if(v){
			this.items.items[0].setValue(v.substring(0,10));
			this.items.items[1].setValue(v.substring(11,16));
		}
		this.items.items[2].setValue(v);
		this.value=v;
	},
    reset:function(v){
    	this.items.items[0].reset();
    	this.items.items[1].reset();
	},
	isValid:function(){
		return true;
	},
	select: function(r) {
        
    },
	listeners: {
    	afterrender: function(){
    		var tb = this.getEl().dom;
    		tb.childNodes[1].style.height = 22;
        	tb.childNodes[1].style.overflow = 'hidden';
            this.getFocusEl().dom.select=function(){
            	
            };
    	}
    }
});