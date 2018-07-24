Ext.require([
             'Gnt.plugin.TaskContextMenu',
             'Sch.plugin.TreeCellEditing',
             'Sch.plugin.Pan',
             'Gnt.panel.Gantt',
             'Gnt.column.PercentDone',
             'Gnt.column.StartDate',
             'Gnt.column.EndDate',
             'Gnt.plugin.Printable',
             'Gnt.widget.AssignmentCellEditor',
             'Gnt.column.ResourceAssignment',
             'Gnt.model.Assignment',
             'erp.util.BaseUtil',
             'Gnt.widget.Calendar'
             ]);
Ext.onReady(function() { 
	Ext.QuickTips.init();  
	function getUrlParam(name){   
		var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
		var r=window.location.search.substr(1).match(reg);   
		if  (r!=null)   return decodeURI(r[2]); 
		return   null;   
	}
	var formCondition = getUrlParam('formCondition');
	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
	var BaseUtil=Ext.create('erp.util.BaseUtil');
    function FormatTaskStore(data){
    	delete data['index'];
    	data.StartDate=Ext.Date.format(data.StartDate, 'Y-m-d H:i:s');
    	data.EndDate=Ext.Date.format(data.EndDate, 'Y-m-d H:i:s');
    	data.BaselineStartDate=Ext.Date.format(data.BaselineStartDate, 'Y-m-d H:i:s');
    	data.BaselineEndDate =Ext.Date.format(data.BaselineEndDate, 'Y-m-d H:i:s');
    	data.parentId=data.parentId==null?0:data.parentId;
    	delete data['id'];
    	delete data['checked'];
    	delete data['expandable'];
    	delete data['expanded'];
    	delete data['children'];
    	delete data['Effort'];
    	delete data['EffortUnit'];
    	delete data['ManuallyScheduled'];
    	delete data['SchedulingMode'];
    	delete data['Draggable'];
    	delete data['Resizable'];
    	delete data['PhantomId'];
    	delete data['PhantomParentId'];
    	delete data['hrefTarget'];
    	delete data['qtip'];
    	delete data['qtitle'];
    	delete data['allowDrop'];
    	delete data['isFirst'];
    	delete data['loaded'];
    	delete data['depth'];
    	delete data['allowDrag'];
    	delete data['loading'];
    	delete data['cls'];
    	delete data['iconCls'];
    	delete data['icon'];
    	delete data['root'];
    	delete data['type'];
    	delete data['isLast'];
    	delete data['taskcode'];
    	delete data['prjplanname'];
    	return data;
    }
	Ext.Ajax.request({
		url : basePath + "plm/gantt/getData.action",
		params:{
			condition:formCondition
		},
		async:false, 
		method : 'get',
		callback : function(options,success,response){
			var rs = new Ext.decode(response.responseText);
			if(rs.exceptionInfo){
				showError(rs.exceptionInfo);return;
			}
			else if(rs.success){
				projectplandate=rs.data.prjplandata[0].prj_start;
				projectplandata=rs.data.prjplandata[0]; 

			}
		}
	}); 	         		     
	var startDate =Sch.util.Date.add(new Date(projectplandate),Sch.util.Date.WEEK,-1);		
	var endDate=Sch.util.Date.add(new Date(projectplandate), Sch.util.Date.WEEK, 20);
	var calendar = new Gnt.data.Calendar({
		data: [
		       {
		    	   Date: new Date(2010, 0, 13),
		    	   Cls: 'gnt-national-holiday'
		       },
		       {
		    	   Date: new Date(2010, 1, 1),
		    	   Cls: 'gnt-company-holiday'
		       },
		       {
		    	   Date: new Date(2010, 0, 16),
		    	   IsWorkingDay: true
		       }
		       ]
	});   
	Ext.define('MyResourceModel', {
		extend : 'Gnt.model.Resource',
		fields:[{name:'EmCode',type:'string'},
		        {name:'EmId',type:'int'}
		]
	});
	var resourceStore = Ext.create("Gnt.data.ResourceStore", {
		model : 'MyResourceModel'
	});
	Ext.define('MyAssignmentModel', {
		extend : 'Gnt.model.Assignment',
	});

	var assignmentStore = Ext.create("Gnt.data.AssignmentStore", {
		model:'MyAssignmentModel',
		autoLoad    : true,
		resourceStore : resourceStore,
		proxy : {
			method: 'GET',
			type : 'ajax',
			url: basePath+'plm/resourceassignment.action',
			extraParams :{
				condition:formCondition,
			},
			reader : {
				type : 'json',
				root : 'assignments'
			}
		},
		listeners : {
			load : function() {
				resourceStore.loadData(this.proxy.reader.jsonData.resources);
			}
		}
	});
	var assignmentEditor = Ext.create('Gnt.widget.AssignmentCellEditor', {
		assignmentStore : assignmentStore,
		resourceStore : resourceStore
	});
	Ext.define('MyTaskModel', {
		extend : 'Gnt.model.Task',

		// A field in the dataset that will be added as a CSS class to each rendered task element
		clsField : 'TaskType',
		fields : [
		          { name : 'TaskType', type : 'string' },
		          { name : 'TaskColor', type : 'string'},
		          {name:'prjplanid',type:'int'},
		          {name:'prjplanname',type:'string'},
		          {name:'recorder',type:'string'},
		          {name:'recorddate',type:'string'},
		          {name:'taskcode',type:'string'},
		          {name:'id',type:'int'},
		          {name:'type',type:'int'},
		          {name:'resourcename',type:'string'}
		          ]
	});
	Ext.define('MyDependencyModel', {
		extend : 'Gnt.model.Dependency',
		fields : [
		          {name:'Id',type:'int'}
		          ]
	});
	var taskStore = Ext.create("Gnt.data.TaskStore", {
		model: 'MyTaskModel',
		sorters : ['StartDate','Id'],
		proxy : {
			type : 'ajax',
			headers : { "Content-Type" : 'application/json' },
			extraParams :{
				condition:formCondition,
			},
			api: {
				read:    basePath+'plm/gantt.action',
				create:  basePath+'plm/ganttcreate.action',
				update:  basePath+'plm/ganttupdate.action',
			},
			writer : {
				type : 'json',
				root : 'jsonData',
				encode : true,
				nameProperty:'data',
				allowSingle : false
			},
			reader : {
				type : 'json',
				root : 'jsonData'
			}
		}
	});
	var today=new Date();
	var dependencyStore = Ext.create("Gnt.data.DependencyStore", {
		model:'MyDependencyModel',
		autoLoad : true,
		proxy: {
			type : 'ajax',
			extraParams :{
				condition:formCondition,
			},
			url:basePath+ 'plm/gantt/getdependency.action',
			method: 'GET',
			reader: {
				type : 'json',
				root:'dependency'
			}
		}
	});

	var printableMilestoneTpl = new Gnt.template.Milestone({
		prefix : 'foo',
		printable : true,
		imgSrc : 'images/milestone.png'
	});
	var nightShiftCalendar = new Gnt.data.calendar.BusinessTime({
		calendarId: 'NightShift',
		name: "Night shift",
		defaultAvailability: [ '00:00-06:00', '22:00-24:00' ]
	});

	var g = Ext.create('Gnt.panel.Gantt', {
		id:'gantt',
		region          : 'center',
		selModel        : new Ext.selection.TreeModel({ ignoreRightMouseSelection : false, mode : 'MULTI'}),
		columnLines     : true,
		//leftLabelField  : 'Name',
		//highlightWeekends : false,
		loadMask: true,
		viewPreset: 'weekAndDayLetter',
		startDate       : startDate,
		endDate         : endDate,
		resourceStore : resourceStore,
		assignmentStore : assignmentStore,
		taskStore : taskStore,
		nightShiftCalendar:nightShiftCalendar,
		dependencyStore  : dependencyStore,
		errorTask:'',
		listeners:{
			'taskclick':function(){
			},
			taskcontextmenu:function(){
			},
			scheduleclick:function(){
			},
			afterrender:function(grid){
				Ext.defer(function(){
					if(errorTask!=''){
						var errorTask = grid.errorTask.substring(1);
						Ext.Msg.alert('提示','任务:'+errorTask + '  没有结束时间!');
					}		        	   	
        	   	},100);
			}
		},    
		lockedViewConfig: {
			plugins: {
				ptype: 'treeviewdragdrop'
			}
		},
		/**  rightLabelField : {
            dataIndex : 'Id',
            renderer : function(value, record) {
                return 'Id: #' + value;
            }
        },*/
		columns : [
		           { 
		        	   xtype:'wbscolumn',
		        	   header:'编号',
		        	   width:50
		           },
		           {
		        	   xtype : 'treecolumn',
		        	   header: '任务',
		        	   sortable: true,
		        	   dataIndex: 'Name',
		        	   width: 200,
		        	   field: {
		        		   allowBlank: false
		        	   },
		        	   renderer : function(v, meta, r) {
		        		   if (!r.data.leaf) meta.tdCls = 'sch-gantt-parent-cell';
		        		   return v;
		        	   }
		           },
		           {
		        	   header : '资源分配', 
		        	   width:120, 
		        	   editor : assignmentEditor,
		        	   xtype : 'resourceassignmentcolumn'

		           },
		           {   
		        	   header:'开始时间' ,
		        	   xtype : 'startdatecolumn',
		        	   field: {
		        		   xtype:'datefield',
		        		   allowBlank: false
		        	   },
		           },
		           {
		        	   header:'结束时间',
		        	   xtype : 'enddatecolumn',
		        	   //hidden : true
		        	   field: {
		        		   xtype:'datefield',
		        		   allowBlank: false
		        	   },
		           },
		           /** {
                    header :'持续时间',
                    xtype : 'durationcolumn'
                },**/
		           {
		        	   header:'完成率(%)',
		        	   xtype : 'percentdonecolumn',
		        	   width : 50
		           },
		           {
		        	   header: '完成状况',
		        	   tdCls: 'sch-column-color',
		        	   renderer:function(val,meta,record){
						   var grid = this.ownerCt;
		        	   	   if(!record.data.EndDate){
		        	   	   	 	grid.errorTask += ',' + record.data.Name;
		        	   	   	 	return;
		        	   	   }
		        		   if(record.data.PercentDone&&record.data.EndDate.getTime()<today&&record.data.PercentDone<100){
		        			   return '<div class="color-column-inner" style="background-color:#EE2C2C" align="center">&nbsp;</div>';
		        		   }else return  '<div class="color-column-inner" style="background-color:#008B00" align="center">&nbsp;</div>';
		        	   }           
		           }   

		           //column displaying task color
		           /** {
                    header: '颜色',
                    xtype: 'templatecolumn',
                    width: 50,
                    tdCls: 'sch-column-color',
                    field: {
                        allowBlank: false
                    },
                    tpl: '<div class="color-column-inner" style="background-color:#00FF00">&nbsp;</div>',
                    listeners: {
                        click : function(panel, el, a, b, event, record) {
                            event.stopEvent();
                            this.rec = record;
                            this.showColumnMenu(el, event, record);
                        }
                    },
                    showColumnMenu: function(el, event, rec){
                        //if color menu is not present, create a new Ext.menu.Menu instance
                        if(!this.colorMenu){
                            this.colorMenu = new Ext.menu.Menu({
                                cls: 'gnt-locked-colormenu',
                                plain: true,
                                items: [
                                    {
                                        text: '更改任务颜色',
                                        menu: {
                                            showSeparator: false,
                                            items: [
                                                Ext.create('Ext.ColorPalette', {
                                                    listeners: {
                                                        select: function(cp, color){      
                                                            this.rec.set('TaskColor', color);
                                                        },
                                                        scope: this
                                                    }
                                                })
                                            ]
                                        }
                                    }
                                ]                             
                            });
                        }

                        this.colorMenu.showAt(event.xy);
                    }                                  
                } **/  
		           ],      
		           tbar : [
		                   {
		                	   xtype: 'buttongroup',
		                	   title: '视图工具',
		                	   columns: 4,
		                	   items: [ {
		                		   iconCls : 'icon-fullscreen',
		                		   text : '全屏',
		                		   scope : this,
		                		   handler : function() {
		                			   window.open(window.location.href);
		                		   }
		                	   },{
		                		   iconCls : 'icon-prev',
		                		   text : '向前',
		                		   scope : this,
		                		   handler : function() {
		                			   g.shiftPrevious();
		                		   }
		                	   },
		                	   {
		                		   iconCls : 'icon-next',
		                		   text : '向后',
		                		   scope : this,
		                		   handler : function() {
		                			   g.shiftNext();
		                		   }
		                	   },
		                	   {
		                		   text : '缩放',
		                		   iconCls : 'zoomfit',
		                		   handler : function() {
		                			   g.zoomToFit();
		                		   },
		                		   scope : this
		                	   },
		                	   {
		                		   text : '收缩',
		                		   iconCls : 'icon-collapseall',
		                		   scope : this,
		                		   handler : function() {
		                			   g.collapseAll();
		                		   }
		                	   },
		                	   {
		                		   text : '展开',
		                		   iconCls : 'icon-expandall',
		                		   scope : this,
		                		   loader:{
		                			   loadMask:true,
		                		   },
		                		   handler : function() {
		                			   var loadMask= g.setLoading("正在展开",true);
		                			   g.expandAll(loadMask.hide());
		                			   // g.expandAll();

		                		   }
		                	   },
		                	   {
		                		   iconCls : 'togglebutton',
		                		   enableToggle: true,
		                		   id : 'readonlybutton',
		                		   text: '只读',
		                		   pressed: true,
		                		   handler: function () {
		                			   g.setReadOnly(this.pressed);
		                			   Ext.getCmp('savebutton').setDisabled(this.pressed);
		                		   },
		                		   /*              listeners: {
                     beforerender: { //bind to the underlying el property on the panel
                                fn: function(){
                                  if(em_id!='10000011'){
                                    this.enableToggle=false;
                                  }
                                 }
                                }
                    }*/
		                	   }]
		                   },
		                   {
		                	   xtype: 'buttongroup',
		                	   title: '视图解析',
		                	   columns: 2,
		                	   items: [{
		                		   iconCls : 'gnt-date',
		                		   text: '6 周',
		                		   scope : this,
		                		   handler : function() {
		                			   g.switchViewPreset('weekAndMonth');
		                		   }
		                	   },
		                	   {iconCls : 'gnt-date',
		                		   text: '10周',
		                		   scope : this,
		                		   handler : function() {
		                			   g.switchViewPreset('weekAndDayLetter');
		                		   }
		                	   },
		                	   {iconCls : 'gnt-date',
		                		   text: '1 年',
		                		   scope : this,
		                		   handler : function() {
		                			   g.switchViewPreset('monthAndYear');
		                		   }
		                	   },
		                	   {iconCls : 'gnt-date',
		                		   text: '5 年',
		                		   scope : this,
		                		   handler : function() {
		                			   var start = new Date(this.getStart().getFullYear(), 0);

		                			   g.switchViewPreset('monthAndYear', start, Ext.Date.add(start, Ext.Date.YEAR, 5));
		                		   }
		                	   }
		                	   ]},{
		                		   xtype: 'buttongroup',
		                		   title: '操作区',
		                		   columns: 3,   
		                		   items: [ {
		                			   iconCls : 'x-advance-print',
		                			   text : '打印',
		                			   handler : function() {
		                				   // Make sure this fits horizontally on one page.
		                				   Ext.getCmp('gantt').zoomToFit();
		                				   Ext.getCmp('gantt').print();
		                			   }
		                		   },
		                		   {
		                			   text: '添加任务',
		                			   iconCls : 'x-advance-add',
		                			   handler: function () { 
		                				   var selectItem=g.selModel.selected.items[0];                     
		                				   if(selectItem){ 
		                					   if(selectItem.data.Id){
		                						   var newTask = new taskStore.model({
		                							   Name: '新增任务',
		                							   leaf : true,                              
		                							   PercentDone: 0,
		                							   type:0,
		                							   prjplanid:projectplandata.prj_id,
		                							   prjplanname:projectplandata.prj_name,
		                							   taskcode:'T'+projectplandata.prj_id+BaseUtil.getRandomNumber('ProjectTask'),
		                							   recorder:recorder,
		                							   recorddate:recorddate,
		                							   StartDate:selectItem.data.StartDate,//给系统的默认时间
		                							   EndDate:selectItem.data.EndDate,
		                						   });      
		                						   var record = g.selModel.selected.items[0];
		                						   if(record.data.leaf){
		                							   record.data.leaf = false;
		                							   record.data.expanded=true;
		                						   }
		                						   record.appendChild(newTask);                      
		                					   }else{
		                						   showError("任务未生成不能给他添加子任务!");
		                					   }
		                				   }  else{
		                					   showError("请选择添加任务的父节点!");
		                				   }
		                			   }    
		                		   },{
		                			   text: '添加父任务',
		                			   iconCls : 'x-advance-add',
		                			   handler: function () {
		                				   var newTask = new taskStore.model({
		                					   Name: 'New task',
		                					   leaf : false,                              
		                					   PercentDone: 0,
		                					   type:0,
		                					   prjplanid:rs.data.prjplandata[0].prj_id,
		                					   prjplanname:rs.data.prjplandata[0].prj_name,
		                					   taskcode:'T'+projectplandata.prj_id+BaseUtil.getRandomNumber('ProjectTask'),
		                					   recorder:recorder,
		                					   recorddate:recorddate,
		                					   parentId:0 });                       
		                				   taskStore.getRootNode().appendChild(newTask) ;                     
		                			   }
		                		   },{
		                			   iconCls : 'x-advance-save',
		                			   id:'savebutton',
		                			   text : '保存',
		                			   listeners: {
		                				   'afterrender':function(btn,opts){
		                					   if(Ext.getCmp('readonlybutton').pressed){
		                						   btn.setDisabled(true);
		                					   }
		                				   }
		                			   },
		                			   handler : function(){
		                				   //g.checkstore();//验证store是否填写完整  要求必填字段 如 日期 并且开始时间要小于或等于结束时间 
		                				   var options={},
		                				   me=g.taskStore;
		                				   de=g.dependencyStore;
		                				   as=g.assignmentStore;
		                				   var toCreate  = me.getNewRecords(),
		                				   toUpdate  = me.getUpdatedRecords(),
		                				   toDestroy = me.getRemovedRecords(),
		                				   toCreateDependency=de.getNewRecords(),
		                				   toDestroyDependency=de.getRemovedRecords(),
		                				   toCreateassign=as.getNewRecords(),
		                				   toUpdateassign=as.getUpdatedRecords(),
		                				   toDestroyassign=as.getRemovedRecords(),
		                				   needsSync = false;
		                				   if (toCreate.length > 0) {
		                					   var create=null;
		                					   options.create = toCreate;		               
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toCreate.length;i++){
		                						   var data = FormatTaskStore(toCreate[i].data);
		                						   jsonData.push(Ext.JSON.encode(data));
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/gantt/ganttcreate.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   }
		                				   if (toUpdate.length > 0) {
		                					   options.update = toUpdate;
		                					   needsSync=true;
		                					   var index = 0;
		                					   var jsonData=new Array();
		                					   console.log(toUpdate.length);
		                					   for(var i=0;i<toUpdate.length;i++){
		                						   console.log(toUpdate[i].data);
		                						   var data = FormatTaskStore(toUpdate[i].data);
		                						   jsonData.push(Ext.JSON.encode(data));
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/gantt/ganttupdate.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });                             
		                				   }
		                				   if (toDestroy.length > 0) {
		                					   options.destroy = toDestroy;
		                					   needsSync=true;
		                					   var index = 0;
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toDestroy.length;i++){
		                						   var data = FormatTaskStore(toDestroy[i].data);
		                						   jsonData.push(Ext.JSON.encode(data));
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/gantt/ganttdelete.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   } 
		                				   if(toCreateDependency.length>0){
		                					   var index = 0;
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toCreateDependency.length;i++){
		                						   var data = toCreateDependency[i].data;
		                						   jsonData[index++] = Ext.JSON.encode(data);
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/gantt/dependencycreate.action",
		                						   params:{
		                							   condition:formCondition,
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   }if(toDestroyDependency.length>0){
		                					   var index=0;
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toDestroyDependency.length;i++){
		                						   var data=toDestroyDependency[i].data;
		                						   jsonData[index++]=Ext.JSON.encode(data);
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/gantt/dependencydelete.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   }if(toCreateassign.length>0){
		                					   var index=0;
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toCreateassign.length;i++){
		                						   // var ddd=g.selModel.selected.items[0];
		                						   var data=toCreateassign[i].data;
		                						   var id=data.TaskId;
		                						   var task=g.taskStore.getById(id);
		                						   var resourceId=data.ResourceId;
		                						   var resource=g.resourceStore.getById(resourceId);
		                						   data.StartDate=task.data.StartDate;
		                						   data.EndDate=task.data.EndDate;
		                						   data.resourcecode=resource.data.EmCode;
		                						   data.detno=1;
		                						   data.Id=0;
		                						   data.resourcename=resource.data.Name;
		                						   data_resourcetype="";
		                						   data.taskname=task.data.Name;
		                						   data.emid=resource.data.EmId;
		                						   data.prjid=task.data.prjplanid;
		                						   data.prjname=task.data.prjplanname;
		                						   jsonData[index++]=Ext.JSON.encode(data);
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/resource/createResource.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   }if(toUpdateassign.length>0){
		                					   var index=0;
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toUpdateassign.length;i++){
		                						   var data=toUpdateassign[i].data;
		                						   jsonData[index++]=Ext.JSON.encode(data);
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/resource/updateResource.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   }if(toDestroyassign.length>0){
		                					   var index=0;
		                					   var jsonData=new Array();
		                					   for(var i=0;i<toDestroyassign.length;i++){
		                						   var data=toDestroyassign[i].data;
		                						   jsonData[index++]=Ext.JSON.encode(data);
		                					   }
		                					   Ext.Ajax.request({
		                						   url : basePath + "plm/resource/deleteResource.action",
		                						   params:{
		                							   jsonData: unescape(jsonData.toString().replace(/\\/g,"%")),
		                						   },
		                						   method : 'post',
		                						   callback : function(options,success,response){
		                						   }
		                					   });
		                				   }
		                				   //Ext.Msg.alert('提示','保存成功!');
		                				   var gridCondition=getUrlParam("gridCondition");
		                				   window.location.href = window.location.href + '?formCondition=' + 
		                				   formCondition + '&gridCondition=' + gridCondition;                    
		                			   }
		                		   },
		                		   {
		                			   text: '删除任务',
		                			   iconCls : 'x-advance-delete',
		                			   handler: function () {                             
		                				   var record = g.selModel.selected.items[0];
		                				   if(record){
		                					   if(record.data.type==1){
		                						   showError('该任务是里程碑不能删除');
		                						   return 
		                					   }
		                					   var parentNode = record.parentNode;
		                					   if(parentNode.childNodes.length == 1){
		                						   parentNode.data.leaf = true;
		                					   }
		                					   parentNode.removeChild(record);
		                				   }else{
		                					   showError("请选择删除要删除任务!");
		                				   }
		                			   }
		                		   },
		                		   {
		                			   text: '查看资源',
		                			   iconCls : 'x-advance-find',
		                			   handler: function () {    
		                				   var panel = Ext.getCmp("resourceproject=" + formCondition);
		                				   value=formCondition.split("=")[1];  
		                				   var caller="Resource";
		                				   var url=basePath+"jsps/plm/resource/assignresource.jsp";                        
		                				   var main = parent.Ext.getCmp("content-panel");
		                				   if(!panel){ 
		                					   var title = "";
		                					   if (value.toString().length>4) {
		                						   title = value.toString().substring(value.toString().length-4);	
		                					   } else {
		                						   title = value;
		                					   }
		                					   panel = { 
		                							   //title : main.getActiveTab().title+'('+title+')',
		                							   title:'资源分配('+title+')',
		                							   tag : 'iframe',
		                							   tabConfig:{tooltip:'资源分配('+title+')'},
		                							   frame : true,
		                							   border : false,
		                							   layout : 'fit',
		                							   iconCls : 'x-tree-icon-tab-tab',
		                							   html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="'+url+'?prjplanid='+value+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
		                							   closable : true,
		                							   listeners : {
		                								   close : function(){
		                									   main.setActiveTab(main.getActiveTab().id); 
		                								   }
		                							   } 
		                					   };
		                					   Ext.getCmp("gantt").openTab(panel,"resourceproject=" + formCondition); 
		                				   }else{ 
		                					   main.setActiveTab(panel); 
		                				   } 
		                			   },
		                			   scope:this
		                		   },

		                		   ]},
		                		   /**     {
                xtype: 'buttongroup',
                title: '日历区',
                columns: 5,
                defaults : { scale : "large" },
                items: [
                    {
                    text            : '查看项目日历',
                    iconCls         : 'gnt-date',
                    menu            : [
                        {
                            xtype           : 'ganttcalendar',                           
                            calendar        : calendar,
                            startDate       : startDate,
                            endDate         : endDate,                            
                            showToday       : false
                        }
                    ]
                },
                {
                iconCls : 'gnt-date',
                text : '设置工作时间',
                handler: function(){    
                    var cal = new Gnt.widget.calendar.Calendar({
                        calendar: g.nightShiftCalendar
                    });
                    var wndAdv = Ext.create('Ext.window.Window', {
                        title: "Edit working time",
                        modal: true,
                        buttons:[{
                            text: 'OK',
                            handler: function(){
                                cal.applyChanges();
                                wndAdv.close();
                            }
                        },{
                            text: 'Cancel',
                            handler: function(){
                                wndAdv.close();
                            }
                        }],

                        width: 600,
                        items : cal
                    });

                    wndAdv.show();
                }
            },
                ]
            },**/
		                		   //'->',
		                		   {
		                			   xtype: 'buttongroup',
		                			   title: '功能区',
		                			   columns : 4,
		                			   items: [
		                			           {
		                			        	   text : '突出关键链',
		                			        	   iconCls : 'togglebutton',
		                			        	   scope : this,
		                			        	   enableToggle : true,
		                			        	   handler : function(btn) {
		                			        		   var v = g.getSchedulingView();
		                			        		   if (btn.pressed) {
		                			        			   v.highlightCriticalPaths(true);
		                			        		   } else {
		                			        			   v.unhighlightCriticalPaths(true);
		                			        		   }
		                			        	   }
		                			           },
		                			           {
		                			        	   iconCls : 'togglebutton',
		                			        	   text : '未按时完成的任务',
		                			        	   enableToggle : true,
		                			        	   scope : this,
		                			        	   handler : function(btn) {
		                			        		   if (btn.pressed) {
		                			        			   g.taskStore.filter(function(task) {
		                			        				   return (task.get('EndDate')<new Date())&&(task.get('PercentDone') <100);
		                			        			   });
		                			        		   } else {
		                			        			   g.taskStore.clearFilter();
		                			        		   }
		                			        	   }
		                			           },
		                			           {
		                			        	   iconCls : 'action',
		                			        	   text : '突出任务>7天',
		                			        	   scope : this,
		                			        	   handler : function(btn) {
		                			        		   g.taskStore.getRootNode().cascadeBy(function(task) {
		                			        			   if (Sch.util.Date.getDurationInDays(task.get('StartDate'), task.get('EndDate')) > 7) {
		                			        				   var el =g.getSchedulingView().getElementFromEventRecord(task);
		                			        				   el && el.frame('lime');
		                			        			   }
		                			        		   }, this);
		                			        	   }
		                			           },
		                			           {
		                			        	   iconCls : 'togglebutton',
		                			        	   text : '筛选: 任务进度  < 30%',
		                			        	   scope : this,
		                			        	   enableToggle : true,
		                			        	   toggleGroup : 'filter',
		                			        	   handler : function(btn) { 
		                			        		   if (btn.pressed) {
		                			        			   g.taskStore.filter(function(task) {
		                			        				   return task.get('PercentDone') < 30;
		                			        			   });
		                			        		   } else {
		                			        			   g.taskStore.clearFilter();
		                			        		   }
		                			        	   }
		                			           },
		                			           /** {
                    iconCls : 'togglebutton',
                    text : '级联变化',
                    scope : this,
                    enableToggle : true,
                    handler : function(btn) {
                        g.setCascadeChanges(btn.pressed);
                    }
                },**/
		                			           {
		                			        	   enableToggle : true,
		                			        	   text : '需提醒的任务',
		                			        	   scope : this,
		                			        	   iconCls : 'togglebutton',   
		                			        	   handler : function(btn) {
		                			        		   if (btn.pressed) {
		                			        			   g.taskStore.filter(function(task) {
		                			        				   return  (Sch.util.Date.getDurationInDays(task.get('StartDate'), new Date())>task.get('Duration')/2)&&(Sch.util.Date.getDurationInDays(task.get('StartDate'), new Date())<task.get('Duration'))&&(task.get('PercentDone')<30);                                
		                			        			   });
		                			        		   } else {
		                			        			   g.taskStore.clearFilter();
		                			        		   }
		                			        	   }
		                			           },
		                			           {
		                			        	   iconCls : 'action',
		                			        	   text : '最后任务',
		                			        	   scope : this,

		                			        	   handler : function(btn) {
		                			        		   var latestEndDate = new Date(0),
		                			        		   latest;
		                			        		   g.taskStore.getRootNode().cascadeBy(function(task) {
		                			        			   if (task.get('EndDate') >= latestEndDate) {
		                			        				   latestEndDate = task.get('EndDate');
		                			        				   latest = task;
		                			        			   }
		                			        		   });
		                			        		   g.getSchedulingView().scrollEventIntoView(latest, true);
		                			        	   }
		                			           },{
		                			        	   xtype:'combo',
		                			        	   scope : this,
		                			        	   id:'combo',
		                			        	   width:100,
		                			        	   emptyText:'筛选类型',
		                			        	   fieldStyle:'background:#F5F5F5',
		                			        	   editable:false,
		                			        	   store: Ext.create('Ext.data.Store', {
		                			        		   fields: ['display', 'value'],
		                			        		   data : [    		    
		                			        		           {"display":"任务名称", "value": "1"},
		                			        		           {"display":"员工名称", "value": "2"}	     
		                			        		           ]
		                			        	   }),
		                			        	   queryMode: 'local',
		                			        	   displayField: 'display',
		                			        	   valueField: 'value',
		                			           },
		                			           {
		                			        	   xtype : 'textfield',
		                			        	   emptyText : '搜索...',
		                			        	   fieldStyle:'background:#F5F5F5',
		                			        	   scope : this,
		                			        	   width:150,
		                			        	   //height:20,
		                			        	   padding: '0 0 -20 0',
		                			        	   enableKeyEvents : true,
		                			        	   listeners : {
		                			        		   keypress : {
		                			        			   fn : function(field, e) {
		                			        				   var value   = field.getValue(); 
		                			        				   var type=Ext.getCmp('combo').getValue();
		                			        				   if (value&&type==2) {
		                			        					   console.log(value); 
		                			        					   g.taskStore.filter(function(task) {
		                			        						   return  (task.get('resourcename').indexOf(value)>0);                                
		                			        					   });
		                			        				   } 
		                			        				   else if(value){
		                			        					   g.taskStore.filter('Name', value, true, false);
		                			        				   }else {
		                			        					   g.taskStore.clearFilter();
		                			        				   }
		                			        			   },
		                			        			   scope : this
		                			        		   },
		                			        		   specialkey : {
		                			        			   fn : function(field, e) {
		                			        				   if (e.getKey() === e.ESC) {
		                			        					   field.reset();
		                			        				   }
		                			        				   g.taskStore.clearFilter();
		                			        			   },
		                			        			   scope : this
		                			        		   }
		                			        	   }
		                			           }
		                			           ]
		                		   }],
		                		   lockedGridConfig : {
		                			   width: 370,
		                			   title : '任务表',
		                			   collapsible : true
		                		   },
		                		   schedulerConfig : {
		                			   collapsible : true,
		                			   title : '计划表'
		                		   },
		                		   leftLabelField : {
		                			   dataIndex : 'Name',
		                			   editor : { xtype : 'textfield' }
		                		   },        
		                		   eventRenderer: function(task){
		                			   if(task.data.EndDate.getTime()<today&&task.data.PercentDone<100){
		                				   return {
		                					   style : 'background-color: #EE2C2C'
		                				   };
		                			   }else return{
		                				   style : 'background-color: #008B00'
		                			   }
		                			   /** return {
                        //style : 'background-color: #'+task.data.TaskColor
                        style : 'background-color: #00FF00'
                };**/
		                		   },
		                		   _fullScreenFn : (function() {
		                			   var docElm = document.documentElement;

		                			   if (docElm.requestFullscreen) {
		                				   return "requestFullscreen";
		                			   }
		                			   else if (docElm.mozRequestFullScreen) {
		                				   return "mozRequestFullScreen";
		                			   }
		                			   else if (docElm.webkitRequestFullScreen) {
		                				   return "webkitRequestFullScreen";
		                			   }
		                		   })(),
		                		   plugins:[
		                		            Ext.create("Gnt.plugin.TaskContextMenu"), 
		                		            Ext.create("Sch.plugin.Pan"), 
		                		            Ext.create('Sch.plugin.TreeCellEditing', { 
		                		            	clicksToEdit: 1 ,
		                		            	listeners : {
		                		            		beforeedit : function() { return !Ext.getCmp('readonlybutton').pressed;
		                		            		}
		                		            	}}),  
		                		            	new Gnt.plugin.Printable({
		                		            		printRenderer : function(task, tplData) {
		                		            			if (task.isMilestone()) {
		                		            				return;
		                		            			} else if (task.isLeaf()) {
		                		            				var availableWidth = tplData.width - 4,
		                		            				progressWidth = Math.floor(availableWidth*task.get('PercentDone')/100);

		                		            				return {
		                		            					// Style borders to act as background/progressbar
		                		            					progressBarStyle : Ext.String.format('width:{2}px;border-left:{0}px solid #7971E2;border-right:{1}px solid #E5ECF5;', progressWidth, availableWidth - progressWidth, availableWidth)
		                		            				};
		                		            			} else {
		                		            				var availableWidth = tplData.width - 2,
		                		            				progressWidth = Math.floor(availableWidth*task.get('PercentDone')/100);

		                		            				return {
		                		            					// Style borders to act as background/progressbar
		                		            					progressBarStyle : Ext.String.format('width:{2}px;border-left:{0}px solid #FFF3A5;border-right:{1}px solid #FFBC00;', progressWidth, availableWidth - progressWidth, availableWidth)
		                		            				};
		                		            			}
		                		            		},

		                		            		beforePrint : function(sched) {
		                		            			var v = sched.getSchedulingView();
		                		            			this.oldRenderer = v.eventRenderer;
		                		            			this.oldMilestoneTemplate = v.milestoneTemplate;
		                		            			v.milestoneTemplate = printableMilestoneTpl;
		                		            			v.eventRenderer = this.printRenderer;
		                		            		},

		                		            		afterPrint : function(sched) {
		                		            			var v = sched.getSchedulingView();
		                		            			v.eventRenderer = this.oldRenderer;
		                		            			v.milestoneTemplate = this.oldMilestoneTemplate;
		                		            		}
		                		            	})
		                		            ],
		                		            tooltipTpl : new Ext.XTemplate(
		                		            		'<h4 class="tipHeader">{Name}</h4>',
		                		            		'<table class="taskTip">', 
		                		            		'<tr><td>开始:</td> <td align="right">{[Ext.Date.format(values.StartDate, "y-m-d")]}</td></tr>',
		                		            		'<tr><td>结束:</td> <td align="right">{[Ext.Date.format(values.EndDate, "y-m-d")]}</td></tr>',
		                		            		'<tr><td>进度:</td><td align="right">{PercentDone}%</td></tr>',
		                		            		'</table>'
		                		            ).compile(),
		                		            applyPercentDone : function(value) {
		                		            	this.getSelectionModel().selected.each(function(task) { task.setPercentDone(value); });
		                		            },

		                		            showFullScreen : function() {
		                		            	this.el.down('.x-panel-body').dom[this._fullScreenFn]();
		                		            },
		                		            openTab : function (panel,id){ 
		                		            	var o = (typeof panel == "string" ? panel : id || panel.id); 
		                		            	var main = parent.Ext.getCmp("content-panel"); 
		                		            	var tab = main.getComponent(o); 
		                		            	if (tab) { 
		                		            		main.setActiveTab(tab); 
		                		            	} else if(typeof panel!="string"){ 
		                		            		panel.id = o; 
		                		            		var p = main.add(panel); 
		                		            		main.setActiveTab(p); 
		                		            	} 
		                		            }


	});   
	var vp = Ext.create("Ext.Viewport", {
		id:'viewport',
		layout  : 'border',
		items   : [
		           {
		        	   region      : 'north',
		        	   contentEl   : 'north',
		        	   bodyStyle   : 'padding:0px'
		           },
		           g
		           ]
	});
});
