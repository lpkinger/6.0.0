Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.BarAcceptNotify.SaveBarcode', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'vendbarcode.barAcceptNotify.saveBarcode.Viewport','vendbarcode.barAcceptNotify.saveBarcode.GridPanel','core.trigger.AddDbfindTrigger',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.button.Close',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.button.GenerateBarcode','core.button.PrintAll','core.button.DeleteAllDetails',
     	    'core.button.PrintAllPackage','core.button.GeneratePaCode','core.button.PrintByCondition','core.button.VendPrintMore'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'erpVendBarcodeGridPanel': { 
    			reconfigure:function(grid){
    			},
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpVendBarcodeGridPanel':{
			    afterrender:function(m){
			    	var grid = Ext.getCmp("vendBarcodeGridPanel");
			    	var items = grid.store.data.items;
	    			Ext.each(items, function(item, index){
	    				var id=item.data['and_id'];
	    				if(id != null && id != ''&& id != '0' && id != 0){
	    					m.getSelectionModel().selectAll(true);
	    				}
	    			})
			    },
			   beforeedit: function(){}			   			   
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				var grid1 = parent.Ext.getCmp("vendSetBarcodeGridPanel");
	    			if(grid1){
    					parent.location="javascript:location.reload()";//窗口关闭后刷新父页面
	    			}
    			}
    		},
    		'button[id=boxqtySet]':{
    			click: function(btn){
    				var grid = Ext.getCmp('vendBarcodeGridPanel');
    				var val = Ext.getCmp("pr_boxqty").value;
    				Ext.Array.each(grid.store.data.items,function(item){
    					item.set('pr_boxqty',val);
    				}
    				);
    			}
    		},
    		'button[id=confirmZplPrint]':{
    			click:function(btn){
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
    						var gridx = Ext.getCmp("vendBarcodeGridPanel");
    						gridx.on('storeloaded',function(){
    							gridx.selModel.selectAll(true);
    						});
    						me.GridUtil.loadNewStore(gridx, {caller: caller, condition:condition });
    						var status = Ext.getCmp('autoPrint').value;
    						var params = new Object();
    				    	params['idS'] = key;
    					    params['condition']= '( ban_anid='+key+")";
    					    if('vendSaveBarcode'== caller){
    					    		caller = 'VendBarcodeInPrint';
    					    	}
    						if(status){
    							me.sendSelfData(caller,dpi,params);
    						}
    						}else if(res.exceptionInfo){
    							var str = res.exceptionInfo;
    							showError(str);return;
    						}
    					}
    		    });
    			}
    		},
    		'button[id=confirmPicturePrint]':{
    			click:function(btn){
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
    						var gridx = Ext.getCmp("vendBarcodeGridPanel");
    						gridx.on('storeloaded',function(){
    							gridx.selModel.selectAll(true);
    						});
    						me.GridUtil.loadNewStore(gridx, {caller: caller, condition:condition });
    						var status = Ext.getCmp('autoPrint').value;
    						var params = new Object();
    				    	params['idS'] = key;
    					    params['condition']= '( ban_anid='+key+")";
    					    if('vendSaveBarcode'== caller){
    					    		caller = 'VendBarcodeInPrint';
    					    	}
    						if(status){
    							me.sendSelfPicData(caller,dpi,params);
    						}
    						}else if(res.exceptionInfo){
    							var str = res.exceptionInfo;
    							showError(str);return;
    						}
    					}
    		    });
    			}
    		},
    		'button[id=confirm]':{//确认生成
    			click: function(btn){
    				var me = this;
	    			var id = key;
	                var gridCondition1 = "and_anidIS" + key+"  order by and_detno asc";
	                var linkCaller = 'vendSaveBarcode';
	    			var grid = Ext.getCmp("vendBarcodeGridPanel");
					if( grid.selModel.getCount() == 0 ){
						showError("没有需要处理的数据!");
						 return;
					}
	 				var items = grid.selModel.getSelection();
	 				var data = new Array();
	 				grid.multiselected = [];
	 				var bool = false;
	 				var boolz = false;
	 				boolb = false;
	 				 Ext.each(items, function(item, index){
	         	        if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	         		        && this.data.and_id != '0' && this.data.and_id  != 0){
	         	            item.index = this.data.and_detno;
	 	        		    grid.multiselected.push(item);
	         	        }   
	 				});
            	   var records = Ext.Array.unique(grid.multiselected);
         	       if(records.length == 0){
         	    	  showError("没有需要处理的数据!");
					  return;
         	       }else if(records.length > 0){
						Ext.each(records, function(record, index){
							if(this.data['rest'] != null && this.data['rest'] != ''&&
								this.data['rest'] != '0' && this.data['rest'] != 0){
								bool = true;
								boolb = true;
								var o = new Object();		
								if(record.data['rest'] > 0){
									o['rest'] = record.data['rest'];
								}else{
									boolb = false;
									showError("本次数量必须大于0");
									return;	
								}
								if(record.data['pr_boxqty'] >= 0){
									o['pr_boxqty'] = record.data['pr_boxqty'];
								}else{
									boolb = false;
									showError("外箱容量不允许小于0");
									return;
								}
								if(grid.keyField){
									o[grid.keyField] = record.data[grid.keyField];
								} 
								if (record.data['mantissapackage'] != null && record.data['mantissapackage'] != '' ){
									record.data['mantissapackage']=record.data['mantissapackage'].replace(/，/ig,','); 
									var re=/^\d+(,\d+)*$/;
								if(re.test(record.data['mantissapackage'])){
									var str = new Array(); 
									var sum=0;
									str = record.data['mantissapackage'].split( "," );
									for (var i = 0; i < str.length; i++ )
									{
										 sum+=parseFloat(str[i]);
									}}else{
										showError("请输入正确的尾数分装数,例如20,30,40");
										return;
									}
								}
								if(grid.necessaryFields){
									Ext.each(grid.necessaryFields, function(f, index){
										var v = record.data[f];
										if(Ext.isDate(v)){
											v = Ext.Date.toString(v);
										}
										o[f] = v;
									});
								}
								o['and_prodcode'] = record.data['and_prodcode'];
								o['vendercode'] = record.data['vendercode'];
								o['and_detno'] = record.data['and_detno'];
								o['mantissapackage'] = record.data['mantissapackage'];
								if( record.data['pr_zxbzs'] > 0){
									o['pr_zxbzs'] = record.data['pr_zxbzs'];
								}else{									
									boolz = true;
									o['pr_zxbzs'] = record.data['rest'];									
								}							
								if(record.data['madedate'] != '' && record.data['madedate'] !=null ){
									o['madedate'] = Ext.Date.format(record.data['madedate'], 'Y-m-d H:i:s');
							    } 
								if(record.data['rest']-sum < 0){
									showError("本次数量不允许小于尾数之和");
									return;
								}else{
									if(record.data['mantissapackage'] != null && record.data['mantissapackage'] != ""){
									if((record.data['rest']-sum)%o['pr_zxbzs'] !=0){
										showError("本次数量-尾数数量不是分装数量的整数倍!");
										return;
									}}
								}
								data.push(o);
							}
         	         })
         	      }
         	      if(boolb&&bool){
					  if(boolz){
						  warnMsg("分装数量未填，是否按本次数量生成条码？", function(btn){
					         if(btn == 'yes'){
					        	 me.newBarcode(data);
					         }
						   });
					  }else{
						  me.newBarcode(data);
					  }						 
					} else {
						showError("存在必填项没有填写！");
					}
    		    }
    		}
      });
     },
     onGridItemClick: function(selModel, record){// grid行选择
     	this.GridUtil.onGridItemClick(selModel, record);
     },
     newBarcode:function(data){
    	  var params = new Object();
    	  var me=this;
    	  params.caller = caller;
    	  params.id = key;
   	      params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));   	      
		  me.setLoading(true); 
		   	Ext.Ajax.request({
				url : basePath + "vendbarcode/acceptNotify/batchGenBarcode.action",			
				params: params,	
				method : 'post',
				async:true,
				callback : function(options,success,response){
					me.setLoading(false);
					var res = new Ext.decode(response.responseText);
    				var grid = parent.Ext.getCmp("vendSetBarcodeGridPanel");
    				if(res.exceptionInfo != null){
						showError(res.exceptionInfo);
						return;
					}
    				var gridx = Ext.getCmp("vendBarcodeGridPanel");
					gridx.on('storeloaded',function(){
						gridx.selModel.selectAll(true);
					});
					me.GridUtil.loadNewStore(gridx, {caller: caller, condition:condition });
					var status = Ext.getCmp('autoPrint').value;
					var params = new Object();
			    	params['idS'] = key;
					if(status){
						me.zplprint();
					}
					if(!grid){
	    				var win = parent.Ext.getCmp('win');
						win.on('hide',function(){
							var panel = parent.Ext.getCmp('form');
							var an_status=getUrlParam('status');
							var formCondition1 = "an_idIS" + key +" and an_codeIS '"+inoutno+"'";
							var gridCondition1 = "ban_anidIS" + key +" order by ban_anddetno asc";
							panel.FormUtil.onAdd('addBarcode'+key, '条形码维护('+inoutno+')', 'jsps/vendbarcode/setBarcode.jsp?_noc=1&whoami=Vendor!Baracceptnotify&key='+key+'&inoutno='+inoutno+'&status='+an_status+'&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1);
						});
					}else{
						win.un('hide');
					}
				}
		  }) 
     },
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
     zebraPrint:function(caller,printer,dpi){
    	    var me = this;
	    	var params = new Object();
	    	params['id'] = key;
	    	params['condition']= '( ban_anid='+key+")";
	    	var printCaller = caller;
	    	if('vendSaveBarcode'== caller){
	    		printCaller = 'VendBarcodeInPrint';
	    	}
	    	sendData(printCaller,printer,dpi,params);
     },
	 zplprint:function(addPanel){
			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/zplPrinter',
				method : 'post',
				callback : function(options,success,response){					
				}
		       });    		
			},
	 sendSelfData:function(caller,dpi,params){
			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/zplPrint.action',
				params: {
					caller:'VendBarcodeInPrint',
					dpi:dpi,
					data: unescape(escape(Ext.JSON.encode(params)))
				},
				method : 'post',
				timeout: 60000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						/*Ext.Msg.alert("提示","打印成功！");*/
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
	    });
		},
		sendSelfPicData:function(caller,dpi,params){
			Ext.Ajax.request({
	    		url : basePath +'api/pda/print/vendorZplPrint.action',
				params: {
					caller:caller,
					dpi:dpi,
					data: unescape(escape(Ext.JSON.encode(params)))
				},
				method : 'post',
				timeout: 60000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						/*Ext.Msg.alert("提示","打印成功！");*/
					}else if(res.exceptionInfo){
						var str = res.exceptionInfo;
						showError(str);return;
					}
				}
 	    });
		} 
});
    
