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
	        		     projectplandate=rs.data.prjplandata[0].prjplan_startdate;	        		   
	        		     }
	        		      }
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
                {name:'cost',type:'string'},
                {name:'budget',type:'string'},
                {name:'currency',type:'int'},
                {name:'id',type:'int'}
            ]
        });
    var printableMilestoneTpl = new Gnt.template.Milestone({
        prefix : 'foo',
        printable : true,
        imgSrc : 'images/milestone.png'
    });
         var taskStore = Ext.create("Gnt.data.TaskStore", {
            model: 'MyTaskModel',
            sorters : 'StartDate',
            proxy : {
                type : 'ajax',
                headers : { "Content-Type" : 'application/json' },
                extraParams :{
                condition:formCondition,
                },
                api: {
                    read:      basePath+'plm/gantt.action',
                    create:     basePath+'plm/ganttcreate.action',
                    destroy:    'webservices/Tasks.asmx/Delete',
                    update:     basePath+'plm/ganttupdate.action',
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
                }
            }
        });
    var g = Ext.create('Gnt.panel.Gantt', {
    	id:'gantt',
    	height:300,
    	layout:'anchor',
        //region          : 'center',
        selModel        : new Ext.selection.TreeModel({ ignoreRightMouseSelection : false, mode : 'MULTI'}),
        columnLines     : true,
        loadMask: true,
        viewPreset: 'weekAndDayLetter',
        taskStore : taskStore,   
    columns : [
                  { 
                   xtype:'wbscolumn',
                   header:'编号',
                   columnWidth:3.5,
                
                },
                {
                    xtype : 'treecolumn',
                    header: '任务',
                    sortable: true,
                    dataIndex: 'Name',
                 
                    flex:1,
                   
                    renderer : function(v, meta, r) {
                        if (!r.data.leaf) meta.tdCls = 'sch-gantt-parent-cell';
                        return v;
                    }
                },
                {   
                    header:'开始时间' ,
                   dataIndex:'StartDate',
                    renderer:function(val,meta,record){
                     return Ext.util.Format.date(val,'Y-m-d');
                   }
                },
                {
                   header:'结束时间',
                   dataIndex:'EndDate',
                    renderer:function(val,meta,record){
                     return Ext.util.Format.date(val,'Y-m-d');
                   } 
                },{
                   header:'消费预测',
                   dataIndex:'budget',
                   format:'0.00',
                   xtype:'numbercolumn',
                   renderer:function(val,meta,record){
                     if(val==0) return null;
                     else{
                          switch(record.data.currency){
                          case 0: return '￥'+Ext.util.Format.number(val,'0,000.00') ;
                          case 1: return  '$'+Ext.util.Format.number(val,'0,000.00'); 
                          }
                          
                     } 
                    
                   },
                   field:{
                   }    
                }, {
                   header:'实际消费',
                   dataIndex:'cost',
                   xtype:'numbercolumn',  
                    renderer:function(val,meta,record){
                     if(val==0) return null;
                      else{
                          switch(record.data.currency){
                          case 0: return '￥'+Ext.util.Format.number(val,'0,000.00') ;
                          case 1: return  '$'+Ext.util.Format.number(val,'0,000.00'); 
                          }   
                     } 
                    },
                   field:{
                   }    
                }, {
                    header:'币别',
                    dataIndex:'currency',
                    renderer:function(val, meta, record){
                      if(val==0) return '人名币';
                      else if(val==1) return '美元';
                    },
                    field:{
                      xtype:'combo',
                      editable:false,
                      displayField:'display',
                      valueField:'value',
                      value:0,      
                      store:{
                       fields:['display','value'],
                       data:[{display:'人民币',value:0},
                       {display:'美元',value:1},        
                      ]      
                  },
                    }
                }, {
                    header: '消费情况',
                     tdCls: 'sch-column-color',
                    renderer:function(val,meta,record){
                    if(record.data.cost>record.data.budget){
                    return '<div class="color-column-inner" style="background-color:red" align="center">&nbsp;</div>';
                    }else return  '<div class="color-column-inner" style="background-color:green" align="center">&nbsp;</div>';
                    }           
                }             
            ],      
        tbar : [{
                xtype: 'buttongroup',
                title: '操作区',
                columns: 6, 
                items: [                 
               {
                    iconCls : 'togglebutton',
                    enableToggle: true,
                    id : 'readonlybutton',
                    text: '只读',
                    pressed: true,
                    handler: function () {
                        if(em_id=='10000011'){
                        g.setReadOnly(this.pressed);
                        Ext.getCmp('savebutton').setDisabled(this.pressed);
                        }else{
                        this.pressed=true;
                        g.setReadOnly(this.pressed);
                        showError('对不起,你没有权限修改!');
                        }
                    },
                    listeners: {
                     beforerender: { //bind to the underlying el property on the panel
                                fn: function(){
                                  if(em_id!='10000011'){
                                    this.enableToggle=false;
                                  }
                                 }
                                }
                    }
                },{
                         iconCls : 'x-advance-print',
                         text : '打印',
                         handler : function() {
                           Ext.getCmp('gantt').zoomToFit();
                          Ext.getCmp('gantt').print();
                         }
                     },
                     {
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
                         var options={},
                             me=g.taskStore;
                            var toCreate  = me.getNewRecords(),
                            toUpdate  = me.getUpdatedRecords(),
                            toDestroy = me.getRemovedRecords(),
                                needsSync = false;
                            if (toCreate.length > 0) {
                            var create=null;
                            options.create = toCreate;
                            var index = 0;
                            var jsonData=new Array();
                            for(var i=0;i<toCreate.length;i++){
                            var data = toCreate[i].data;
                            jsonData[index++] = Ext.JSON.encode(data)+'#@';
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
                               for(var i=0;i<toUpdate.length;i++){
                              var data = toUpdate[i].data;
                             jsonData[index++] = Ext.JSON.encode(data)+'#@';
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
                              var data = toDestroy[i].data;
                             jsonData[index++] = Ext.JSON.encode(data)+'#@';
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
                            var gridCondition=getUrlParam("gridCondition");
                             	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition + '&gridCondition=' + gridCondition;                    
         			}
                 },{
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
                },  {
                    xtype : 'textfield',
                    emptyText : '搜索...',
                    scope : this,
                    width:150,
                    //height:20,
                    padding: '0 0 -20 0',
                    enableKeyEvents : true,
                    listeners : {
                        keyup : {
                            fn : function(field, e) {
                                var value   = field.getValue();
                                    
                                if (value) {
                                    g.taskStore.filter('Name', field.getValue(), true, false);
                                } else {
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
                ]}
        ],
         lockedGridConfig : {
                //layout:'column',
                //region:'center',
                anchor:'100% 100%',
                title : '任务表',
            useArrows: true,
            rootVisible: false,
            autoScroll:true
            },
          schedulerConfig : {
                //hidden:true,
               height: '0',
               collapsible : true,
                title : '计划表'
            },

            leftLabelField : {
                dataIndex : 'Name',
                editor : { xtype : 'textfield' }
            },        
          eventRenderer: function(task){
                return {
                        style : 'background-color: #'+task.data.TaskColor
                };
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
    } ,
    // Experimental, not X-browser
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
    })()

    
    });   
     var vp = Ext.create("Ext.Viewport", {
             id:'viewport',
            layout  : 'fit',
            items   : [
              /**  {
                     region      : 'north',
                     contentEl   : 'north',
                     bodyStyle   : 'padding:0px'
                },**/
                g
            ]
        });
});
