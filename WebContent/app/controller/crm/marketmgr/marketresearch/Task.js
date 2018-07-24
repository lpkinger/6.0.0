Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.marketresearch.Task', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','crm.marketmgr.marketresearch.Task','core.grid.Panel2','core.toolbar.Toolbar',//'plm.task.TaskForm',
    		'core.button.Upload','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.Print','core.button.TurnCustomer',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField'
    	],
    init:function(){
       var me=this;
       var type=0;
    	this.control({ 
    	'erpGridPanel2': {
    		  itemclick: this.onGridItemClick
    		 },
    		
    		'erpSaveButton': {
    			click: function(btn){
    			var grid = Ext.getCmp('grid');
    			var prjtestmancode=Ext.getCmp('prjtestmancode');
    			var value=prjtestmancode.value;
    			var prjplanid=Ext.getCmp('prjplanid').value;
    			var count=0,
    			    index=0; 
    				var grids = Ext.ComponentQuery.query('gridpanel');
		               if(grids.length > 0){
		   					var s = grids[0].getStore().data.items;
		   					for(var i=0;i<s.length;i++){
		   					  var rowdata=s[i].data;
		   					     count+=s[i].data.ra_units;
		   					 if(rowdata.ra_resourcecode!=''){
								  s[i].set('ra_prjid',Ext.getCmp('prjplanid').value);
		   					      s[i].set('ra_prjname',Ext.getCmp('prjplanname').value);
		   					   if(rowdata.ra_enddate==null||rowdata.ra_enddate==""){
		   					    showError('第'+rowdata.ra_detno+' 行结束时间没填!');
		   					    return
		   					   }
		   					   if(rowdata.ra_startdate==null||rowdata.ra_startdate==""){
		   					    showError('第'+rowdata.ra_detno+' 行开始时间没填!');
		   					    return
		   					   }
		   					   if(new Date(rowdata.ra_startdate).getTime()>new Date(rowdata.ra_enddate).getTime()){
		   					   showError('第'+rowdata.ra_detno+' 行时间设置不对!');
		   					    return
		   					   }
		   					
		   					 }
		   					 
		   					}
		    				}		    	
    			 if(prjtestmancode&&prjtestmancode.value!=""){	
    			   if(count>90){
    			     showError('该任务有测试人员分配不能超过90%!');
    			     return
    			   }
    			   if(type==0){
    			   this.setValue(value,prjplanid);
    			   }
    			   this.save(btn);
    			 }else {
    			    if(count>100){
    			     showError('该任务有测试人员分配不能超过100%!');
    			     return
    			   }
    			   this.save(btn);
    			 }   			
    			
    			}
    			
    		},
    		'textfield[name=name]': {
    			change: function(field){
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.store.data.items, function(item){
    					item.set('ra_taskname',field.value);
    				});
    			}
    		},
    		'textfield[name=prjplanname]':{
    		    change: function(field){
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.store.data.items, function(item){
    					item.set('ra_prjname',field.value);
    				});
    			}
    		},
    		'textfield[name=percentdone]':{
    		change: function(field){
    				var grid = Ext.getCmp('grid');
    				Ext.Array.each(grid.store.data.items, function(item){
    					item.set('ra_taskpercentdone',field.value);
    				});
    			}
    		},
    		'datefield[name=startdate]':{
    			
    		},
    		'erpYnField':{
    		 change:function(field){
    		  var grid=Ext.getCmp('grid');
    		 Ext.Array.each(grid.store.data.items, function(item){
    					item.set('ra_needattach',field.value);
    				});
    		 }
    		},
    		'datefield[name=enddate]':{
    			
    		},
    		'dbfindtrigger[name=parentname]': {
    			afterrender:function(trigger){
    			trigger.dbKey='prjplanid';
    			trigger.mappingKey='prjplanid';
    			trigger.dbMessage='请选择该任务的项目计划ID';
    			}
    		},
    		'dbfindtrigger': {
    			change: function(trigger){
    				if(trigger.name == 'prjplanid'){
    					var grid = Ext.getCmp('grid');
		            Ext.Array.each(grid.store.data.items, function(item){
			                       item.set('ra_prjid',trigger.value);
		                             });
    				}
    			}
    		},
            'dbfindtrigger[name=ra_resourcecode]': {
    			afterrender:function(trigger){
    			trigger.gridKey='prjplanid';
    			trigger.mappinggirdKey='tm_prjid';
    			trigger.gridErrorMessage='请选择该任务的项目计划ID';
    			}
    		},
    		 
    		'dbfindtrigger[name=prjtestmancode]': {
    		     //只能从项目团队里面去找人
    			afterrender:function(trigger){
    			trigger.dbKey='prjplanid';
    			trigger.mappingKey='tm_prjid';
    			trigger.dbMessage='请选择该任务的项目计划ID';
    			},
    			change:function(trigger){
    			
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			},
    			afterrender:function(btn){
    				
    			}
    		},
    		'erpTurnCustomerButton':{
    			afterrender:function(btn){
    				btn.hide();
    			},
    			beforerender:function(btn){
    				btn.setText('交付件');
					btn.setWidth(100);
    			},
    			click:function(btn){
    				var winurl=window.location.href;
    				var whoami='MarketTaskReport!common';
    				if(Ext.getCmp('reporttemplatecode').value){
    					whoami=Ext.getCmp('reporttemplatecode').value;
    				}
    				var s='marketTaskReport.jsp?whoami='+whoami;
    				if(Ext.getCmp('taskcode').value){
    					s+="&mr_taskcode='"+Ext.getCmp('taskcode').value+"'";
    				}
    				var html='<iframe width=100% height=100% src="'+s+'"/>';
    				var win=new Ext.window.Window({
			    		height:500,
			    		width:800,
			    		modal:true,
			    		listeners : {
    	    				close : function(){
    	    					window.location.href=winurl;
    	    				}
    	    			},
			    		html:html});
			    	win.show();
    			}
    		},
    		'erpUpdateButton': {
    		   afterrender: function(btn){
    				var percentdone = Ext.getCmp('percentdone');
    				if(percentdone && percentdone.value == 100){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    			var prjtestmancode=Ext.getCmp('prjtestmancode');
    			var count=0;
					var grids = Ext.ComponentQuery.query('gridpanel');
		         if(grids.length > 0){
		   					var s = grids[0].getStore().data.items;
		   					for(var i=0;i<s.length;i++){
		   					  var rowdata=s[i].data;
		   					  count+=rowdata.ra_units;
		   					  if(rowdata.ra_resourcecode!=''){		   					      
		   					      s[i].set('ra_prjid',Ext.getCmp('prjplanid').value);
		   					      s[i].set('ra_prjname',Ext.getCmp('prjplanname').value);
		   					        if(rowdata.ra_enddate==null||rowdata.ra_enddate==""){
		   					    showError('第'+rowdata.ra_detno+' 行结束时间没填!');
		   					    return
		   					   }
		   					   if(rowdata.ra_startdate==null||rowdata.ra_startdate==""){
		   					    showError('第'+rowdata.ra_detno+' 行开始时间没填!');
		   					    return
		   					   }
		   					   if(new Date(rowdata.ra_startdate).getTime()>new Date(rowdata.ra_enddate).getTime()){
		   					   showError('第'+rowdata.ra_detno+' 行时间设置不对!');
		   					    return
		   					   }
		   					  }
		   					 }
		    				}
							if(prjtestmancode.isDirty()&&prjtestmancode.value!=''){
							  if(count>90){
		    				   showError('该任务有测试人员分配不能超过90%!!');
		    				   return;
		    				  }	
							}else if(count>100){
							showError('该任务有测试人员分配不能超过100%!');
		    				   return;
							}
		    				    					
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    		  afterrender: function(btn){
    				var percentdone = Ext.getCmp('percentdone');
    				var point=Ext.getCmp('point').value;
    				if(percentdone && percentdone.value == 100){
    					btn.hide();
    					if(percentdone.value==100){
    					showMessage('提示','任务按时完成！分数:'+point);
    					}else {
    					showMessage('提示','任务未按时完成！分数:'+point);
    					}
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onDelete({id: Number(Ext.getCmp('id').value)});
    			}
    		},
    	   'erpAuditButton': {
    		   beforerender: function(btn){
    			   btn.setText('交付件列表');
					btn.setWidth(120);
    			},
    			click: function(btn){
    				var s=basePath+"/jsps/common/datalist.jsp?whoami=MarketTaskReport&urlcondition=mr_taskcode='"+Ext.getCmp('taskcode').value+"'";
    				var html='<iframe width=100% height=100% src="'+s+'"/>';
    				var win=new Ext.window.Window({
			    		height:500,
			    		width:800,
			    		modal:true,
			    		html:html});
			    	win.show();
    			}
    		},		
    		 'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTask', '创建任务', 'jsps/crm/marketmgr/marketresearch/task.jsp');
    			}
    		},
    		
    		'erpGridPanel2': {
    		  itemclick: this.onGridItemClick
    		},
    		'erpSubmitButton':{
				beforerender: function(btn){
					btn.setText('项目子计划');
					btn.setWidth(120);
				},
				click:function(){
					me.FormUtil.onAdd('subprj', '项目计划', 'jsps/crm/projectscheduler/projectScheduler.jsp?startDate='+ Ext.getCmp('startdate').value+'&endDate='+Ext.getCmp('enddate').value+'&formCondition='+'prjplanid='+Ext.getCmp('prjplanid').value+'&level='+Ext.getCmp('id').value);
				}
			},
    		
    	});
    },
     onGridItemClick: function(selModel, record){//grid行选择
    	this.gridLastSelected = record;
    	var grid = Ext.getCmp('grid');
    	if(record.data[grid.necessaryField] == null || record.data[grid.necessaryField] == ''){
    		this.gridLastSelected.findable = true;//空数据可以在输入完code，并移开光标后，自动调出该条数据
    	} else {
    		this.gridLastSelected.findable = false;
    	}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
  
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	checkDate:function(data){
	        if(data.ra_resourcecode!=''){
		   		 if(rowdata.ra_enddate==null||rowdata.ra_enddate==""){
		   			 showError('第'+rowdata.ra_detno+' 行结束时间没填!');
		   			 return
		   		    }
		   		 if(rowdata.ra_startdate==null||rowdata.ra_startdate==""){
		   			 showError('第'+data.ra_detno+' 行开始时间没填!');
		   			  return
		   			}
		   		if(new Date(rowdata.ra_startdate).getTime()>new Date(rowdata.ra_enddate).getTime()){
		   			showError('第'+rowdata.ra_detno+' 行时间设置不对!');
		   			 return
		   			}
		   	}
	},
	save: function(btn){
		var me = this;
		if(Ext.getCmp('taskcode').value == null || Ext.getCmp('taskcode').value == ''){
			me.BaseUtil.getRandomNumber();
		}
		me.FormUtil.beforeSave(me);
	},
	setValue:function(code,prjid){
	   var res='';
	    	Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'common/dbfind.action',
        	method : 'post',
        	 async:false, 
        	params : {
        		which : 'grid',
	   			caller : 'Teammember',
	   			field:'ra_resourcecode',	
	   			condition:"tm_employeecode like '%"+code+"%' AND tm_prjid='"+ prjid+"'"  ,
	   			page: 1,
	   			pageSize: 13,
	   		},
        	callback : function(options,success,response){
        		 res = new Ext.decode(response.responseText);
        	}
        });
   
        var grid=Ext.getCmp('grid');
        var startdate=Ext.getCmp('startdate').value;
        var enddate=Ext.getCmp('enddate').value;
        var keyValues=new Ext.decode(res.data);
            keyValues=keyValues[0];
        var index=0;
         for(var i=0;i<grid.store.data.items.length;i++){
            if(grid.store.data.items[i].data.ra_resourcecode==""){
    			            index=i;
    			            break;
    			       }	
         }
        grid.getSelectionModel().select(index,true , true );
	    var select=grid.getSelectionModel();//detailgrid里面selected
    	var records = select.selected.items[0];//selected的数据        
    		for(var i=0;i<res.fields.length;i++){    		
    		var k=res.fields[i].name;   	   		
    		   Ext.Array.each(Ext.getCmp('grid').dbfinds,function(ds){   		      
            		if(k == ds.dbGridField) {     	   			
            		records.set(ds.field,keyValues[k]);
            		}
            	});    		
    		}
             records.set('ra_startdate',startdate);
             records.set('ra_enddate',enddate);
             records.set('ra_units',10); 
             records.set('ra_type',1);
             type=1; 
	}
});