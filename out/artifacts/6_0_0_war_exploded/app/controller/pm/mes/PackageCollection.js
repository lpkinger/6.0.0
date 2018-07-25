Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.PackageCollection', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.PackageCollection','core.trigger.DbfindTrigger',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Close', 'core.button.Print',
    		'core.trigger.TextAreaTrigger','core.trigger.BoxCodeTrigger','core.grid.ButtonColumn'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {
    				me.query();
    			}
    		},
    		'#ms_sncode': {
    			specialkey: function(f, e){//按ENTER执行确认
    				if ([e.ENTER, e.RETURN, e.SPACE].indexOf(e.getKey()) > -1) {
    					Ext.defer(function(){
    						var val = e.target.value;
    						if(val && val!=null && val!=''){
        						me.onConfirm(val);
            				}
    					}, 50);   	
    				}
    			}
    		},
    		'#pa_code':{
    			afterrender:function(){
    				Ext.create('Ext.tip.ToolTip', {
					     target: 'pa_code-triggerWrap',
					     html: '生成箱号'
					 });
    			},
    			specialkey: function(f, e) { //按ENTER执行确认,带出箱内数量
                    if (e.getKey() == e.ENTER) {
                        if (f.value != null && f.value != '') {
                            me.getQty(f.value);
                        }
                    }
                },
                blur:function(f){
                	if (f.value != null && f.value != '') {
                            me.getQty(f.value);
                        }
                }
    		},
    		'textfield[id=pa_inqty]':{
    			change:function(f,n,o){
					var qty = Ext.getCmp("pa_totalqty").value;
					if(n == qty){
						Ext.getCmp("pa_restqty").setValue(0);
					}else{
						Ext.getCmp("pa_restqty").setValue(qty-n);//设置剩余数的值
					}
				}
    		},    		
    		'button[id=updateQty]':{
    			click:function(btn){
    				var pa_code = Ext.getCmp("pa_code").value;
    				if(!pa_code || Ext.isElement(pa_code)){//
    					showError("请先输入箱号！");
    					return ;
    				}
    				me.updateQty(pa_code);
    			}
    		},
    		'erpPrintButton':{
    			click:function(btn){//打印
    			    //判断是否选择标签模板
    				var lps_id = Ext.getCmp('template').value, totalqty = Ext.getCmp("pa_totalqty").value,
    				restqty = Ext.getCmp('pa_restqty').value,pa_outboxcode = Ext.getCmp('pa_code').value;
    				if(pa_outboxcode == '' || pa_outboxcode == null){
    					showError('箱号不允许为空!');
    					return ;
    				}
    			//	if(lps_id && !Ext.isEmpty(lps_id)){
    					//判断箱是否装满，未装满提示“是否继续打印”
    					if(restqty > 0){
		    					warnMsg("该箱未装满，确定要打印吗?", function(btn){
			    					if(btn == 'yes'){
			    						me.print(pa_outboxcode,lps_id);
			    					}
		    				 });
    					}else{
    						me.print(pa_outboxcode,lps_id);
    					}
    			//	}else{
    			//		showError("请先选择标签模板！");
    			//	}
    			}
    		}
    	});
    },

	onConfirm : function(val){//确认采集序列号
		var me = this;
		var sc_code = Ext.getCmp("sc_code").value, mc_makecode = Ext.getCmp("mc_makecode").value,
		 pa_code = Ext.getCmp("pa_code").value, pa_restqty = Ext.getCmp("pa_restqty").value,
		 st_code = Ext.getCmp("st_code").value,ms_sncode = val,
		 mc_code =Ext.getCmp('mc_code').value; result = Ext.getCmp('t_result');
		 var grid = Ext.getCmp('querygrid');
		 //判断剩余装箱数量
		if(Ext.isEmpty(sc_code)){
			showError('请先指定资源编号!');
			return ;			
		}else if(Ext.isEmpty(mc_code)){
			showError('请先指定作业单号!');
			return ;
		}else if(Ext.isEmpty(pa_code)){
			showError('请先指定箱号!');
			return ;
		}else if(pa_restqty == 0 ||pa_restqty == '0'){
			showError('箱内总数已达到总容量!');
			return ;
		} if(Ext.isEmpty(ms_sncode)){
			result.append('请输入序列号!');
			return ;
		}
		grid.setLoading(true);
		var condition = {sc_code:sc_code,pa_code:pa_code,st_code:st_code,mc_makecode:mc_makecode,ms_sncode:ms_sncode,mc_code:mc_code};
		Ext.Ajax.request({//采集序列号
        	url : basePath + "pm/mes/getPackageDetail.action",
        	params: {condition: unescape(escape(Ext.JSON.encode(condition)))},
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			result.append(res.exceptionInfo, 'error');
        			Ext.getCmp("ms_sncode").setValue('');
        			showError(res.exceptionInfo);return;
        		}else{
        			me.query();       		
        			//更新已装数量，剩余可装数量
        			var pa_inqty = Ext.getCmp("pa_inqty").value;
        			Ext.getCmp("pa_inqty").setValue(pa_inqty+1);
        			var qty = Ext.getCmp("pa_totalqty").value;
        			Ext.getCmp("pa_restqty").setValue(qty-pa_inqty-1);
        			Ext.getCmp("ms_sncode").setValue('');
        			result.append('采集序列号：'+ms_sncode+'成功！');
        		}
        	}
        });			
	},
	query : function(){
		var me = this, querygrid = Ext.getCmp('querygrid');
		//判断资源编号不为空,制造单号,箱号
		var sc_code = Ext.getCmp("sc_code").value, mc_makecode = Ext.getCmp("mc_makecode").value,
		 pa_code = Ext.getCmp("pa_code").value, mc_code = Ext.getCmp("mc_code").value;
		if(Ext.isEmpty(sc_code)){
			showError('请先指定资源编号!');
			return ;			
		}else if(Ext.isEmpty(mc_code)){
			showError('请先指定作业单号');
			return ;
		}else if(Ext.isEmpty(pa_code) ){
			showError('请先指定箱号');
			return ;
		}		
		var condition = {sc_code:sc_code,mc_makecode:mc_makecode,pa_code:pa_code,mc_code:mc_code};
		var gridParam = {caller: caller, condition: unescape(escape(Ext.JSON.encode(condition)))};
		me.loadNewStore(querygrid, gridParam);
	},
	loadNewStore : function(grid,param){//筛选	
		var me = this;
		grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/mes/loadQueryGridStore.action",
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(data == null || data.length == 0){
        			Ext.getCmp('pa_inqty').setValue(0);
        			var qty = Ext.getCmp("pa_totalqty").value;
        			Ext.getCmp("pa_restqty").setValue(qty);
        			grid.store.removeAll();
        		} else {
        			if(grid.buffered) {
        				var ln = data.length, records = [], i = 0;
        			    for (; i < ln; i++) {
        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
        			    }
        			    grid.store.purgeRecords();
        			    grid.store.cacheRecords(records);
        			    grid.store.totalCount = ln;
        			    grid.store.guaranteedStart = -1;
        			    grid.store.guaranteedEnd = -1;
        			    var a = grid.store.pageSize - 1;
        			    a = a > ln - 1 ? ln - 1 : a;
        			    grid.store.guaranteeRange(0, a);
        			} else {
        				grid.store.loadData(data);
        			}
        			Ext.getCmp('pa_inqty').setValue(data.length);
        			var qty = Ext.getCmp("pa_totalqty").value;
        			Ext.getCmp("pa_restqty").setValue(qty-data.length);
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });	
	},
	 getQty:function(data){
    	Ext.Ajax.request({//拿到grid的columns
    		url : basePath + "pm/bom/getDescription.action",
    		params: {
    			tablename: 'package',
    			field: 'pa_totalqty',
    			condition: "pa_status=0 and pa_outboxcode='"+data+"'"
    		},
    		method : 'post',
    		callback : function(options,success,response){
    		var res = new Ext.decode(response.responseText);
    		if(res.exceptionInfo){
    			showError(res.exceptionInfo);
    			Ext.getCmp('pa_code').setValue('');
    			Ext.getCmp('pa_totalqty').setValue(0);
    			return;
    		}
    		if(res.description == null){
    			showError('箱号:'+data+'错误，不存在或者状态无效!');
    			Ext.getCmp('pa_code').setValue('');
    			Ext.getCmp('pa_totalqty').setValue(0);
    			return;
    		}else if(res.description == '0' || res.description == 0){
    			showError('箱号:'+data+'错误，库存数量为0!');
    			Ext.getCmp('pa_code').setValue('');
    			Ext.getCmp('pa_totalqty').setValue(0);
    			return;
    		}else{//包装箱号正确设置编号数量
    			Ext.getCmp('pa_totalqty').setValue(res.description);
    		}
    	 }
	  });
    },
    updateQty : function(pa_code){  	
	   var me = this;
	   var win=Ext.create('Ext.window.Window', {
		   width: 430,
		   height: 250,
		   closeAction: 'destroy',
		   title: '<h1>修改箱内容量</h1>',
		   layout: {
			   type: 'vbox'
		   },
		   items: [{
			   margin: '5 0 0 5',
			   xtype: 'textfield',
			   fieldLabel: '箱号',
			   name: 'boxcode',
			   value: pa_code,
			   id:'boxcode',
			   readOnly:true
		   },
		   {
			   margin: '5 0 0 5',
			   xtype: 'numberfield',
			   fieldLabel: '箱内容量',
			   name: 'qty',
			   value: Ext.getCmp("pa_totalqty").value ||0,
			   id:'qty',
			   allowBlank:false,
			   fieldStyle : "background:rgb(224, 224, 255);",    
			   labelStyle:"color:red;"	
		   }],
		   buttonAlign: 'center',
		   buttons: [{
			   xtype: 'button',
			   text: '保存',
			   width: 60,
			   iconCls: 'x-button-icon-save',
			   handler: function(btn) {
				   var w = btn.up('window');
				   me.updateQ(w);				 
			   }
		   },
		   {
			   xtype: 'button',
			   columnWidth: 0.1,
			   text: '关闭',
			   width: 60,
			   iconCls: 'x-button-icon-close',
			   margin: '0 0 0 10',
			   handler: function(btn) {
				   var win = btn.up('window');
				   win.close();
				   win.destroy();
			   }
		   }]
	   });
	   win.show();	       
    },
    updateQ:function(w){   	
	   var boxcode = w.down('field[name=boxcode]').getValue();
	   var qty = w.down('field[name=qty]').getValue();   
	   var oqty = Ext.getCmp("pa_totalqty").getValue();
	   if(oqty == qty){
	   	   showError('修改的箱内容量等于原箱内容量!');
		   return;
	   }
	   if (!qty || qty <0 || qty == 0 ) {
		   showError('请填写有效的箱内容量!');
		   return;
	   } else {    				 
		   Ext.Ajax.request({
			   url: basePath + 'pm/mes/updatePackageQty.action',
			   params: {
				   pa_outboxcode: boxcode,
				   pa_totalqty  : qty   ,
				   caller       : caller
			   },
			   method: 'post',
			   callback: function(opt, s, res) {
				   var r = new Ext.decode(res.responseText);
				   if (r.success) {					  
					   showMessage('提示', '更新成功!', 1000);
					   Ext.getCmp("pa_code").setValue(boxcode);
					   Ext.getCmp("pa_totalqty").setValue(qty);
					   Ext.getCmp("pa_restqty").setValue(qty - Ext.getCmp("pa_inqty").getValue());
					   w.close();
				       w.destroy();
				   } else if (r.exceptionInfo) {
					   showError(r.exceptionInfo);
				   } 
			   }
		   });
	   }	       
    },
    print:function(pa_outboxcode,lps_id){
    	var me = this;
    	//me.FormUtil.getActiveTab().setLoading(true);//loading...
    	 if (!Ext.fly('ext-attach-download')) {  
		   var frm = document.createElement('form');  
		   frm.id = 'ext-attach-download';  
		   frm.name = id;  
		   frm.className = 'x-hidden';
		   document.body.appendChild(frm);  
	   }
		Ext.Ajax.request({
	   		url : basePath + 'pm/mes/printPackageSN.action',
	   		params: {
	   			pa_outboxcode: pa_outboxcode,
	   			lps_id       : lps_id,
	   			caller       : caller		  
	   		},
	   		method : 'POST',
	   		form: Ext.fly('ext-attach-download'),
	   		isUpload: true,
	   		callback : function(o, s, res){
	   			//me.FormUtil.getActiveTab().setLoading(false);
	   			/*var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
	   			if(r.success){
	   				var fso, tf;  
                     fso = new ActiveXObject("Scripting.FileSystemObject");  
                     tf = fso.CreateTextFile("D:\\资料\\测试\\ftp.txt", true);  
                     tf.WriteLine("Testing 1, 2, 3.") ;  
	   			}*/
	   		}
		});
    }

});