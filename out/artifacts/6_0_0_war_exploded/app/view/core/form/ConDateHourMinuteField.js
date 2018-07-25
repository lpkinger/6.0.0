/**
 * 方便选择的时间区间
 * @author zhouy
 */
Ext.define('erp.view.core.form.ConDateHourMinuteField', {
	extend: 'Ext.form.FieldContainer',
	alias: 'widget.condatehourminutefield',
	layout: 'column',
	requires:['erp.view.core.form.DateHourMinuteField'],
	valuePrint:"",
	items: [],
	layout: 'column',
	showscope: true,
	defaultamstarttime:'08:00',
	defaultamendtime:'12:00',
	defaultpmstarttime:'14:00',
	defaultpmendtime:'18:00',
	timeregex:/^(([01]?[0-9])|(2[0-3])):[0-5]?[0-9]$/,
	dateregex:/^(d{4})-(d{2})-(d{2})$/,
	minDate:null,
	minTime:null,
	maxDate:null,
	maxTime:null,
	initComponent : function(){
		this.cls = (this.cls || '') + ' x-form-field-multi';
		this.callParent(arguments);
		var me = this;
		if(me.value){
			me.minDate=me.value.substring(0,10);
			me.minTime=me.value.substring(11,16);
		}
		if(me.secondvalue){
			me.maxDate=me.secondvalue.substring(0,10);
			me.maxTime=me.secondvalue.substring(11,16);
		}
		me.insert(0, Ext.create('Ext.form.field.Date', {
			columnWidth: 0.3,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			value:me.minDate,
			groupName:me.groupName,
			allowBlank: me.allowBlank,
			editable:false,
			listeners: {
				change: function(field){
					var date = me.items.items[0].value;
					var time = me.items.items[1].value;
					me.items.items[3].setMinValue(date);
					if(me.items.items[3].value && date>me.items.items[3].value){
						me.items.items[3].setValue(null);
					}
					if(date!=null && date !='' && time!=null && time !='' && me.timeregex.test(time)){
						me.firstValue=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						this.value=me.firstValue;
						me.items.items[5].setValue(me.firstValue);
					}
				}
			}
		}));
		me.insert(1, Ext.create('erp.view.core.form.TimeMinuteField', {
			columnWidth: 0.2,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			allowBlank: me.allowBlank,
			value:me.minTime,
			groupName:me.groupName,
			editable:false,
			listeners: {
				change: function(){
					var date = me.items.items[0].value;
					var time = me.items.items[1].value;
					if(date!=null && date !='' && time!=null && time !='' && me.timeregex.test(time)){
						me.firstValue=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						this.value=me.firstValue;
						me.items.items[5].setValue(me.firstValue);
					}
				}
			}
		}));
		me.insert(2,{
			xtype: 'label',
			text: ' -- ',
			paddings: '0 10 0 10'
		});
		me.insert(3, Ext.create('Ext.form.field.Date', {
			columnWidth: 0.3,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			value:me.maxDate,
			editable:false,
			groupName:me.groupName,
			allowBlank: me.allowBlank,
			listeners: {
				change: function(){
					var date = me.items.items[3].value;
					var time = me.items.items[4].value;
					me.items.items[0].setMaxValue(date);
					if(me.items.items[0].value && me.items.items[0].value>date){
						me.items.items[0].setValue(null);
					}
					if(date!=null && date !='' && time!=null && time !='' && me.timeregex.test(time)){
						me.secondvalue=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						Ext.getCmp(me.secondname).setValue(me.secondvalue);
					}
				}
			}
		}));
		me.insert(4, Ext.create('erp.view.core.form.TimeMinuteField', {
			columnWidth: 0.2,
			fieldStyle: me.fieldStyle,
			readOnly:me.readOnly,
			value:me.maxTime,
			groupName:me.groupName,
			allowBlank: me.allowBlank,
			listeners: {
				change: function(){
					var date = me.items.items[3].value;
					var time = me.items.items[4].value;
					if(date!=null && date !='' && time!=null && time !='' && me.timeregex.test(time)){
						me.secondvalue=Ext.Date.format(date,'Y-m-d')+" "+time+":00";
						Ext.getCmp(me.secondname).setValue(me.secondvalue);
					}
				}
			}
		}));
		me.insert(5,{
			xtype:'hidden',
			name:me.name,
			value:me.value
		});
		me.insert(6,{
			xtype: 'button',
			iconCls: 'x-button-icon-add',
			cls: 'x-btn-tb',
			width:20,
			hidden:me.readOnly==true,
			listeners:{
				mouseover:function(btn,e){
					btn.showMenu();
				}
			},
			menuAlign:'tl-bl?',
			menu:{
				xtype:'menu',
				width:100,
				items:[{
					text: '上午',
					iconCls: 'main-msg',
					listeners: {
						click: function(m){
							me.setDateFieldValue(1);
						}
					}
				},{
					text: '下午',
					iconCls: 'main-msg',
					width:80,
					listeners: {
						click: function(m){
							me.setDateFieldValue(2);
						}
					}
				},{
					text: '整天',
					width:80,
					iconCls: 'main-msg',
					listeners: {
						click: function(m){
							me.setDateFieldValue(3);
						}
					}
				},{
					text:'非整天',
					width:50,
					iconCls: 'main-msg',
					listeners: {
						click: function(m){
							me.setDateFieldValue(4);
						}
					}
				}]
			}
		});
	},
	setDateFieldValue: function(v){
		v = Number(v);
		var now=new Date(),
		minDate,
		maxDate,
		minTime,
		maxTime;
		var me = this;
		var baseData=this.getSystemSet();
		if(baseData.as_amstarttime){
			me.defaultamstarttime=baseData.as_amstarttime;
			me.defaultamendtime=baseData.as_amendtime;
			me.defaultpmstarttime=baseData.as_pmstarttime;
			me.defaultpmendtime=baseData.as_pmendtime;
		}
		switch (v) {
		case 1://上午
			minDate = now;
			maxDate = now;
			minTime =me.defaultamstarttime;
			maxTime =me.defaultamendtime;
			break;
		case 2://下午
			minDate = now;
			maxDate = now;
			minTime =me.defaultpmstarttime;
			maxTime =me.defaultpmendtime;
			break;
		case 3://整天
			minDate = now;
			maxDate = now;
			minTime =me.defaultamstarttime;
			maxTime =me.defaultpmendtime;
			break;
		case 4://非整天
			minDate = null;
			maxDate = null;
			minTime = null;
			maxTime = null;
			break;
		default:
			minDate = null;
		maxDate = null;
		minTime = null;
		maxTime = null;
		break;
		}
		me.items.items[0].setValue(minDate);
		me.items.items[1].setValue(minTime);
		me.items.items[3].setValue(maxDate);
		me.items.items[4].setValue(maxTime);
	},
	isValid: function(){
		return this.firstField.isValid();
	},
	setValue: function(value){
		this.firstField.setValue(value);
	},
	getValue: function(){
		return this.value;
	},
	setReadOnly:function(bool){
		Ext.Array.each(this.items.items,function(item){
			if(item.setReadOnly)item.setReadOnly(bool);
			if(item.xtype='button'){
				if(bool) item.hide();
				else item.show();
			}
			
		});
		this.fireEvent('resize',this);
	},
	setFieldStyle:function(style){
		Ext.Array.each(this.items.items,function(item){
			if(item.setFieldStyle) item.setFieldStyle(style);
		});
	},
	hideScope: function(bool) {
		if(bool)
			this.items.items[0].hide();
		else 
			this.items.items[0].show();
	},
	mregex: /^[1-9]\d{3}[0-1]\d$/,
	getSystemSet: function() {
		var result = false;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			async: false,
			params: {
				caller: 'AttendSystem',
				fields: 'as_amstarttime,as_amendtime,as_pmstarttime,as_pmendtime',
				condition:'1=1'
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				} else if(r.success && r.data){
					result = r.data;
				}
			}
		});
		return result;
	},
	listeners: {
    	afterrender: function(){
    		var tb = this.getEl().dom;
    		tb.childNodes[1].style.height = 22;
        	tb.childNodes[1].style.overflow = 'hidden';
    	}
    }
});