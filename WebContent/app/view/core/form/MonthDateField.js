/**
 * 选择年月picker
 * --2017.9.13 hey 添加grid兼容
 */
Ext.define('erp.view.core.form.MonthDateField', {
	extend : 'Ext.form.field.Trigger',
	alias : 'widget.monthdatefield',
	triggerCls : Ext.baseCSSPrefix + "form-date-trigger",
	initComponent : function() {
		this.callParent(arguments);
		this.addEvents({
			afterChangeValue : true
		});
	},
	listeners : {
		afterrender : function(field) {
			if (field.fromnow) {
				this.minYearMonth = this.currentMonth();
				this.setMinValue(this.minYearMonth);
			}
			if (field.autoValue && Ext.isEmpty(field.value)) {
				field.setValue(field.currentMonth());
			}
		},
		change : function(field) {
			if (this.monthPicker && !this.monthPicker.hidden) {
				this.monthPicker.hide();
			}
		}
	},	
	currentMonth : function() {
		return Number(Ext.Date.format(new Date(), 'Ym'));
	},
	onTriggerClick : function() {
		var me = this;		
		if (this.monthPicker && !this.monthPicker.hidden) {
			this.monthPicker.hide();
			return;
		}		
		//hey start 在主从表字段下弹窗
		me.getOwner();//获取grid
		if(this.owner&&this.owner.xtype.indexOf('Grid')>=0){		
			var record = this.owner.selModel.lastSelected;
			if(record!=null&&this.ownerCt.xtype!='toolbar'){
				if(record&&record.index>=7){
					this.createMonthPicker().showAt(this.column.x-40,this.column.y-200);
				}
				else this.createMonthPicker().showAt(this.column.x-40,this.column.y+25);
			}
			else this.createMonthPicker().show();
		}else{
			this.createMonthPicker().show();
		}	
		//hey end 
		this.monthPicker.prevEl.dom.onclick = function() {// 翻页时刷新年月样式
			me.setMaxValue(me.maxYearMonth);
			me.setMinValue(me.minYearMonth);
		};
		this.monthPicker.nextEl.dom.onclick = function() {
			me.setMaxValue(me.maxYearMonth);
			me.setMinValue(me.minYearMonth);
		};
	},
	fromnow : false,// 可选择的月份从当前时间开始
	autoValue : true, // 取当前时间
	regex : /^[1-9]\d{3}[0-1]\d$/,
	regexText : '格式不正确!',
	editable:false,
	createMonthPicker : function() {
		var b = this, a = b.monthPicker;
		if (!a) {
			b.monthPicker = a = Ext.create("Ext.picker.Month", {
				renderTo : Ext.getBody(),
				floating : true,
				ownerCt : b,
				listeners : {
					scope : b,
					cancelclick : b.onCancelClick,
					okclick : b.onOkClick,
					yearclick : b.onYearChoose,
					yeardblclick : b.onOkClick,
					monthdblclick : b.onOkClick,
					afterrender : function(p) {
						if(b.value!=null &&b.value.length==6){
							var year=b.value.substring(0,4);
							var month=b.value.substring(4,6);
							var time =year+'-'+month;
							p.setValue(new Date(time));
						}
						
						if (b.maxValue) {
							p.setMaxDate(b.maxValue);
						}
						if (b.minValue) {
							p.setMinDate(b.minValue);
						}
					}
				},
				setMaxDate : function(dt) {
					this.maxDate = dt;
					var years = this.years;
					Ext.each(years.elements, function(el) {
						if (Number(el.innerHTML) > dt.getFullYear()) {
							el.style.color = '#EEE9E9';
						} else {
							el.style.color = 'black';
						}
					});
				},
				setMinDate : function(dt) {
					this.minDate = dt;
					var years = this.years;
					Ext.each(years.elements, function(el) {
						if (Number(el.innerHTML) < dt.getFullYear()) {
							el.style.color = '#EEE9E9';
						} else {
							el.style.color = 'black';
						}
					});
				}
			});
			a.alignTo(b.inputEl, 'tl-bl?');
		}
		return a;
	},
	onCancelClick : function() {
		this.monthPicker.hide();
	},
	onOkClick : function() {
		var vals = this.monthPicker.getValue();
		var a = vals[0], b = vals[1];
		if (vals.length == 2) {
			a = a == null ? new Date().getMonth() : a;
			a = Number(a) + 1;
			a = a < 10 ? '0' + a : a;
			b = b == null ? new Date().getFullYear() : b;
			if (this.minValue) {
				if (Number(b + '' + a) < this.minYearMonth) {
					return;
				}
			}
			if (this.maxValue) {
				if (Number(b + '' + a) > this.maxYearMonth) {
					return;
				}
			}
			//hey start 主从表字段赋值
			if(this.owner&&this.owner.xtype.indexOf('Grid')>=0&&this.ownerCt.xtype!='toolbar'){
				var value = (b + '' + a);
				if (!Ext.isEmpty(value) && !this.regex.test(value)) {			
					var y = new Date().getFullYear(), m = new Date().getMonth() + 1;
					m = m < 10 ? '0' + m : m;
					value = Number(y + '' + m);
				}				
				record = this.owner.selModel.lastSelected;
				if(record!=null) record.set(this.name,value);
				else this.setValue(b + '' + a);
			}else{
				this.setValue(b + '' + a);
			}
			//hey end 主从表字段赋值
		}
		this.fireEvent('afterChangeValue', this);
		this.monthPicker.hide();
	},
	setMaxValue : function(value) {
		if (this.regex.test(value)) {
			var me = this, picker = me.monthPicker, maxValue = Ext.Date.parse(
					value.toString().substring(0, 4) + '-'
							+ value.toString().substring(4, 6), 'Y-m');
			me.maxValue = maxValue;
			me.maxYearMonth = Number(value.toString().substring(0, 4) + ''
					+ value.toString().substring(4, 6));
			if (picker) {
				picker.setMaxDate(maxValue);
			}
		}
	},
	setMinValue : function(value) {
		if (this.regex.test(value)) {
			var me = this, picker = me.monthPicker, minValue = Ext.Date.parse(
					value.toString().substring(0, 4) + '-'
							+ value.toString().substring(4, 6), 'Y-m');
			me.minValue = minValue;
			me.minYearMonth = Number(value.toString().substring(0, 4) + ''
					+ value.toString().substring(4, 6));
			if (picker) {
				picker.setMinDate(minValue);
			}
		}
	},
	onYearChoose : function(picker, value, e) {
		var me = this;
		var bool;
		Ext.each(picker.months.elements, function(el, m) {// (m+1)的实际为1,3,5,7,9,11,2,4,6,8,10,12,并非与月份对应
			m = me.getMonthNum(el.innerHTML);
			var ym = Number('' + value[1] + '' + m);
			bool = true;
			if (me.maxYearMonth && ym > me.maxYearMonth) {
				el.style.color = '#EEE9E9';
				bool = false;
			} else {
				el.style.color = 'black';
			}
			if (me.minYearMonth && ym < me.minYearMonth) {
				el.style.color = '#EEE9E9';
			} else {
				if (bool) {
					el.style.color = 'black';
				}
			}
		});
	},
	getMonthNum : function(m) {
		var arr = {
			"一月" : '01',
			"二月" : '02',
			"三月" : '03',
			"四月" : '04',
			"五月" : '05',
			"六月" : '06',
			"七月" : '07',
			"八月" : '08',
			"九月" : '09',
			"十月" : '10',
			"十一月" : '11',
			"十二月" : '12'
		};
		return arr[m];
	},
	setValue : function(value) {				
		if (!Ext.isEmpty(value) && !this.regex.test(value)) {			
			var y = new Date().getFullYear(), m = new Date().getMonth() + 1;
			m = m < 10 ? '0' + m : m;
			value = Number(y + '' + m);
		}
		this.callParent(arguments);
	},
	hasValid : function() {
		return this.regex.test(this.value);
	},
	getOwner: function() {
		var me = this;
		if (me.el) {
			var gridEl = me.el.up('.x-grid');
			if (gridEl) {
				var grid = Ext.getCmp(gridEl.id);
				if (grid) {
					me.owner = grid;
					me.column = grid.down('gridcolumn[dataIndex=' + me.name + ']');
				}
			}
		}
	}
});