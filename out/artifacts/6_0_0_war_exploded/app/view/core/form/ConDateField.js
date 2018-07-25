/**
 * From-To datefield With combobox
 * @author yingp
 */
Ext.define('erp.view.core.form.ConDateField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.condatefield',
    layout: 'hbox',
    valuePrint:"",
    items: [],
    showscope: true,
    initComponent : function(){
    	this.cls = (this.cls || '') + ' x-form-field-multi';
    	this.callParent(arguments);
    	var me = this, allowBlank = (Ext.isDefined(me.allowBlank) ? me.allowBlank : true);
    	me.combo = Ext.create('Ext.form.field.ComboBox', {
    		width: 75,
    		editable: false,
    		hidden: !me.showscope,
    		fieldStyle: 'background:#C1CDC1',
    		store: Ext.create('Ext.data.Store', {
    		    fields: ['display', 'value'],
    		    data : [
    		        {"display":"-请选择-", "value": 7},
    		        {"display":"今天", "value": 1},
    		        {"display":"昨天", "value": 2},
    		        {"display":"本月", "value": 3},
    		        {"display":"上个月", "value": 4},
    		        {"display":"本年度", "value": 5},
    		        {"display":"上年度", "value": 6},
    		        {"display":"近半年", "value": 8},
    		        {"display":"自定义", "value": 7}
    		    ]
    		}),
    	    queryMode: 'local',
    	    displayField: 'display',
    	    valueField: 'value',
    	    value: 3,
    	    listeners: {
    	    	select: function(combo, records, obj){
    	    		me.setDateFieldValue(combo.value);
    	    	}
    	    }
    	});
    	me.insert(0, me.combo);
    	me.from = me.insert(1, {
	        xtype: 'datefield',
	        id: me.name + '_from',
	        name: me.name + '_from',
	        format: 'Y-m-d',
	        allowBlank: allowBlank,
	        flex: 1,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(){
	        		var from = Ext.getCmp(me.name + '_from').value;
	        		var to = Ext.getCmp(me.name + '_to').value;
	        		var v = me.items.items[0].value;
	        		if(v != 7) {
	        			Ext.getCmp(me.name + '_to').setMinValue(Ext.getCmp(me.name + '_from').value);
	        		}
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	          		me.firstVal = from;
	        		me.secondVal = to;
	        		if(to!=''&&from!=''){
	        		me.value = "BETWEEN to_date('" + Ext.Date.format(from,'Y-m-d') + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
        				+ Ext.Date.format(to,'Y-m-d') + " 23:59:59','yyyy-MM-dd HH24:mi:ss')";
	        		if(me.ownerCt){
	        			var tablename = me.ownerCt.tablename;
	        			me.valuePrint="{"+tablename+"."+me.name+"}>=date('"+Ext.Date.format(from,'Y-m-d')+"') and {"+tablename+"."+me.name+"}<=date('"+Ext.Date.format(to,'Y-m-d')+"')";
	        		}
	        		}else {
		        		  me.value=null;
		        	}
	        	}
	        }
	    });
    	me.to = me.insert(2, {
	        xtype: 'datefield',
	        id: me.name + '_to',
	        name: me.name + '_to',
	        allowBlank: allowBlank,
	        format: 'Y-m-d',
	        flex: 1,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(){
	        		var from = Ext.getCmp(me.name + '_from').value;
	        		var to = Ext.getCmp(me.name + '_to').value;
	        		var v = me.items.items[0].value;
	        	    Ext.getCmp(me.name + '_from').setMaxValue(Ext.getCmp(me.name + '_to').value);
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	          		me.firstVal = from;
	        		me.secondVal = to;
	        		if(to!=''&& from!=''){
	        		me.value = "BETWEEN to_date('" + Ext.Date.format(from,'Y-m-d') + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
	        			+ Ext.Date.format(to,'Y-m-d') + " 23:59:59','yyyy-MM-dd HH24:mi:ss')";
	        		if(me.ownerCt){
	        			var tablename = me.ownerCt.tablename;
	        			me.valuePrint = "{"+tablename+"."+me.name+"}>=date('"+Ext.Date.format(from,'Y-m-d')+"') and {"+tablename+"."+me.name+"}<=date('"+Ext.Date.format(to,'Y-m-d')+"')";
	        		}
	        	  }else {
	        		  me.value=null;
	        	  }
    			}
	        }
	    });
    	var t = this.value;
    	if(!t || !t in [1,2,3,4,5,6,7]) {
    		t = 3;
    	}
    	this.value = null;
    	this.setValue(t);
	},
	setDateFieldValue: function(v){
		v = Number(v);
		var me = this;
		var from = Ext.getCmp(me.name + '_from');
		var to = Ext.getCmp(me.name + '_to');
		var now = new Date();                    //当前日期  
		var nowDay = now.getDate();              //当前日  
		var nowMonth = now.getMonth();           //当前月  
		var nowYear = now.getYear();             //当前年  
		nowYear += (nowYear < 2000) ? 1900 : 0;
		var maxDate = null;
		var minDate = null;
		switch (v) {
			case 1://今天
				minDate = now;
				maxDate = now;
				break;
			case 2://昨天
				minDate = new Date(nowYear, nowMonth, nowDay - 1);
				maxDate = new Date(nowYear, nowMonth, nowDay - 1);
				break;
			case 3://本月
				minDate = Ext.Date.getFirstDateOfMonth(now);
				maxDate = Ext.Date.getLastDateOfMonth(now);
				break;
			case 4://上个月
				minDate = Ext.Date.getFirstDateOfMonth(new Date(nowYear, nowMonth - 1, 1));
				maxDate = Ext.Date.getLastDateOfMonth(new Date(nowYear, nowMonth - 1, 1));
				break;
			case 5://本年度
				minDate = new Date(nowYear, 0, 1);
				maxDate = new Date(nowYear, 11, 31);
				break;
			case 6://上年度
				minDate = new Date(nowYear - 1, 0, 1);
				maxDate = new Date(nowYear - 1, 11, 31);
				break;
			case 8://近半年
				if(nowMonth > 5){
					minDate = new Date(nowYear, nowMonth-6, nowDay);
				}else{
					minDate = new Date(nowYear - 1, 12 - 6 + nowMonth, nowDay);
				}
				maxDate = new Date(nowYear, nowMonth, nowDay);
				break;
			case 7://自定义
				minDate = new Date(1970, 0, 1);
				maxDate = new Date(nowYear + 100, 11, 31);
				break;
			default:
				minDate = new Date(1970, 0, 1);
				maxDate = new Date(nowYear + 100, 11, 31);
				break;
		}
		from.setMaxValue(maxDate);
		from.setMinValue(minDate);
		to.setMaxValue(maxDate);
		to.setMinValue(minDate);
		if(v == 7) {
			from.setValue(null);
			to.setValue(null);
			me.firstVal = null;
			me.secondVal = null;
			from.setEditable(true);
			to.setEditable(true);
		} else {
			from.setValue(minDate);
			to.setValue(maxDate);
			me.firstVal = minDate;
			me.secondVal = maxDate;
			from.setEditable(false);
			to.setEditable(false);
		}
	},
	setValue: function(v){
		if (v) {
			this.combo.setValue(v);
	    	this.setDateFieldValue(v);
		}
	},
	getValue: function(){
		return this.value;
	},
	listeners: {
    	afterrender: function(){
    		var tablename = this.ownerCt == null ? '' : this.ownerCt.tablename;
    		if(this.firstVal != null && this.secondVal != null) {
	        	this.valuePrint = "{"+tablename+"."+this.name+"}>=date('"+Ext.Date.toString(this.firstVal)+"') and {"
	        		+tablename+"."+this.name+"}<=date('"+Ext.Date.toString(this.secondVal)+"')";
    		}
    	}
    },
    hideScope: function(bool) {
    	if(bool)
    		this.items.items[0].hide();
    	else 
    		this.items.items[0].show();
    },
    mregex: /^[1-9]\d{3}[0-1]\d$/,
    setMonthValue: function(val) {
    	if(this.mregex.test(val)) {
    		var me = this,
	    		d = Ext.Date.parse(val + '01', 'Ymd'),
	    		f = Ext.Date.getFirstDateOfMonth(d),
	    		l = Ext.Date.getLastDateOfMonth(d);
			var from = Ext.getCmp(me.name + '_from');
			var to = Ext.getCmp(me.name + '_to');
			from.setMaxValue(l);
			from.setMinValue(f);
			to.setMaxValue(l);
			to.setMinValue(f);
			from.setValue(f);
			to.setValue(l);
    	}
    },
    getFilter: function() {
    	var me = this, fromVal = me.from.getValue(), toVal = me.to.getValue();
    	return (fromVal || toVal) ? {
    		"gte": fromVal ? Ext.Date.format(fromVal, 'Y-m-d') : null,
    		"lte": toVal ? Ext.Date.format(toVal, 'Y-m-d') : null
    	} : null;
    }
});