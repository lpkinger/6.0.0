Ext.QuickTips.init();
Ext.define('erp.controller.pm.mps.MpsDesk', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','pm.mps.MpsDesk','core.grid.Panel2','core.toolbar.Toolbar','core.button.ExecuteOperation','pm.mps.DeskForm',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
	       ],
	       init:function(){ 
	    	   var me=this;      	
	    	   this.control({ 
	    		   'dbfindtrigger[name=mm_code]':{
	    			   afterrender:function(f){
	    				   var kind=me.BaseUtil.getUrlParam('kind');   
	    				   if (kind) {
	    					   f.dbBaseCondition="mm_kind ='"+kind+"'";    
	    				   }
	    				   var code= me.BaseUtil.getUrlParam('code');
	    				   if (code) {
	    					   f.setValue(code);
	    					   f.autoDbfind('form', caller, f.name, f.name + ' like\'%' + f.value + '%\'');
	    				   }
	    				   window.showWarning = me.showWarning;
	    			   },
	    			   aftertrigger:function(f,record){
	    				   var me = this;
	    				   var form = Ext.getCmp('form');
	    				   Ext.defer(function(){
	    					   if(record.data['mm_runstatus'] == '已运算' && !Ext.getCmp('mm_warn')){
	    						   me.addWarn();
	    						   me.getSeriousWarn();
	    					   }else if(record.data['mm_runstatus'] != '已运算'){
	    						   Ext.getCmp('need').setDisabled(false);
	    						   var mm_warn = Ext.getCmp('mm_warn');
	    						   if(mm_warn) form.remove(mm_warn);
	    						   var error1 = Ext.getCmp('error1');
	    						   if(error1) form.remove(error1);
	    						   var error2 = Ext.getCmp('error2');
	    						   if(error2) form.remove(error2);
	    					   }else {
	    						   me.getSeriousWarn();
	    					   }
	    				   },500);
	    			   }
	    		   },
	    		   'erpExecuteOperationButton':{
	    			   click:function(btn){
	    				   me.ExecuteOperation();
	    			   }   		
	    		   },
	    		   'button[id=tomake]':{
	    			   click:function(btn){ 
						   	// confirm box modify
							// zhuth 2018-2-1
							Ext.Msg.confirm('提示', '确定要转制造?', function(btn) {
								if(btn == 'yes') {
									var code=Ext.getCmp('mm_code').getValue();
									Ext.Ajax.request({//拿到grid的columns
										url: basePath + 'pm/mps/turnmake.action',
										params: {
											code: code,
											caller: caller
										},
										method: 'post',
										timeout: 6000,
										callback: function (options, success, response) {
											var res = new Ext.decode(response.responseText);
											if (res.exceptionInfo) {
												showError(res.exceptionInfo);
												return;
											} else if (res.success) {
												showError(res.message);
												window.location.href = basePath + "jsps/pm/mps/MpsDesk.jsp?kind=MRP&&code=" + code;
											}
										}
									});
								}
							});
	    			   }   		
	    		   },
	    		   'button[id=topurchase]':{
	    			   	click:function(btn){
	    			   		var iftest = Ext.getCmp('mm_iftest');
							if(iftest && iftest.value == -1){
								showError('需求模拟的MRP计划不允许投放');
								return;
							}
	    				   	this.BaseUtil.getSetting(caller,'unAuditApplicationforMRP',function(bool){
								// confirm box modify
								// zhuth 2018-2-1
							   	Ext.Msg.confirm('提示', bool?'所有未提交的MRP投放请购单将作废,确定要转采购?':'所有未审核的请购单将作废,确定要转采购?', function(btn) {
									if(btn == 'yes') {
										var code=Ext.getCmp('mm_code').getValue();
										Ext.Ajax.request({//拿到grid的columns
											url : basePath + 'pm/mps/turnpurchase.action',
											params: {
												code:code,
												caller:caller
											},
											method : 'post',
											timeout: 6000,
											callback : function(options,success,response){ 
												var res = new Ext.decode(response.responseText);  
												if(res.exceptionInfo){ 
													showError(res.exceptionInfo);
													return;
												}else if(res.success){ 
													showError(res.message);
													window.location.href=basePath+"jsps/pm/mps/MpsDesk.jsp?kind=MRP&&code="+code;
												}
											}
										});
									}								   
							   	});
	    				   	})
	    			   	}   		
	    		   },
	    		   'button[id=topurchaseforecast]':{
	    			   click:function(btn){ 
	    				   var code=Ext.getCmp('mm_code').getValue();
	    				   Ext.Ajax.request({//拿到grid的columns
	    					   url : basePath + 'pm/mps/turnpurchaseforecast.action',
	    					   params: {
	    						   code:code,
	    						   caller:caller
	    					   },
	    					   method : 'post',
	    					   timeout: 6000,
	    					   callback : function(options,success,response){ 
	    						   var res = new Ext.decode(response.responseText);  
	    						   if(res.exceptionInfo){ 
	    							   showError(res.exceptionInfo);
	    							   return;
	    						   }else if(res.success){ 
	    							   showError(res.message);
	    							   window.location.href=basePath+"jsps/pm/mps/MpsDesk.jsp?kind=MRP&&code="+code;
	    						   }
	    					   }
	    				   });	
	    			   }   		
	    		   },
	    		   'button[id=product]':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('mm_id').getValue();
	    				   var condition="md_mpsid='"+id+"'";
	    				   me.FormUtil.onAdd('MPSProduct'+id,'物料','/jsps/pm/mps/DeskProduct.jsp?whoami=MPSSupply&_noc=1&urlcondition='+condition);
	    			   }
	    		   },
	    		   'button[id=supply]':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('mm_id').getValue();
	    				   var code=Ext.getCmp('mm_code').getValue();
	    				   var condition="md_mpsid='"+id+"' AND md_kind='SUPPLY' AND nvl(pr_supplytype,' ') <>'VIRTUAL'";
	    				   me.FormUtil.onAdd('MPSSupply'+id,'供应','/jsps/pm/mps/MRPThrow.jsp?whoami=MPSSupply&_noc=1&urlcondition='+condition+"&mpscode="+code);
	    			   }   		
	    		   },
	    		   'button[id=need]':{
	    			   click:function(btn){
	    				   var iftest = Ext.getCmp('mm_iftest');
    					   if(iftest && iftest.value ==-1){
							   showError('需求模拟的MRP计划不允许投放');
							   return;
    					   }
	    				   var id=Ext.getCmp('mm_id').getValue();
	    				   var code=Ext.getCmp('mm_code').getValue();
	    				   var condition="md_mpsid='"+id+"'AND md_kind='NEED' AND nvl(pr_supplytype,' ') <>'VIRTUAL' ";
	    				   me.FormUtil.onAdd('MPSNeed'+id,'需求','/jsps/pm/mps/MRPThrow.jsp?whoami=MPSNeed&_noc=1&urlcondition='+condition+"&mpscode="+code);
	    			   }   		
	    		   },
	    		   'button[id=error]':{
	    			   click:function(btn){
	    				   showWarning();
	    			   }
	    		   },
	    		   'button[id=ecnAnalysis]':{//剩余供应ECN分析
	    			   click:function(btn){
	    				   var id=Ext.getCmp('mm_id').getValue();
	    				   var code=Ext.getCmp('mm_code').getValue();
	    				   var condition="md_mpsid='"+id+"' AND md_kind='SUPPLY' AND nvl(pr_supplytype,' ') <>'VIRTUAL' AND md_model='取消'";
	    				   me.FormUtil.onAdd('EcnAnalysis!Query'+id,'剩余供应ECN分析','/jsps/pm/mps/MRPThrow.jsp?whoami=EcnAnalysis!Query&_noc=1&urlcondition='+condition+"&mpscode="+code);
	    			   }
	    		   },
	    		   'button[id=close]':{
	    			   click:function(btn){
	    				   var main = parent.Ext.getCmp("content-panel");
	    				   main.getActiveTab().close();
	    			   }   		
	    		   },
	    		   'button[id=dullstockdeal]':{//呆滞库存处理
	    			   click:function(btn){
	    				   var id=Ext.getCmp('mm_id').getValue();
	    				   var code=Ext.getCmp('mm_code').getValue();
	    				   var condition="md_mpsid='"+id+"' AND md_kind='SUPPLY' AND nvl(pr_supplytype,' ') <>'VIRTUAL' and md_worktype='剩余库存'";
	    				   me.FormUtil.onAdd('MRPOnhandThrow'+id,'MRP呆滞库存处理','/jsps/pm/mps/MRPThrow.jsp?whoami=MRPOnhandThrow&_noc=1&urlcondition='+condition+"&mpscode="+code);
	    			   }   		
	    		   }
	    	   });  
	       },
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   this.GridUtil.onGridItemClick(selModel, record);
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt; 
	       },
	       save: function(btn){
	    	   var me = this;
	    	   if(Ext.getCmp('team_code').value == null || Ext.getCmp('team_code').value == ''){
	    		   me.BaseUtil.getRandomNumber();
	    	   }
	    	   me.FormUtil.beforeSave(me);
	       },
	       changeGrid: function(trigger){
	    	   var grid = Ext.getCmp('grid');
	    	   Ext.Array.each(grid.store.data.items, function(item){
	    		   item.set('tm_prjid',trigger.value);
	    	   });
	       }, 
	       ExecuteOperation:function(){
	    	   var me = this;
	    	   var code=Ext.getCmp('mm_code').getValue(); 
	    	   /*  var main = parent.Ext.getCmp('content-panel');*/
	    	   var mb = new Ext.window.MessageBox();
	    	   mb.wait('正在运算中','请稍后...',{
	    		   interval: 10000, //bar will move fast!
	    		   duration: 1000000,
	    		   increment: 20,
	    		   /*  text: 'Runing...',*/
	    		   scope: this
	    	   });
	    	   Ext.Ajax.request({//拿到grid的columns
	    		   url : basePath + 'pm/mps/RunMrp.action',
	    		   params: {
	    			   code:code, 
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
	    				   Ext.Msg.alert('提示',res.message,function(){
	    					   if(res.message=='运算成功') {
	    						   window.location.href=basePath+"jsps/pm/mps/MpsDesk.jsp?kind=MRP&&code="+code;
	    					   }
	    				   });
	    			   }
	    		   }
	    	   });
	       },
	       showWarning:function(){
	    	   var deskForm = Ext.getCmp('mpsdeskform');
	    	   var id=Ext.getCmp('mm_id').getValue();
	    	   if(Ext.isEmpty(id)){
	    		   return;
	    	   }
	    	   var win = new Ext.window.Window({
	    		   id : 'win',
	    		   title: "异常信息",
	    		   height: "70%",
	    		   width: "80%",
	    		   maximizable : false,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   xtype: 'erpGridPanel2',
	    			   anchor: '100% 100%', 
	    			   id:'errorgrid',
	    			   condition: 'mm_mpsid='+id,
	    			   caller:'MRPmessage',
	    			   bbar:{},
	    			   listeners:{
	    				   itemclick: function(selModel, record){
	    					   deskForm.GridUtil.onGridItemClick(selModel, record);
	    				   }	
	    			   },
	    			   plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
	    				   clicksToEdit: 1
	    			   })]
	    		   }],
	    		   bbar: {id:'mps_errorWin',items:['->',{
	    			   text:'导出',
	    			   iconCls: 'x-button-icon-excel',
	    			   cls: 'x-btn-gray',
	    			   handler: function(btn){
	    				   deskForm.BaseUtil.createExcel('MRPmessage', 'detailgrid', 'mm_mpsid='+id,'错误信息');
	    			   }
	    		   },{
	    		   	margin:'0 0 0 10',
	    			   text:'关闭',
	    			   cls: 'x-btn-gray',
	    			   iconCls: 'x-button-icon-close',
	    			   listeners: {
	    				   click: function(){
	    					   win.close();
	    				   }
	    			   }
	    		   },'->']}
	    	   });
	    	   win.show();
	       },
	       addWarn: function() {
	    	   var form = Ext.getCmp('form');
	    	   form.insert(form.items.items.length,{
	    		   allowDecimals:true,
	    		   checked:false,
	    		   columnWidth:1,
	    		   dataIndex:"mm_warn",
	    		   displayField:null,
	    		   editable:true,
	    		   fieldConfig:null,
	    		   fieldLabel:"异常报告",
	    		   group:2,
	    		   groupName:"执行情况",
	    		   hideTrigger:false,
	    		   id:"mm_warn",
	    		   labelAlign:"left",
	    		   logic:null,
	    		   fieldStyle:'color:red',
	    		   maskRe:null,
	    		   maxLength:20,
	    		   maxLengthText:"字段长度不能超过20字符!",
	    		   modify:false,
	    		   name:"mm_warn",
	    		   queryMode:null,
	    		   readOnly:true,
	    		   renderfn:null,
	    		   table:"MpsMain",
	    		   text:null,
	    		   value:'<a href="javascript:void(0);" onclick="showWarning()">点击查看</a>',
	    		   valueField:null,
	    		   xtype:"displayfield"
	    	   });
	       },
	       getSeriousWarn: function() {
	    	   var form = Ext.getCmp('form');
	    	   Ext.getCmp('need').setDisabled(false);
	    	   var me = this;
	    	   var mm_code = Ext.getCmp(form.codeField).value;
	    	   Ext.Ajax.request({
	    		   url: basePath + 'pm/mps/getSeriousWarn.action',
	    		   params: {
	    			   code:mm_code, 
	    			   caller:caller
	    		   },
	    		   method: 'post',
	    		   timeout: 600000,
	    		   callback: function(options,success,response) {
	    			   var res = new Ext.decode(response.responseText); 
	    			   var bool;
	    			   if(res.exceptionInfo){ 
	    				   showError(res.exceptionInfo);
	    				   return;
	    			   }else if(res.success && res.data.length > 0) {
	    				   Ext.Array.each(res.data,function(item){
	    					   if(item.id == 'error2'){
	    						   bool = true;
	    					   }
	    					   var component = Ext.getCmp(item.id);
	    					   if(!component){
	    						   form.insert(form.items.items.length,{
	    							   allowDecimals:true,
	    							   checked:false,
	    							   columnWidth:1,
	    							   dataIndex:item.id,
	    							   displayField:null,
	    							   editable:true,
	    							   labelWidth:265,
	    							   fieldConfig:null,
	    							   fieldLabel:item.name + ' 记录数',
	    							   group:2,
	    							   groupName:"执行情况",
	    							   hideTrigger:false,
	    							   id:item.id,
	    							   labelAlign:"left",
	    							   logic:null,
	    							   labelStyle:'color:red',
	    							   fieldStyle:'color:red',
	    							   maskRe:null,
	    							   maxLength:20,
	    							   maxLengthText:"字段长度不能超过20字符!",
	    							   modify:false,
	    							   name:item.id,
	    							   queryMode:null,
	    							   readOnly:true,
	    							   renderfn:null,
	    							   table:"MpsMain",
	    							   text:null,
	    							   value:item.count,
	    							   valueField:null,
	    							   xtype:"displayfield"
	    						   });
	    					   }else{
	    						   component.setValue(item.count);
	    					   }
	    					   if(res.data.length == 1){
	    						   if(bool){
	    							   var error1 = Ext.getCmp('error1');
	    							   if(error1) form.remove(error1);
	    						   }else{
	    							   var error2 = Ext.getCmp('error2');
	    							   if(error2) form.remove(error2);
	    						   }
	    					   }	    				   
	    				   });
	    				   Ext.getCmp('need').setDisabled(false);
	    			   }else {
	    				   var error1 = Ext.getCmp('error1');
	    				   if(error1) form.remove(error1);
	    				   var error2 = Ext.getCmp('error2');
	    				   if(error2) form.remove(error2);
	    			   }
	    		   }
	    	   });
	    }
   }
);