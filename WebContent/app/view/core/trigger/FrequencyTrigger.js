Ext.define('erp.view.core.trigger.FrequencyTrigger',
				{	extend : 'Ext.form.field.Trigger',
					alias : 'widget.frequencytrigger',
					requiers : [ 'erp.view.core.form.TimeMinuteField',
							'erp.view.core.picker.TimePicker','erp.view.core.picker.HighlightableDatePicker' ],
					triggerCls : 'x-form-freq-trigger',
					onTriggerClick : function() {
						var currentDate=new Date();
						var trigger = this,freqwin = this.freqwin;
						this.setFieldStyle('background:#C6E2FF;');
						var bool=false;
						if (!freqwin) {
							bool=true;
						freqwin = new Ext.window.Window({
							id : 'freqwin',
							height : 345,
							width : 700,
							maximizable : false,
							resizable:false,
							closable :false,
							buttonAlign : 'center',
							layout : 'border',
							defaults: 
			                {
			                 split: true,                
			                 bodyStyle: 'background:#EDEDED;'
			                }, 
			         	   buttons : [{
			        		   text : '确认',	
			        		   iconCls: 'x-button-icon-save',
			        		   cls: 'x-btn-gray',
			        		   handler : function(btn){
			        			   trigger.setValue(btn.ownerCt.ownerCt.items.items[4].items.items[0].getValue().replace(/<br>/g, ''));
			        			   if(Ext.getCmp('freqwin').items.items[3].items.items[0].items.items[0].minutePicker)Ext.getCmp('freqwin').items.items[3].items.items[0].items.items[0].minutePicker.hide();
			        			   Ext.getCmp('freqwin').hide();
			        			  
			        		   }
			        	   },{
			        		   text : '关  闭',
			        		   iconCls: 'x-button-icon-close',
			        		   cls: 'x-btn-gray',
			        		   handler : function(){
			        			   if(Ext.getCmp('freqwin').items.items[3].items.items[0].items.items[0].minutePicker)Ext.getCmp('freqwin').items.items[3].items.items[0].items.items[0].minutePicker.hide();
			        			   Ext.getCmp('freqwin').hide();
			        		   }
			        	   }],
							items : [ {
								xtype : 'panel',
								value:'FREQ=DAILY;',
								region : 'north',
								height:50,
								items : [ {
									fieldLabel : '推送周期',
									xtype : 'combo',
									editable : false,									
									store : Ext.create('Ext.data.Store', {
										fields : [ 'display', 'value' ],
										data : [ {
											"display" : "月",
											"value" : 'monthly'
										}, {
											"display" : "周",
											"value" : 'weekly'
										}, {
											"display" : "日",
											"value" : 'daily'
										} ]
									}),
									queryMode : 'local',
									displayField : 'display',
									valueField : 'value',
									value : '日',
									listeners : {
										afterrender:function(){
											trigger.showValue();
										},
										select : function(combo, records, obj) {
											trigger.changeCombo(combo.value);
											trigger.showValue();
										}
									}
								} ]
							}, {
								xtype : 'panel',
								region : 'west',
								value:'',
								layout:'fit',
								width:'40%',
								height:50,
								items : [ {
									xtype: 'highlightdate',
		                            itemId: 'datePicker',
									fieldStyle : 'background:#EDEDED',
									format: "Y-m", 
									maxDate:new Date(currentDate.getFullYear(),currentDate.getMonth(),31),
									minDate:new Date(currentDate.getFullYear(),currentDate.getMonth(),1),
									tmpMonthDay:[],
									listeners : {									
										select:function(picker,date){
											if(picker.selectedDates[date.toDateString()])
											Ext.Array.remove(picker.tmpMonthDay,Ext.util.Format.date(date, 'd'));
											else picker.tmpMonthDay.push(Ext.util.Format.date(date, 'd'));
											var BYMONTHDAY='';
											if (picker.tmpMonthDay.length>0)BYMONTHDAY='BYMONTHDAY='+Ext.Array.unique(picker.tmpMonthDay).join(',')+';';	
											this.ownerCt.value=BYMONTHDAY;
											trigger.showValue();
										}
									}
								} ]
							}, {
								xtype : 'form',
								region : 'center',
								value:'',
								title:'星期',
								width:'30%',
								items : [ {
									xtype : 'checkboxgroup',
									columns:2,
							        vertical: true,
									items : [{
										boxLabel : '星期一',
										name : 'MON',
										inputValue : 'MON'
									}, {
										boxLabel : '星期二',
										name : 'TUE',
										inputValue : 'TUE'
									}, {
										boxLabel : '星期三',
										name : 'WED',
										inputValue : 'WED'
									}, {
										boxLabel : '星期四',
										name : 'THU',
										inputValue : 'THU'
									}, {
										boxLabel : '星期五',
										name : 'FRI',
										inputValue : 'FRI'
									}, {
										boxLabel : '星期六',
										name : 'SAT',
										inputValue : 'SAT'
									}, {
										boxLabel : '星期日',
										name : 'SUN',
										inputValue : 'SUN'
									}],
									listeners:{
										change:function(){
											var BYDAY='';
											if(Ext.Object.getValues(this.getValue()).length>0)BYDAY='BYDAY='+Ext.Object.getValues(this.getValue()).join(',')+';';	
											this.ownerCt.value=BYDAY;
											trigger.showValue();
										}
									}
								} ]
								
							}, {
								xtype : 'panel',							
								region : 'south',
								value:'',
								height:50,
								items : [ {
									xtype : 'fieldcontainer',
									fieldLabel : '时间',
									hour:[],
									layout : {
										type : 'column',
									},
									items : [ {
										xtype : 'multihourfield',
										editable:false,
										listeners:{
											afterChangeValue: function(t){
												if(t.value!=""){
													var hour = t.value;								
													var BYHOUR='BYHOUR='+hour+';',BYMINUTE='BYMINUTE=0;',BYSECOND='BYSECOND=0;';
													t.ownerCt.ownerCt.value=BYHOUR+'<br>'+BYMINUTE+'<br>'+BYSECOND;
												}
												else t.ownerCt.ownerCt.value='';
												trigger.showValue();
											},
										}
									}, {
										xtype : 'button',
										text : '清除',
										width : 80,
										handler : function(btn) {
											btn.ownerCt.items.items[0].setValue(null);
											btn.ownerCt.ownerCt.value='';
											trigger.showValue();
										}
									}]
								} ]
							}, {
								xtype : 'panel',
								region : 'east',
								title:'推送频率',
								width:'30%',
								autoScroll:true,
								items : [ {
									xtype : 'displayfield',
									name : 'freqDisplay',			
									value : 'FREQ=DAILY;'
								} ]
							} ],

						});
						this.freqwin = freqwin;
						freqwin.items.items[1].setDisabled(true);
						freqwin.items.items[2].setDisabled(true);
												
						}																	
						freqwin.show();	
						
						if(bool){
						if(trigger.value.indexOf('FREQ=DAILY;')>=0) {freqwin.items.items[0].items.items[0].setValue('daily').fireEvent('select',freqwin.items.items[0].items.items[0],freqwin.items.items[0].items.items[0].getStore().getById('daily'));}
						else if(trigger.value.indexOf('FREQ=WEEKLY;')>=0) {
							freqwin.items.items[0].items.items[0].setValue('weekly').fireEvent('select',freqwin.items.items[0].items.items[0],freqwin.items.items[0].items.items[0].getStore().getById('weekly'));					
							if(trigger.value.indexOf('BYDAY')>=0){
								var days=trigger.value.substring(trigger.value.indexOf('BYDAY')+6,trigger.value.indexOf(';',trigger.value.indexOf('BYDAY'))).split(',');													
								var items=freqwin.items.items[2].items.items[0].items.items;
								Ext.each(items, function(item){
									if(Ext.Array.contains(days, item.name)) item.setValue(true);
									});
							}							
						}						
						else if(trigger.value.indexOf('FREQ=MONTHLY;')>=0) {
							freqwin.items.items[0].items.items[0].setValue('monthly').fireEvent('select',freqwin.items.items[0].items.items[0],freqwin.items.items[0].items.items[0].getStore().getById('monthly'));						
							if(trigger.value.indexOf('BYMONTHDAY')>=0){
								var monthdays=trigger.value.substring(trigger.value.indexOf('BYMONTHDAY')+11,trigger.value.indexOf(';',trigger.value.indexOf('BYMONTHDAY'))).split(',');													
								var item=freqwin.items.items[1].items.items[0];											
								Ext.each(monthdays, function(monthday){										
								freqwin.items.items[1].items.items[0].setValue(new Date(currentDate.getFullYear(),currentDate.getMonth(),monthday)).fireEvent('select',freqwin.items.items[1].items.items[0],new Date(currentDate.getFullYear(),currentDate.getMonth(),monthday));												
								});
						}
						}
						if(trigger.value.indexOf('BYHOUR')>=0 && trigger.value.indexOf('BYMINUTE')>=0){
						var hour=trigger.value.substring(trigger.value.indexOf('BYHOUR')+7,trigger.value.indexOf(';',trigger.value.indexOf('BYHOUR')));																	
						var BYHOUR='BYHOUR='+hour+';',BYMINUTE='BYMINUTE=0;',BYSECOND='BYSECOND=0;';
						freqwin.items.items[3].items.items[0].items.items[0].setValue(hour);
						freqwin.items.items[3].value=BYHOUR+'<br>'+BYMINUTE+'<br>'+BYSECOND;
						trigger.showValue();}
						}	
					},
					changeCombo: function(val, v){
						switch (val) {
							case 'daily':								
								Ext.getCmp('freqwin').items.items[1].setDisabled(true);
								Ext.getCmp('freqwin').items.items[2].setDisabled(true);
								Ext.getCmp('freqwin').items.items[0].value='FREQ=DAILY;';
								break;
							case 'weekly':
								Ext.getCmp('freqwin').items.items[1].setDisabled(true);
								Ext.getCmp('freqwin').items.items[2].setDisabled(false);
								Ext.getCmp('freqwin').items.items[0].value='FREQ=WEEKLY;';
								break;
							case 'monthly':
								Ext.getCmp('freqwin').items.items[1].setDisabled(false);
								Ext.getCmp('freqwin').items.items[2].setDisabled(true);
								Ext.getCmp('freqwin').items.items[0].value='FREQ=MONTHLY;';
								break;							
						}
					},	
					showValue:function(){					
						switch (Ext.getCmp('freqwin').items.items[0].value) {
						case 'FREQ=DAILY;':								
							Ext.getCmp('freqwin').items.items[4].items.items[0].setValue(Ext.getCmp('freqwin').items.items[0].value+'<br>'+Ext.getCmp('freqwin').items.items[3].value);
							break;
						case 'FREQ=WEEKLY;':
							Ext.getCmp('freqwin').items.items[4].items.items[0].setValue(Ext.getCmp('freqwin').items.items[0].value+'<br>'+Ext.getCmp('freqwin').items.items[2].value+'<br>'+Ext.getCmp('freqwin').items.items[3].value);
							break;
						case 'FREQ=MONTHLY;':
							Ext.getCmp('freqwin').items.items[4].items.items[0].setValue(Ext.getCmp('freqwin').items.items[0].value+'<br>'+Ext.getCmp('freqwin').items.items[1].value+'<br>'+Ext.getCmp('freqwin').items.items[3].value);
							break;							
					}						
				}
				});