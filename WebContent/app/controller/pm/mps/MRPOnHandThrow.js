Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MRPOnHandThrow', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'pm.mps.MRPOnHandThrow.Viewport','pm.mps.MRPOnHandThrow.Form','pm.mps.MRPOnHandThrow.MRPOnHandThrowGrid',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.form.ConMonthDateField','core.form.YnField',
     		'core.form.FtDateField','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.Close',
     		'core.form.MonthDateField','core.button.TurnGoodsUp','core.button.GetB2CProductKind','core.button.ExecuteOperation',
     		'core.button.TurnDeviceInApply'
     	],
    GridUtil:Ext.create('erp.util.GridUtil'), 	
    BaseUtil:Ext.create('erp.util.BaseUtil'), 	
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
        				count: grid.store.getCount()
        			});
        			grid.store.on('datachanged',function(store){     
        			    grid.selModel.select(grid.multiselected);
        			});
    			},
    			storeloaded:function(grid){
    				grid.down('#storeCount').update({
        				count: grid.store.getCount()
        			});
    				grid.multiselected=[];
    				var remind = ''
    				if(grid.store.getCount()>0){
	    				var records=grid.store.data.filterBy(function(record){
	    				    return Ext.isEmpty(record.data['pr_uuid']) || record.data['pr_uuid']==0 ;
	    				});
	    				var remind=records.length>0?Ext.String.format('<p style="color:#436EEE;padding-left:20px;font-size:14px;">未匹配物料:{0}条;商城中不存在的品牌、器件，您可以提交品牌入库申请、器件入库申请 </p>',
	    			    		records.length
						):'';
    				}
    			    Ext.getCmp('dealform').down('#reminder').setText(remind);
    			},
    			edit:function(ed,d){
    				if(d.field=='po_lockqty'){
	    				//发送请求更新物料锁库数
	    			   me.updatelockqty(d);
    				}   			
    			},
    			itemclick: function(view,record){
    				me.itemclick(view,record,me);
    			}
    		}, 
    		'textfield[name=mdd_mpscode]':{
    			afterrender:function(f){
				   f.dbBaseCondition="mm_runkind ='B2C'";    		          	          
				   Ext.Ajax.request({ //获取最近一次时间的计划编号
			        	url : basePath + 'common/getFieldData.action',
			        	params: {
			        	  field : "mm_code", 
			        	  caller : "(select * from mpsmain where mm_runenddate is not null and mm_runkind='B2C' order by mm_runenddate desc )",
			        	  condition : "rownum=1"
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        	var res = new Ext.decode(response.responseText); 
			        	   if(res.exceptionInfo){ 
			        			return;
			        		}else if(res.success && res.data != null){ 
			        			 f.setValue(res.data);
		      	                 f.autoDbfind('form', caller, f.name, f.name + ' like\'%' + f.value + '%\'');
		      	                 Ext.getCmp('dealform').onQuery();
		      	                 /*setTimeOut(function(){
		      	                    Ext.getCmp('dealform').onQuery(); 
		      	                 },500);*/
			        		}
			        	}
			        });
			     
    			}
    		},
    		'textfield[name=gu_currency]':{//币别取商城客户中的币别
    			afterrender:function(t){
    				 Ext.Ajax.request({ //获取最近一次时间的计划编号
			        	url : basePath + 'common/getFieldData.action',
			        	params: {
			        	  field : "CU_CURRENCY", 
			        	  caller : "configs left join customer on cu_code=data",
			        	  condition : "caller='B2CSetting' and code='B2CCusomter'"
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        	var res = new Ext.decode(response.responseText); 
			        	   if(res.exceptionInfo){ 
			        			return;
			        		}else if(res.success && res.data != null){ 
			        			t.setValue(res.data);
		    				 	if(res.data =='USD'){
		    				 		Ext.getCmp("gd_taxrate").setValue('');
		    				 	}else{
		    				 		Ext.getCmp("gd_taxrate").setValue(17);
		    				 	}
			        		}
			        	}
			        });
    			}
    		},
    		'erpTurnGoodsUpButton':{//转上架申请
    			click: function(btn){    
    			  //币别税率必填
    			  var cy = Ext.getCmp("gu_currency");
    			  if(cy && (cy.value == null || cy.value == "")){
    			  	 showError("请选择的币别");
    			  	 return;
    			  }
    			  cy = Ext.getCmp("gd_taxrate");
    			  if(cy && (cy.value == null || cy.value == "")){
    			  	 showError("请填写税率");
    			  	 return;
    			  }
    			  var gridStore = me.NeedSelectThrow();
    			  if(gridStore != null){
    			  	me.GoodsUp(gridStore,btn);   				  
    			  }; 
    			}
    		},
    		'erpGetB2CProductKindButton':{
    			afterrender:function(btn){
    				btn.setDisabled(true);
    			},
    			click:function(){
    				var record = Ext.getCmp('batchDealGridPanel').selModel.lastSelected;
    				if(record && record.get("mdd_prodcode")){
    					me.onCellItemClick(record);
    				} 
    			}
    		},
    		'erpExecuteOperationButton':{
				  click:function(btn){
				     me.ExecuteOperation();
				  }
    		},
    		'erpTurnDeviceInApplyButton':{//转器件入库申请
    			click: function(btn){    
    				var gridStore = me.NeedSelectThrow();
      			  	if(gridStore != null){
      			  		me.TurnDeviceInApply(gridStore,btn);   				  
      			  	}; 
    			}
    		},
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
    NeedSelectThrow: function(){  //获取选取的需要操作的明细
    	var grid = Ext.getCmp('batchDealGridPanel');
    	var items = grid.getMultiSelected();
    	var me = this;
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		var records = Ext.Array.unique(grid.multiselected);
		var data = new Array();
		var form = Ext.getCmp("dealform");
		if(records.length>0){
		   	var bool = false;
			Ext.each(records, function(record, index){
				var f = form.fo_detailMainKeyField;
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
	        		||(f && this.data[f] != null && this.data[f] != ''
		        		&& this.data[f] != '0' && this.data[f] != 0)){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} else {
						params.id[index] = record.data[form.fo_detailMainKeyField];
					}
					if(grid.toField){
						Ext.each(grid.toField, function(f, index){
							var v = Ext.getCmp(f).value;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								o[f] = v;
							} else {
								o[f] = '';
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							if(Ext.isNumber(v)){
								v = (v).toString();
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			if(bool && !me.dealing){
				return data;
			}else {
				showError("没有需要处理的数据!");
				return ;
			}
		}else {
			showError("请勾选需要的明细!");
			return ;
		}		
	 },
     GoodsUp:function(store,btn){//转上架申请			
		if(this.throwing) {
			alert('正在转上架...不要重复点击!');
			return;
		}
		var me = this, gridstore = store;
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		if(btn) btn.setDisabled(true);
		this.throwing = true;
		Ext.Ajax.request({
	   		url : basePath + "pm/MPSMain/TurnGoodsUp.action",
	   		params: {
	   			mainCode:Ext.getCmp('mdd_mpscode').value,
	   			caller:caller,
	   			gridStore:unescape(Ext.JSON.encode(gridstore).replace(/\\/g,"%")),
	   			toCode:""
	   		},
	   		timeout: 60000,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			btn.setDisabled(false);
				me.throwing = false;
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
	   				Ext.Msg.alert("提示", "处理成功!", function(){ 
	   					Ext.getCmp('dealform').onQuery();
	   				});
	   			}
	   		}
		});  					
	},
	updatelockqty:function(d){
		if(d.record.dirty){
			if(Ext.isNumber(d.value) && (d.value==0 ||d.value>0)){
				Ext.Ajax.request({
			   		url : basePath + "pm/mps/updatePoLockqty.action",
			   		params: {
			   			data:unescape(escape(Ext.JSON.encode(d.record.data))),
			   			caller:caller
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				d.record.set('po_lockqty',d.originalValue);
			   				showError(localJson.exceptionInfo);			   				
			   			}
		    			if(localJson.success){	
		    				d.record.commit();
		    				showMessage("提示", "修改成功");		    				 
			   			}
			   		}
				}); 
			}else{
				d.record.set('po_lockqty',d.originalValue);
			}
		}		
	},
	 itemclick:function(view,record,me){
    	var show=0;
    	me.GridUtil.onGridItemClick(view,record); 
    	var fieldValue=record.data["mdd_prodcode"];
    	var btn = Ext.getCmp('getb2cproductkind');
        if(fieldValue==undefined||fieldValue==""||fieldValue==null){
	        if(btn && !btn.disabled){
				 btn.setDisabled(true);
			}
        }else{
        	if(btn && btn.disabled){
			     btn.setDisabled(false);
			}
        }
    },
    onCellItemClick:function(record){
		var me = this;
		// grid行选择
		var uuid = record.data['pr_uuid'] || record.data['mdd_uuid'];	
		if(!Ext.isEmpty(uuid)){
			//根据uuid获取相关信息
			/*me.getByUUid(uuid,function(data){ 	
    		    if(data != null){
    				me.createWin(data);
    		    }					    		  
	  		});	*/		
		}else{
			var linkCaller = 'Product';
			var status= '';
			var win = new Ext.window.Window({
				id : 'uuWin',
				height : "100%",
				width : "80%",
				maximizable : true,
				closeAction : 'destroy',
				buttonAlign : 'center',
				layout : 'anchor',
				title : '获取编号',
				items : [{
					tag : 'iframe',
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe id="iframe_'+linkCaller+'" src="'
							+ basePath
							+ 'jsps/scm/product/getUUid.jsp?type='
							+ linkCaller+'&status='+status
							+ '" height="100%" width="100%" frameborder="0"></iframe>'
				}]
			});
		  win.show();	
		}
	},
	/*createWin:function(data){
		var me = this, win = me.orWin;
		me.data=data;
		if (!win) {
		     me.orWin=win = Ext.create('Ext.Window',{  
				id : 'wind',
				title:'标准料号',
				height : '65%',
				width : '65%',
				maximizable : true,
				buttonAlign : 'center',
				closeAction:'hide',
				layout : 'anchor',
				items : [{
					xtype:'erpComponentGrid',
					anchor: '100% 100%'					
				}],
				bbar: ['->',{
						text:'关闭',
						cls: 'x-btn-gray',
						iconCls: 'x-button-icon-close',
						listeners: {
							click: function(btn){
								 btn.up('window').close();
							}
						}
					},'->'],
				listeners:{
					beforeshow:function(win){
						var g = win.down('erpComponentGrid');					
						g.store.loadData(me.data);	
						dataCount = data.length;
						Ext.getCmp('pagingtoolbar').afterOnLoad();
					}
				}
		  });		 
		}	
		 win.show(); 
	}*/
	ExecuteOperation:function(){
		var me = this;
		/**
		库存运算前，前台JS弹出提示：“确定运算？将自动作废上一次库存运算后未审核的上、下架单据！”
		 如果选择“确定”则执行：
		 1）作废业务单据：来源于MRP投放的上架、下架单，并且主表状态<>‘已作废’ ，且明细行状态 NVL(GD_SENDSTATUS,' ')<>'已上传'）；
		 2）执行审批流清除（可参考MRP自动运算存储过程）
	    */
		warnMsg("确定运算？将自动作废上一次库存运算后未审核的上、下架单据！", function(btn){
			if(btn == 'yes'){
				var mb = new Ext.window.MessageBox();
			    mb.wait('正在运算中','请稍后...',{
				   interval: 10000, 
				   duration: 1000000,
				   increment: 20,
				   scope: this
				});
				 Ext.Ajax.request({//拿到grid的columns
			        	url : basePath + 'pm/mps/RunMrpAndGoods.action',
			        	params: {
			        	  caller:caller
			        	},
			        	method : 'post',
			        	timeout: 600000,
			        	callback : function(options,success,response){
			        	mb.close();
			        	var res = new Ext.decode(response.responseText); 
			        	   if(res.exceptionInfo){ 
			        			showError(res.exceptionInfo);
			        			return;
			        		}else if(res.success){ 
			        			Ext.Msg.alert('提示',"运算成功",function(){      
			        				var condition="mdd_mpscode='"+res.message+"' AND nvl(pr_supplytype,' ') <>'VIRTUAL' and mdd_action='UP'";
							    	window.location.href = basePath+'jsps/pm/mps/MRPOnHandThrow.jsp?whoami=MRPOnhandThrow&_noc=1&urlcondition='+condition+'&mdd_mpscode='+res.message;
			        			});
			        		}
			        	}
			        });
			} else {//不运算
				return;
			}
		});
	},
	TurnDeviceInApply:function(store,btn){//转上架申请			
		if(this.throwing) {
			alert('正在转器件入库申请...不要重复点击!');
			return;
		}
		var me = this, gridstore = store;
		var main = parent.Ext.getCmp("content-panel");
		main.getActiveTab().setLoading(true);//loading...
		if(btn) btn.setDisabled(true);
		this.throwing = true;
		Ext.Ajax.request({
	   		url : basePath + "pm/MPSMain/TurnDeviceInApply.action",
	   		params: {
	   			caller:caller,
	   			gridStore:unescape(Ext.JSON.encode(gridstore).replace(/\\/g,"%")),
	   		},
	   		timeout: 60000,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			btn.setDisabled(false);
				me.throwing = false;
	   			main.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			}
    			if(localJson.success){
    				if(localJson.log){
    					showMessage("提示", localJson.log);
    					Ext.getCmp('dealform').onQuery();
	   					var url = "jsps/common/datalist.jsp?whoami=DeviceInApply";
	   					me.FormUtil.onAdd('DeviceInApply', 'ERP器件入库申请列表', url);
    				}
	   			}
	   		}
		});  					
	}
});


		 