/**
 * 选择年份picker
 */
Ext.define('erp.view.core.form.YearDateField', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.yeardatefield',
    triggerCls: Ext.baseCSSPrefix + "form-date-trigger",
    initComponent : function(){
    	this.callParent(arguments);
    	if(this.fromnow){
			this.minYear = new Date().getFullYear();
    		this.setMinValue(this.minYear);
    	}
    },
    onTriggerClick: function(){
    	var me = this;
    	if(this.yearPicker && !this.yearPicker.hidden){
    		this.yearPicker.hide();
    		return;
    	}
    	this.createyearPicker().show();
    	this.yearPicker.prevEl.dom.onclick = function(){//翻页时刷新样式
    		me.setMaxValue(me.maxYear);
    		me.setMinValue(me.minYear);
    	};
    	this.yearPicker.nextEl.dom.onclick = function(){
    		me.setMaxValue(me.maxYear);
    		me.setMinValue(me.minYear);
    	};
    },
    fromnow: false,
    regex: /^[1-9]\d{3}$/,
    regexText: '格式不正确!',
    createyearPicker: function(){
    	var b = this, a = b.yearPicker;
    	if (!a) {
			b.yearPicker = a = Ext.create("Ext.picker.Month", {
						renderTo : Ext.getBody(),
						floating : true,
						ownerCt: b,
						listeners : {
							scope : b,
							cancelclick : b.onCancelClick,
							okclick : b.onOkClick,
							yeardblclick : b.onOkClick,
							afterrender: function(p){
								if(b.maxValue){
									p.setMaxDate(b.maxValue);
								}
								if(b.minValue){
									p.setMinDate(b.minValue);
								}
							}
						},
						setMaxDate : function(dt){
					        this.maxDate = dt;
					        var years = this.years;
					        Ext.each(years.elements, function(el){
					        	if(Number(el.innerHTML) > dt.getFullYear()){
					        		el.style.color = '#EEE9E9';
					        	} else {
					        		el.style.color = 'black';
					        	}
					        });
					    },
					    setMinDate : function(dt){
					        this.minDate = dt;
					        var years = this.years;
					        Ext.each(years.elements, function(el){
					        	if(Number(el.innerHTML) < dt.getFullYear()){
					        		el.style.color = '#EEE9E9';
					        	} else {
					        		el.style.color = 'black';
					        	}
					        });
					    },
						totalYears: 10,
					    yearOffset: 5,
						renderTpl: [
						            '<div id="{id}-bodyEl" class="{baseCls}-body">',
						              '<div class="{baseCls}-years">',
						                  '<div class="{baseCls}-yearnav">',
						                      '<button id="{id}-prevEl" class="{baseCls}-yearnav-prev"></button>',
						                      '<button id="{id}-nextEl" class="{baseCls}-yearnav-next"></button>',
						                  '</div>',
						                  '<tpl for="years">',
						                      '<div class="{parent.baseCls}-item {parent.baseCls}-year"><a href="#" hidefocus="on">{.}</a></div>',
						                  '</tpl>',
						              '</div>',
						              '<div class="' + Ext.baseCSSPrefix + 'clear"></div>',
						            '</div>',
						            '<tpl if="showButtons">',
						              '<div id="{id}-buttonsEl" class="{baseCls}-buttons"></div>',
						            '</tpl>'
						        ]
					});
			a.alignTo(b.inputEl, 'tl-bl?');
		}
		return a;
    },
    onCancelClick: function(){
    	this.yearPicker.hide();
    },
    onOkClick: function(){
    	var vals = this.yearPicker.getValue();
    	var a = vals[1];
    	if(vals.length == 2){
    		a = a == null ? new Date().getFullYear() : a;
    		if(this.minValue){
    			if(Number(a) < this.minYear){
    				return;
    			}
    		}
    		if(this.maxValue){
    			if(Number(a) > this.maxYear){
    				return;
    			}
    		}
    		this.setValue(a);
    	}
    	this.yearPicker.hide();
    },
    setMaxValue: function(value){
    	if(this.regex.test(value)){
	    	var me = this,
	          	picker = me.yearPicker,
	          	maxValue = Ext.Date.parse(value.toString().substring(0, 4), 'Y');
	 	     me.maxValue = maxValue;
	 	     me.maxYear = Number(value.toString().substring(0, 4));
	 	     if (picker) {
	 	         picker.setMaxDate(maxValue);
	 	     }
    	}
    },
    setMinValue : function(value){
    	if(this.regex.test(value)){
	    	var me = this,
	          	picker = me.yearPicker,
	          	minValue = Ext.Date.parse(value.toString().substring(0, 4), 'Y');
	 	     me.minValue = minValue;
	 	     me.minYear = Number(value.toString().substring(0, 4));
	 	     if (picker) {
	 	         picker.setMinDate(minValue);
	 	     }
    	}
    },
    setValue: function(value){
    	if(!this.regex.test(value)){
    		value = new Date().getFullYear();
    	}
    	this.callParent(arguments);
    }
});