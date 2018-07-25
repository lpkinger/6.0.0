/**
 *条码拆分
 */
Ext.define('erp.view.core.button.CombiningAndBreaking', {
	extend : 'Ext.Button',
	alias : 'widget.erpCombiningAndBreakingButton',
	iconCls : 'x-button-icon-check',
	cls : 'x-btn-gray',
	id : 'CombiningAndBreaking',
	text : $I18N.common.button.erpCombiningAndBreakingButton,
	style : {
		marginLeft : '10px'
	},
	width:110,
	FormUtil: Ext.create('erp.util.FormUtil'),	
	GridUtil: Ext.create('erp.util.GridUtil'),
	initComponent : function() {
		this.callParent(arguments);
	},
	handler: function(btn){
		var me = this;
		var bool = false;
		var or_barcode;
		var or_baremain;
		var grid = Ext.getCmp('batchDealGridPanel');
	    var items = grid.selModel.getSelection();
	    var count = items.length;
	    var id ;
        var win = new Ext.window.Window({
             id: 'wincab',
             height: '70%',
             width: '70%',
             maximizable: false,
             title:'<span>条码拆分</span>',
             buttonAlign: 'center',
             layout: 'column',
             bodyStyle:"background-color:#F5F5F5;",
             items: [{
					xtype: 'gridpanel',
					id: 'grid1',
					anchor : '100% 70%',
					columnWidth:1,
					columns: [
					{
						style :"text-align:center",
						text: 'ID',
						width: 0,
						hidden : true,
						dataIndex: 'bar_id',
						ignore: true,
					},{
						style :"text-align:center",
						text: '条码',
						flex : 7,
						id:'bar_code1',
						dataIndex: 'bar_code',
						logic:"necessaryField",
						editor: {
							xtype: "textfield",
			                hideTrigger: true,
			                store: null,
			                queryMode: "local"
			            }
					},{
						style :"text-align:center",
						text: '数量',
						flex : 3,
						dataIndex: 'bar_remain',				
			            xtype: 'numbercolumn',
			            align: "left",
			            renderer : function(val, meta, record) {
							var v1 = record.get('bar_remain');
							var v2 = record.get('bar_code');
							var v;
							if (v2 != null && v2 !='' && v1 == 0 ) {
								v = 0;
							} else{
								v=v1;
							}
							return v;
			            }
					}],
					GridUtil: Ext.create('erp.util.GridUtil'),				
					listeners:{
						edit:function(ed,d){
							var grid = Ext.getCmp('grid1');
							var sum = 0;
							if(d.field == 'bar_code'){	
								if(d.value != null && d.value != ''){
									me.FormUtil.getFieldValue("barcode","nvl(max(bar_remain),0)","bar_code='"+d.value+"' and bar_status <> -2 and bar_status <> -1","bar_remain",d.record);									
									me.add1EmptyItems(grid);
									var items = grid.getStore().data.items;//获取store里面的数据
									 Ext.each(items, function(item) {
										sum+= item.data['bar_remain'];
									 });
									Ext.getCmp("barNumber").setValue(sum);
								}
							}
						},					
					},
				
					dbfinds: [],
					columnLines: true,
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }),
				    Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					store: new Ext.data.Store({
						fields: ['bar_id', 'bar_code', 'bar_remain'],
						data: [{},{}]
					}),
				    onGridItemClick: function(selModel, record){// grid行选择
					     	this.GridUtil.onGridItemClick(selModel, record);
					  },
					/*necessaryFields:['bar_code', 'bar_remain','bar_id'],*/
             	},
                  {
	            	fieldLabel:"总数量",
	 		    	/*labelStyle:"color:red;",*/
	 		    	labelWidth:50,
	 		    	xtype:"displayfield",
	 		        minValue: 0,
	 		        allowBlank:false,
	 		        columnWidth:.25,
	 		        name : 'barNumber',
	 		        id:'barNumber',
	 		        autoStripChars:true,
	 		        hideTrigger:true,
	 		        margin:'10px 0px 5px 0px'
             },{
	            	fieldLabel:"最小包",
	 		    	labelStyle:"color:red;",
	 		    	labelWidth:60,
	 		    	xtype:"textfield",
	 		        minValue: 0,
	 		        allowBlank:false,
	 		        columnWidth:.25,
	 		        name : 'barZXBZS',
	 		        id:'barZXBZS',
	 		        autoStripChars:true,
	 		        hideTrigger:true,
	 		        margin:'10px 0px 5px 0px'
             },{
	            	fieldLabel:"尾包数量",
	 		    	/*labelStyle:"color:red;",*/
	 		    	labelWidth:80,
	 		    	xtype:"textfield",
	 		        minValue: 0,
	 		        allowBlank:true,
	 		        columnWidth:.45,
	 		        name : 'barWS',
	 		        id:'barWS',
	 		        autoStripChars:true,
	 		        hideTrigger:true,
	 		        margin:'10px 0px 5px 0px'
             },{

					xtype: 'gridpanel',
					id: 'grid',
					anchor : '100% 70%',
					columnWidth:1,
					multiSelect: true,
					multiselected: [],
					forceFit : true,
					checkOnly:true,
					columns: [
					{
						style :"text-align:center",
						text: 'ID',
						width: 0,
						hidden : true,
						dataIndex: 'bar_id',
						ignore: true,
					},{
						style :"text-align:center",
						text: '新条码',
						flex : 7,
						dataIndex: 'bar_code',
						logic:"necessaryField",
						editor: {
							xtype: "displayfield",
			                hideTrigger: true,
			                store: null,
			                queryMode: "local"
			            }
					},{
						style :"text-align:center",
						text: '数量',
						logic:"necessaryField",
						flex : 3,
						dataIndex: 'bar_remain',
						editor: {
			                xtype: "displayfield",
			                hideTrigger: true,
			                store: null,
			                queryMode: "local"
			            },
			            xtype: 'numbercolumn',
			            align: "left",
			            format: ""
					}],
					GridUtil: Ext.create('erp.util.GridUtil'),
					listeners:{
					        beforeedit:function(editor, e, eOpts){
					            return false;//不可编辑
					        }
					    },
					selType:'checkboxmodel',
					dbfinds: [],
					columnLines: true,
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 10
				    }),
				    Ext.create('erp.view.core.plugin.CopyPasteMenu')],
					store: new Ext.data.Store({
						fields: ['bar_id', 'bar_code', 'bar_remain'],
						data: []
					}),
				   onGridItemClick: function(selModel, record){// grid行选择
					     	this.GridUtil.onGridItemClick(selModel, record);
					  },
					/*necessaryFields:['bar_code', 'bar_remain','bar_id'],*/
             }],
		    buttons : [{
						text : '确定',
						id :'confirmcab',
						handler : function(btn) {
							var barArr = new Array();   //getComponent
							var remains = 0;
							var count = 0;  //上面form的数据
							var numberWS = Ext.getCmp('barWS').value;
							var numberZXBZS = Ext.getCmp('barZXBZS').value;
							if(numberZXBZS == null || numberZXBZS ==''){
								Ext.Msg.alert('温馨提示',
								'请输入最小包数');
								return;
							}
							var grid = Ext.getCmp('grid1');
							var items = grid.getStore().data.items;
							Ext.each(items, function(item, index){
					        	if(this.data['bar_remain'] == '0' && this.data['bar_remain'] == 0){
					        		barArr.push(""+items[index].data['bar_code']+"");	
					        	}
					        });
							if(barArr != null && barArr != ''){
								showError("条码"+barArr +"不存在或者状态无效");
								return;
							}
							Ext.each(items, function(item, index){
					        	if(this.data['bar_code'] != null && this.data['bar_code'] != ''
					        		&& this.data['bar_remain'] != '0' && this.data['bar_remain'] != 0){
					        		barArr.push("'"+items[index].data['bar_code']+"'");	
					        		remains +=items[index].data['bar_remain']; 
					        		count++;
					        	}
					        });
							
							var re=/^\d+(,\d+)*$/;
							var sum=0;	
							if(numberWS != null && numberWS !='' && numberWS.indexOf("，") > 0){								
									numberWS=numberWS.replace(/，/ig,',');
									var count1= numberWS.length - numberWS.replace(',',"").length;
							}
							if(count ==1){
								if(count1 == 0 && (numberWS == remains)){
									Ext.Msg.alert('温馨提示',
									'不允许拆将条码拆分为本身');
									return;
								}else if( numberZXBZS == remains){
										Ext.Msg.alert('温馨提示',
										'不必要的拆分');
										return;
								}
							}
							
							if(items.length == 0){
								Ext.Msg.alert('温馨提示',
								'请输入条码');
								return;
							}
							var reg = /^[0-9]*$/;
							if(!reg.test(numberZXBZS)){
								Ext.Msg.alert('温馨提示',
								'最小包只能为数字');
								return;
							}
							if(numberWS != null && numberWS != ''){
								if(re.test(numberWS)){
									var str = new Array(); 
									str = numberWS.split( "," );
									for (var i = 0; i < str.length; i++ )
									{
										 sum+=parseFloat(str[i]);
									}}else{
										/*showError("请输入正确的拆分数量,例如20,30,40");*/
										Ext.Msg.alert('温馨提示',
										'请输入正确的拆分数量,例如20,30,40');
										return;
									}
								    if (sum <= 0) {
										Ext.Msg.alert('温馨提示',
												'拆分数量必须大于0');
										return;
									}
							}
						    if(remains <= 0){
								Ext.Msg.alert('温馨提示',
								'请输入有效条码');
								return;
							}else if(sum > remains){
								Ext.Msg.alert('温馨提示',
								'总数量不允许小于尾包数量之和');
								return;
							}else{
								if(numberWS != null && numberWS != ""){
								if((remains-sum)%numberZXBZS !=0){
									Ext.Msg.alert('温馨提示',
									'本次数量-尾数数量不是分装数量的整数倍!');
									return;
								}}else{
									if(remains%numberZXBZS !=0){
										Ext.Msg.alert('温馨提示',
										'本次数量-尾数数量不是分装数量的整数倍!');
										return;
									}
								}
							
							}
						    
							//暂时去掉
							/*if(remains>sum){
								number = number+','+(remains-sum);
							}*/
							if (barArr) {
								me.combiningAndBreaking(barArr,remains,numberZXBZS,numberWS);
							}
						}
					},{
		                xtype: 'checkboxfield',
		                name: 'autoPrint',
		                id:'autoPrint',
		                boxLabel: '自动打印条码',
		            	style:'margin-left:50px',
		            	checked:true
		            },{
						text : '打印新条码',
						id : 'printNew',
						iconCls: 'x-button-icon-print',
						disabled : true, 
							handler : function(btn) {
							var me = this;
							caller = 'Barcode!BaPrint';
							me.idS = '';
							var	lps_Barcaller = 'Barcode!Print';
							var bool = false;
							var grid = Ext.getCmp('grid');
							var items = grid.selModel.getSelection();
						    var idArray = [];
						    Ext.each(items, function(item, index){
					        	if(this.data['bar_id'] != null && this.data['bar_id'] != ''
					        		&& this.data['bar_id'] != '0' && this.data['bar_id'] != 0){
					        		bool = true;  
					        		idArray.push(this.data['bar_id']);
					        	}
					        });
						  if(bool){
						  	var idS = idArray.toString();
						  	//me.print(idS,lps_Barcaller);
						  	me.idS = idS;
						  	me.zplPrint(lps_Barcaller);
						  }else{
							  showError("没有需要打印的行,请先拆分出新条码");
						  }
						  },
					    zplPrint:function(caller){//zpl打印语句
					   		// 获取选择打印文件,用户选择分辨率，和打印机
							var me = this;
							if(!me.window){
					    		setup_web_print(function(printers,selected_printer){
					    			me.window = Ext.create('Ext.window.Window', {
										autoShow: true,
										title: '选择打印模板',
										width: 400,
										height: 300,
										layout: 'anchor',
										id :'template-win2',
										closeAction:'hide',
										items: [{ 							    					
											  anchor:'100% 100%',
											  xtype:'form',  							
											  buttonAlign : 'center',
											  items:[{
											        xtype: 'combo',
													id: 'printers',
													fieldLabel: '打印机列表', 									
													store: Ext.create('Ext.data.Store', {
														autoLoad: true,
													    fields: ['display', 'value'],
													    data:printers 									 
													}),
													queryMode: 'local',
												    displayField: 'display',
												    valueField: 'value',
													width:361,
												    allowBlank:false,
												    value:selected_printer.uid,
												    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
													style:'margin-left:15px;margin-top:15px;'
											  },{
											        xtype: 'combo',
													id: 'dpi',
													fieldLabel: '打印机分辨率', 		
													style:'margin-left:15px;margin-top:15px;',
													store: Ext.create('Ext.data.Store', {
													   fields: ['display', 'value'],
													   data : [{"display": '203dpi', "value": '203'},
													           {"display": '300dpi', "value": '300'},
													           {"display": '600dpi', "value": '600'},
													           {"display": '1200dpi', "value": '1200'}]
												   }),
												    displayField: 'display',
												    valueField: 'value',
												    queryMode: 'local',
												    value:resolutionCookie||'203',
													width:361,
												    allowBlank:false,
												    selectOnFocus:true//用户不能自己输入,只能选择列表中有的记录  
												}]	 							    	     				    							           	
										 }], 
										buttonAlign: 'center',
										buttons: [{
											text: '确定',
											handler: function(b) {
												var printers = Ext.getCmp('printers');
												var dpi = Ext.getCmp('dpi');
												if(printers && printers.value!= null){
													me.zebraPrint(caller,printers.value,dpi.value);
													b.ownerCt.ownerCt.close();
												}else{
													alert("请选择打印机 ");
												}   
											}
										}, {
											text: '取消',
											handler: function(b) {
												b.ownerCt.ownerCt.close();
											}
										}]
									});   			
					    		});
							}else if(me.window.isHidden()){
								me.window.show();
							}
					   	},
					   zebraPrint:function(caller,printer,dpi){
					   	    var me = this;
					    	var params = new Object();
					    	params['condition'] = 'bar_id in ('+me.idS +")";
					    	sendData(caller,printer,dpi,params);
						 }
		            },
				  {
					text : '取消',
					id : 'cancle',
					handler : function(btn) {
						var win = btn.ownerCt.ownerCt;
						win.close();
					}
				}
				],
		         });
    	  		win.show();
	},
	combiningAndBreaking: function(ids,remains,numberZXBZS,numberWS){ 
		var me=this;
		ids = ids.toString();
		me.setLoading(true); 
		Ext.Ajax.request({
	    	url : basePath +'scm/reserve/combiningAndBreaking.action',
			params: {
				ids:ids,
				total_remain: remains,
				zxbzs:numberZXBZS,
				every_remain: numberWS
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				me.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var data = res.data;
					Ext.MessageBox.alert('提示', '条码拆分成功!',function(){
						var ids = new Array();
						for(var i= 0;i<data.length;i++){
							ids.push(data[i].BAR_ID);
						}
						me.loadData(ids);
						Ext.getCmp('printNew').enable();
						Ext.getCmp('confirmcab').setDisabled(true);
						var status = Ext.getCmp('autoPrint').value;
						if(status){
							var	lps_Barcaller = 'Barcode!Print';
							var grid = Ext.getCmp("grid");
					    	var items = grid.store.data.items;
					    	var idArray = [];
					    	Ext.each(items, function(item, index){
			    				var id=item.data['bar_id'];
			    				if(id != null && id != ''&& id != '0' && id != 0){
			    					idArray.push(id);
			    				}
			    			})
					    	var idS = idArray.toString();
						  	//me.print(idS,lps_Barcaller);
						  	me.idS = idS;
						  	me.zplPrint(lps_Barcaller);
						}
					});
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
			}
	    });
	},
	 selModel: Ext.create('Ext.selection.CheckboxModel',{
    	checkOnly : true,
		ignoreRightMouseSelection : false,
		listeners:{
	        selectionchange:function(selModel, selected, options){
	        	selModel.view.ownerCt.summary();
	        	selModel.view.ownerCt.selectall = false;
	        }
	    },
	    getEditor: function(){
	    	return null;
	    },
	    onHeaderClick: function(headerCt, header, e) {
	        if (header.isCheckerHd) {
	            e.stopEvent();
	            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
	                this.deselectAll(true);
	            } else {
	                this.selectAll(true);
	                this.view.ownerCt.selectall = true;
	            }
	        }
	    }
	 }),
	 setLoading : function(b) {
			var mask = this.mask;
			if (!mask) {
				this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
					msg : "处理中,请稍后...",
					msgCls : 'z-index:10000;'
				});
			}
			if (b)
				mask.show();
			else
				mask.hide();
     },
     zplPrint:function(caller){//zpl打印语句
	   		// 获取选择打印文件,用户选择分辨率，和打印机
			var me = this;
			if(!me.window){
	    		setup_web_print(function(printers,selected_printer){
	    			me.window = Ext.create('Ext.window.Window', {
						autoShow: true,
						title: '选择打印模板',
						width: 400,
						height: 300,
						layout: 'anchor',
						id :'template-win3',
						closeAction:'hide',
						items: [{ 							    					
							  anchor:'100% 100%',
							  xtype:'form',  							
							  buttonAlign : 'center',
							  items:[{
							        xtype: 'combo',
									id: 'printers',
									fieldLabel: '打印机列表', 									
									store: Ext.create('Ext.data.Store', {
										autoLoad: true,
									    fields: ['display', 'value'],
									    data:printers 									 
									}),
									queryMode: 'local',
								    displayField: 'display',
								    valueField: 'value',
									width:361,
								    allowBlank:false,
								    value:selected_printer.uid,
								    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
									style:'margin-left:15px;margin-top:15px;'
							  },{
							        xtype: 'combo',
									id: 'dpi',
									fieldLabel: '打印机分辨率', 		
									style:'margin-left:15px;margin-top:15px;',
									store: Ext.create('Ext.data.Store', {
									   fields: ['display', 'value'],
									   data : [{"display": '203dpi', "value": '203'},
									           {"display": '300dpi', "value": '300'},
									           {"display": '600dpi', "value": '600'},
									           {"display": '1200dpi', "value": '1200'}]
								   }),
								    displayField: 'display',
								    valueField: 'value',
								    queryMode: 'local',
								    value:resolutionCookie||'203',
									width:361,
								    allowBlank:false,
								    selectOnFocus:true//用户不能自己输入,只能选择列表中有的记录  
								}]	 							    	     				    							           	
						 }], 
						buttonAlign: 'center',
						buttons: [{
							text: '确定',
							handler: function(b) {
								var printers = Ext.getCmp('printers');
								var dpi = Ext.getCmp('dpi');
								if(printers && printers.value!= null){
									me.zebraPrint(caller,printers.value,dpi.value);
									b.ownerCt.ownerCt.close();
								}else{
									alert("请选择打印机 ");
								}   
							}
						}, {
							text: '取消',
							handler: function(b) {
								b.ownerCt.ownerCt.close();
							}
						}]
					});   			
	    		});
			}else if(me.window.isHidden()){
				me.window.show();
			}
	   	},
	   	
	   zebraPrint:function(caller,printer,dpi){
	   	    var me = this;
	    	var params = new Object();
	    	params['condition'] = 'bar_id in ('+me.idS +")";
	    	sendData(caller,printer,dpi,params);
		 },
		 loadData: function(value) {
				var me = this;
				me.getFieldsValues("barcode",
						'bar_id, bar_code, bar_remain', "bar_id in (" +value+") order by bar_id desc", [], function(data){
					var datas = Ext.JSON.decode(data), _datas = [];
					var grid = Ext.getCmp('grid');
					if(datas.length > 0) {
						var keys = Ext.Object.getKeys(datas[0]);
						Ext.Array.each(datas, function(d){
							var obj = {};
							Ext.Array.each(keys, function(key){
								obj[key.toLowerCase()] = d[key];
							});
							_datas.push(obj);
						});
					}
						store = grid.getStore();
					_datas.length > 0 && grid.store.loadData(_datas);
					grid.store.each(function(){
						this.dirty = true;
					});
				});
			},
			
			getFieldsValues: function(caller, fields, condition, data, fn){
				Ext.Ajax.request({
					url : basePath + 'common/getFieldsDatas.action',
					async: false,
					params: {
						caller: caller,
						fields: fields,
						condition: condition
					},
					method : 'post',
					callback : function(options,success,response){
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
						if(localJson.success){
							data = localJson.data;
							fn && fn.call(null, localJson.data);
						}
					}
				});
			},
			add1EmptyItems: function(grid, count, append){
				var store = grid.store, 
					items = store.data.items, arr = new Array();
				var detno = grid.detno;
				count = count || 1;
				append = append === undefined ? true : false;
				if(typeof grid.sequenceFn === 'function')
					grid.sequenceFn.call(grid, count);
				else {
					if(detno){
						var index = items.length == 0 ? 0 : Number(store.last().get(detno));
						for(var i=0;i < count;i++ ){
							var o = new Object();
							o[detno] = index + i + 1;
							arr.push(o);
						}
					} else {
						for(var i=0;i < count;i++ ){
							var o = new Object();
							arr.push(o);
						}
					}
					store.loadData(arr, append);
					var i = 0;
					store.each(function(item, x){
						if(item.index) {
							i = item.index;
						} else {
							if (i) {
								item.index = i++;
							} else {
								item.index = x;
							}
						}
					});
				}
			},
	   	
});