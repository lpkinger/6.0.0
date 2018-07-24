Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProfitBarcode', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'scm.reserve.profitBarcode.Viewport','scm.reserve.profitBarcode.GridPanel','core.trigger.AddDbfindTrigger',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.button.Close',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.button.GenerateBarcode','core.button.PrintAll','core.button.DeleteAllDetails',
     	    'core.button.PrintAllPackage','core.button.GeneratePaCode','core.button.PrintByCondition','core.button.PrintMore'
     	],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpProfitBarcodeGridPanel':{
			    	afterrender: function(m){
    				    var grid = Ext.getCmp("profitBarcodeGridPanel");
   			    		var items = grid.store.data.items;
   			    		Ext.each(items, function(item, index){
   	    				var id=item.data['bsd_id'];
   	    				if(id != null && id != ''&& id != '0' && id != 0){
   	    					m.getSelectionModel().selectAll(true);
   	    				}
   	    			})
    			},
			   beforeedit: function(){}			   			   
    		},
    		'erpCloseButton': {
    			click: function(btn){
	    			var grid1 = window.parent.Ext.getCmp("profitGridPanel");
					if(grid1){
						parent.location="javascript:location.reload()";//窗口关闭后刷新父页面
					}	
    			}
    		},
    		'button[id=boxqtySet]':{
    			click: function(btn){
    				var grid = Ext.getCmp('profitBarcodeGridPanel');
    				var val = Ext.getCmp("pr_boxqty").value;
    				Ext.getCmp(grid.needField4).setValue(Ext.getCmp("pr_boxqty").value);
    				Ext.Array.each(grid.store.data.items,function(item){
    					item.set(grid.needField4,val);
    				}
    				);
    			}
    		},
    		'button[id=confirm]':{//确认生成
    			click: function(btn){
    			var id =key;
    			var no=inoutno;
                var linkCaller = 'profitBarcode';
    			var grid =Ext.getCmp("profitBarcodeGridPanel");
				if( grid.selModel.getCount() == 0 ){
					showError("没有需要处理的数据!");
					 return;
				}
				var params = new Object();
 				var items = grid.selModel.getSelection();
 				var data = new Array();
 				grid.multiselected = [];
 				var bool = false;
 				var boolz = false;
 				 Ext.each(items, function(item, index){
         	        if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
         		        && this.data['bsd_bsid'] != '0' && this.data['bsd_bsid']  != 0){
         	            item.index = this.data['bsd_id'];
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
								var o = new Object();				 
								if(grid.keyField){
									o[grid.keyField] = record.data[grid.keyField];
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
								if(record.data['rest'] >= 0){
									o['rest'] = record.data['rest'];
									}else{
										showError("本次数量不允许小于0");
										return;	
								}
								if(record.data['pr_boxqty'] >= 0){
									o['pr_boxqty'] = record.data['pr_boxqty'];
								}else{
									showError("外箱容量不允许小于0");
									return;
								}
								o['bsd_prodcode'] = record.data['bsd_prodcode'];
								o['vendercode'] = record.data['vendercode'];
								o['bsd_detno'] = record.data['bsd_detno'];
								if( record.data['pr_zxbzs'] > 0){
									o['pr_zxbzs'] = record.data['pr_zxbzs'];
								}else{									
									boolz = true;
									o['pr_zxbzs'] = record.data['rest'];									
								}			
								if(record.data['madedate'] != '' && record.data['madedate'] !=null ){
										o['madedate'] = Ext.Date.format(record.data['madedate'], 'Y-m-d H:i:s');
								} 
								data.push(o);
							}
         	      })
         	      }      	    
         	     if(bool){
					  if(boolz){
						  warnMsg("有最小包装未填，是否按本次数量生成条码？", function(btn){
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
    		}} 	
      });
     },
     onGridItemClick: function(selModel, record){// grid行选择
     	this.GridUtil.onGridItemClick(selModel, record);
     },
     newBarcode:function(data){
    	 var me=this;
    	 var params = new Object();
	   	  params.caller = caller;
	   	  params.id = key;
  	      params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%")); 
  	      me.setLoading(true);
		   	Ext.Ajax.request({
				url : basePath + "scm/reserve/barStock/batchGenBarcode.action",			
				params: params,
				method : 'post',
				async:true,
				callback : function(options,success,response){
					me.setLoading(false);
					me.FormUtil.setLoading(false);
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo != null){
						showError(res.exceptionInfo);
						return;
					}
					var gridx = Ext.getCmp("profitBarcodeGridPanel");
					gridx.on('storeloaded',function(){
						gridx.selModel.selectAll(true);
					});
					me.GridUtil.loadNewStore(gridx, {caller: caller, condition:condition });
					var grid1 = window.parent.Ext.getCmp("profitGridPanel");
					if(data.length!=0){
						var status = Ext.getCmp('autoPrint').value;
						var reportname='';
						var params = new Object();
				    	params['idS'] = key;
				    	var lps_Barcaller = '';
				    	if(caller == 'profitBarcode'){
							lps_Barcaller = 'BarStock!BarcodePrint';
						}
						if(status){
							me.zplprint();
						}
						if(!grid1){
							var win = parent.Ext.getCmp('win');
							win.on('hide',function(){
								var pi_class=(formCondition.split('IS')[formCondition.split('IS').length-1]).replace("'","").replace("'","");
								var panel = parent.Ext.getCmp('form');
								var formCondition1 = "bs_idIS" + key +"and bs_codeIS '"+inoutno+"'"+"and bs_classIS'"+pi_class+"'";
								var gridCondition1 = "bdd_bsidIS" + key +" order by bdd_id asc";
	    	                    var linkCaller = 'profit';
	    	                    panel.FormUtil.onAdd('addBarcode'+key, '条形码维护('+inoutno+')', 'jsps/scm/reserve/profit.jsp?_noc=1&whoami=' 
			                    		  + linkCaller +'&key='+key+'&inoutno='+inoutno+'&status='+status+'&formCondition=' + formCondition1 + '&gridCondition=' 
			                    		  + gridCondition1);
	    	                });
						}else{
							win.un('hide');
						}
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
	    	params['condition']= '( bdd_bsid='+key+")";
	    	var printCaller = caller;
	    	if('profitBarcode'== caller){
	    		printCaller = 'BarStock!BarcodePrint';
	    	}
	    	sendData(printCaller,printer,dpi,params);
     },
     zplprint:function(addPanel){
 		var me = this;
 		if(!me.window){
			setup_web_print(function(printers,selected_printer){
				me.window = Ext.create('Ext.window.Window', {
					autoShow: true,
					title: '选择打印模板',
					width: 400,
					height: 300,
					layout: 'anchor',
					closeAction:'hide',
					id :'template-win',
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
     }
});
    
