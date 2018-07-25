Ext.QuickTips.init();
Ext.define('erp.controller.plm.task.ProjectMainTask', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'plm.task.ProjectMainTask','core.form.Panel','core.grid.Panel2','core.grid.YnColumn','core.button.ImportExcel','core.button.Load','core.button.ImportMpp',
	       'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit','core.form.HrefField','core.button.ExportExcelButton',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.DeleteDetail','core.button.CreateCheckList','core.grid.detailAttach',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.button.TurnTask','core.button.End','core.button.ResEnd'
	       ],
	       init:function(){
	    	   var me=this,statuscode=null;
	    	   this.control({ 
	    		   'erpGridPanel2': {		  
	    			   itemclick: this.onGridItemClick
	    		   },
	    		   'erpSaveButton': {
	    			   click:function(btn){
	    				   var form = me.getForm(btn);
	    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					   me.BaseUtil.getRandomNumber();//自动添加编号
	    				   }
	    				   this.FormUtil.beforeSave(this);
	    			   },
	    			   afterender:function(btn){	
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   }, 
	    		   'erpUpdateButton':{
	    			   click:function(){
	    				   this.FormUtil.onUpdate(this);
	    			   },
	    			   beforerender:function(btn){
	    				   btn.formBind=true;  
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();	    					 
	    				   }
	    			   }
	    		   },
	    		   'erpDeleteButton':{
	    			   click:function(){
	    				   this.FormUtil.onDelete(Ext.getCmp("pt_id").getValue());
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();	
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   /*   'erpImportExcelButton':{
	    			   afterrender:function(btn){
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }  
	    		   },*/
	    		   'erpLoadButton':{
	    			   afterrender:function(btn){
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click:function(button){
	    				   warnMsg("确认要载入任务节点吗?", function(btn){
	    					   if(btn == 'yes'){
	    						   me.LoadTaskNode(button);
	    					   }
	    				   });
	    			   }  
	    		   },
	    		   'erpExportExcelButton':{
	    			   afterrender:function(btn){
	    				   btn.exportCaller='ImportProjectTask';
	    			   }   
	    		   },
	    		   /*   'erpDeleteDetailButton': {
	    			   afterrender: function(btn){
	    				   btn.ownerCt.add({
	    					   id:'attachform',
	    					   xtype:'form',
	    					   layout:'column',
	    					   bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
	    					   items: [{
	    						   xtype: 'filefield',
	    						   name: 'file',
	    						   buttonOnly: true,
	    						   hideLabel: true,
	    						   disabled:true,
	    						   width: 90,
	    						   height: 17,
	    						   id:'attachfile',
	    						   buttonConfig: {
	    							   iconCls: 'x-button-icon-pic',
	    							   text: '上传附件',
	    						   },
	    						   listeners: {
	    							   change: function(field){
	    								   var filename = '';
	    								   if(contains(field.value, "\\", true)){
	    									   filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
	    								   } else {
	    									   filename = field.value.substring(field.value.lastIndexOf('/') + 1);
	    								   }
	    								   field.ownerCt.getForm().submit({
	    									   url: basePath + 'common/upload.action?em_code=' + em_code,
	    									   waitMsg: "正在解析文件信息",
	    									   success: function(fp,o){
	    										   if(o.result.error){
	    											   showError(o.result.error);
	    										   } else {
	    											   Ext.Msg.alert("恭喜", filename + " 上传成功!");
	    											   field.setDisabled(true);
	    											   var record=Ext.getCmp('grid').selModel.lastSelected;
	    											   if(record){
	    												   record.set('attachs',filename+";"+o.result.filepath);
	    											   }
	    										   }
	    									   }	
	    								   });
	    							   }
	    						   }}]
	    				   });
	    			   }
	    		   },*/
	    		   'erpSubmitButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   var recorddate = Ext.Date.format(Ext.getCmp('pt_recorddate').value, 'Ymd');
	    				   //不要给默认录入日期 提示交期不能为空不让提交
	    				   if(Ext.Date.format(Ext.getCmp('pt_taskstartdate').getValue(), 'Ymd') < recorddate){
	    					   showError('节点开始日期小于单据录入日期,单据提交失败!');return;
	    				   }
	    				   me.FormUtil.onSubmit(id);
	    			   },
	    			   afterrender:function(btn){	    				
	    				   if(statuscode!='ENTERING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onResSubmit(id);
	    			   },
	    			   afterrender:function(btn){
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpAuditButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onAudit(id);
	    			   },
	    			   afterrender:function(btn){
	    				   if(statuscode!='COMMITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpResAuditButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onResAudit(id);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpEndButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   warnMsg('确认结案研发任务书吗?', function(btn){
	    					   if(btn == 'yes'){
	    						   me.FormUtil.onEnd(id);
	    					   } else {
	    						   return;
	    					   }
	    				   });

	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='AUDITED'&&statuscode!='DOING'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpResEndButton':{
	    			   click:function(btn){
	    				   var id=Ext.getCmp('pt_id').getValue();
	    				   me.FormUtil.onResEnd(id);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='FINISHED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'erpTurnTaskButton':{
	    			   click:function(btn){
	    				   me.TurnTask(btn);
	    			   },
	    			   afterrender:function(btn){
	    				   if(statuscode!='AUDITED'){
	    					   btn.hide();
	    				   }
	    			   }
	    		   },
	    		   'filefield[id=excelfile]':{
	    			   change: function(field){
	    				   if(contains(field.value, "\\", true)){
	    					   filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
	    				   } else {
	    					   filename = field.value.substring(field.value.lastIndexOf('/') + 1);
	    				   }
	    				   field.ownerCt.getForm().submit({
	    					   url: basePath + 'common/upload.action?em_code=' + em_code,
	    					   waitMsg: "正在解析文件信息",
	    					   success: function(fp,o){
	    						   if(o.result.error){
	    							   showError(o.result.error);
	    						   } else {	            				
	    							   var filePath=o.result.filepath;	
	    							   var keyValue=Ext.getCmp('pt_id').getValue();
	    							   var startdate=Ext.getCmp('pt_taskstartdate').getValue();
	    							   if(!startdate||startdate==""||startdate==null){
	    								   showError('请先设置节点开始时间');
	    								   return;
	    							   }else {
	    								   Ext.Ajax.request({//拿到form的items
	    									   url : basePath + 'plm/main/ImportExcel.action',
	    									   params:{
	    										   id:keyValue,
	    										   fileId:filePath,
	    										   startdate:startdate
	    									   },
	    									   method : 'post',
	    									   callback : function(options,success,response){
	    										   var result=Ext.decode(response.responseText);
	    										   if(result.success){
	    											   var grid=Ext.getCmp('grid');
	    											   var param={
	    													   caller:caller,
	    													   condition:'ptid='+keyValue
	    											   };
	    											   grid.GridUtil.loadNewStore(grid,param);
	    										   }else{
	    											   if(result.exceptionInfo != null){
	    												   showError(result.exceptionInfo);return;
	    											   }
	    										   }
	    									   }
	    								   });
	    							   }
	    						   }
	    					   }	
	    				   });
	    			   }	   					   

	    		   },
	    		   'filefield[id=mppfile]':{
	    			   change: function(field){
	    				   if(contains(field.value, "\\", true)){
	    					   filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
	    				   } else {
	    					   filename = field.value.substring(field.value.lastIndexOf('/') + 1);
	    				   }
	    				   field.ownerCt.getForm().submit({
	    					   url: basePath + 'common/upload.action?em_code=' + em_code,
	    					   waitMsg: "正在解析文件信息",
	    					   success: function(fp,o){
	    						   if(o.result.error){
	    							   showError(o.result.error);
	    						   } else {	            				
	    							   var filePath=o.result.filepath;	
	    							   var keyValue=Ext.getCmp('pt_id').getValue();
	    							   var startdate=Ext.getCmp('pt_taskstartdate').getValue();
	    							   if(!startdate||startdate==""||startdate==null){
	    								   showError('请先设置节点开始时间');
	    								   return;
	    							   }else {
	    								   Ext.Ajax.request({//拿到form的items
	    									   url : basePath + 'plm/main/ImportMpp.action',
	    									   params:{
	    										   id:keyValue,
	    										   fileId:filePath,
	    										   startdate:startdate
	    									   },
	    									   method : 'post',
	    									   callback : function(options,success,response){
	    										   var result=Ext.decode(response.responseText);
	    										   if(result.success){
	    											   var grid=Ext.getCmp('grid');
	    											   var param={
	    													   caller:caller,
	    													   condition:'ptid='+keyValue
	    											   };
	    											   grid.GridUtil.loadNewStore(grid,param);
	    										   }else{
	    											   if(result.exceptionInfo != null){
	    												   showError(res.exceptionInfo);return;
	    											   }
	    										   }
	    									   }
	    								   });
	    							   }
	    						   }
	    					   }	
	    				   });
	    			   }	   					   

	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   this.FormUtil.beforeClose(this);
	    			   },
	    			   afterrender:function(btn){
	    				   statuscode=Ext.getCmp('pt_statuscode').getValue();
	    				   if(statuscode!='ENTERING'){
	    					   Ext.getCmp('grid').setReadOnly(true);
	    				   }
	    			   }
	    		   },	
	    		   'htmleditor[name=pt_prjcode]':{
	    			   afterrender:function(editor){
	    				   editor.getToolbar().hide();
	    				   editor.readOnly=true;
	    				   editor.setValue('<a style="text-decoration:none;" href="javascript:parent.openFormUrl(' + editor.value + ',\'prj_code\',\'jsps/plm/project/project.jsp\',\'立项申请\''+ ');">' + editor.value + '</a>');

	    			   }
	    		   },
	    		   'htmleditor[name=pt_prcode]':{
	    			   afterrender:function(editor){
	    				   editor.getToolbar().hide();
	    				   editor.readOnly=true;
	    				   editor.setValue('<a style="text-decoration:none;" href="javascript:parent.openFormUrl(\'' + editor.value + '\',\'pr_code\',\'jsps/plm/project/projectReview.jsp\',\'项目评审\''+ ');">' + editor.value + '</a>');

	    			   }
	    		   },
	    		   'multidbfindtrigger[name=resourcecode]':{
	    			   afterrender:function(trigger){
	    				   trigger.gridKey='pt_prjid';
	    				   trigger.mappinggirdKey='tm_prjid';
	    				   trigger.gridErrorMessage='请选择该任务的项目ID';
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
	    						   if(!trigger.ownerCt){
	    							   var grid = trigger.owner;
	    							   var record = grid.selModel.lastSelected;
	    							   var multidata=new Object();
	    							   var multivalue=trigger.multiValue;
	    							   var  units=0;
	    							   if(multivalue.length>0){
	    								   var keys=Ext.Object.getKeys(multivalue[0].data);
	    								   var key=null;
	    								   Ext.each(trigger.multiValue, function(item, index){
	    									   for(var i=0;i<keys.length;i++){
	    										   key=keys[i];	   	    									
	    										   if(index==0){
	    											   multidata[key]=item.data[key];
	    										   }else {
	    											   multidata[key]+=","+item.data[key];
	    										   }	    										 

	    									   }
	    									   units+=100;
	    								   });
	    								   console.log(grid.dbfinds);
	    								   Ext.Array.each(keys, function(k){
	    									   Ext.Array.each(grid.dbfinds,function(ds){
	    										   if(Ext.isEmpty(ds.trigger) || ds.trigger == trigger.name) {
	    											   if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {	    	
	    												   if((units>100 || units==100 )&&ds.field=='resourceunits'){       		   
	    													   var arr=multidata['tm_employeecode'].split(",");
	    													   var un=parseInt(100/(arr.length)),lastValue="";
	    													   for(var i=0;i<arr.length;i++){
	    														   if(i<arr.length-1) lastValue+=un+",";
	    														   else lastValue+=100-un*i;
	    													   }
	    													   record.set(ds.field,lastValue);
	    												   }
	    												   else  record.set(ds.field, multidata[k]);
	    											   }
	    										   }
	    									   });
	    								   });
	    							   }


	    						   } else {
	    							   var k = Ext.Object.getKeys(trigger.multiValue),cp;
	    							   Ext.each(k, function(key){
	    								   cp = Ext.getCmp(key);
	    								   if(cp.setValue !== undefined)
	    									   cp.setValue(trigger.multiValue[key]);
	    							   });
	    							   trigger.setValue(trigger.multiValue[trigger.name]);
	    						   }
	    						   win.close();
	    					   }
	    				   };
	    			   }
	    		   }

	    	   });
	       },
	       onGridItemClick:function(selModel,record,el){
	    	   var me=this;
	    	   if (Ext.getCmp('fileform')) {
	    		   Ext.getCmp('fileform').setDisabled(false);
	    	   }
	    	   this.GridUtil.onGridItemClick(selModel, record);
	    	   if(record.data.handstatuscode&&record.data.handstatuscode!='UNACTIVE'){ 
	    		   var taskid=record.data.id;
	    		   if(taskid==0||!taskid) return;
	    		   var data=this.getData(taskid,'plm/record/getRecordData.action');
	    		   var store = Ext.create('Ext.data.Store', {
	    			   autoLoad:true,
	    			   fields:[{name:'tr_startdate',type:'date',format:'Y-m-d H:i:s'},{name:'tr_enddate',type:'date',format:'Y-m-d H:i:s'}, {name:'tr_usehours',type:'floatcolumn2'},{name:'tr_recorder',type:'string'},{name:'tr_type',type:'string'}],
	    			   data: Ext.decode(data)
	    		   });
	    		   if(menu==null){
	    			   menu= Ext.create('Ext.menu.Menu', {
	    				   async:false, 
	    				   id: 'mainMenu',    
	    				   ownerCt : this.ownerCt,
	    				   renderTo:Ext.getBody(),
	    				   width:'600',
	    				   style: {
	    					   overflow: 'visible', 
	    				   },
	    				   items: [{
	    					   width:600,
	    					   xtype:'tabpanel',
	    					   bbar: [{  
	    						   xtype: 'button', 
	    						   text: '关闭',
	    						   iconCls: 'x-button-icon-close',
	    						   style :'margin-left:230px',
	    						   handler:function(btn){                          
	    							   Ext.getCmp('mainMenu').hide();
	    						   }
	    					   }],
	    					   items:[{
	    						   title:'任务用时',
	    						   xtype:'grid',
	    						   columnLines:true,
	    						   id:'smallgrid',
	    						   height:200,
	    						   buttonAlign:'center',
	    						   store:store,
	    						   bodyStyle: 'background:#EEE5DE; padding:0px;',
	    						   features : [Ext.create('Ext.grid.feature.Grouping',{
	    							   //startCollapsed: true,
	    							   groupHeaderTpl: '{name} '
	    						   }),{
	    							   ftype : 'summary',
	    							   showSummaryRow : true,//不显示默认合计行
	    							   generateSummaryData: function(){
	    								   var me = this,
	    								   data = {},
	    								   store = me.view.store,
	    								   columns = me.view.headerCt.getColumnsForTpl(),
	    								   i = 0,
	    								   length = columns.length,
	    								   //fieldData,
	    								   //key,
	    								   comp;
	    								   //将feature的data打印在toolbar上面
	    								   for (i = 0, length = columns.length; i < length; ++i) {
	    									   comp = Ext.getCmp(columns[i].id);
	    									   data[comp.id] = me.getSummary(store, comp.summaryType, comp.dataIndex, false);
	    									   var tb = Ext.getCmp(columns[i].dataIndex + '_' + comp.summaryType);
	    									   if(tb){
	    										   tb.setText(tb.text.split(':')[0] + ':' + data[comp.id]);
	    									   }
	    								   }
	    								   return data;
	    							   }
	    						   }],
	    						   columns: [{ header: '开始时间',  dataIndex: 'tr_startdate' ,flex: 1 ,cls :'x-grid-header-1',format:'Y-m-d H:i:s',xtype:'datecolumn'},
	    						             { header: '结束时间', dataIndex: 'tr_enddate', flex: 1 ,cls :'x-grid-header-1',format:'Y-m-d H:i:s',xtype:'datecolumn'},
	    						             { header: '累计时间(H)', dataIndex: 'tr_usehours',cls :'x-grid-header-1',align:'right',summaryType:'sum'},
	    						             { header: '操作人',  dataIndex:'tr_recorder',cls :'x-grid-header-1'},
	    						             {header : '类型',   dataIndex:'tr_type',flex: 1,cls :'x-grid-header-1'}
	    						             ]	  
	    					   },{
	    						   title:'任务日志',
	    						   height:200,
	    						   taskid:taskid,
	    						   listeners:{
	    							   activate: function(tab){
	    								   me.getLogData(tab);
	    							   }
	    						   },
	    						   items:[{
	    							   xtype:'grid',
	    							   height:200,
	    							   columnLines:true,
	    							   bodyStyle: 'background:#EEE5DE; padding:0px;',
	    							   store :Ext.create('Ext.data.Store', {
	    								   autoLoad:true,
	    								   groupField: 'WR_RECORDER',
	    								   fields:[{name:'WR_ID',type:'int'},{name:'WR_PERCENTDONE',type:'int',format:0},{name:'WR_RECORDDATE',type:'date',format:'Y-m-d H:i:s'}, {name:'WR_RECORDER',type:'string'},{name:'WR_REDCORD',type:'string'}],
	    								   data: []
	    							   }),
	    							   features : [Ext.create('Ext.grid.feature.Grouping',{
	    								   //startCollapsed: true,
	    								   groupHeaderTpl: '{name} (共 {rows.length}项) '
	    							   })],
	    							   columns: [{header:'ID',dataIndex:'WR_ID',width:0},{ header: '提交率',  dataIndex: 'WR_PERCENTDONE' ,width:60 ,cls :'x-grid-header-1',xtype:'numbercolumn'},
	    							             { header: '提交时间', dataIndex: 'WR_RECORDDATE',width:150 ,cls :'x-grid-header-1',format:'Y-m-d H:i:s',xtype:'datecolumn'},
	    							             { header: '操作人',  dataIndex:'WR_RECORDER',width:60,cls :'x-grid-header-1'},
	    							             {header : '日志',   dataIndex:'WR_REDCORD',
	    							            	 renderer:function(val,meta,record){
	    							            		 console.log(record);
	    							            		 return '<a href="javascript:openFormUrl(' + record.data.WR_ID + ',\'wr_id\',\'jsps/plm/record/recordlog.jsp?_noc=1\',\'任务日志\''+ ');">' + val + '</a>';
	    							            	 },
	    							            	 flex: 1,cls :'x-grid-header-1'}]
	    						   }]
	    					   }]
	    				   }]
	    			   });  
	    		   }
	    		   else {
	    			   var data=Ext.decode(data);
	    			   Ext.getCmp('smallgrid').getStore().loadData(data);
	    			   menu.items.items[0].items.items[1].taskid=record.data.id;
	    			   if(menu.items.items[0].activeTab.title=='任务日志'){
	    				   me.getLogData(menu.items.items[0].activeTab);
	    			   }
	    		   }
	    		   menu.alignTo(el, 'tl-bl?',[280, 0]);
	    		   menu.show();
	    	   }
	       } ,
	       getLogData:function(tab){
	    	   Ext.Ajax.request({
	    		   url : basePath + 'common/getFieldsDatas.action',
	    		   params: {
	    			   fields:'WR_ID,WR_PERCENTDONE,WR_RECORDDATE,WR_RECORDER,WR_REDCORD',
	    			   caller:'WorkRecord',
	    			   condition:'wr_taskid='+tab.taskid
	    		   },
	    		   method : 'post',
	    		   async:true,
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   tab.items.items[0].getStore().loadData(new Ext.decode(localJson.data));
	    		   }
	    	   });
	       },
	       getData:function(keyValue,url){
	    	   var data=new Array();
	    	   Ext.Ajax.request({
	    		   url : basePath + url,
	    		   params: {
	    			   id: keyValue
	    		   },
	    		   method : 'post',
	    		   async:false,
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   data=localJson.data;
	    		   }
	    	   });
	    	   return data;
	       }, 
	       TurnTask:function(btn){
	    	   var form=btn.ownerCt.ownerCt;
	    	   var id=Ext.getCmp('pt_id').getValue();
	    	   Ext.Ajax.request({
	    		   url : basePath + form.TurnTaskUrl,
	    		   params: {
	    			   id: id
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','任务节点激活成功!',function(){window.location.reload();});
	    			   } else {
	    				   if(localJson.exceptionInfo){
	    					   var str = localJson.exceptionInfo;
	    					   if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	    						   str = str.replace('AFTERSUCCESS', '');
	    						   submitSuccess(function(){
	    							   window.location.reload();
	    						   });
	    					   }
	    					   showMessage("提示", str);return;
	    				   }
	    			   }
	    		   }
	    	   });
	       },
	       LoadTaskNode:function(btn){	    
	    	   var form=btn.ownerCt.ownerCt;
	    	   var keyValue=Ext.getCmp('pt_id').getValue();
	    	   var producttype=Ext.getCmp('pt_producttype').getValue();
	    	   form.FormUtil.setLoading(true);
	    	   Ext.Ajax.request({
	    		   url : basePath + form.LoadTaskNodeUrl,
	    		   params: {
	    			   id:keyValue,
	    			   type:producttype
	    		   },
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var localJson = new Ext.decode(response.responseText);
	    			   form.FormUtil.setLoading(false);
	    			   if(localJson.success){
	    				   Ext.Msg.alert('提示','载入成功!',function(){
	    					   var grid=Ext.getCmp('grid');
	    					   var param={caller:caller,condition:'ptid='+keyValue};
	    					   grid.GridUtil.loadNewStore(grid,param);
	    				   });
	    			   } else {
	    				   if(localJson.exceptionInfo){
	    					   var str = localJson.exceptionInfo;
	    					   showMessage("提示", str);return;
	    				   }
	    			   }
	    		   }
	    	   });
	       },
	       openFormUrl:function(value, keyField, url, title){
	    	   url =url+'?formCondition='+keyField+"="+value;
	    	   var panel = Ext.getCmp(keyField + "=" + value); 
	    	   var main = parent.Ext.getCmp("content-panel");
	    	   var showtitle='';
	    	   url = url.replace(/IS/g, "=\'").replace(/&/g, "\'&");
	    	   if(!panel){ 
	    		   if (title && title.toString().length>4) {
	    			   showtitle = title.toString().substring(0,4);	
	    		   }
	    		   panel = { 
	    				   title : showtitle,
	    				   tag : 'iframe',
	    				   tabConfig:{tooltip:title.toString() + '(' + keyField + "=" + value + ')'},
	    				   frame : true,
	    				   border : false,
	    				   layout : 'fit',
	    				   iconCls : 'x-tree-icon-tab-tab',
	    				   html : '<iframe src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
	    				   closable : true,
	    				   listeners : {
	    					   close : function(){
	    						   main.setActiveTab(main.getActiveTab().id); 
	    					   }
	    				   } 
	    		   };
	    		   openTab(panel, keyField + "=" + value);
	    	   }else{ 
	    		   main.setActiveTab(panel); 
	    	   } 
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       }
});