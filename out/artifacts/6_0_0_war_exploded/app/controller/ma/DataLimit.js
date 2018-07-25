Ext.QuickTips.init();
Ext.define('erp.controller.ma.DataLimit', {
	extend : 'Ext.app.Controller',
	//stores: ['ma.DataLimitStore'],
	views : [ 'ma.datalimit.DataLimit', 'ma.datalimit.LimitDetail', 'ma.datalimit.LimitForm','core.form.MultiField','core.trigger.DbfindTrigger','ma.datalimit.SourceWindow',
	          'core.form.FtField','core.form.FtDateField','core.form.FtFindField','core.form.FtNumberField', 'core.form.MonthDateField','common.batchDeal.Form','common.batchDeal.GridPanel',
	          'core.trigger.AddDbfindTrigger','core.button.Sync','core.trigger.EmpTrigger', 'core.trigger.MultiDbfindTrigger','core.trigger.SearchField',
	          'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton',
	          'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
	          'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber'],
	          FormUtil: Ext.create('erp.util.FormUtil'),
	          BaseUtil: Ext.create('erp.util.BaseUtil'),
	          GridUtil: Ext.create('erp.util.GridUtil'),
	          flag:true,
	          init : function() {
	        	  var me = this;
	        	  this.control({
	        		  'button[itemId=copy]': {
	        			  click: function(btn) {
	        				  var instanceid_=Ext.getCmp('instanceid_');
	        				  if(instanceid_.value) me.CopyPower();
	        				  else showError('请选择有效的人员和数据类型复制权限!');
	        			  }
	        		  },
	        		  'button[itemId=delete]':{//清除权限
	        			  click:function(btn){
	        				  var me = this, grid = Ext.getCmp('limitdetail'),instanceid_=Ext.getCmp('instanceid_');
	        				  var items = grid.selModel.getSelection(),dItems=new Array(),rItems=new Array();
	        				  if(items.length<1) showError("没有需要处理的数据!");
	        				  Ext.each(items, function(item, index){
	        					  if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        						  && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        						  dItems.push({
	        							  id_:item.data.id_  
	        						  });    		
	        					  }else rItems.push(item);
	        				  });	        				 
	        				  grid.getStore().remove(rItems);
	        				  if(dItems.length>0){
	        					  me.FormUtil.setLoading(true);
	        					  Ext.Ajax.request({
	        						  url:basePath+'ma/datalimit/deleteLimitPower.action',
	        						  method:'POST',
	        						  params:{
	        							  data:unescape(Ext.JSON.encode(dItems))
	        						  },
	        						  callback:function(options, success, response){
	        							  me.FormUtil.setLoading(false); 
	        							  var res = new Ext.decode(response.responseText);
	        							  if(res.success){
	        								  Ext.Msg.alert('提示','删除成功!',function(){ 
	        									  Ext.getCmp('limitdetail').getStore().load({
	        										  params:{
	        											  InstanceId_:instanceid_.value
	        										  }
	        									  });
	        								  });
	        							  }
	        						  }	 

	        					  });
	        				  } 
	        			  }
	        		  },
	        		  'button[itemId=datasync]': {// 同步权限
	      				click: function(btn) {
	      					this.showSyncWin();
	      				}
	      			},
	        		  'erpVastDealButton': {
	        			  click: function(btn){
	        				  var url=btn.ownerCt.ownerCt.dealUrl,instanceid_=Ext.getCmp('instanceid_');
	        				  warnMsg("复制权限将选中人员对应数据类型权限全部覆盖?", function(btn){
	        					  if(btn == 'yes'){
	        						  me.vastDeal(url, instanceid_.value);
	        					  } else {
	        						  return;
	        					  }
	        				  });
	        			  }
	        		  },
	        		  'combo[name=limit_id_]':{
	        			  change:function(field,newvalue,oldvalue){
	        				  var empid_=Ext.getCmp('empid_');
	        				  var jobid_=Ext.getCmp('jobid_');
	        				  if(empid_.value && Ext.getCmp('empradio').checked){
	        					  me.getDataLimitInstance(newvalue,empid_.value,null);
	        				  }
	        				  if(jobid_.value && Ext.getCmp('jobradio').checked){
	        					  me.getDataLimitInstance(newvalue,null,jobid_.value);
	        				  }
	        			  }
	        		  },
	        		  'multidbfindtrigger[name=em_position]': {				
	     				 afterrender: function(f){
	     				 	f.ownerCt.down('radio[name=synctype1]').setValue(true);						
	     					f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(false);
	     							
	     					f.ownerCt.down('radio[name=synctype2]').setValue(false);						
	     					f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
	     					f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(true);
	     									 					 	
	     				 },

	     				aftertrigger: function(t, rs) {	
	     					if(rs.length > 0) {
	     						var m = t.ownerCt;
	     						Ext.Array.each(rs, function(r, i){
	     							if(i == 0) {
	     								t.jo_id = r.get('jo_id');
	     								t.setValue(r.get('jo_name'));		
	     							} else {
	     								m.insert(m.items.length - 2, {
	     			    					xtype: 'multidbfindtrigger',
	     			    					name: 'em_position',			    					
	     			    					fieldLabel: '岗位名称',
	     			    					jo_id: r.get('jo_id'),
	     			    					value: r.get('jo_name'),
	     			    					p: 2,
	     			    					editable: false,
	     			    					autoDbfind: false,
	     			    					clearable: true,
	     			    					onTrigger2Click:function(){
	     			    						m.remove(this);	
	     			    						me.getSyncDatas(m);
	     			    					}
	     			    				});
	     							}													
	     						});
	     					} else {
	     						t.setValue(null);
	     						t.jo_id = null;
	     					}					
	     					me.getSyncDatas(t.ownerCt);
	     				}
	     			},
	     			'multidbfindtrigger[name=em_code]': {
	    				aftertrigger: function(t, rs) {
	    					if(rs.length > 0) {
	    						var m = t.ownerCt;
	    						Ext.Array.each(rs, function(r, i){
	    							if(i == 0) {
	    								t.em_id = r.get('em_id');
	    								t.setValue(r.get('em_name'));		
	    							} else {
	    								m.insert(m.items.length, {
	    			    					xtype: 'multidbfindtrigger',
	    			    					name: 'em_code',			    					
	    			    					fieldLabel: '员工编号',
	    			    					em_id: r.get('em_id'),
	    			    					value: r.get('em_name'),
	    			    					p: 2,
	    			    					editable: false,
	    			    					autoDbfind: false,
	    			    					clearable: true,
	    			    					onTrigger2Click:function(){
	    			    						m.remove(this);	
	    			    						me.getSyncDatas2(m);
	    			    					}
	    			    				});
	    							}													
	    						});
	    					} else {
	    						t.setValue(null);
	    						t.em_id = null;
	    					}					
	    					me.getSyncDatas2(t.ownerCt);
	    				}
	    			},
	        		  'radio[id=empradio]':{
	        			  change: function(f) {	
								if(f.checked){
									Ext.getCmp('emcode_').setDisabled(!f.value);																					
									Ext.getCmp('jocode_').setDisabled(f.value);	        				
									var limitid_=Ext.getCmp('limit_id_');
									var empid_=Ext.getCmp('empid_');
									if(limitid_.value)me.getDataLimitInstance(limitid_.value,empid_.value||-1,null);															
								}}
	        		  },
	        		  'radio[id=jobradio]':{
	        				change: function(f) {
								if(f.checked){	
									Ext.getCmp('jocode_').setDisabled(!f.value);																													
									Ext.getCmp('emcode_').setDisabled(f.value);								
									var limitid_=Ext.getCmp('limit_id_');
			        				var jobid_=Ext.getCmp('jobid_');
			        				var task=new Ext.util.DelayedTask(function(){
			        					if(limitid_.value)me.getDataLimitInstance(limitid_.value,null,jobid_.value||-1);
			        				});
			        				task.delay(1);			        		
							}}
	        		  },
	        		  'hidden[name=empid_]':{
	        			  change:function(field,newvalue,oldvalue){
	        				  var limitid_=Ext.getCmp('limit_id_');
	        				  if(limitid_.value){
	        					  me.getDataLimitInstance(limitid_.value,newvalue,null);
	        				  }
	        			  }
	        		  },
	        		  'hidden[name=jobid_]':{
	        			  change:function(field,newvalue,oldvalue){
	        				  var limitid_=Ext.getCmp('limit_id_');
	        				  if(limitid_.value){
	        					  me.getDataLimitInstance(limitid_.value,null,newvalue);
	        				  }
	        			  }
	        		  },
	        		  'checkbox[name=nolimit_]':{
	        			  change:function(field,newvalue,oldvalue){
	        				  var querys=Ext.ComponentQuery.query('button[itemId=select]'),noaddlimit_=Ext.getCmp('noaddlimit_'),
	        				  usereport_=Ext.getCmp('usereport_'); 				
	        				  if(querys.length>0) {
	        					  querys[0].setDisabled(newvalue==1);
	        				  }
	        				  noaddlimit_.setDisabled(newvalue==1);
	        				  usereport_.setDisabled(newvalue==1);
	        				  if(newvalue==1){
	        					  noaddlimit_.setValue(0);
	        					  usereport_.setValue(0);
	        				  }
	        				  if(me.flag){
	        					  if(newvalue){
	        						  warnMsg('是否将分配全部权限信息?', function(btn){
	        							  if(btn != 'yes' && btn != 'ok'){
	        								  me.flag=false;
	        								  field.setValue(0);					
	        							  }else {
	        								  var noaddlimit_=Ext.getCmp('noaddlimit_'),usereport_=Ext.getCmp('usereport_');
	        								  noaddlimit_.setDisabled(true);
	        								  usereport_.setDisabled(true);
	        								  var querys=Ext.ComponentQuery.query('button[itemId=select]');
	        								  if(querys.length>0) querys[0].setDisabled(true);
	        							  }
	        						  });
	        					  }else{
	        						  warnMsg('是否将已有全部权限信息清除?', function(btn){
	        							  if(btn != 'yes' && btn != 'ok'){
	        								  me.flag=false;
	        								  field.setValue(1);									
	        							  }else {
	        								  var noaddlimit_=Ext.getCmp('noaddlimit_'),usereport_=Ext.getCmp('usereport_');
	        								  noaddlimit_.setDisabled(false);
	        								  usereport_.setDisabled(false);		
	        								  var querys=Ext.ComponentQuery.query('button[itemId=select]');
	        								  if(querys.length>0) querys[0].setDisabled(false);
	        							  } 
	        						  });
	        					  }
	        				  }else me.flag=true;


	        			  }
	        		  },
	        		  'radiogroup':{
	        			  change:function(field,newvalue,oldvalue){
	        				  var c=Ext.getCmp('condition_');
	        				  if(c.hidden && newvalue.limittype_=='condition'){
	        					  c.show();
	        					  c.setHeight(80);
	        				  }else if(!c.hidden)c.hide();
	        			  }
	        		  },
	        		  'button[itemId=select]':{
	        			  click:function(btn){	        				  
	        				  var limitid_=Ext.getCmp('limit_id_'),empid_=Ext.getCmp('empid_'),jobid_=Ext.getCmp('jobid_'),record=limitid_.findRecordByValue(limitid_.value),limittype_=Ext.getCmp('limittype_').getValue().limittype_;	        				
	        				  if(!limitid_.value) showError('未选择相应的数据类型!');
	        				  else if(!empid_.value &&  Ext.getCmp('empradio').checked) showError('未选择相应的人员!');
	        				  else if(!jobid_.value &&  Ext.getCmp('jobradio').checked) showError('未选择相应的岗位!');
	        				  else {
	        					  Ext.widget('sourcewindow',{
	        						  limitId_:limitid_.value,
	        						  table:record.get('table_'),
	        						  LimitType:limittype_,
	        						  modal:true,
	        						  title:'<div align="center">设置权限('+(limittype_=='detail'?'按明细数据':'按条件')+')</div>'
	        					  }).show();
	        				  }

	        			  }	
	        		  },
	        		  'button[itemId=createSql]':{
	        			  click:function(btn){
	        				  var w=btn.ownerCt.ownerCt,g=w.down('#querygrid'),_c=g.getCondition();
	        				  if(_c){
	        					  Ext.getCmp('condition_').setValue(_c);
	        					  w.close();
	        				  }else {
	        					  showError('未设置任何条件');
	        				  }
	        				 
	        			  }
	        		  },
	        		  'button[itemId=query]':{
	        			  click:function(btn){
	        				  var w=btn.ownerCt.ownerCt,g=w.down('#querygrid'),dg=w.down('#datagrid'),con=g.getCondition();
	        				  if(con!=""){
	        					  dg.getStore().load({
	        						  params:{
	        							  condition:con
	        						  }
	        					  });
	        				  }

	        			  }
	        		  },
	        		  'button[itemId=selectdata]':{
	        			  click:function(btn){
	        				  var w=btn.ownerCt.ownerCt,g=w.down('#datagrid'),selects=g.getSelectionModel().getSelection();
	        				  var limitdetail=Ext.getCmp('limitdetail');
	        				  if(selects.length>0){
	        					  limitdetail.insertRecords(selects);
	        				  }else showError('请选择需要分配的数据!');	
	        				  w.close();
	        			  }
	        		  },
	        		  'button[itemId=save]':{
	        			  click:function(btn){
	        				  var form=btn.ownerCt.ownerCt,grid=Ext.getCmp('limitdetail'),params=new Object();
	        				  var r=form.getValues();
	        				  if(Ext.getCmp('jobradio').checked){
	        					  if(!Ext.getCmp('jobid_').value){showError('未选择相应的岗位!');return};
	        					  delete r['empid_']};
	        				  if(Ext.getCmp('empradio').checked){
	        					  if(!Ext.getCmp('empid_').value){showError('未选择相应的人员!');return};
	        					  delete r['jobid_']};
	        				  delete r['emcode_'];
	        				  delete r['emname_'];
	        				  delete r['jocode_'];
	        				  delete r['joname_'];
	        				  params.formData=unescape(escape(Ext.JSON.encode(r)));
	        				  var changes=grid.getChange();
	        				  if(changes.added.length>0){
	        					  params.inserts=unescape(changes.added.toString());
	        				  }
	        				  if(changes.updated.length>0){
	        					  params.updates=unescape(changes.updated.toString());
	        				  }
	        				  me.FormUtil.setLoading(true);
	        				  Ext.Ajax.request({
	        					  url:basePath+'ma/datalimit/InstanceDataLimit.action',
	        					  method:'post',
	        					  params:params,
	        					  callback:function(options, success, response){
	        						  me.FormUtil.setLoading(false); 
	        						  var res = new Ext.decode(response.responseText);
	        						  if(res.success){	        							  
	        							  Ext.Msg.alert('提示','保存成功!',function(){ 	
	        								  if(Ext.getCmp('jobradio').checked)me.getDataLimitInstance(Ext.getCmp('limit_id_').value,null,Ext.getCmp('jobid_').value);	
	        		        				  if(Ext.getCmp('empradio').checked)me.getDataLimitInstance(Ext.getCmp('limit_id_').value,Ext.getCmp('empid_').value,null);		        								          						
	        							  });
	        						  }
	        					  }	 
	        				  });
	        			  }
	        		  }
	        	  });
	          },
	          getDataLimitInstance:function(limitid_,emid,joid){
	        	  var me=this;
	        	  me.FormUtil.setLoading(true);
	        	  Ext.Ajax.request({
	        		  url:basePath+'ma/datalimit/getDataLimitInstance.action',
	        		  method:'get',
	        		  params:{
	        			  empid_:emid,
	        			  jobid_:joid,
	        			  limitid_:limitid_
	        		  },
	        		  callback : function(options, success, response){
	        			  me.FormUtil.setLoading(false); 
	        			  var res={nolimit_:1};
	        			  if (response.responseText) res = new Ext.decode(response.responseText);
	        			  me.refreshLimits(res);
	        		  }	
	        	  });
	          },
	          refreshLimits:function(data){
	        	  var me=this,nolimit_=Ext.getCmp('nolimit_'),instanceid_=Ext.getCmp('instanceid_'),noaddlimit_=Ext.getCmp('noaddlimit_'),usereport_=Ext.getCmp('usereport_'),condition_=Ext.getCmp('condition_'),limittype_=Ext.getCmp('limittype_');
	        	 // nolimit_.setDisabled(false);        	  
	        	  	if(Ext.getCmp('jobradio').checked&&!(Ext.getCmp('jocode_').value)){
	        			Ext.getCmp('nolimit_').setDisabled(true);
	        	}
	        		if(Ext.getCmp('jobradio').checked&&Ext.getCmp('jocode_').value){
	        			Ext.getCmp('nolimit_').setDisabled(false);
	        	}
	        		if(Ext.getCmp('empradio').checked&&!(Ext.getCmp('emcode_').value)){
	        			Ext.getCmp('nolimit_').setDisabled(true);
	        	}
	        		if(Ext.getCmp('empradio').checked&&Ext.getCmp('emcode_').value){
	        			Ext.getCmp('nolimit_').setDisabled(false);
	         }
	        	  if(data){
	        		  instanceid_.setValue(data['instanceid_']);
	        		  noaddlimit_.setValue(data['noaddlimit_']);
	        		  usereport_.setValue(data['usereport_']);
	        		  condition_.setValue(data['condition_']);
	        		  limittype_.setValue({limittype_:data['limittype_']});
	        		  me.flag=false;
	        		  nolimit_.setValue(data['nolimit_']);	                     
	        		  Ext.getCmp('limitdetail').getStore().load({
	        			  params:{
	        				  InstanceId_:data['instanceid_'] ||-1
	        			  }
	        		  });
	        	  } 
	          },
	          CopyPower:function(){
	        	  var win=Ext.getCmp('emp_select');
	        	  if(!win){
	        		  win=new Ext.window.Window({
	        			  id:'emp_select',
	        			  width:'90%',
	        			  height:'95%',
	        			  title:'复制权限',
	        			  closeAction:'hide',
	        			  layout:'anchor',
	        			  items:[{
	        				  anchor:'100% 20%',
	        				  xtype:'erpBatchDealFormPanel',
	        				  caller:'EmployeeCopyPower'
	        			  },{
	        				  anchor:'100% 80%',
	        				  xtype:'erpBatchDealGridPanel',
	        				  caller:'EmployeeCopyPower'
	        			  }]
	        		  });
	        	  }
	        	  win.show();
	          },
	          vastDeal: function(url,instanceId){
	        	  var me = this, grid = Ext.getCmp('batchDealGridPanel');
	        	  var items = grid.selModel.getSelection();
	        	  Ext.each(items, function(item, index){
	        		  if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        			  && this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
	        			  item.index = this.data[grid.keyField];
	        			  grid.multiselected.push(item);        		
	        		  }
	        	  });
	        	  var form = Ext.getCmp('dealform'),params=new Object();
	        	  var records = Ext.Array.unique(grid.multiselected);
	        	  if(records.length > 0){
	        		  var data=new Array();
	        		  Ext.each(records,function (item){
	        			  data.push({
	        				  em_id:item.data[grid.keyField],
	        				  InstanceId_:instanceId
	        			  });
	        		  });
	        		  if(!me.dealing){
	        			  params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
	        			  me.dealing = true;
	        			  var main = parent.Ext.getCmp("content-panel");
	        			  main.getActiveTab().setLoading(true);//loading...
	        			  Ext.Ajax.request({
	        				  url : basePath + url,
	        				  params: params,
	        				  method : 'post',
	        				  timeout: 6000000,
	        				  callback : function(options,success,response){
	        					  main.getActiveTab().setLoading(false);
	        					  me.dealing = false;
	        					  var localJson = new Ext.decode(response.responseText);
	        					  if(localJson.exceptionInfo){
	        						  var str = localJson.exceptionInfo;
	        						  if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
	        							  str = str.replace('AFTERSUCCESS', '');
	        							  grid.multiselected = new Array();	        							 
	        						  }
	        						  showError(str);return;
	        					  }
	        					  if(localJson.success){
	        						  if(localJson.log){
	        							  showMessage("提示", localJson.log);
	        						  }
	        						  grid.multiselected = new Array();
	        						  grid.ownerCt.close();
	        					  }
	        				  }
	        			  });
	        		  } else { 
	        			  showError("没有需要处理的数据!");
	        		  }
	        	  } else {
	        		  showError("请勾选需要的明细!");
	        	  }
	          },
	      	showSyncWin: function() {
	    		var me = this, win = Ext.create('Ext.Window', {
	    			title: '权限同步',
	    			width: 320,
	    			height: 400,
	    			autoScroll:true,
	    			closable:false,
	    			defaults: {
	    				margin: '3 10 10 5'
	    			},
	    			items: [{
	    				xtype: 'radio',
	    				name: 'synctype1',
	    				boxLabel: '按岗位同步',		
	    				checked: true,
	    				listeners: {
	    					change: function(f) {							
	    						if(f.checked){
	    						f.ownerCt.down('radio[name=synctype1]').setValue(f.value);						
	    						f.ownerCt.down('multidbfindtrigger[name=em_position]').setValue(null);
	    						f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(!f.value);
	    							
	    						f.ownerCt.down('radio[name=synctype2]').setValue(!f.value);						
	    						f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
	    						f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(f.value);
	    						
	    						}
	    						for(var i=f.ownerCt.items.items.length-1;i>1;i--){
	    							var itemf = f.ownerCt.items.items[i];
	    							if(itemf.xtype=='multidbfindtrigger'&&itemf.fieldLabel=="岗位名称"){								
	    								f.ownerCt.remove(itemf);								
	    									}
	    								}					
	    							}
	    						}			
	    			},{
	    				xtype: 'multidbfindtrigger',
	    				name: 'em_position',
	    				editable: false,
	    				fieldLabel: '岗位名称'
	    			},{
	    				xtype: 'radio',				
	    				name: 'synctype2',
	    				checked: false,
	    				boxLabel: '按个人同步',
	    				listeners: {
	    					change: function(f) {					
	    						if(f.checked){
	    						f.ownerCt.down('radio[name=synctype2]').setValue(f.value);						
	    						f.ownerCt.down('multidbfindtrigger[name=em_code]').setValue(null);
	    						f.ownerCt.down('multidbfindtrigger[name=em_code]').setDisabled(!f.value);
	    														
	    						f.ownerCt.down('radio[name=synctype1]').setValue(!f.value);
	    						f.ownerCt.down('multidbfindtrigger[name=em_position]').setValue(null);						
	    						f.ownerCt.down('multidbfindtrigger[name=em_position]').setDisabled(f.value);
	    												
	    						}
	    						for(var i=f.ownerCt.items.items.length-1;i>3;i--){
	    							var itemf = f.ownerCt.items.items[i];
	    							if(itemf.xtype=='multidbfindtrigger'&&itemf.fieldLabel=="员工编号"){
	    								f.ownerCt.remove(itemf);								
	    									}
	    								}							
	    							}
	    						}
	    			},{
	    				xtype: 'multidbfindtrigger',
	    				name: 'em_code',
	    				fieldLabel: '员工编号'
	    			}],
	    			buttonAlign: 'center',
	    			buttons: [{
	    				cls: 'x-btn-blue',
	    				xtype: 'erpSyncButton',
	    				itemId : 'sync',
	    				
	    				autoClearCache: true
	    				//syncUrl:'ma/power/syncPower.action'
	    			}, {
	    				text: $I18N.common.button.erpCloseButton,
	    				cls: 'x-btn-blue',
	    				handler: function(b) {
	    					b.up('window').close();
	    				}
	    			}]		 
	    		});
	    		win.show();
	    	},
			getSyncDatas:function(w){	
				var jo_id = null, name = null;
				var j = new Array(),items=w.items.items;
				if(items && items.length >= 4) {
					Ext.each(items, function(item){
						if(item.name=='em_position'){
							jo_id = item.jo_id;
							if(jo_id!=null && !Ext.Array.contains(j, jo_id)) {
								j.push(jo_id);	
							}	
						}
					});
				}
				w.down('erpSyncButton[itemId=sync]').syncdatas = j.join(',');
				w.down('erpSyncButton[itemId=sync]').caller ='DataJobPower!Post';
			},
			getSyncDatas2:function(w){					
				var em_id = null, name = null;
				var j = new Array(),items=w.items.items;
				if(items && items.length >= 4) {
					Ext.each(items, function(item){
						if(item.name=='em_code'){
							em_id = item.em_id;
							if(em_id!=null && !Ext.Array.contains(j, em_id)) {
								j.push(em_id);											
							}	
						}
					});
				}
				w.down('erpSyncButton[itemId=sync]').syncdatas = j.join(',');
				w.down('erpSyncButton[itemId=sync]').caller ='DataPositionPower!Post';
			}
});