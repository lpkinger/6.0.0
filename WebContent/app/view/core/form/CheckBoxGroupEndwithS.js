/**
 * 多个checkbox+文本框
 */
Ext.define('erp.view.core.form.CheckBoxGroupEndwithS', {
    extend: 'Ext.form.CheckboxGroup',
    alias: 'widget.erpcheckboxgroupendwiths',
    layout: 'column',
    baseCls: 'x-field',
    //fieldContainer默认为x-component x-container x-container-default
    initComponent: function() {
        this.callParent(arguments);
        var me = this;
        if(this.fieldLabel.length > 12){
        	this.labelAlign = 'top';
        }
        var logic = this.logic;
        //没办法做到忽略
        var arr = logic.split('#');
        if (arr.length > 1) {
            logic = arr[1];
            this.logic = 'ignore';
        } else logic = arr[0];
        me.insert(0, {
            xtype: 'hidden',
            name: this.name,
            id: this.name + "_checks",
            value: this.value
        });
        var arr = logic.split(";");
        var value = this.value;
        var checked = false;
        for (var i = 1; i < arr.length + 1; i++) {
            if (value && value.split(";")[i - 1] == 1) {
                checked = true;
            } else checked = false;
            if(arr.length>1){
            	var w=1/Math.min((arr.length-1),6);
            	if(i==arr.length){
            		w=1/(2*Math.min((arr.length-1),6));
            	}
            	me.insert(i, {
	                xtype: 'checkbox',
	                columnWidth: w,
	                boxLabel: arr[i - 1],
	                style: {
	                    marginLeft: '10px'
	                },
	                checked: checked,
	                readOnly: me.readOnly,
	                listeners: {
	                    change: function(field, newValue, oldValue) {
	                        if (newValue) {
	                            me.setFiledValue();
	                        } else {
	                            me.setFiledValue();
	                        }
	                    }
	                }
                });
            }else{
            	 me.insert(i, {
	                xtype: 'checkbox',
	                columnWidth: 1 /Math.min(arr.length+1, 6),
	                boxLabel: arr[i - 1],
	                style: {
	                    marginLeft: '10px'
	                },
	                checked: checked,
	                readOnly: me.readOnly,
	                listeners: {
	                    change: function(field, newValue, oldValue) {
	                        if (newValue) {
	                            me.setFiledValue();
	                        } else {
	                            me.setFiledValue();
	                        }
	                    }
	                }
                });
            }
        }
        var value1=value.split(";")[arr.length];
        if(!value1){
        	value1='';
        }
        me.insert(i, {
                xtype: 'textfield',
                columnWidth: (2/Math.min((arr.length-1),6))<1?(2/Math.min((arr.length-1),6)):(1/Math.min((arr.length-1),6)),
                boxLabel:'',
                style: {
                    marginLeft: '10px'
                },
                value:value1,
                readOnly: me.readOnly,
                listeners: {
                    change: function(field) {
                            me.setFiledValue();
                    }
                }
         });
    },
    listeners: {
        afterrender: function() { //去掉fieldContainer默认的高度和滚动样式
        	if(this.getEl().dom.childNodes.length>1){
	        	this.getEl().dom.childNodes[1].style.height = 22;
	            this.getEl().dom.childNodes[1].style.overflow = 'hidden';
        	}
        }
    },
    isValid: function() {
        return this.items.items[0].isValid();
    },
    setValue: function(value) {
        this.value = value;
    },
    isDirty: function() {
        return true;
    },
    setFiledValue: function(field) {
        var value = "";
        var items = this.items.items;
        for (var i = 1; i < items.length; i++) {
            if (items[i].xtype=='checkbox' && items[i].value ) {
                value += "1;";
            } else if(items[i].xtype=='checkbox'){
                value += "-1;";
            }else if(items[i].xtype=='textfield'&& items[i].value){
          		  value += items[i].value+";";
            }else{
            	value += ";";
            }
        }
        value = value.substring(0, value.length - 1);
        this.items.items[0].setValue(value);
    },
    getValue: function() {
        return this.value;
    },
    setReadOnly: function(bool) {
        Ext.Array.each(this.items.items,
        function(item) {
            item.setReadOnly(bool);
        });
    },
    setFieldStyle: function(style) {
        this.items.items[0].setFieldStyle(style);
    },
    setFieldValues: function(value) {
        var items = this.items.items;console.log(items);
        for (var i = 1; i < items.length; i++) {
        	var v=value.split(';')[i-1];
            if (items[i].xtype=='checkbox' && v==1 ) {
                console.log('check');
                items[i].setValue(true);
            } else if(items[i].xtype=='checkbox'&& v==-1){
                items[i].setValue(false);
            }else if(items[i].xtype=='textfield'&& v){
          		items[i].setValue(v);
            }else{
            	items[i].setValue('');
            }
        }
    }
});