Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SaveBarcode', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'scm.reserve.saveBarcode.Viewport','scm.reserve.saveBarcode.GridPanel','core.trigger.AddDbfindTrigger',
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
    	me.resized = false;
    	this.control({
    		'erpGridPanel2': { 
    			reconfigure:function(grid){
    			},
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpSaveBarcodeGridPanel':{
			    afterrender:function(m){
			    	var grid = Ext.getCmp("saveBarcodeGridPanel");
			    	var items = grid.store.data.items;
	    			Ext.each(items, function(item, index){
	    				var id=item.data['pd_id'];
	    				if(id != null && id != ''&& id != '0' && id != 0){
	    					m.getSelectionModel().selectAll(true);
	    				}
	    			})
			    },
			   beforeedit: function(){}			   			   
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				var grid1 = parent.Ext.getCmp("setBarcodeGridPanel");
	    			if(grid1){
    					parent.location="javascript:location.reload()";//窗口关闭后刷新父页面
	    			}
    			}
    		},
    		'button[id=boxqtySet]':{
    			click: function(btn){
    				var grid = Ext.getCmp('saveBarcodeGridPanel');
    				var val = Ext.getCmp("pr_boxqty").value;
    				Ext.Array.each(grid.store.data.items,function(item){
    					item.set('pr_boxqty',val);
    				}
    				);
    			}
    		},
    		'button[id=confirm]':{//确认生成
    			click: function(btn){
    				var me = this;
	    			var id = key;
	                var gridCondition1 = "pd_piidIS" + key+" and pd_inqty-nvl(pd_barcodeinqty,0)>0 order by pd_pdno asc";
	                //这里的formCondition1也是去掉了pr_tracekind>0
	                var linkCaller = 'saveBarcode';
	    			var grid = Ext.getCmp("saveBarcodeGridPanel");
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
	         		        && this.data.pd_id != '0' && this.data.pd_id  != 0){
	         	            item.index = this.data.pd_pdno;
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
								if (record.data['pd_mantissapackage'] != '' || record.data['madedate'] != null ){
									record.data['pd_mantissapackage']=record.data['pd_mantissapackage'].replace(/，/ig,','); 
									var re=/^\d+(,\d+)*$/;
								if(re.test(record.data['pd_mantissapackage'])){
									var str = new Array(); 
									var sum=0;
									str = record.data['pd_mantissapackage'].split( "," );
									for (var i = 0; i < str.length; i++ )
									{
										 sum+=parseFloat(str[i]);
									}}else{
										boolb = false;
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
								o['pd_prodcode'] = record.data['pd_prodcode'];
								o['vendercode'] = record.data['vendercode'];
								o['pd_mantissapackage'] = record.data['pd_mantissapackage'];
								o['pd_pdno'] = record.data['pd_pdno'];
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
									boolb = false;
									showError("本次数量不允许小于尾数之和");
									return;
								}else{
									if(record.data['pd_mantissapackage'] != null && record.data['pd_mantissapackage'] != ""){
									if((record.data['rest']-sum)%o['pr_zxbzs'] !=0){
										boolb = false;
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
				url : basePath + "scm/reserve/batchGenBarcode.action",			
				params: params,	
				method : 'post',
				async:true,
				callback : function(options,success,response){
					me.setLoading(false);
					var res = new Ext.decode(response.responseText);
    				var grid = parent.Ext.getCmp("setBarcodeGridPanel");
    				if(res.exceptionInfo != null){
						showError(res.exceptionInfo);
						return;
					}
    				var gridx = Ext.getCmp("saveBarcodeGridPanel");
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
							var pi_class=(formCondition.split('IS')[formCondition.split('IS').length-1]).replace("'","").replace("'","");
							var formCondition1 = "pi_idIS" + key +" and pi_inoutNoIS '"+inoutno+"'"+" and pi_classIS'"+pi_class+"'";
							var gridCondition1 = "bi_piidIS" + key +" order by bi_pdno asc,bi_barcode asc";
							panel.FormUtil.onAdd('addBarcode'+key, '条形码维护('+inoutno+')', 'jsps/scm/reserve/setBarcode.jsp?_noc=1&whoami=ProdInOut!BarcodeIn&key='+key+'&inoutno='+inoutno+'&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1);
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
	    	params['condition']= '( bi_piid='+key+")";
	    	var printCaller = caller;
	    	if('saveBarcode'== caller){
	    		printCaller = 'BarcodeInPrint';
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
    
