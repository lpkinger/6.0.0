/**
 *条码拆分
 */
Ext.define('erp.view.core.button.BreakingBatch', {
	extend : 'Ext.Button',
	alias : 'widget.erpBreakingBatchButton',
	iconCls : 'x-button-icon-check',
	cls : 'x-btn-gray',
	id : 'BreakingBatch',
	text : $I18N.common.button.erpBreakingBatchButton,
	style : {
		marginLeft : '10px'
	},
	width:100,
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
        var id ;
        Ext.each(items, function(item, index){
        	if(this.data['bar_id'] != null && this.data['bar_id'] != ''
        		&& this.data['bar_id'] != '0' && this.data['bar_id'] != 0){
        		id = this.data['bar_id'];
        		or_barcode = this.data['bar_code'];
        		or_baremain = this.data['bar_remain'];
        		bool = true;    		
        	}
        });
      if(bool){
    	  if(items.length == 1){
    	  var win = new Ext.window.Window({
             id: 'win',
             height: '50%',
             width: '50%',
             maximizable: false,
             title:'<span>条码拆分</span>',
             buttonAlign: 'center',
             layout: 'column',
             bodyStyle:"background-color:#F5F5F5;",
             items: [{ 
     		    xtype:'displayfield',
            	name:'barcode',
    		    fieldLabel:'条码',
    		    id : 'barcode',
    		    readOnly:true,
    		    columnWidth:1,
    		    labelWidth:60,
    		    allowBlank : false,
    		    style:'border:none;'
    		},{
    		    name:'bar_remain',
    		    fieldLabel:'数量',
    		    id : 'bar_remain',
    		    readOnly:true,
    		    labelWidth:60,
    		    allowBlank : false,
    		    columnWidth:.3,
    		    xtype:'displayfield'
    		},{
		    	fieldLabel:"需拆分数量",
		    	labelStyle:"color:red;",
		    	labelWidth:80,
		    	xtype:"numberfield",
		        minValue: 0,
		        allowBlank:false,
		        columnWidth:.45,
		        name : 'barNumber',
		        id:'barNumber',
		        autoStripChars:true,
		        hideTrigger:true
		    },
		    { 
     		    xtype:'displayfield',
            	name:'barcode1',
    		    fieldLabel:'新条码1',
    		    id : 'barcode1',
    		    readOnly:true,
    		    columnWidth:.75,
    		    labelWidth:60,
    		    allowBlank : false
    		},
    		{ 
     		    xtype:'displayfield',
            	name:'number1',
    		    fieldLabel:'数量',
    		    id : 'number1',
    		    readOnly:true,
    		    columnWidth:.25,
    		    labelWidth:40,
    		    allowBlank : false
    		},
    		{ 
     		    xtype:'displayfield',
            	name:'barcode2',
    		    fieldLabel:'新条码2',
    		    id : 'barcode2',
    		    readOnly:true,
    		    columnWidth:.75,
    		    labelWidth:60,
    		    allowBlank : false
    		},{ 
     		    xtype:'displayfield',
            	name:'number2',
    		    fieldLabel:'数量',
    		    id : 'number2',
    		    readOnly:true,
    		    columnWidth:.25,
    		    labelWidth:40,
    		    allowBlank : false
    		},
    		{ 
     		    xtype:'displayfield',
            	name:'barid',
    		    fieldLabel:'bar_id',
    		    id : 'barid',
    		    readOnly:true,
    		    columnWidth:0,
    		    hidden:true
	    		}],
		    buttons : [{
						text : '确定',
						id :'confirm',
						handler : function(btn) {
							var number = Ext.getCmp('barNumber').value;
							var bar_remain = Ext.getCmp("bar_remain").value;
							if (!number) {
								Ext.Msg.alert('温馨提示',
										'请输入拆分数量');
								return;
							}else if (number <= 0) {
								Ext.Msg.alert('温馨提示',
										'拆分数量必须大于0');
								return;
							}else if(number >= bar_remain){
								Ext.Msg.alert('温馨提示',
								'需拆分数量必须小于条码原数量');
								return;
							}
							if (id) {
								me.breakingBatch(id);
							}
						}
					},
						{
					text : '打印新条码',
					id : 'print1',
					iconCls: 'x-button-icon-print',
					disabled : true, 
						handler : function(btn) {
						var me = this;
						caller = 'Barcode!BaPrint';
						me.idS = '';
						var	lps_Barcaller = 'Barcode!Print';
						var bool = false;
						var grid = Ext.getCmp('batchDealGridPanel');
					    var item = Ext.getCmp("barid").value;
					    var idArray = [];
				    	if(item != null && item != '' && item != '0' && item != 0 ){
				    		idArray.push(item);
				    		bool = true;    		
				    	}
					  if(bool){
					  	var idS = idArray.toString();
					  	//me.print(idS,lps_Barcaller);
					  	me.idS = idS;
					  	me.zplPrint(lps_Barcaller);
					  }else{
						  showError("没有需要打印的行,请先拆分出新条码");
					  }
					  },
					  beforePrint: function(f,callback) {
							var me = this;
				    		Ext.Ajax.request({
				    			url: basePath + 'common/JasperReportPrint/getFields.action',
				    			method: 'post',
				    			params:{
				    				caller:f
				    			},
				    			callback: function(opt, s, r) {
				    				var rs = Ext.decode(r.responseText);    				
				    				callback.call(null,rs);
				    			}
				    		});
					},
				   print : function(idS,lps_Barcaller){
				      	var me = this;
					    Ext.Ajax.request({
						   url : basePath + 'common/JasperReportPrint/getPrintType.action',
						   method : 'get',
						   async : false,
						   callback : function(opt, s, res){
							   var r = new Ext.decode(res.responseText);
							   if(r.success && r.printtype){
							   		me.printType=r.printtype;
							   }
						   	}
					    }); 			
				     	 me.beforePrint(lps_Barcaller,function(data){
								if(data.datas.length>1){		
									this.window = Ext.create('Ext.window.Window', {
										autoShow: true,
										title: '选择打印类型',
										width: 400,
										height: 300,
										layout: 'anchor',
										items: [{ 							    					
											  anchor:'100% 100%',
											  xtype:'form',
											  id :'printbycondition',
											  buttonAlign : 'center',
											  items:[{
											        xtype: 'combo',
													id: 'template',
													fieldLabel: '选择打印类型', 									
													store: Ext.create('Ext.data.Store', {
														autoLoad: true,
													    fields: ['TITLE','ID','REPORTNAME'],
													    data:data.datas 									 
													}),
													queryMode: 'local',
												    displayField: 'TITLE',
												    valueField: 'ID',
													width:300,
												    allowBlank:false,
												    selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录  
													style:'margin-left:15px;margin-top:15px;',
													listeners : {
												      afterRender : function(combo) {
												         combo.setValue(data.datas[0].ID);
												      }
												   }
												}]	 							    	     				    							           	
										 }], 
										buttonAlign: 'center',
										buttons: [{
											text: '确定',
											handler: function(b) {
												var temp = Ext.getCmp('template');
												if(temp &&  temp.value!= null){
													var selData = temp.valueModels[0].data;
													me.jasperReportPrint(lps_Barcaller,selData.REPORTNAME,idS);
												}else{
													alert("请选择打印模板");
												}   								
											}
										}, {
											text: '取消',
											handler: function(b) {
												b.ownerCt.ownerCt.close();
											}
										}]
									});   					
							}else if(data.datas.length == 0){
								showError("请先配置对应的打印文件")
							}else{
								me.jasperReportPrint(lps_Barcaller,data.datas[0].REPORTNAME,idS);
							}    			
						});
				    },
					jasperReportPrint : function(caller,reportname,idS){
				    	var me = this;
				    	var params = new Object();
				    	params['idS'] = idS;
				    	Ext.Ajax.request({
					    	url : basePath +'common/JasperReportPrint/print.action',
							params: {
								params: unescape(escape(Ext.JSON.encode(params))),
								caller: caller,
								reportname:reportname
							},
							method : 'post',
							timeout: 360000,
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.success){
									var printcondition = '(barcode.bar_id in ('+idS+'))';
									printcondition = res.info.whereCondition=='' ? 'where '+printcondition :'where '+res.info.whereCondition+' and '+printcondition;
									var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+printcondition+'&otherParameters=&printType='+res.info.printtype;
									window.open(url,'_blank');
								}else if(res.exceptionInfo){
									var str = res.exceptionInfo;
									showError(str);return;
								}
							}
					    });
				    },
				    zplPrint:function(caller){//zpl打印语句
				   		// 获取选择打印文件,用户选择分辨率，和打印机
						var me = this;
						if(!me.window){
				    		setup_web_print(function(printers,selected_printer){
				    			me.window = Ext.getCmp('template-winNew') || Ext.create('Ext.window.Window', {
									autoShow: false,
									title: '选择打印模板',
									width: 400,
									height: 300,
									layout: 'anchor',
									id :'template-winNew',
									closeAction:'hide',
									items: [{ 							    					
										  anchor:'100% 100%',
										  xtype:'form',  							
										  buttonAlign : 'center',
										  items:[{
										        xtype: 'combo',
												id: 'printersNew',
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
												id: 'dpiNew',
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
											var printers = Ext.getCmp('printersNew');
											var dpi = Ext.getCmp('dpiNew');
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
				    			me.window.show();
				    		});
						}else if(me.window.isHidden() ){
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
				listeners : {
					beforerender:function(){        							
						Ext.getCmp('barcode').setValue(or_barcode);
						Ext.getCmp('bar_remain').setValue(or_baremain);
						if(Ext.getCmp("barcode1").value == null || Ext.getCmp("barcode1").value ==''){
							Ext.getCmp('barcode1').hide();
							Ext.getCmp('number1').hide();
						}
						if(Ext.getCmp("barcode2").value == null || Ext.getCmp("barcode2").value ==''){
							Ext.getCmp('barcode2').hide();
							Ext.getCmp('number2').hide();
						}
					}
				}
		         });
    	  		win.show();
    	  		win.on("close",function(){
    	  			var grid = Ext.getCmp("batchDealGridPanel");
    	  			caller = 'Barcode!BaPrint';
    	  			grid.ownerCt.down('form').onQuery();
    	  	     });
    	  }else{
    		  showError("只能勾选一条拆分的数据"); 
    	  }
      }else{
		  showError("没有勾选需要拆分的行,请勾选");
	  }
	},
	breakingBatch: function(id){
		var or_remain;
		var or_barcode = Ext.getCmp("barcode").getValue(); //原条码
		var or_baremain = Ext.getCmp("bar_remain").getValue(); //原数量
		var bar_remain = Ext.getCmp("barNumber").getValue(); //输入拆分的数量
		var grid = Ext.getCmp("batchDealGridPanel");
		var barcode1 = Ext.getCmp('barcode1'); //新条码1
		var barcode2 = Ext.getCmp('barcode2'); //新条码2
		var number1 = Ext.getCmp('number1'); 
		var number2 = Ext.getCmp('number2');
		var bar_id = Ext.getCmp("barid");  //返回的bar_id
		var idArray = [];
		Ext.Ajax.request({
	    	url : basePath +'scm/reserve/breakingBatch.action',
			params: {
				or_barcode:or_barcode,
				or_remain: or_baremain,
				bar_remain: bar_remain
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var data = res.data;
					Ext.MessageBox.alert('提示', '条码拆分成功!',function(){
						/*grid.ownerCt.down('form').onQuery();*/
						Ext.getCmp('barcode1').show();
						Ext.getCmp('number1').show();
						Ext.getCmp("bar_remain").setValue(or_baremain - bar_remain);
						barcode1.setValue(data[0].BAR_CODE);
						number1.setValue(data[0].BAR_REMAIN);
						idArray.push(data[0].BAR_ID);
						if(data.length > 1){
							barcode2.setValue(data[1].BAR_CODE);
							number2.setValue(data[1	].BAR_REMAIN);
							idArray.push(data[1].BAR_ID);
							Ext.getCmp('barcode2').show();
							Ext.getCmp('number2').show();
						}
						bar_id.setValue(idArray);
						Ext.getCmp('print1').enable();
						Ext.getCmp('confirm').setDisabled(true);
					});
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
			}
	    });
	}
	
});