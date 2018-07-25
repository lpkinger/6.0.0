Ext.define('Ext.calendar.App', {

	requires: [
	           'Ext.Viewport',
	           'Ext.layout.container.Border',
	           'Ext.picker.Date',
	           'Ext.calendar.util.Date',
	           'Ext.calendar.CalendarPanel',
	           'Ext.calendar.data.MemoryCalendarStore',
	           'Ext.calendar.data.MemoryEventStore',
	           'Ext.calendar.data.Events',
	           'Ext.calendar.data.Calendars',
	           'Ext.calendar.form.EventWindow'
	           ],

	           constructor : function() {
	        	   var mm=this;
	        	   this.checkScrollOffset();
	        	   this.calendarStore = Ext.create('Ext.calendar.data.MemoryCalendarStore', {
	        		   data: Ext.calendar.data.Calendars.getData()
	        	   });

	        	   // A sample event store that loads static JSON from a local file. Obviously a real
	        	   // implementation would likely be loading remote data via an HttpProxy, but the
	        	   // underlying store functionality is the same.
	        	   this.eventStore = Ext.create('Ext.calendar.data.MemoryEventStore', {
	        		   data: Ext.calendar.data.Events.getData()
	        	   });

	        	   // This is the app UI layout code.  All of the calendar views are subcomponents of
	        	   // CalendarPanel, but the app title bar and sidebar/navigation calendar are separate
	        	   // pieces that are composed in app-specific layout code since they could be omitted
	        	   // or placed elsewhere within the application.
	        	   Ext.create('Ext.Viewport', {
	        		   layout: 'border',
	        		   renderTo: 'calendar-ct',
	        		   items: [{
	        			   id: 'app-header',
	        			   region: 'north',
	        			   height: 35,
	        			   border: false,
	        			   contentEl: 'app-header-content'
	        		   },{
	        			   id: 'app-center',
	        			   title: '...', // will be updated to the current view's date range
	        			   region: 'center',
	        			   layout: 'border',
	        			   listeners: {
	        				   'afterrender': function(){
	        					   Ext.getCmp('app-center').header.addCls('app-center-header');
	        				   }
	        			   },
	        			   items: [{
	        				   id:'app-west',
	        				   region: 'west',
	        				   width: 179,
	        				   border: false,
	        				   baseCls:'ext-cal-west',
	        				   items: [{
	        					   xtype: 'datepicker',
	        					   id: 'app-nav-picker',
	        					   cls: 'ext-cal-nav-picker',
	        					   listeners: {
	        						   'select': {
	        							   fn: function(dp, dt){
	        								   Ext.getCmp('app-calendar').setStartDate(dt);
	        							   },
	        							   scope: this
	        						   }
	        					   }
	        				   }]
	        			   },{
	        				   xtype: 'calendarpanel',
	        				   eventStore: this.eventStore,
	        				   calendarStore: this.calendarStore,
	        				   border: false,
	        				   id:'app-calendar',
	        				   region: 'center',
	        				   activeItem: 3, // month view
	        				   monthViewCfg: {
	        					   showHeader: true,
	        					   showWeekLinks: true,
	        					   showWeekNumbers: true
	        				   },
	        				   listeners: {
	        					   'eventclick': {
	        						   fn: function(vw, rec, el){
	        							   this.showEditWindow(rec, el);
	        							   this.clearMsg();
	        						   },
	        						   scope: this
	        					   },
	        					   'eventover': function(vw, rec, el){
	        						   /*   if(rec.data.Remark!=null && rec.data.Remark!='' ){
	        						   var tip=Ext.create('Ext.tip.ToolTip', {
	        						        target:el,    					        
	        						        trackMouse: true,
	        						        renderTo: Ext.getBody(),
	        						        html:'<div style="font-size:12px;color:#6E8B3D" >'+rec.data.Remark+'</div>',
	        						        bodyStyle: {
	        						            background: '#F5FFFA',
	        						            padding: '10px',
	        						            border :'0 0 0 0'
	        						        },
	        						        buttons: [
  { text: 'Button 1' }
]
	        						    });
	        						   tip.show();
	        						   }*/
	        		/*			   var tip=Ext.create('Ext.tip.ToolTip', {
       						        target:el,    					        
    						        trackMouse: true,
    						        renderTo: Ext.getBody(),
    						        html:'<div style="font-size:12px;color:#6E8B3D" >'+rec.data.Remark+'</div>',
    						        bodyStyle: {
    						            background: '#F5FFFA',
    						            padding: '10px',
    						            border :'0 0 0 0'
    						        },
    						        buttons: [{ text: 'Button 1' }]
    						      });*/
	        						   var conf=mm.menuConfig(rec);
	        						   if(menu==null){
	       	    						menu= Ext.create('Ext.menu.Menu', {
	       	    							async:false, 
	       	    							id: 'mainMenu',    
	       	    							ownerCt : this.ownerCt,
	       	    							width:300,
	       	    							renderTo:Ext.getBody(),
	    	    							style: {
	    	    								overflow: 'visible', 
	    	    							},	       	    					
	       	    							items:[{
	       	    								xtype:'panel',	       	    							
		        						        width:300,
	       	    								items:[{
	       	    									html:'<div id="RemarkId" style="font-size:12px;color:#6E8B3D;" >'+rec.data.Remark+'</div>',
	       	    								}],
	       	    							 dockedItems: [{ 
   								        	  buttonAlign:'center',
   								        	  xtype: 'toolbar',
   								        	  dock: 'bottom',
   								        	  style: 'background:#EEE9BF; padding:0px;',
   								        	  items: [{  
   								        		  xtype: 'button', 
   								        		  text: conf.text,//'<font color="blue">拜访报告</font>',
   								        		  iconCls: 'x-button-icon-add',
   								        		  style :'margin-left:100px',
   								        		  disabled:rec.data.CalendarId==2,
   								        		  handler:function(btn){
   								        			  menu=null;
   								        			  Ext.getCmp('mainMenu').destroy();
   								        			var win =Ext.create('Ext.window.Window',{  
   														id : 'singlewin',
   														height : window.innerHeight*0.9,
   														width : window.innerWidth*0.9,
   														maximizable : true,
   														buttonAlign : 'center',
   														layout : 'anchor',
   														title:conf.title,//'创建拜访记录',
   														items : [{
   															frame : true,
   															anchor : '100% 100%',
   															layout : 'fit',
   															html : conf.html
   															//html : '<iframe id="iframe_form" src="'+basePath+'jsps/crm/customermgr/customervisit/visitRecord.jsp?taskId='+rec.data.EventId+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'					
   														     }]
   													    });
   													win.show();
   								        		  }
   								        	  }]
   								             }]		        						        
	       	    							}]
	       	    						});  
	       	    					}
	       	    					else {
	       	    						document.getElementById('RemarkId').innerHTML=rec.data.Remark;
	       	    					    menu.items.items[0].dockedItems.items[0].items.items[0].setHandler(conf.handler);
	       	    					    menu.items.items[0].dockedItems.items[0].items.items[0].setText(conf.text);
	       	    					    menu.items.items[0].dockedItems.items[0].items.items[0].setDisabled(rec.data.CalendarId==2);
	       	    					}
	       	    					menu.alignTo(el, 'tl-bl?',[280, 0]);
	       	    					menu.show();	        					   
	        					   },
	        					   'eventout': function(vw, rec, el){
	        						   //console.log('Leaving evt rec='+rec.data.Title+', view='+ vw.id +', el='+el.id);
	        					   },
	        					   'eventadd': {
	        						   fn: function(cp, rec){
	        							   this.showMsg('任务 '+ rec.data.Title +' 添加成功!');
	        						   },
	        						   scope: this
	        					   },
	        					   'eventupdate': {
	        						   fn: function(cp, rec){
	        							   this.showMsg('任务 '+ rec.data.Title +' 修改成功!');
	        						   },
	        						   scope: this
	        					   },
	        					   'eventcancel': {
	        						   fn: function(cp, rec){
	        							   // edit canceled
	        						   },
	        						   scope: this
	        					   },
	        					   'viewchange': {
	        						   fn: function(p, vw, dateInfo){
	        							   if(this.editWin){
	        								   this.editWin.hide();
	        							   }
	        							   if(dateInfo){
	        								   // will be null when switching to the event edit form so ignore
	        								   Ext.getCmp('app-nav-picker').setValue(dateInfo.activeDate);
	        								   this.updateTitle(dateInfo.viewStart, dateInfo.viewEnd);
	        							   }
	        						   },
	        						   scope: this
	        					   },
	        					   'dayclick': {
	        						   fn: function(vw, dt, ad, el){
	        							   this.showEditWindow({
	        								   StartDate: dt,
	        								   IsAllDay: ad
	        							   }, el);
	        							   this.clearMsg();
	        						   },
	        						   scope: this
	        					   },
	        					   'rangeselect': {
	        						   fn: function(win, dates, onComplete){
	        							   this.showEditWindow(dates);
	        							   this.editWin.on('hide', onComplete, this, {single:true});
	        							   this.clearMsg();
	        						   },
	        						   scope: this
	        					   },
	        					   'eventmove': {
	        						   fn: function(vw, rec){
	        							   var mappings = Ext.calendar.data.EventMappings,
	        							   time = rec.data[mappings.IsAllDay.name] ? '' : ' \\a\\t g:i a';                                

	        							   var error =this.updateTask(rec);
	        							   if(error==null || error==''){
	        								   rec.commit();
	        								   this.showMsg('任务 '+ rec.data[mappings.Title.name] +' 移动到 '+
	        										   Ext.Date.format(rec.data[mappings.StartDate.name], ('F jS'+time)));
	        							   }else this.showMsg(error);
	        						   },
	        						   scope: this
	        					   },
	        					   'eventresize': {
	        						   fn: function(vw, rec){
	        							   rec.commit();
	        							   this.showMsg('任务 '+ rec.data.Title +' 修改成功!');
	        						   },
	        						   scope: this
	        					   },
	        					   'eventdelete': {
	        						   fn: function(win, rec){
	        							   this.eventStore.remove(rec);
	        							   this.showMsg('任务 '+ rec.data.Title +' 删除成功!');
	        						   },
	        						   scope: this
	        					   },
	        					   'initdrag': {
	        						   fn: function(vw){
	        							   if(this.editWin && this.editWin.isVisible()){
	        								   this.editWin.hide();
	        							   }
	        						   },
	        						   scope: this
	        					   }
	        				   }
	        			   }]
	        		   }]
	        	   });
	           },
	           showEditWindow : function(rec, animateTarget){
	        	   var me=this;
	        	   if(rec.StartDate && rec.StartDate<new Date()){
	        		   Ext.defer(function(){
	        				   me.showMsg("新增任务日期不能小于当前日期!");
	        			   },100);
	        		   return;
	        	   } 
	        	   if(!this.editWin){
	        		   this.editWin = Ext.create('Ext.calendar.form.EventWindow', {
	        			   calendarStore: this.calendarStore,
	        			   listeners: {
	        				   'eventadd': {
	        					   fn: function(win, rec){
	        						   win.hide();
	        						   rec.data.IsNew = false;
	        						   var turn=this.saveTask(rec);
	        						   if(turn.id){
	        							   rec.data.manuallyscheduled=turn.manuallyscheduled;
	        							   rec.data.tasktype=turn.tasktype;
	        							   rec.data.prjplanid=turn.prjplanid;
	        							   rec.data.EventId=turn.id;
	        							   rec.data.taskid=turn.id;
		        						   this.eventStore.add(rec);
		        						   this.eventStore.sync(); 		        						
		        						   this.showMsg('任务 '+ rec.data.Title +' 添加成功!');
	        						   }else {
	        							   win.commit=false;
		        						   this.showMsg(turn.backerror);
		        						   win.hide();
	        						   }
	        						   
	        					   },
	        					   scope: this
	        				   },
	        				   'eventupdate': {
	        					   fn: function(win, rec){
	        						   var error=this.updateTask(rec);
	        						   if(error==null || error==''){
	        							   rec.commit();
	        							   this.eventStore.sync();
	        							   this.showMsg('任务 '+ rec.data.Title +' 修改成功!');
	        						   }else {
	        							win.commit=false;
	        						   this.showMsg(error);}
	        						   win.hide();
	        					   },
	        					   scope: this
	        				   },
	        				   'eventdelete': {
	        					   fn: function(win, rec){
	        						  var error=this.deleteTask(rec);
	        						   if(error==null || error==''){
	        							   this.eventStore.remove(rec);
		        						   this.eventStore.sync();
		        						   this.showMsg('任务 '+ rec.data.Title +' 删除成功!');
	        						   }else {
	        						    this.showMsg(error);
	        						   }
	        						   win.hide();
	        						
	        					   },
	        					   scope: this
	        				   },
	        				   'editdetails': {
	        					   fn: function(win, rec){
	        						   win.hide();
	        						   Ext.getCmp('app-calendar').showEditForm(rec);
	        					   }
	        				   }
	        			   }
	        		   });
	        	   }
	        	   this.editWin.show(rec, animateTarget);
	           },

	           // The CalendarPanel itself supports the standard Panel title config, but that title
	           // only spans the calendar views.  For a title that spans the entire width of the app
	           // we added a title to the layout's outer center region that is app-specific. This code
	           // updates that outer title based on the currently-selected view range anytime the view changes.
	           updateTitle: function(startDt, endDt){
	        	   var p = Ext.getCmp('app-center'),
	        	   fmt = Ext.Date.format;

	        	   if(Ext.Date.clearTime(startDt).getTime() == Ext.Date.clearTime(endDt).getTime()){
	        		   p.setTitle(fmt(startDt, 'F j, Y'));
	        	   }
	        	   else if(startDt.getFullYear() == endDt.getFullYear()){
	        		   if(startDt.getMonth() == endDt.getMonth()){
	        			   p.setTitle(fmt(startDt, 'F j') + ' - ' + fmt(endDt, 'j, Y'));
	        		   }
	        		   else{
	        			   p.setTitle(fmt(startDt, 'F j') + ' - ' + fmt(endDt, 'F j, Y'));
	        		   }
	        	   }
	        	   else{
	        		   p.setTitle(fmt(startDt, 'F j, Y') + ' - ' + fmt(endDt, 'F j, Y'));
	        	   }
	           },

	           // This is an application-specific way to communicate CalendarPanel event messages back to the user.
	           // This could be replaced with a function to do "toast" style messages, growl messages, etc. This will
	           // vary based on application requirements, which is why it's not baked into the CalendarPanel.
	           showMsg: function(msg){
	        	   Ext.fly('app-msg').update(msg).removeCls('x-hidden');
	           },
	           clearMsg: function(){
	        	   Ext.fly('app-msg').update('').addCls('x-hidden');
	           },
	           menuConfig:function(rec){
		        	var conf=new Object();
		        	if(rec.data.tasktype=='researchtask'){
		        		conf.text='<font color="blue">调研报告</font>';
		        		conf.title='创建调研报告';
		        		conf.html='<iframe id="iframe_form" src="'+basePath+'jsps/crm/customermgr/customervisit/visitRecord.jsp?taskId='+rec.data.taskid||rec.data.EventId+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>';
		        		conf.handler=function(btn){
		        					menu=null;
				        			Ext.getCmp('mainMenu').destroy();
				        			var win =Ext.create('Ext.window.Window',{  
										id : 'singlewin',
										height : window.innerHeight*0.9,
										width : window.innerWidth*0.9,
										maximizable : true,
										buttonAlign : 'center',
										layout : 'anchor',
										title:'创建调研报告',
										items : [{
											frame : true,
											anchor : '100% 100%',
											layout : 'fit',
											html : '<iframe id="iframe_form" src="'+basePath+'jsps/crm/marketmgr/marketresearch/researchReport.jsp?whoami='+rec.data.manuallyscheduled+'&cond=idIS'+rec.data.taskid||rec.data.EventId+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'					
										     }]
									    });
									win.show();
				        		  };
		        	}
		        	if(rec.data.tasktype=='agendatask'){
		        		conf.text='<font color="blue">拜访报告</font>';
		        		conf.title='创建拜访记录';
		        		conf.html='<iframe id="iframe_form" src="'+basePath+'jsps/crm/customermgr/customervisit/visitRecord.jsp?taskId='+rec.data.taskid||rec.data.EventId+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>';
		        		conf.handler=function(btn){
		        			  menu=null;
			        			  Ext.getCmp('mainMenu').destroy();
			        			var win =Ext.create('Ext.window.Window',{  
									id : 'singlewin',
									height : window.innerHeight*0.9,
									width : window.innerWidth*0.9,
									maximizable : true,
									buttonAlign : 'center',
									layout : 'anchor',
									title:'创建拜访记录',
									items : [{
										frame : true,
										anchor : '100% 100%',
										layout : 'fit',
										html : '<iframe id="iframe_form" src="'+basePath+'jsps/crm/customermgr/customervisit/visitRecord.jsp?taskId='+rec.data.taskid||rec.data.EventId+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'					
									     }]
								    });
								win.show();
			        		  };
		        	}
		        	return conf;
		           },
	           checkScrollOffset: function() {
	        	   var scrollbarWidth = Ext.getScrollbarSize ? Ext.getScrollbarSize().width : Ext.getScrollBarWidth();

	        	   // We check for less than 3 because the Ext scrollbar measurement gets
	        	   // slightly padded (not sure the reason), so it's never returned as 0.
	        	   if (scrollbarWidth < 3) {
	        		   Ext.getBody().addCls('x-no-scrollbar');
	        	   }
	        	   if (Ext.isWindows) {
	        		   Ext.getBody().addCls('x-win');
	        	   }
	           },
	           saveTask:function(rec){
	        	   var me=this,r=me.formatData(rec.data),params=new Object(),turn=new Object();
	        	   params.formStore = unescape(escape(Ext.JSON.encode(r)));
	        	   Ext.Ajax.request({
	        		   url : basePath + 'plm/task/saveAgenda.action',
	        		   params : params,
	        		   async:false,
	        		   method : 'post',
	        		   callback : function(options,success,response){
	        			   var localJson = new Ext.decode(response.responseText);
	        			   if(localJson.success){
	        				  turn.id= localJson.id;
	        				  turn.manuallyscheduled= localJson.manuallyscheduled;
	        				  turn.tasktype= localJson.tasktype;
	        				  turn.prjplanid= localJson.prjplanid;
	        			   } else if(localJson.exceptionInfo){
	        				   var str = localJson.exceptionInfo;
	        				   turn.backerror=str;
	        			   } else{
	        				   turn.backerror='操作失败!';
	        			   }
	        		   }

	        	   });
	        	   return turn;
	           },
	           updateTask:function(rec){
	        	   var me=this,r=me.formatData(rec.data),params=new Object(),backerror=null;
	        	   r.id=rec.data.taskid||rec.data.EventId;
	        	   if(mid!=r.prjplanid){
	        		   showError('任务对应的不是该项目,不能进行更新操作!');
	        		   return '操作失败!';
	        	   }
	        	   
	        	   params.formStore = unescape(escape(Ext.JSON.encode(r)));
	        	   Ext.Ajax.request({
	        		   url : basePath + 'plm/task/updateAgenda.action',
	        		   params : params,
	        		   async:false,
	        		   method : 'post',
	        		   callback : function(options,success,response){
	        			   var localJson = new Ext.decode(response.responseText);
	        			   if(localJson.success){
	        			   } else if(localJson.exceptionInfo){
	        				   var str = localJson.exceptionInfo;
	        				   backerror=str;
	        			   } else{
	        				   backerror='操作失败!';
	        			   }
	        		   }

	        	   });
	        	   return backerror;
	           },
	           deleteTask:function(rec){
	        	   var me=this,r=me.formatData(rec.data),backerror=null;
	        	   if(mid!=r.prjplanid){
	        		   showError('任务对应的不是该项目,不能进行删除操作!');
	        		   return '操作失败!';
	        	   }
	        	   Ext.Ajax.request({
	        		   url : basePath + 'plm/task/deleteAgenda.action',
	        		   params :{
	        			   id:rec.get('taskid')||rec.get('EventId'),
	        		   },
	        		   async:false,
	        		   method : 'post',
	        		   callback : function(options,success,response){
	        			   var localJson = new Ext.decode(response.responseText);
	        			   if(localJson.success){
	        			   } else if(localJson.exceptionInfo){
	        				   backerror = localJson.exceptionInfo;
	        			   } else{
	        				   backerror='保存失败!';
	        			   }
	        		   }

	        	   });
	        	   return backerror;
	           },
	           formatData:function(data){
	        	   return {
	        		   //type:data.CalendarId,
	        		   enddate:Ext.Date.format(data.EndDate,'Y-m-d H:i:s'),
	        		   name:data.Title,
	        		   startdate:Ext.Date.format(data.StartDate,'Y-m-d H:i:s'),
	        		   resourcecode:emcode,
	        		   prjplanid:data.prjplanid?data.prjplanid:mid,
	        		   description:data.description,
	        		   responsible:data.IsAllDay,
	        		   resizable:data.TimeSet,
	        		   caller:caller
	        	   };
	           }
},
function() {
	/*
	 * A few Ext overrides needed to work around issues in the calendar
	 */

	Ext.form.Basic.override({
		reset: function() {
			var me = this;
			me.getFields().each(function(f) {
				f.reset();
			});
			return me;
		}
	});
	Ext.data.MemoryProxy.override({
		updateOperation: function(operation, callback, scope) {
			operation.setCompleted();
			operation.setSuccessful();
			Ext.callback(callback, scope || me, [operation]);
		},
		create: function() {
			this.updateOperation.apply(this, arguments);
		},
		update: function() {
			this.updateOperation.apply(this, arguments);
		},
		destroy: function() {
			this.updateOperation.apply(this, arguments);
		}
	});
});