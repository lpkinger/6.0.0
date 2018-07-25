/**
 * 多种打印格式，按箱号打印
 */	
Ext.define('erp.view.core.button.PrintMoreBox',{ 
		id:'printmorebox',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintMoreBoxButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	printType:'',
    	text: $I18N.common.button.erpPrintMoreBoxButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
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
        	var grid = Ext.getCmp('setBarcodeGridPanel');
        	var lps_Barcaller = '';
        	if(caller == 'ProdInOut!BarcodeIn'){
				lps_Barcaller = 'ProdInOut!OutBoxcodeIn';
			}
        	var me = this;
        	var bool = false;
        	var bool2 = false;
 	        /*var items = grid.store.data.items;*/
 	        var items = grid.selModel.getSelection();
 	        Ext.each(items, function(item, index){
 	        	if(this.data['bi_id'] != null && this.data['bi_id'] != ''
 	        		&& this.data['bi_id'] != '0' && this.data['bi_id'] != 0){
 	        		bool = true;   
 	        	}
 	        	if(this.data['bi_outboxcode'] != null && this.data['bi_outboxcode'] != ''
 	        		&& this.data['bi_outboxcode'] != '0' && this.data['bi_outboxcode'] != 0){
 	        		bool2 = true;    		
 	        	}
 	        });
 			/*if(bool){
 				me.Print(lps_Barcaller, "ALL");
 			}else{
	        	 showError('没有需要打印的明细!');
	         }*/
 	       if(bool){
 	    	   if(bool2){
 	    		   //me.Print(lps_Barcaller,' ');   
 	    		  me.zplPrint(lps_Barcaller,' ');
 	    	   }else{
 	    		   showError('不存在需要打印的箱号!');
 	    	   }
	        }else{
	        	 warnMsg("确定打印全部箱号", function(btn){
			         if(btn == 'yes'){
			        	var bool1 = false;
			        	var bool2 = false;
			 	        var items = grid.store.data.items;
			 	        Ext.each(items, function(item, index){
			 	        	if(this.data['bi_id'] != null && this.data['bi_id'] != ''
			 	        		&& this.data['bi_id'] != '0' && this.data['bi_id'] != 0){
			 	        		bool1 = true;    		
			 	        	}
			 	        	if(this.data['bi_outboxcode'] != null && this.data['bi_outboxcode'] != ''
			 	        		&& this.data['bi_outboxcode'] != '0' && this.data['bi_outboxcode'] != 0){
			 	        		bool2 = true;    		
			 	        	}
			 	        });
			 			if(bool1){
			 				if(bool2){
			 					//me.Print(lps_Barcaller, "ALL");
			 					me.zplPrint(lps_Barcaller,'ALL');
			 				}else{
			 					showError('不存在需要打印的箱号!');
			 				}
			 			}else{
				        	 showError('没有需要打印的明细!');
				         }
			 		}
			     })
			 }
    	},
    	Print :function (caller,type){  // 获取选择打印文件
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
	    	var grid = Ext.getCmp('setBarcodeGridPanel');	  
	    	var condition='';
	    	var params = new Object();
	    	params['idS'] = key;
	    	
	    	if(type =='ALL'){//全部打印
	 	    	condition = '( bi_piid='+key+")";
	    	}else{	//勾选打印    		
	 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
		    	var o;
		    	var printcondition;
		    	var data = new Array();
	 	        Ext.each(items, function(item, index){
	 	        	if(this.data['bi_outboxcode'] != null && this.data['bi_outboxcode'] != ''
	 	        		&& this.data['bi_outboxcode'] != '0' && this.data['bi_outboxcode'] != 0){
	 	        	     o = this.data['bi_outboxcode']; 
	 	        	}
	 	        	data.push(o);
	 	        });
	 	        params['data'] = data.join(",");	
	 	        data=Ext.Array.unique(data);
	 	        condition=' (bi_piid='+ key +' and bi_outboxcode in ('+data+'))';
	    	}	 
	    	console.log(condition);
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
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		zplPrint:function(caller,type){//zpl打印语句
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
									me.zebraPrint("ProdInOut!OutBoxcodeIn",printers.value,dpi.value,type);
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
     	zebraPrint:function(caller,printer,dpi,type){
       	    var me = this;
	    	var grid = Ext.getCmp('setBarcodeGridPanel');	    	
	    	var condition='';
	    	var params = new Object();
	    	params['id'] = key;
	    	if(type =='ALL'){//全部打印
	 	    	condition = '(bi_piid='+key+")";
	    	}else{	//勾选打印    		
	 	        var items = grid.selModel.getSelection();	 	 	 	        	 	       	    	
		    	var o;
		    	var printcondition;
		    	var data = new Array();
	 	        Ext.each(items, function(item, index){
	 	        	if(this.data['bi_outboxcode'] != null && this.data['bi_outboxcode'] != ''
	 	        		&& this.data['bi_outboxcode'] != '0' && this.data['bi_outboxcode'] != 0){
	 	        	     o = this.data['bi_outboxcode']; 
	 	        	}
	 	        	data.push(o);
	 	        });
	 	        data = Ext.Array.unique(data);
	 	        condition=' (bi_piid='+ key +' and bi_outboxcode in ('+data+'))';
	    	}	
	    	params['condition']= condition;
	    	sendData(caller,printer,dpi,params);
		 }
	});