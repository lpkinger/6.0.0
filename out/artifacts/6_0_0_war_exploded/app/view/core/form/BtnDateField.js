Ext.define("erp.view.core.form.BtnDateField", {  
    extend: "Ext.form.field.Trigger",  
    alias: "widget.btndatefield",
    id:'btndatefield',
    requires:['erp.view.core.picker.BtnDatePicker'],
    trigger1Cls : Ext.baseCSSPrefix + 'form-clear-trigger',
	trigger2Cls : Ext.baseCSSPrefix + 'form-date-trigger',
    isChoose1:false,
    isChoose2:false,
    chooseType:'',
    onTrigger1Click:function(){
    	var me = this;
    	me.inputEl.dom.disabled=false;
    	me.inputEl.dom.style.background="#eee";
    	me.reset();
    	me.resetValue(true);
    	me.fireEvent('dateclick');
    },
    resetValue:function(bool){
    	if(this.BtnPicker){
	    	if(bool){
	    		var a = Ext.getCmp(this.BtnPicker.dataIndex+'eq');
				a.el.dom.style.background = '#A0A0A0';
				this.BtnPicker.changeBtn(a,'eq');
				this.BtnPicker.fireEvent('clickHeadBtn','eq');
	    		this.BtnPicker.setMaxDate(new Date('2999-12-31'));
	        	this.BtnPicker2.setMinDate(new Date('1970-01-01'));
	    	}
	    	this.BtnPicker.setValue(new Date());
	    	this.BtnPicker2.setValue(new Date());
    	}
    },
    onTrigger2Click: function(){
    	var me = this;
    	if(me.BtnPicker&&me.rawValue==''){
    		me.resetValue();
    	}
    	var x = me.getEl().getX();
    	var y = me.getEl().getY()+me.getEl().getHeight();
    	if(!me.BtnPicker){
    		me.BtnPicker = me.createBtnPicker();
    		me.BtnPicker2 = me.createBtnPicker2();
    		me.BtnPicker.showAt(me.getX1(x),y);
    		me.mon(Ext.getDoc(), {
                mousewheel: me.collapseIf,
                mousedown: me.collapseIf,
                scope: me
            });
    		me.focus(true,100);
    	}else if(!me.BtnPicker.hidden){
    		me.BtnPicker.hide();
    		me.BtnPicker2.hide();
    	}else if(me.BtnPicker.hidden){
    		me.BtnPicker.showAt(me.getX1(x),y);
    		if(me.chooseType=='between'){
    			me.BtnPicker2.showAt(me.getX1(x),y+195);
    		}
    		me.BtnPicker.focus(true,100);
    	}
    	
    },
    //根据组件的位置改变日期在不同的位置显示
    getX1:function(x,picker){
    	var length = Ext.getBody().getWidth();
    	if(length - x < 220){
    		return x+this.getWidth()-220;
    	}else{
    		return x;
    	}
    },
    createBtnPicker: function() {  
        var me = this;
        return new Ext.create("erp.view.core.picker.BtnDatePicker",{  
            pickerField: me,
            floating: true,
            hidden: true,  
            disabledDaysText : "Disabled",
        	disabledDatesText : "Disabled",
            renderTo:Ext.getBody(),
            startDay : 0,
            dataIndex:this.dataIndex,
        	showToday : true,
            minText : "This date is before the minimum date",
        	maxText : "This date is after the maximum date",
        	onOkClick : function(b, e) {
        		var d = this, g = e[0], c = e[1], a = new Date(c, g, d.getActive()
        						.getDate());
        		if (a.getMonth() !== g) {
        			a = new Date(c, g, 1).getLastDateOfMonth()
        		}
        		d.update(a);
        		d.footerEl.setHeight(31);
        		d.hideMonthPicker();
        	},
        	onCancelClick : function() {
        		this.footerEl.setHeight(31);
        		this.hideMonthPicker();
        	},
            listeners: {  
                scope: me,
                select: function(picker,date){
                	var format = 'Y-m-d';
                	var value = '';
                	if(picker.isFormat!=''){
        				format = picker.isFormat;
        				picker.isFormat='';
        				value = Ext.Date.format(date,format);
                    	me.setValue('='+value);
                    	me.filterType='~';
                    	me.fireEvent('dateclick');
                    	picker.hide();
        			}else if(me.chooseType=='between'){
        				var datepicker = Ext.getCmp(me.dataIndex+'datepicker');
        				value = Ext.Date.format(date,format);
        				datepicker.setMinDate(date);
        				var value2 = Ext.Date.format(datepicker.value,format);
        				me.isChoose1 = true;
                		if(me.isChoose1&&me.isChoose2){
                			if(new Date(value)>new Date(value2)){
                				showError('上面开始日期不能晚于下面结束日期!');
                				return;
                			}
                			me.setValue(value+'~'+value2);
                			me.filterType='~';
                			me.fireEvent('dateclick');
                			me.inputEl.dom.disabled="disabled";
                			me.inputEl.dom.style.background="#C8C8C8";
                			datepicker.hide();
                			picker.hide();
                			me.isChoose1 = false;
                			me.isChoose2 = false;
                		}else{
                			return false;
                		}
        			}else{
        				value = Ext.Date.format(date,format);
        				if(me.chooseType=='startWith'){
        					me.filterType='>='
        					me.setValue('>='+value);
        				}else if(me.chooseType=='endWith'){
        					me.filterType='<='
        					me.setValue('<='+value);
        				}else{
        					me.setValue('='+value);
        					me.filterType='='
        				}
        				me.fireEvent('dateclick');
                    	picker.hide();
        			}
                	me.focus();
                },
                'clickHeadBtn':function(type){
                	if(type == 'eq'||type == 'startWith'||type == 'endWith'){
                		me.BtnPicker2.hide();
                	}else if(type == 'between'){
                		var x = me.getEl().getX();
                    	var y = me.getEl().getY()+me.getEl().getHeight();
                    	me.BtnPicker2.showAt(me.getX1(x),y+195);
                	}
                	me.chooseType = type;
                },
                show:function(picker){
                	var value = me.value==''?me.emptyText:me.value;
                	if(value!=null && value!=''&&me.chooseType!='between'){
                		var reg = /^(>=|<=|=)?[0-9]{4}-((0[1-9])|(1[0-2])){1}-((0[1-9])|((1|2)[0-9])|((3)[0-1])){1}$/;
                		if(value.match(reg)==null){
            				picker.callbackValue(picker,'eq',new Date());
                		}else{
                			if(value.indexOf('>=')==0){
                				picker.callbackValue(picker,'startWith',new Date(value.split('>=')));
                			}else if(value.indexOf('<=')==0){
                				picker.callbackValue(picker,'endWith',new Date(value.split('<=')));
                			}else if(value.indexOf('=')==0){
                				picker.callbackValue(picker,'eq',new Date(value.split('=')));
                			}else{
                				picker.callbackValue(picker,'eq',new Date(value));
                			}
                		}
                	}
                }
            },
            callbackValue:function(picker,type,date){
            	var a = Ext.getCmp(me.dataIndex+type);
    			picker.setValue(date);
    			a.el.dom.style.background = '#A0A0A0';
				picker.changeBtn(a,type);
				picker.fireEvent('clickHeadBtn',type);
            },
            keyNavConfig: {  
                esc: function() {  
                	me.BtnPicker.hide();
            		me.BtnPicker2.hide();
                }  
            },
          id:me.dataIndex+'btnpicker'
        });
    },
     collapseIf: function(e) {
        var me = this;
        if (!me.isDestroyed && !e.within(me.bodyEl, false, true) && !e.within(me.BtnPicker.el, false, true)&& !e.within(me.BtnPicker2.el, false, true)) {
            me.BtnPicker.hide();
            me.BtnPicker2.hide();
        }
    },
    createBtnPicker2: function() {  
        var me = this; 
        return new Ext.create("Ext.picker.Date",{  
            pickerField: me,
            floating: true,
            hidden: true,  
            disabledDaysText : "Disabled",
        	disabledDatesText : "Disabled",
            renderTo:Ext.getBody(),
            startDay : 0,
        	showToday : true,
            minText : "This date is before the minimum date",
        	maxText : "This date is after the maximum date",
        	onOkClick : function(b, e) {
        		var d = this, g = e[0], c = e[1], a = new Date(c, g, d.getActive()
        						.getDate());
        		if (a.getMonth() !== g) {
        			a = new Date(c, g, 1).getLastDateOfMonth()
        		}
        		d.update(a);
        		d.footerEl.setHeight(0);
        		//消除包裹日期组件的阴影
        		d.el.prev().setHeight(d.el.prev().getHeight()-31);
        		d.hideMonthPicker();
        	},
        	onCancelClick : function() {
        		this.footerEl.setHeight(0);
        		this.el.prev().setHeight(this.el.prev().getHeight()-31);
        		this.hideMonthPicker();
        	},
            listeners: {  
                afterrender:function(d){
                	d.todayBtn.hide();
                	d.setWidth(220);
                	d.monthBtn.on('click',function(){d.footerEl.setHeight(31);});
                },
                select: function(picker,date){
                	var value = Ext.Date.format(date,'Y-m-d');
                	var btndatepicker = Ext.getCmp(me.dataIndex+'btnpicker');
                	btndatepicker.setMaxDate(date);
                	if(btndatepicker.between){
                		var value1 = Ext.Date.format(btndatepicker.value,'Y-m-d');
                		me.isChoose2 = true;
                		if(me.isChoose1!=''&&me.isChoose2!=null){
                			if(new Date(value1)>new Date(value)){
                				showError('下面结束日期不能早于上面开始日期!');
                				return;
                			}
                			me.setValue(value1+'~'+value);
                			me.filterType='~';
                			me.fireEvent('dateclick');
                			me.inputEl.dom.disabled="disabled";
                			me.inputEl.dom.style.background="#C8C8C8";
                			btndatepicker.hide();
                			picker.hide();
                			me.isChoose1 = false;
                			me.isChoose2 = false;
                		}
                	}
                },
                show:function(picker){
                	var picker2 = Ext.getCmp(me.dataIndex+'btnpicker');
                	picker.setMinDate(picker2.value);
                }
            },  
            keyNavConfig: {  
                esc: function() {  
                	me.BtnPicker.hide();
            		me.BtnPicker2.hide();
                }  
            },
           id:me.dataIndex+'datepicker'
        });
    },
    listeners:{
    	afterrender:function(me){
    		this.triggerEl.item(0).setDisplayed('none');
    		this.on('dataclick',function(){});
    	},
    	'change':function(me,newValue){
    		//只有介于的日期才会不可编辑并且提供删除按钮
    		if(newValue.indexOf('~')>-1){
    			me.triggerEl.item(0).setDisplayed('block');
    			me.triggerEl.item(1).setDisplayed('none');
    		}else if(newValue==''){
    			me.triggerEl.item(0).setDisplayed('none');
    			me.triggerEl.item(1).setDisplayed('block');
    		}
    	},
    	'resetBtn':function(){
    		if(this.BtnPicker){
    			this.BtnPicker.eqBtn.el.dom.style.background = '#A0A0A0';
    			this.BtnPicker.changeBtn(this.BtnPicker.eqBtn,'eq');
    			this.BtnPicker.fireEvent('clickHeadBtn','eq');
    		}
    	}
    }
});