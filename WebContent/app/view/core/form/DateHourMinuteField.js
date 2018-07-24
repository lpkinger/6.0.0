/**
 * 方便选时分
 * @author zhouy
 */
Ext.define('erp.view.core.form.DateHourMinuteField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.datehourminutefield',
    layout: 'column',
    items: [],
    height: 27,
    defaultamstarttime:'08:00',
	defaultamendtime:'12:00',
	defaultpmstarttime:'14:00',
	defaultpmendtime:'18:00',
	timeregex:/^(([01]?[0-9])|(2[0-3])):[0-5]?[0-9]$/,
	dateregex:/^(d{4})-(d{2})-(d{2})$/,
    initComponent : function(){
    	this.callParent(arguments);
    	var me = this;
    	if(me.value){
			me.Date=me.value.substring(0,10);
			me.Time=me.value.substring(10,16);//原来时间显示有问题，多切割了一位。
		}
    	me.insert(0, Ext.create('Ext.form.field.Date', {
			columnWidth: 0.6,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			name:me.name+"_date",
			groupName:me.groupName,
			height: 22,
			value:me.Date,
			allowBlank: me.allowBlank,
			listeners: {
				change: function(field){
					var date = me.items.items[0].value;
					var time = me.items.items[1].value;
					if(date!=null && date !='' && time!=null && time !='' && me.timeregex.test(time)){
						me.value=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						this.value=me.firstValue;
						me.setValue(me.value);
					}
				}
			}
		}));
		me.insert(1, Ext.create('erp.view.core.form.TimeMinuteField', {
			columnWidth: 0.4,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			name:me.name+"_time",
			groupName:me.groupName,
			height: 22,
			value:me.Time,
			allowBlank: me.allowBlank,
			listeners: {
				change: function(){
					var date = me.items.items[0].value;
					var time = me.items.items[1].value;
					if(date!=null && date !='' && time!=null && time !='' && me.timeregex.test(time)){
						me.value=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						console.log('1');
						me.setValue(me.value);
						console.log('2');
					}
				}
			}
		}));
	},
	getValue: function(){
		return this.value;
	},
	setValue: function(v){
		console.log('v  :'+v);
		if(v){
		this.items.items[0].setValue(v.substring(0,10));
		this.items.items[1].setValue(v.substring(11,16));
		}
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
    		//this.setValueString(from, to);
    	}
    }
});