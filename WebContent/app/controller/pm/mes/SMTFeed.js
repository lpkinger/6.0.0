Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.SMTFeed', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.mes.SMTFeed','core.form.Panel','common.query.GridPanel','core.button.ImportMPData',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn','core.button.ChangeMake',
    		'core.button.Query','core.button.Close', 'core.button.Enable', 'core.button.Stop',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.SearchField'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {
    				me.query();
    				Ext.getCmp('input').focus();
    			}
    		},
    		'erpEnableButton' : {
    			click: function(btn){
    				var status = Ext.getCmp('de_runstatus').value;
    				if(status&&status =='运行中'){
    					showError('当前机台状态是"运行中"，无需启用！');
						return;
    				}
    				warnMsg("确定要启用吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/enableDevice.action',
    	    			   		params: {
    	    			   			decode: Ext.getCmp('mc_devcode').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					Ext.getCmp('de_runstatus').setValue('运行中');
    	    		    					//window.location.reload();
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpStopButton' : {
    			click: function(btn){
    				var status = Ext.getCmp('de_runstatus').value;
    				if(status&&status =='停止'){
    					showError('当前机台状态是"停止"，无需暂停！');
						return;
    				}
    				warnMsg("确定要暂停吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/stopDevice.action',
    	    			   		params: {
    	    			   			decode: Ext.getCmp('mc_devcode').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					Ext.getCmp('de_runstatus').setValue('停止');
    	    		    					//window.location.reload();
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'erpChangeMakeButton':{
    			click:function(btn){//工单切换
		    	   var win = new Ext.window.Window({  
		    		  modal : true,
		        	  id : 'win',
		        	  height : '35%',
		        	  width : '30%',       	 
		        	  layout : 'anchor',   
		        	  bodyStyle: 'background: #f1f1f1;',
					  bodyPadding:5,
					  title:'工单切换',
		        	  items : [{
		        	  	anchor: '100% 100%',
		                xtype: 'form',
		                bodyStyle: 'background: #f1f1f1;',		               
			            items:[{
			        	      xtype:'dbfindtrigger',
			        		  name:'mcCode',
			        		  fieldLabel:'作业单号',
			        		  id:'mcCode',
			        		  allowBlank:false,	
			        		  fieldStyle : "background:rgb(224, 224, 255);",    
						      labelStyle:"color:red;"
			        	  },{
			        		  xtype:'textfield',
			        		  name:'makeCode',
			        		  fieldLabel:'工单号',
			        		  id:'makeCode',
			        		  allowBlank:false ,
			        		  readOnly:true
			        	  }],
		                buttonAlign : 'center',
			            buttons: [{
							text: '确定切换'	,
							cls: 'x-btn-gray',
							iconCls: 'x-button-icon-save',
							id:'confirmBtn',
							formBind: true, //only enabled once the form is valid
		                    handler: function(btn) {
		                        me.confirmChangeMake();	                        
							  }
						  }]
		    	       }]
		    		});
    	         win.show(); 
    			},
    			afterrender:function(btn){
    				btn.hide();
    			}   			
    		},
    		'erpImportMPDataButton':{
    			click:function(btn){//导入备料单数据
		    	   var win = new Ext.window.Window({  
		    		  modal : true,
		        	  id : 'win',
		        	  height : '35%',
		        	  width : '30%',       	 
		        	  layout : 'anchor',   
		        	  bodyStyle: 'background: #f1f1f1;',
					  bodyPadding:5,
					  title:'导入备料单数据',
		        	  items : [{
		        	  	anchor: '100% 100%',
		                xtype: 'form',
		                bodyStyle: 'background: #f1f1f1;',		               
			            items:[{
			        	      xtype:'dbfindtrigger',
			        		  name:'mp_code',
			        		  fieldLabel:'备料单号',
			        		  id:'mp_code',
			        		  allowBlank:false,	
			        		  fieldStyle : "background:rgb(224, 224, 255);",    
						      labelStyle:"color:red;",
							  listeners :{
							      afterrender:function(t){
				    				t.setHideTrigger(false);
				    				t.setReadOnly(false);//用disable()可以，但enable()无效			    				
				    				var code = Ext.getCmp("mc_code").value;
				    				if(code == null || code == ''){
				    					showError("请先选择作业单号!");
				    					t.setHideTrigger(true);
				    					t.setReadOnly(true);
				    				} else {
				    					t.dbBaseCondition = "mp_mccode='" + code + "'";
				    				}
				    			}
							  }
			        	  }],
		                buttonAlign : 'center',
			            buttons: [{
							text: '确定'	,
							cls: 'x-btn-gray',
							iconCls: 'x-button-icon-save',
							id:'confirmBtn',
							formBind: true, //only enabled once the form is valid
		                    handler: function(btn) {
		                        me.confirmImportMPData();	                        
							  }
						  },{
							text: '取消'	,
							cls: 'x-btn-gray',
							iconCls: 'x-button-icon-save',
							id:'closeBtn',
		                    handler: function(btn) {
		                        win.close();	                        
							  }
						  }]
		    	       }]
		    		});
    	         win.show(); 
    			},
    			afterrender:function(btn){
    				btn.hide();
    			}   			
    		},
    		'#mc_devcode':{
    			change:function(tf,newValue,oldValue){
    				if(newValue != oldValue){
    					me.setButtonS();    	               
	    			}
    			}
    		},
    		'#mc_code':{
    			change:function(tf,newValue,oldValue){
    				if(newValue != oldValue){
    					me.setButtonS();  
    				}
    			}
    		},
    		'#input': {
    			specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
    						me.onConfirm();
        				}
    				}
    			}
    		},
			'#confirm' : {
				click: function(btn) {
					me.onConfirm();
				},
				afterrender:function(btn){
					btn.disable(true);
				}
			},
			'#blankAll' : {
				click: function(btn) {
					var result = Ext.getCmp('t_result'), devcode = Ext.getCmp('mc_devcode').value,
						mccode = Ext.getCmp('mc_code').value, macode = Ext.getCmp('mc_makecode').value,
						sccode = Ext.getCmp("mc_sourcecode").value;
					if(Ext.isEmpty(mccode)){
						showError('请先指定作业单号！');
						return;
					}
					if(Ext.isEmpty(devcode)){
						showError('请先指定设备！');
						return;
					}
					warnMsg("确定全部退回?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'pm/mes/blankAll.action',
    	    			   		params: {				
    	    			   			macode : macode,						//制造单号
    	    			   			devcode: devcode,						//设备
    	    			   			mccode : mccode	,                       //作业单号
    	    			   			sccode : sccode                         //资源编号
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var r = new Ext.decode(response.responseText);
    	    			   			if(r.exceptionInfo){
    	    			   				result.append(r.exceptionInfo,'error');
    	    			   				showError(r.exceptionInfo);
    	    			   			}
    	    		    			if(r.success){
    	    		    				result.append('全部下料成功!','success');
    	    		    				me.getGrid();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
				},
				afterrender:function(btn){
					btn.disable(true);
				}
			}
    	});
    },
    beforeQuery: function(call, cond) {
    	var me = this,cbtn = Ext.getCmp('changeMakeBtn'),dbtn = Ext.getCmp('importMPDataBtn');
    	var btn1 =  Ext.getCmp("confirm"),btn2 = Ext.getCmp("blankAll");
		Ext.Ajax.request({
			url: basePath + 'pm/mes/beforeSMTFeedQuery.action',
			params: {
				caller: call,
				condition: unescape(escape(Ext.JSON.encode(cond)))
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
					btn1.setDisabled(true);
					btn2.setDisabled(true);
					if(!cbtn.isHidden()){
						cbtn.hide();
					}
					if(!dbtn.isHidden()){
						dbtn.hide();
					}
				}else if(rs.message == null){
					showError("作业单号："+Ext.getCmp("mc_code").value+"，未指定站料编号!");
					btn1.setDisabled(true);
					btn2.setDisabled(true);
					if(!cbtn.isHidden()){
						cbtn.hide();
					}
					if(!dbtn.isHidden()){
						dbtn.hide();
					}
				}else {					
					btn1.setDisabled(false);
					btn2.setDisabled(false);
					if(cbtn.isHidden()){
						cbtn.show();
					}	
					if(dbtn.isHidden()){
						dbtn.show();
					}
					me.getGrid();
				}
			}
		});
	},
	confirmChangeMake :function(){//确认切换
		var me = this;
		var mc_devcode = Ext.getCmp("mc_devcode").value,mc_code = Ext.getCmp("mc_code").value,
		mc_makecode = Ext.getCmp('mc_makecode').value,mc_linecode = Ext.getCmp('mc_linecode').value,
		mcCode = Ext.getCmp('mcCode').value,makeCode = Ext.getCmp('makeCode').value;
		Ext.Ajax.request({
			url: basePath + 'pm/mes/confirmChangeMake.action',
			params: {
				mc_devcode   : mc_devcode  , //机台编号，
				mc_code      : mc_code     , //原作业单号
				mc_makecode  : mc_makecode , //制造单号
				mc_linecode  : mc_linecode , //线别
				mcCode       : mcCode      , //转至作业单
				makeCode     : makeCode      //转至工单
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);		
				}else{
					showMessage("提示", '工单切换成功!');
					Ext.getCmp("mc_code").setValue(mcCode);
					Ext.getCmp('win').close();
					me.query();
				}
			}
		});
	},
	confirmImportMPData:function(){//导入备料单数据
		var me = this;
		var mc_devcode = Ext.getCmp("mc_devcode").value,mc_code = Ext.getCmp("mc_code").value,
		mc_makecode = Ext.getCmp('mc_makecode').value,mc_linecode = Ext.getCmp('mc_linecode').value,
		mp_code = Ext.getCmp('mp_code').value,sccode = Ext.getCmp('mc_sourcecode').value;
		Ext.Ajax.request({
			url: basePath + 'pm/mes/confirmImportMPData.action',
			params: {
				mc_devcode   : mc_devcode  , //机台编号，
				mc_code      : mc_code     , //作业单号
				mc_makecode  : mc_makecode , //制造单号
				mc_linecode  : mc_linecode , //线别
				mp_code      : mp_code     , //备料单号
				sccode       : sccode        //资源编号
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);		
				}else{
					showMessage("提示", '导入备料单数据成功!');
					Ext.getCmp('win').close();
					me.query();
				}
			}
		});
	},
	spellCondition: function(form, condition){
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
				} else if(f.xtype == 'yeardatefield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				}else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						var _a = '';
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(_a == ''){
									_a += f.logic + "='" + d.data.value + "'";
								} else {
									_a += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += _a + ')';
					}
				}	else if(f.xtype=='adddbfindtrigger' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + ' in (' ;		
					} else {
						condition += ' AND ' + f.logic + ' in (';
					}
					var str=f.value,constr="";
					for(var i=0;i<str.split("#").length;i++){
						if(i<str.split("#").length-1){
							constr+="'"+str.split("#")[i]+"',";
						}else constr+="'"+str.split("#")[i]+"'";
					}
					condition +=constr+")";
				} else {
					if(f.value != null && f.value != ''){
						var val = String(f.value);
						if(contains(val, 'BETWEEN', true) && contains(val, 'AND', true)){
							if(condition == ''){
								condition += f.logic + " " + f.value;
							} else {
								condition += ' AND (' + f.logic + " " + f.value + ")";
							}
						} else if(f.logic == 'ym_view_param') {
							if(condition == ''){
								condition += " " + f.value;
							} else {
								condition += ' AND (' + f.value + ")";
							}
						} else if(contains(val, '||', true)){
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
								condition += str;
							} else {
								condition += ' AND (' + str + ")";
							}
						} else {							
							if(val.indexOf('%') >= 0) {
								if(condition == ''){
									condition += f.logic + " like '" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + " like '" + f.value + "')";
								}
							} else {
								if(f.logic=='CONDITION'){
									if(condition == ''){
										condition +=  f.value ;
									} else {
										condition += ' AND '  + f.value;
									}
								}else{
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
			}
		});
		return condition;
	},
	onConfirm : function(){
		var me = this,  get = Ext.getCmp('get').value,back = Ext.getCmp('back').value, change = Ext.getCmp('change').value,add = Ext.getCmp('add').value ;
		var result = Ext.getCmp('t_result'), grid = Ext.getCmp('querygrid'),
			mlscode = Ext.getCmp('mlscode').value, fecode = Ext.getCmp('fecode').value,
			barcode = Ext.getCmp('barcode').value, devcode = Ext.getCmp('mc_devcode').value,
			mccode = Ext.getCmp('mc_code').value,  destatus = Ext.getCmp('de_runstatus').value,
			macode = Ext.getCmp('mc_makecode').value, linecode = Ext.getCmp('mc_linecode').value,
			tableAB = Ext.getCmp('tableAB').value, sccode = Ext.getCmp('mc_sourcecode').value,
			input = Ext.getCmp('input').value,dbtn = Ext.getCmp('importMPDataBtn');		
		if(Ext.isEmpty(mccode)){
			showError('请先指定作业单号！');
			return;
		}
		if(Ext.isEmpty(sccode)){
			showError('请先指定资源号！');
			return;
		}
		if(Ext.isEmpty(devcode)){
			showError('请先指定设备！');
			return;
		}
		if(Ext.isEmpty(linecode)){
			showError('请先指定线别！');
			return;
		}	
		if(dbtn.isHidden()){
			showError('请先执行筛选！');
			return;
		}
		if(get){
			if(destatus != '停止'){
				showError('当前机台运行状态必须是“停止”！');
				return;
			}			
			if(Ext.isEmpty(mlscode)){
				me.checkMslcode(mccode,input);//判断站位是否属于作业单										
				return;
			}	
			if(Ext.isEmpty(barcode)){					
				//判断料卷编号是否正确，数量是否大于0
				me.checkBarcode(input);
				return;
			}					
			if(Ext.isEmpty(fecode)){
				Ext.getCmp("fecode").setValue(input);
				Ext.getCmp("input").emptyText = "请录入站位编号";
				Ext.getCmp('input').setValue('');
				fecode = Ext.getCmp('fecode').value;
				me.FormUtil.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + 'pm/mes/getSMTFeed.action',
			   		params: {
			   			mpcode : Ext.getCmp('mpcode').value, 	//备料单号
			   			fecode : fecode,						//飞达编号
			   			mlscode: mlscode,						//站位
			   			macode : macode,						//制造单号
			   			table  : tableAB,						//板面
			   			barcode: barcode,						//卷料编号
			   			mccode : mccode,						//作业单号
			   			licode : linecode,						//线别
			   			devcode: devcode,						//设备号
			   			sccode : sccode							//资源号
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			me.FormUtil.getActiveTab().setLoading(false);
			   			var r = new Ext.decode(response.responseText);
			   			if(r.exceptionInfo){
			   				result.append(r.exceptionInfo, 'error');
			   			}
		    			if(r.success){
		    				result.append('上料成功！');
		    				Ext.getCmp('input').setValue('');
		    				Ext.getCmp("fecode").setValue();
		    				Ext.getCmp("mlscode").setValue();
		    				Ext.getCmp('barcode').setValue();
		    				Ext.getCmp('number').setValue();
		    				me.getGrid();		    				
			   			}
			   		}
				});
		   }
		} else if(back){
			if(Ext.isEmpty(input)){					
			   return ;
		    }
			me.FormUtil.getActiveTab().setLoading(true);//loading...
    		Ext.Ajax.request({
    			  url : basePath + 'pm/mes/backSMTFeed.action',
    			  params: {
    			   	mlscode: input,						    //站位
    			   	macode : macode,						//制造单号
    			   	mccode : mccode,						//作业单号
    			   	licode : linecode,						//线别
    			   	devcode: devcode,						//设备号
    			   	sccode : sccode							//资源号
    			  },
    			 method : 'post',
    			 callback : function(options,success,response){
    			   	me.FormUtil.getActiveTab().setLoading(false);
    			   	var r = new Ext.decode(response.responseText);
    			   	if(r.exceptionInfo){
    			   		result.append(r.exceptionInfo, 'error');
    			   	}
    		    	if(r.success){
    		    		Ext.getCmp("input").emptyText = "请录入站位编号";
    		    		result.append('站位：' + input+',退料成功！');
    		    		Ext.getCmp('input').setValue('');
		    			Ext.getCmp("fecode").setValue();
		    			Ext.getCmp("mlscode").setValue();
		    			Ext.getCmp('barcode').setValue();
		    			Ext.getCmp('number').setValue();
    		    		me.getGrid();
    			   	}
    			 }
    		 });
		} else if(change){//换料
			if(Ext.isEmpty(mlscode)){
				me.checkMslcode(mccode,input);
				return;
			}
			if(Ext.isEmpty(barcode)){
				me.checkBarcode(input);
			}			
			barcode = Ext.getCmp('barcode').value;
			me.FormUtil.getActiveTab().setLoading(true);//loading...
    		Ext.Ajax.request({
    			 url : basePath + 'pm/mes/changeSMTFeed.action',
    			 params: {
    			   mlscode: mlscode,					//站位
    			   barcode: barcode ,                   //卷料编号
    			   macode : macode,						//制造单号
    			   table  : tableAB,					//板面
    			   mccode : mccode,						//作业单号
    			   licode : linecode,					//线别
    			   devcode: devcode,					//设备号
    			   sccode : sccode						//资源号
    			},
    			method : 'post',
    		    callback : function(options,success,response){
    			   	me.FormUtil.getActiveTab().setLoading(false);
    			   	var r = new Ext.decode(response.responseText);
    			   	if(r.exceptionInfo){
    			   		Ext.getCmp("input").emptyText = "请录入料卷编号";
    			   		Ext.getCmp('input').setValue('');
    			   		Ext.getCmp('barcode').setValue();
		    			Ext.getCmp('number').setValue();
    			   		result.append(r.exceptionInfo, 'error');
    			   		return;
    			   	}
    		    	if(r.success){
    		    		Ext.getCmp("input").emptyText = "请录入站位编号";
    		    		result.append('站位：'+mlscode+',换料：' + barcode + '，成功！');
    		    		Ext.getCmp('input').setValue('');
		    			Ext.getCmp("fecode").setValue();
		    			Ext.getCmp("mlscode").setValue();
		    			Ext.getCmp('barcode').setValue();
		    			Ext.getCmp('number').setValue();
    		    		me.getGrid();
    			   	}
    			   }
    		});			
		} else if(add){//接料
			if(Ext.isEmpty(mlscode)){
				me.checkMslcode(mccode,input);
				return;
			}
			if(Ext.isEmpty(barcode)){
				me.checkBarcode(input);
			}			
			barcode = Ext.getCmp('barcode').value;
			me.FormUtil.getActiveTab().setLoading(true);//loading...
    		Ext.Ajax.request({
    			 url : basePath + 'pm/mes/addSMTFeed.action',
    			 params: {
    			   	mlscode: mlscode,						//站位
    			   	barcode: barcode,						//卷料编号
    			   	macode : macode,						//制造单号
    			   	table  : tableAB,						//板面
    			   	mccode : mccode,						//作业单号
    			   	licode : linecode,						//线别
    			   	devcode: devcode,						//设备号
    			   	sccode : sccode							//资源号
    			  },
    			  method : 'post',
    			  callback : function(options,success,response){
    			   	me.FormUtil.getActiveTab().setLoading(false);
    			   	var r = new Ext.decode(response.responseText);
    			   	if(r.exceptionInfo){   			   		
    			   		Ext.getCmp("input").emptyText = "请录入料卷编号";
    			   		Ext.getCmp('input').setValue('');
    			   		Ext.getCmp('barcode').setValue();
		    			Ext.getCmp('number').setValue();
    			   		result.append(r.exceptionInfo, 'error');return;
    			   	}
    		    	if(r.success){
    		    		Ext.getCmp("input").emptyText = "请录入站位编号";
    		    		Ext.getCmp('input').setValue('');
    		    		result.append('站位：' + mlscode+',接料：' + barcode+'，成功！');    		    		
    		    		me.getGrid();
    			   	}
    			 }
    		});
		}	
	},
	query : function(){
		var me = this;
		var mc_devcode = Ext.getCmp("mc_devcode").value,mc_code = Ext.getCmp("mc_code").value;
		if(Ext.isEmpty(mc_devcode)){
			showError("请录入机台号再进行筛选!");
			return ;
		}
		if(Ext.isEmpty(mc_code)){
			showError("请录入作业单号再进行筛选!");
			return ;
		}		
		me.beforeQuery(caller,{msl_devcode:mc_devcode,mc_code:mc_code});		
	},
	checkMslcode : function(mccode,input){
		var result = Ext.getCmp('t_result');
		//判断站位是否属于作业单
		Ext.Ajax.request({
    		url : basePath + "pm/bom/getDescription.action",
    		params: {
    			tablename: 'productsmtlocation left join productsmt on ps_id=psl_psid left join makeCraft on mc_pscode=ps_code',
    			field: 'psl_prodcode',
    			condition: "mc_code='"+mccode+"' and psl_location='"+input+"'"
    		},
    		method : 'post',
    		callback : function(options,success,response){
    				var res = new Ext.decode(response.responseText);
    				if(res.exceptionInfo){
    					showError(res.exceptionInfo);return;
    				}
    				if(res.description == null){
    					showError('站位错误，不存在作业单'+mccode+'中!');Ext.getCmp('input').setValue('');return;
    				}else{
    					Ext.getCmp("input").emptyText = "请录入料卷编号";
    					Ext.getCmp("mlscode").setValue(input);		
    					Ext.getCmp('input').setValue('');						
						result.append('站位采集成功，请采集物料'+res.description+'的条码');								
    				}
    			}
		});	
	},
	checkBarcode : function(input){
		var result = Ext.getCmp('t_result');
		Ext.Ajax.request({
    		url : basePath + "pm/bom/getDescription.action",
    		params: {
    			tablename: 'Barcode',
    			field: 'bar_remain',
    			condition: "bar_status=0 and bar_code='"+input+"'"
    		},
    		method : 'post',
    		async:false,
    		callback : function(options,success,response){
    				var res = new Ext.decode(response.responseText);
    				if(res.exceptionInfo){
    					showError(res.exceptionInfo);return;
    				}
    				if(res.description == null){
    					 showError('料卷编号错误，不存在或者状态无效!');Ext.getCmp('input').setValue('');return;
    				}else if(res.description == '0' || res.description == 0){
    					 showError('料卷编号错误，库存数量为0!');Ext.getCmp('input').setValue('');return;
    				}else{//料卷编号正确设置编号数量
    				     Ext.getCmp('barcode').setValue(input);
    					 Ext.getCmp('number').setValue(res.description);
    				     Ext.getCmp("input").emptyText = "请录入飞达编号";
    					 Ext.getCmp('input').setValue('');
    				}
    		}
	    });		
	},
	getGrid :function(){
		var me = this;
		Ext.getCmp('input').setValue('');
		Ext.getCmp("fecode").setValue();
		Ext.getCmp("mlscode").setValue();
	    Ext.getCmp('barcode').setValue();
		Ext.getCmp('number').setValue();
		var querygrid = Ext.getCmp('querygrid'), form = Ext.getCmp('form');
		var urlcondition = querygrid.defaultCondition || '';		
		condition = me.spellCondition(form, urlcondition);
		if(Ext.isEmpty(condition)) {
			condition = querygrid.emptyCondition || '1=1';
		}
		var gridParam = {caller: caller, condition: condition, start: 1, end: getUrlParam('_end')||1000};
		querygrid.GridUtil.loadNewStore(querygrid, gridParam);
	},
	setButtonS:function(){
		var cbtn = Ext.getCmp('changeMakeBtn'),dbtn = Ext.getCmp('importMPDataBtn');
    	var btn1 =  Ext.getCmp("confirm"),btn2 = Ext.getCmp("blankAll");
    	btn1.setDisabled(true);
		btn2.setDisabled(true);
		if(!cbtn.isHidden()){
			cbtn.hide();
		}
		if(!dbtn.isHidden()){
		  dbtn.hide();
		}
	}
});