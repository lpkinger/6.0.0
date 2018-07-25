Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MRPThrow', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'common.batchDeal.Viewport','common.batchDeal.Form','pm.mps.MRPThrowGrid','core.button.ScanReplaceProd','pm.mps.MrpReplaceGrid','core.button.SupplyTurnNeed',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.form.ConMonthDateField','core.form.YnField',
     		'core.button.VastDeal','core.button.VastPrint','core.button.VastAnalyse','core.button.GetVendor','core.form.FtDateField',
     		'core.button.VastTurnPurc','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.DealMake','core.button.Close',
     		'core.button.MakeOccur','core.button.SaleOccur','core.button.AllThrow','core.button.SelectThrow','core.button.AvailableReplaceProd','core.form.MonthDateField',
     		'core.button.TurnGoodsUp','core.trigger.AddDbfindTrigger','core.button.ThrowCancel','core.trigger.MultiDbfindTrigger'
     	],
    init:function(){
    	var me = this;   
    	this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
        			if(form && form.items.items.length > 0){
        				//根据form字段的多少来调节form所占高度
        				var height = window.innerHeight;
            			var cw = 0;
            			Ext.each(form.items.items, function(){
            				cw += this.columnWidth;
            			});
            			cw = Math.ceil(cw);
            			if(cw == 0){
            				cw = 5;
            			} else if(cw > 2 && cw <= 5){
            				cw -= 1;
            			} else if(cw > 5 && cw < 8){
            				cw = 4;
            			}
            			cw = Math.min(cw, 5);
            			form.setHeight(height*cw/10 + 10);
            			grid.setHeight(height*(10 - cw)/10 - 10);            			
        			}
        			grid.down('#storeCount').update({
        				count: grid.store.data.items[0].get(grid.keyField) ==0 ? 0 : grid.store.totalCount
        			});
        			grid.store.on('datachanged',function(store){     
        			    grid.selModel.select(grid.multiselected);
        			   
        			});
    			},
    			storeloaded:function(grid){
    				grid.down('#storeCount').update({
        				count: grid.store.data.items[0].get(grid.keyField) ==0 ? 0 : grid.store.totalCount
        			});
    				grid.multiselected=[];
    			}
    			
    		}, 
    		'erpSelectThrowButton': {   			
    			click: {
    				lock: 2000,
                	fn:function(btn){    		
	    				 var gridStore = me.NeedSelectThrow();
	    				 if(gridStore != null){
	    				   me.NeedThrow(gridStore)
	    			   }; 
	    			}
    			}
    		}, 
    		'erpVastDealButton': {
    			click: function(btn){
   				  var gridStore = me.NeedSelectThrow();
    			  if(gridStore != null){
    			  	if(caller == 'MRPOnhandThrow'){
    			  		 me.GoodsUp(gridStore,btn);
    			  	}else{
    				     me.NeedThrow(gridStore);
    			  	}
    			  }; 
    			}
    		},
    		'erpThrowCancelButton' : {
    			click : function(btn){
    				var gridStore = me.NeedSelectThrow();
   				 	if(gridStore != null){
   				 		me.throwCancel(gridStore);
   				 	}; 
    			}
    		},
    		'condatefield[name=md_needdate]':{
    			afterrender:function(field){
    				field.combo.setValue(7);
    				field.setDateFieldValue(7);
    			}
    		},
    		'dbfindtrigger[name=md_mpscode]':{
    			afterrender:function(field){
    				field.setValue(getUrlParam('mpscode'));
    			}
    		},
    		'erpAvailableReplaceProdButton': {
    			click: function(btn){    	
    			var urlcon=Ext.create('erp.util.BaseUtil').getUrlParam('urlcondition');
        		if(urlcon){
        			  var array=urlcon.split('AND');
        			  var id=array[0].split('=')[1].trim().replace(/'/g,"");		
        		}
    		    var condition="md_mpsid='"+id+"'";
    		    me.FormUtil.onAdd('MRPNeedReplace','可用替代维护','/jsps/common/batchDeal.jsp?whoami=MRPNeedReplace&_noc=1&urlcondition='+condition);   		     	   		
    			}
    		}, 
    		'erpScanReplaceProdButton':{
    			click:function(btn){
    				var grid=Ext.getCmp('batchDealGridPanel');
    				var selected = grid.getSelectionModel().getLastSelected( );
    				var value=selected.data[grid.keyField];
    				Ext.create('Ext.window.Window', {
    				    title: '查看替代料',
    				    height:400,
    				    width: 505,
    				    id:'win',
    				    layout: 'fit',
    				    items: {
    				        xtype: 'MrpReplaceGrid',
    				        border: false,
    				        caller:'MRPReplace',
    				        condition:"mr_mdid="+value
    				    },
    				    buttonAlign:'center',
    				    buttons:[{
    				    	xtype:'button',
    				    	text: $I18N.common.button.erpCloseButton,
    						iconCls: 'x-button-icon-close',
    				    	cls: 'x-btn-gray',
    				    	width: 60,
    				    	style: {
    				    		marginLeft: '10px'
    				        },
    				        handler:function(btn){
    				        	btn.ownerCt.ownerCt.close();
    				        }
    				    }]
    				}).show();
    			}
    		},
    		'erpVastPrintButton':{
    			beforerender:function(btn){
    			 btn.handler=function(){
    				 var kind=caller;
    				 var idStr='';
    				 var title='';
    				 var dateFW='';
    				 var mpscode=Ext.getCmp('md_mpscode').value;
    				 var reportName='MRPData';
    				 if(caller=="MPSNeed"){
    					  condition='{MRPData.md_mpscode}='+"'"+mpscode+"'"+' and '+'{MRPData.md_kind}='+"'NEED'"+' and '+'{PRODUCT.pr_supplytype}<>'+"'虚拟件'";
    				 }
    				 if(caller=="MPSSupply"){
    					  condition='{MRPData.md_mpscode}='+"'"+mpscode+"'"+' and '+'{MRPData.md_kind}='+"'SUPPLY'"+' and '+'{PRODUCT.pr_supplytype}<>'+"'虚拟件'";
    				 }
    				 me.FormUtil.batchPrint(idStr,reportName,condition,title,dateFW);
    				 
    			 };
    		   }
    			
    		},
    		'erpSupplyTurnNeedButton':{
    			click:function(){ 
    				me.SupplyTurnNeed();
    			}
    		},
    		'erpAllThrowButton':{
    			afterrender:function(btn){
    				//处理一些需要条件传回来赋值
        			var urlcon=Ext.create('erp.util.BaseUtil').getUrlParam('urlcondition');
        			if(urlcon){
        			  var array=urlcon.split('AND');
        			  for(var i=0;i<array.length;i++){
        			    var field=Ext.getCmp(array[i].split('=')[0].trim());
        			    if(field){
        			       field.setValue(array[i].split('=')[1].trim().replace(/'/g,""));
        			    }  
        			  }			
        			}
        			if(caller=='MPSSupply'){
        				btn.hide();
        			}
    			} ,
    			click: function(btn){ 
    				warnMsg("确定要投放当前筛选结果的所有需求?", function(btn){
       				 me.NeedThrowBycondition();
       				});
    			}
    		}, 
    		'combo[name=md_sourcekind]':{
    			select :function(field){
    			 
    			}
    		}
    	});
    },
    countGrid: function(){
    	//重新计算合计栏值
    	var grid = Ext.getCmp('batchDealGridPanel');
    	Ext.each(grid.columns, function(column){
			if(column.summary){
				var sum = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						sum += Number(item.value);
					}
				});
				Ext.getCmp(column.dataIndex + '_sum').setText(column.text + '(sum):' + sum);
			} else if(column.average) {
				var average = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						average += Number(item.value);
					}
				});
				average = average/grid.store.data.items.length;
				Ext.getCmp(column.dataIndex + '_average').setText(column.text + '(average):' + average);
			} else if(column.count) {
				var count = 0;
				Ext.each(grid.store.data.items, function(item){
					if(item.value != null && item.value != ''){
						count++;
					}
				});
				Ext.getCmp(column.dataIndex + '_count').setText(column.text + '(count):' + count);
			}
		});
    },
    getCondition: function(){
		var grid = Ext.getCmp('batchDealGridPanel');
		//grid.multiselected = new Array();
		var form = Ext.getCmp('dealform');
		var condition = grid.defaultCondition || ''; 
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(f.value != null && f.value != ''){
							if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value, '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else {
								if(condition == ''){
									condition += f.logic + "='" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + "='" + f.value + "')";
								}
							}
						}
					}
				}
			}
		}); 
		return condition;
	},
    NeedSelectThrow: function(){  //获取选取的需要操作的明细
    	var grid = Ext.getCmp('batchDealGridPanel');
    	var count=0;
    	if(grid.multiselected.length==0){
    		var items = grid.selModel.getSelection();
            Ext.each(items, function(item, index){
            	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
            		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
            		grid.multiselected.push(item);
            	}
            });
    	}
		var records = Ext.Array.unique(grid.multiselected);
		var gridStore = new Array();
		var dd;
		if(records.length>0){
		   	 Ext.each(records, function(records, index){
			   	 if(records.data.md_prodcode!=''){
			   	  dd=new Object();
			   	  dd['md_id']=records.data.md_id;
				  gridStore[index] =  Ext.JSON.encode(dd);
				  count++;
				  }
			});	
			return gridStore;	
		} else {
			showError("没有需要处理的数据!");
			return ;
		}		
	 },
 
		NeedThrow:function(store){
			if(this.throwing) {
				alert('正在执行...不要重复点击!');
				return;
			}
			var me = this, gridstore = store;
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			var btn = Ext.getCmp('erpSelectThrowButton');
			if(btn) btn.setDisabled(true);
			this.throwing = true;
			var maKind=Ext.getCmp('md_pokind');
			var apKind = Ext.getCmp('md_apkind');
			var purcaseCop = Ext.getCmp('md_purchasecop');
			Ext.Ajax.request({
		   		url : basePath + "pm/MPSMain/NeedThrow.action",
		   		params: {
		   			mainCode:Ext.getCmp('md_mpscode').value,
		   			caller:caller,
		   			gridStore:unescape(gridstore.toString().replace(/\\/g,"%")),
		   			toWhere:'AUTO',
		   			toCode:Ext.getCmp('md_ordercode').value,
		   			maKind:maKind?maKind.value:'',
		   			condition:'' ,
		   			purcaseCop:purcaseCop?purcaseCop.value:'',
		   			apKind:apKind?apKind.value:''
		   		},
		   		timeout: 180000,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			btn.setDisabled(false);
					me.throwing = false;
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", str);
    	   				} else {
    	   					showError(str);return;
    	   				}
		   			}
	    			if(localJson.success){
	    				if(localJson.resmap.log){
	    					showMessage("提示", localJson.resmap.log);
	    				}
	    				var okqty=localJson.resmap.count;
	    				var ngqty=localJson.resmap.errcount;
		   				Ext.Msg.alert("提示", "成功投放:"+okqty+"条，投放失败:"+ngqty+"条,具体原因查看明细投放备注", function(){ 
		   					Ext.getCmp('dealform').onQuery();
		   				});
		   			}
		   		}
			});  
			
		},		
		NeedThrowBycondition:function(){ 
			var condition="";
			condition=this.getCondition();
			var main = parent.Ext.getCmp("content-panel");
			if (Ext.getCmp('pr_manutype').value!='PURCHASE'){
				showMessage("提示", "全部投放必须选择生产类型[外购]的物料");
				return;
			}
			var apKind = Ext.getCmp('md_apkind');
			var maKind = Ext.getCmp('md_pokind');
			var purcaseCop = Ext.getCmp('md_purchasecop');
			main.getActiveTab().setLoading(true);//loading... 
			Ext.Ajax.request({
		   		url : basePath + "pm/MPSMain/NeedThrow.action",
		   		params: {
		   			mainCode:Ext.getCmp('md_mpscode').value,
		   			caller:"MpsNeed",
		   			gridStore:null,
		   			toWhere:'AUTO',
		   			toCode:Ext.getCmp('md_ordercode').value,
		   			maKind:maKind?maKind.value:'',
		   			condition:condition ,
		   			purcaseCop:purcaseCop?purcaseCop.value:'',
		   			apKind:apKind?apKind.value:''
		   		},
		   		method : 'post',
		   		timeout: 60000,
		   		callback : function(options,success,response){
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", str);
    	   				} else {
    	   					showError(str);return;
    	   				}
		   			}
	    			if(localJson.success){
	    				if(localJson.resmap.log){
	    					showMessage("提示", localJson.resmap.log);
	    				}
	    				var okqty=localJson.resmap.count;
	    				var ngqty=localJson.resmap.errcount;
		   				Ext.Msg.alert("提示", "成功投放:"+okqty+"条，投放失败:"+ngqty+"条,具体原因查看明细投放备注", function(){ 
		   					Ext.getCmp('dealform').onQuery();
		   				});
		   			}
		   		}
			});  
		},
		SupplyTurnNeed:function(){
			var grid = Ext.getCmp('batchDealGridPanel');
	    	var count=0; 
	    	if(grid.multiselected.length==0){
	        var items = grid.selModel.getSelection();
	        Ext.each(items, function(item, index){
	        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        		grid.multiselected.push(item);
	        	}
	        });  
	    	}
			var records = Ext.Array.unique(grid.multiselected);
			var gridStore = new Array();
			var dd;
			if(records.length>0){
			   	 Ext.each(records, function(records, index){
			   	 if(records.data.md_prodcode!=''){
			   	  dd=new Object();
			   	  dd['md_id']=records.data.md_id;
				  gridStore[index] =  Ext.JSON.encode(dd);
				  count++;
				  }
				});
			   	var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
			   	Ext.Ajax.request({
			   		url : basePath + "pm/mps/turnsupplytoneed.action",
			   		params: {
			   			mainCode:Ext.getCmp('md_mpscode').value,
			   			gridStore:unescape(gridStore.toString().replace(/\\/g,"%"))
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				showError(localJson.exceptionInfo);
			   				return "";
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
		    				var okqty=localJson.resmap.count;
		    				var ngqty=localJson.resmap.errcount;
			   				Ext.Msg.alert("提示", "成功投放:"+okqty+"条，投放失败:"+ngqty+"条,具体原因查看明细投放备注", function(){ 
			   					Ext.getCmp('dealform').onQuery();
			   				});
			   			}
			   		}
				});  
			  
			} else {
				showError("没有需要处理的数据!");
				}
	    },
	    throwCancel : function(gridStore){
			if(this.throwing) {
				alert('正在执行...不要重复点击!');
				return;
			}
			var me = this;
			var main = parent.Ext.getCmp("content-panel");
			main.getActiveTab().setLoading(true);//loading...
			var btn = Ext.getCmp('erpThrowCancelButton');
			if(btn) btn.setDisabled(true);
			this.throwing = true;
			Ext.Ajax.request({
		   		url : basePath + "pm/MPSMain/ThrowCancel.action",
		   		params: {
		   			caller:caller,
		   			gridStore:unescape(gridStore.toString().replace(/\\/g,"%")),
		   		},
		   		timeout: 60000,
		   		method : 'post',
		   		callback : function(options,success,response){
		   			btn.setDisabled(false);
					me.throwing = false;
		   			main.getActiveTab().setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", str);
    	   				} else {
    	   					showError(str);return;
    	   				}
		   			}
	    			if(localJson.success){
	    				if(localJson.log){
	    					showMessage("提示", localJson.log);
	    					Ext.getCmp('dealform').onQuery();
	    				}
		   			}
		   		}
			});  
			
		
	    }
    });