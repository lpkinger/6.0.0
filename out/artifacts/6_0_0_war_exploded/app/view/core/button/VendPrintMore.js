/**
 * 多种打印格式，打印条码
 */	
Ext.define('erp.view.core.button.VendPrintMore',{ 
		id:'vendprintmore',
		extend: 'Ext.Button', 
		alias: 'widget.erpVendPrintMoreButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	printType:'',
    	text: '打印条码',
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
        initComponent : function(){ 
			this.callParent(arguments); 
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
        handler: function(){
            var bool = false;
    		var me = this;
    		var boolAll = false;
			var grid = Ext.getCmp('vendSetBarcodeGridPanel');
	        var items = grid.selModel.getSelection();  //勾选的数据
	        var data = grid.store.data.items;  //数据
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		bool = true;    
	        		if(items.length == data.length){
	        			bool = false;
	        			boolAll = true;
	        		}
	        	}
	        });

			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/getPrintType.action',
				params: {
					type:'Barcode'
				},
				method : 'post',
				timeout: 60000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						if(bool){
				    		//me.Print(caller,' ');
							me.zplPrint(caller,' ');
				        }else{
				        	var bool1 = false;
				        	 var items = grid.store.data.items;
					 	        Ext.each(items, function(item, index){
					 	        	if(this.data['ban_id'] != null && this.data['ban_id'] != ''
					 	        		&& this.data['ban_id'] != '0' && this.data['ban_id'] != 0){
					 	        		bool1 = true;    		
					 	        	}
					 	        });
					 	     //在这里区分是全部勾选还是全都不选的全部打印
				        	if(boolAll){
					 			if(bool1){
					 				me.zplPrint(caller, "ALL");
					 			}else{
						        	 showError('没有需要打印的明细!');
						         }
					 		
				        	}else{
				        		 warnMsg("确定打印全部条码", function(btn){
							         if(btn == 'yes'){
							 			if(bool1){
							 				//me.Print(caller, "ALL");
							 				me.zplPrint(caller, "ALL");
							 			}else{
								        	 showError('没有需要打印的明细!');
								         }
							 		}
							     })
				        	}
				        	
						 }
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
 	    });
    	},
    	
    	Print:function (caller,type){
    		  // 获取选择打印文件
    		var me = this;
    		me.beforePrint(caller,function(data){
				if(data.datas.length>1){   				
					Ext.create('Ext.window.Window', {
						autoShow: true,
						title: '选择打印模板',
						width: 400,
						height: 300,
						layout: 'anchor',
						id :'template-win',
						items: [{ 							    					
							  anchor:'100% 100%',
							  xtype:'form',  							
							  buttonAlign : 'center',
							  items:[{
							        xtype: 'combo',
									id: 'template',
									fieldLabel: '打印模板', 									
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
									me.jasperReportPrint(caller,selData.REPORTNAME,type);
									b.ownerCt.ownerCt.close();
									window.location.href = window.location.href;
								}else{
									alert("请选择打印模板 ");
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
					me.jasperReportPrint(caller,data.datas[0].REPORTNAME,type);
				}    			
			});
    	},
    	
    	jasperReportPrint:function(caller,reportname,type){    	
	    	var grid = Ext.getCmp('vendSetBarcodeGridPanel');	    	
	    	var condition='';
	    	var params = new Object();
	    	params['idS'] = key;
	    	
	    	if(type =='ALL'){//全部打印
	 	    	condition = '( ban_anid='+key+")";
	    	}else{	//勾选打印    		
	 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
		    	var o;
		    	var printcondition;
		    	var data = new Array();
	 	        Ext.each(items, function(item, index){
	 	        	if(this.data['ban_id'] != null && this.data['ban_id'] != ''
	 	        		&& this.data['ban_id'] != '0' && this.data['ban_id'] != 0){
	 	        	     o = this.data['ban_id'];   		
	 	        	}
	 	        	data.push(o);
	 	        });
	 	        params['data'] = data.join(",");	 	       
	 	        condition='( ban_anid='+ key +' and ban_id in ('+data+'))';
	    	}	    

	       Ext.Ajax.request({
	    		url : basePath +'common/JasperReportPrint/print.action',
				params: {
					caller:caller,
					reportname:reportname,
					params: unescape(escape(Ext.JSON.encode(params)))
				},
				method : 'post',
				timeout: 3600,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						condition = res.info.whereCondition=='' ? 'where '+condition :'where '+res.info.whereCondition+' and '+condition;
						var url = res.info.printurl+'?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+encodeURIComponent(condition)+'&printType='+res.info.printtype;
						window.open(url,'_blank');
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
	    	});
    	},
    	
    	zplPrint:function(caller,type){
    		var me = this;
    		var ac_class  = getUrlParam('ac_class');
    		if(ac_class =='Accept'){
    			if(!me.window){
    	    		setup_web_print(function(printers,selected_printer){
    	    			me.window = Ext.create('Ext.window.Window', {
    						autoShow: true,
    						title: '选择打印模板',
    						width: 400,
    						height: 300,
    						layout: 'anchor',
    						id :'template-win',
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
    									me.zebraPrint("VendBarcodeInPrint",printers.value,dpi.value,type);
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
    		}else{
    			Ext.Ajax.request({
    	    		url : basePath +'api/pda/print/zplPrinter',
    				method : 'post',
    				callback : function(options,success,response){					
    				}
     	       });    		
    		}
       	},
       	
       zebraPrint:function(caller,printer,dpi,type){
       	    var me = this;
	    	var grid = Ext.getCmp('vendSetBarcodeGridPanel');	    	
	    	var condition='';
	    	if(type =='ALL'){//全部打印
	 	    	condition = '( ban_anid='+key+")";
	 	    	var result = 0;
	            Ext.Ajax.request({
	                url : basePath + 'common/getFieldData.action',
	                async: false,
	                params: {
	                    caller: 'baracceptnotify',
	                    field: 'count(ban_id)',
	                    condition: condition
	                },
	                method : 'post',
	                callback : function(opt, s, res){
	                    var r = new Ext.decode(res.responseText);
	                    if(r.exceptionInfo){
	                        showError(r.exceptionInfo);return;
	                    } else if(r.success){
	                        result = r.data;
	                        if(result > 50){
	                        	var num = Math.ceil(result/50);
	        	            	for(var i = 1;i<= num;i++){ //循环i次发送请求
	        	            		   var params = new Object();
	 	        	        	    	params['id'] = key;
	 	        	        	    	params['condition']= condition;
	 	        	            		params['page'] = i;
	 	        	            		params['pageSize'] = 50;
	 	        	            		sendData(caller,printer,dpi,params);
	        	            	}
	        	            }else if(result >0){   
	        	            	var params = new Object();
	        	        	    params['id'] = key;
	        	        	    params['condition']= condition;
	        	            	sendData(caller,printer,dpi,params);
	        	            }
	                    }
	                }
	            });
	    	}else{	//勾选打印    		
	 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
		    	var o;
		    	var printcondition;
		    	var data = new Array();
		    	var count =0;
	 	        Ext.each(items, function(item, index){
	 	        	if(this.data['ban_id'] != null && this.data['ban_id'] != ''
	 	        		&& this.data['ban_id'] != '0' && this.data['ban_id'] != 0){
	 	        	     o = this.data['ban_id'];   		
	 	        	}
	 	        	data.push(o);
	 	        	count++;
	 	        	if(count>=50){
	 	        		if(count%50 == 0){
	 	        			condition='(ban_anid='+ key +' and ban_id in ('+data.join(",")+'))';
	 	        			var params = new Object();
	 	        			params['id'] = key;
	 	        			params['condition']= condition;
	 	        			sendData(caller,printer,dpi,params);
	 	        			data = new Array();
	 	        			count = 0;
	 	        		}
	 	        	}
	 	        });
	 	        if((items.length<50 && items.length>0) || count > 0){
	 	        	condition='(ban_anid='+ key +' and ban_id in ('+data.join(",")+'))';
	 	        	var params = new Object();
	        	    params['id'] = key;
	        	    params['condition']= condition;
	 	        	sendData(caller,printer,dpi,params);
	 	        }
	    	}	
	           
		 }
	});
	