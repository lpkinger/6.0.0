Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.Task', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'core.form.Panel','plm.task.Task','core.grid.Panel2','core.toolbar.Toolbar','plm.task.TaskForm','core.button.Upload','core.grid.YnColumn',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.ResSubmit','core.button.Save','core.button.Close','core.button.Print','core.trigger.MultiDbfindTrigger',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.form.MultiField',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.ColorField','core.form.YnField','core.form.FileField'
	       ],
	       init:function(){
	    	   var me=this;
	    	   this.control({ 
	    		   'erpSaveButton': {
	    			   click: function(btn){
	    				   var count=0;
	    				   var resourceunits=Ext.getCmp('resourceunits');
	    				   console.log(resourceunits);
	    				   if(resourceunits){
	    					   var arr=resourceunits.value.split(",");
	    					   Ext.Array.each(arr,function(item){
	    						   count+=Number(item);
	    					   });
	    				   }   	
	    				   if(count!=100){
	    					   showError('资源分配比率不对!');
	    				   }else  this.save(btn);
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
	    		   'dbfindtrigger[name=parentid]': {
	    			   afterrender:function(trigger){
	    				   trigger.dbKey='prjplanid';
	    				   trigger.mappingKey='prjplanid';
	    				   trigger.dbMessage='请选择该任务的所属项目';
	    			   }
	    		   },
	    		   'multidbfindtrigger[name=resourcecode]': {
	    			   afterrender:function(trigger){
	    				   trigger.dbKey='prjplanid';
	    				   trigger.mappingKey='tm_prjid';
	    				   trigger.dbMessage='请选择该任务的所属项目';
	    			   }
	    		   },
	    		   'multidbfindtrigger[name=pretaskdetno]': {
	    			   afterrender:function(trigger){
	    				   trigger.dbKey='prjplanid';
	    				   trigger.mappingKey='prjplanid';
	    				   trigger.dbMessage='请选择该任务的所属项目';
	    			   }
	    		   },
	    		   'field[name=sourcecode]':{
	     			   afterrender:function(f){
	     				   f.setFieldStyle({
	     					   'color': 'red'
	     				   });
	     				   f.focusCls = 'mail-attach';
	     				   var c = Ext.Function.bind(me.openSource, me);
	     				   Ext.EventManager.on(f.inputEl, {
	     					   mousedown : c,
	     					   scope: f,
	     					   buffer : 100
	     				   });
	     			   }
	    			},
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   afterrender: function(btn){
	    				     var status=Ext.getCmp('statuscode');
		    				  if(status && status.value!='ENTERING'){
		    					  btn.hide();
		    				  }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpDeleteButton': {
	    			   afterrender: function(btn){
	    				   var percentdone = Ext.getCmp('percentdone');
	    				   var point=Ext.getCmp('point').value;
	    				   if(percentdone && percentdone.value == 100){
	    					   btn.hide();
/*	    					   if(percentdone.value==100){
	    						   showMessage('提示','任务按时完成！分数:'+point);
	    					   }else {
	    						   showMessage('提示','任务未按时完成！分数:'+point);
	    					   }*/
	    				   }
	    			   },
	    			   click: function(btn){
	    				   this.FormUtil.onDelete({id: Number(Ext.getCmp('id').value)});
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			  afterrender:function (btn){
	    				  var status=Ext.getCmp('statuscode');
	    				  if(status && status.value != 'COMMITED'){
	    					  btn.hide();
	    				  }
	    			  },
	    			  click:function(btn){
	    				  me.FormUtil.onResSubmit(Ext.getCmp('id').value);
	    			  }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('id').value);
	    			   }
	    		   },		
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addTask', '创建任务', 'jsps/plm/task/task.jsp');
	    			   }
	    		   },
	    		   'button[id=mutidbaffirm]':{    			
	    			   beforerender:function(btn){
	    				   btn.handler=function(){
	    					   var win=btn.ownerCt.ownerCt;
	    					   var trigger=win.dbtriggr;
	    					   if(trigger.multistore){
	    						   var me = Ext.getCmp('multigrid');
	    						   trigger.setValue(me.multiselected.substring(1));
	    						   win.close();
	    					   } else {
	    						   var k = Ext.Object.getKeys(trigger.multiValue),cp;
	    						   Ext.each(k, function(key){
	    							   cp = Ext.getCmp(key);
	    							   if(cp.setValue !== undefined) {
	    								   if(key=="resourceunits"){
	    									   var arr=trigger.multiValue["resourcecode"].split("#");
	    									   var un=parseInt(100/(arr.length)),lastValue="";
	    									   for(var i=0;i<arr.length;i++){
	    										   if(i<arr.length-1) lastValue+=un+",";
	    										   else lastValue+=100-un*i;
	    									   }
	    									   cp.setValue(lastValue);   
	    								   }else if(key=="resourcetimerate"){
	    									   var arr=trigger.multiValue["resourcecode"].split("#"),lastValue="";	    									   
	    									   for(var i=0;i<arr.length;i++){
	    										   if(i<arr.length-1) lastValue+="100,";
	    										   else lastValue+="100";
	    									   }
	    									   cp.setValue(lastValue);  
	    								   }else cp.setValue(trigger.multiValue[key].replace(/#/g,","));
	    							   };
	    						   });
	    						   trigger.setValue(trigger.multiValue[trigger.name].replace(/#/g,","));
	    						   win.close();
	    					   }
	    				   };
	    			   }
	    		   },
	    		   'erpGridPanel2': {
	    			   itemclick: this.onGridItemClick
	    		   }	    	
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
	       },
	       openSource : function(e, el, obj) {
	    	   var f = obj.scope;
		   		if(f.value) {
		   			this.FormUtil.onAdd(null, f.ownerCt.down('#sourcecode').value, 
		   					f.ownerCt.down('#sourcelink').value);
		   		}
	       }
});