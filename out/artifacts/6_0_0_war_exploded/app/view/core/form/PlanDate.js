Ext.define('erp.view.core.form.PlanDate', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.plandate',
    layout: 'hbox',
    items: [],
    height: 22,
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
    	me.insert(0, {
    		xtype: 'combo',
    		flex: 1,
    		editable: false,
    		fieldStyle: 'background:#C1CDC1',
    		store: Ext.create('Ext.data.Store', {
    		    fields: ['display', 'value'],
    		    data : [
    		        {"display":"月计划", "value": '月计划'},
    		        {"display":"年计划", "value": '年计划'},
    		        {"display":"季度计划", "value": '季度计划'}
    		    ]
    		}),
    	    queryMode: 'local',
    	    displayField: 'display',
    	    valueField: 'value',
    	    value: '月计划',
    	    listeners: {
    	    	select: function(combo, records, obj){
    	    		me.changePlan(combo.value);  
    	    	}
    	    }
    	});
    	me.insert(1, Ext.create('erp.view.core.form.MonthDateField', {
			flex: 1,
			editable: false,
			fromnow: true,
			name: me.name,
//			listeners: {
//				render: function(f){
//					if(me.items.items[0].value != '月计划'){
//						alert(f.value);
//						alert(me.items.items[0].value);
////						me.items.items[1].setValue(f.value);
//						me.changePlan(me.items.items[0].value);						
//					}
//				}
//    	    }
		}));
    	me.insert(2, {
			flex: 3,
			xtype: 'displayfield',
			listeners: {
				change: function(f){
					var form = f.up('erpWorkPlanFormPanel');
					if(form){
						var type = form.down('dbfindtrigger').value;
						var lastplan = form.down('workplanfield');
						var nextplan = form.down('workplanfield2');
						var summary = form.down('#wp_summary');
//						console.log(summary);
						var title = '';//nextplan计划标题
						var title2 = '';//lastplan计划标题
						var val = this.ownerCt.items.items[0].value,
			    		    val2 = this.ownerCt.items.items[1].value,
			    		    y,m;
						switch (val) {
						case '月计划':
							if(/^[1-9]\d{3}[0-1]\d$/.test(val2)){
								y = val2.toString().substring(0, 4);
								m = val2.toString().substr(4); 
								title = em_name + '的' + y + '年' + m + '月' + type;
								if(m != '01'){
									var moo = (m-1)<10 ? ('0'+(m-1)) : (m-1);
									lastplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年'+moo+'月份计划');	
									summary.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年'+moo+'月份总结');
									title2 = em_name + '的' + y + '年' + moo + '月' + type;
								} else {
									title2 = em_name + '的' + (y-1) + '年' + 12 + '月' + type;
									lastplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+(y-1)+'年'+12+'月份计划');
									summary.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+(y-1)+'年'+12+'月份总结');
								}
								nextplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年'+m+'月份计划');
							}
							break;
						case '年计划':
							if(/^[1-9]\d{3}$/.test(val2)){
								y = val2.toString().substring(0, 4);
								title = em_name + '的' + y + '年年度' + type;
								title2 = em_name + '的' + (y-1) + '年年度' + type;
								lastplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+(y-1)+'年年度计划');
								summary.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+(y-1)+'年年度总结');
								nextplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年年度计划');
							}
							break;
						case '季度计划':
							if(/^[1-9]\d{3}[1-4]$/.test(val2)){
								y = val2.toString().substring(0, 4);
								m = Number(val2.toString().substr(4)); 
								title = em_name + '的' + y + '年第' + m + '季度' + type;
								if(m != 1){
									title2 = em_name + '的' + y + '年第' + (m-1) + '季度' + type;
									lastplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年第'+(m-1)+'季度计划');
									summary.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年第'+(m-1)+'季度总结');
								} else {
									title2 = em_name + '的' + (y-1) + '年第' + 4 + '季度' + type;
									lastplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+(y-1)+'年第'+4+'季度计划');
									summary.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+(y-1)+'年第'+4+'季度总结');
								}
								nextplan.setTitle('<img src="' + basePath + 'resource/images/icon/detail.png" width=20 height=20/>&nbsp;&nbsp;'+y+'年'+m+'季度计划');
							}
							break;
						default:
							break;
			    	    }
						form.down('#lasttitle').setValue(title2);//一定要先给title2赋值	
						form.down('textfield').setValue(title);	
						form.down('#import').setText('查看'+title2+'完成情况');
					}
				}
			}
		});
    	me.setValue(me.value);
	},
	changePlan: function(val, v){
		var me = this;
		var value = me.items.items[1].value;
		me.items.items[1].destroy(true);
		switch (val) {
			case '月计划':
				me.insert(1, Ext.create('erp.view.core.form.MonthDateField', {
					flex: 1,
					editable: false,
					fromnow: true,
					value: value,
					name: me.name,
					listeners: {
						afterrender: function(f){
							if(v){
								f.setDisabled(true);
							}
						},
						change: function(){
							me.showValue();
						}
					}
				}));
				break;
			case '年计划':
				me.insert(1, Ext.create('erp.view.core.form.YearDateField', {
					flex: 1,
					editable: false,
					fromnow: true,
					value: v==null? value : v,
					name: me.name,
					listeners: {
						afterrender: function(f){
							if(v){
								f.setDisabled(true);
							}
						},
						change: function(){
							me.showValue();
						}
					}
				}));
				break;
			case '季度计划':
				me.insert(1, {
					xtype: 'fieldcontainer',
					flex: 2,
					layout: 'hbox',
					items: [Ext.create('erp.view.core.form.YearDateField', {
						flex: 1,
						editable: false,
						fromnow: true,
						listeners: {
							afterrender: function(f){
								if(v){
									f.fromnow = false;
									f.setValue(v.substring(0, 4));
									f.setDisabled(true);
								}
							},
							change: function(f){
								f.ownerCt.value = f.value + '' + f.ownerCt.down('combobox').value;
								me.showValue();
							}
						}
					}),{
						flex: 1.5,
						xtype: 'combobox',
						editable: false,
			    		store: Ext.create('Ext.data.Store', {
			    		    fields: ['display', 'value'],
			    		    data : [
			    		        {"display":"第一季度", "value": 1},
			    		        {"display":"第二季度", "value": 2},
			    		        {"display":"第三季度", "value": 3},
			    		        {"display":"第四季度", "value": 4}
			    		    ]
			    		}),
			    	    queryMode: 'local',
			    	    displayField: 'display',
			    	    valueField: 'value',
			    	    value: 1,
			    	    name: me.name,
						listeners: {
							afterrender: function(f){
								if(v){
									f.setValue(v.substring(4));
									f.setDisabled(true);
								}
							},
							change: function(f){
								f.ownerCt.value = f.ownerCt.down('yeardatefield').value + '' + f.value;
								me.showValue();
							}
						}
					}],
					listeners: {
						afterrender: function(f){
							f.value = f.ownerCt.down('yeardatefield').value + '' + f.down('combobox').value;
							me.showValue();
						}
					}
				});
				break;
			default:
				break;
		}
		this.showValue();
	},
	reset: function(){
		var me = this;
		me.items.items[0].reset();
		me.items.items[1].reset();
	},
	setValue: function(value){
		var me = this;
		if(value && contains(value, ';')){
			var p = value.split(';')[0],
				v = value.split(';')[1];
			if(!Ext.isEmpty(p)){
				me.items.items[0].setValue(p);
				me.items.items[0].setDisabled(true);
				me.items.items[1].setValue(v);					
				me.items.items[1].setDisabled(true);
				if(p != '月计划'){
					me.changePlan(p, v);
				}
			}
		}
		this.showValue();
	},
	listeners: {
    	afterrender: function(){
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    	}
    },
    getValue: function(){//以;隔开{类型;值}
    	var me = this;
    	return me.items.items[0].value + ';' + me.items.items[1].value;
    },
    isValid: function(){
    	return true;
    },
    showValue: function(){
    	var val = this.items.items[0].value,
    		value = this.items.items[1].value,
    		s = '',
    		y,m;
    	switch (val) {
			case '月计划':
				if(/^[1-9]\d{3}[0-1]\d$/.test(value)){
					y = value.toString().substring(0, 4);
					m = value.toString().substr(4);
					var d = new Date(y, Number(m), 1);
					d = new Date(d.getTime() - 24*60*60*1000).getDate();  
					s = y + '-' + m + '-01 ~ ' + y + '-' + m + '-' + d;
				}
				break;
			case '年计划':
				if(/^[1-9]\d{3}$/.test(value)){
					y = value.toString().substring(0, 4);
					s = y + '-01-01 ~ ' + y + '-12-31';
				}
				break;
			case '季度计划':
				if(/^[1-9]\d{3}[1-4]$/.test(value)){
					y = value.toString().substring(0, 4);
					m = Number(value.toString().substr(4));
					var d = new Date(y, Number(3*m), 1);
					d = new Date(d.getTime() - 24*60*60*1000).getDate();  
					s = y + '-' + (3*m - 2) + '-01 ~ ' + y + '-' + 3*m + '-' + d;
				}
				break;
			default:
				break;
    	}
    	this.down('displayfield').setValue(s);
    }
});