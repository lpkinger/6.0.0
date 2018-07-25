/**
 * 打印按钮
 */	
Ext.define('erp.view.core.button.BatchPrint',{ 
		id:'print',
		extend: 'Ext.Button', 
		alias: 'widget.erpBatchPrintButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBatchPrintButton,
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
		handler : function (){
			var me = this;
			me.idS = '';
			var lps_Barcaller='';
			if(caller == 'Barcode!BaPrint'){
				lps_Barcaller = 'Barcode!Print';
			}
			if(caller == 'NewBar!BaPrint'){
				lps_Barcaller = caller;
			}
			var bool = false;
			var grid = Ext.getCmp('batchDealGridPanel');
	        var items = grid.selModel.getSelection();
	        var idArray = [];
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		idArray.push(this.data[grid.keyField]);
	        		bool = true;    		
	        	}
	        });
	      if(bool){
	      	var idS = idArray.toString();
	      	//me.print(idS,lps_Barcaller);
	      	me.idS = idS;
	      	me.zplPrint(lps_Barcaller);
	      }else{
			  showError("没有勾选需要打印的行,请勾选");
		  }
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
	});