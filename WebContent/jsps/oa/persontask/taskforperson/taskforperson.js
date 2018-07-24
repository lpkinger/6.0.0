Ext.ns('App');

//Ext.Loader.setConfig({ enabled : true, disableCaching : true });
//Ext.Loader.setPath('Sch', '../../js/Sch');

//Ext.require([
//    'Sch.panel.SchedulerGrid'
//]);
Ext.onReady(function() {
        function getUrlParam(name){   
		     var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
		     var r=window.location.search.substr(1).match(reg);   
		     if  (r!=null)   return decodeURI(r[2]); 
		     return   null;   
		}
		var prjplanid=getUrlParam("prjplanid");
		 var resourcedata=null;
          eventdata=null;
          Ext.Ajax.request({
    		url : basePath + "plm/resource/getResourceData.action",
    		params:{
    			id:prjplanid
    		},
    		method : 'get',
    		callback : function(options,success,response){
    			var rs = new Ext.decode(response.responseText);
    			if(rs.exceptionInfo){
    				showError(rs.exceptionInfo);return;
    			}
    			if(rs.success){
    			resourcedata=rs.data.resource;
    			eventdata=rs.data.event;
    			}
    		}
    	});
    var fiscalYear = {
        displayDateFormat : 'Y-m-d',
        shiftIncrement : 1,
        shiftUnit : "YEAR",
        timeColumnWidth : 90,
        timeResolution : {
            unit : "MONTH",
            increment : 1
        },
        headerConfig : {
            bottom : {
                unit : "MONTH",
                dateFormat : 'M Y'
            },
            middle: {
                unit : "QUARTER",
                renderer : function(start, end, cfg) {
                    var quarter = Math.floor(start.getMonth() / 3) + 1,
                        fiscalQuarter = quarter === 4 ? 1 : (quarter + 1);
                        
                    return Ext.String.format('FQ{0} {1}', fiscalQuarter, start.getFullYear() + (fiscalQuarter === 1 ? 1 : 0));
                }
            },
            top : {
                unit : "YEAR",
                cellGenerator : function(viewStart, viewEnd) {
                    var cells = [];
                        
                    // Simplified scenario, assuming view will always just show one US fiscal year
                    return [{
                        start : viewStart,
                        end : viewEnd,
                        header : 'Fiscal Year ' + (viewStart.getFullYear() + 1)
                    }];
                }
            }
        }
    };    
    Sch.preset.Manager.registerPreset("fiscalYear", fiscalYear);
    App.Scheduler.init(resourcedata,eventdata);
});
App.Scheduler = {
    
    // Initialize application
    init : function() {
     function getUrlParam(name){   
		     var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
		     var r=window.location.search.substr(1).match(reg);   
		     if  (r!=null)   return decodeURI(r[2]); 
		     return   null;   
		}
		var prjplanid=getUrlParam("prjplanid");
        Ext.define('MyModel', { 
            extend: 'Sch.model.Event', 
            //fields : ['PercentAllocated'] 
            fields:['Type','PercentAllocated','Name']
        });

         Ext.define('MyResource', { 
            extend: 'Sch.model.Resource', 
            fields : ['LikesBacon', 'LikesChrome', 'LikesIE6'] 
        });
        Ext.Ajax.request({
    		url : basePath + "plm/resource/getResourceData.action",
    		params:{
    			id:prjplanid
    		},
    		method : 'get',
    		callback : function(options,success,response){
    			var rs = new Ext.decode(response.responseText);
    			if(rs.exceptionInfo){
    				showError(rs.exceptionInfo);return;
    			}
    			if(rs.success){
    			resourcedata=rs.data.resource;
    			eventdata=rs.data.event;
    			var resourceStore = Ext.create('Sch.data.ResourceStore', {
            model : 'MyResource',
           
            data:resourcedata
        });
        var summaryCol = Ext.create("Sch.plugin.SummaryColumn", { 
            header : '工时总和(d/h)', 
            width: 100,
            showPercent : false 
        });
          var summaryCol2 = Ext.create("Sch.plugin.SummaryColumn", { 
            header : '区间所占比率(%)', 
            showPercent : true,
            align : 'center',
            width: 100,
            renderer : function(j, a, f) {
				var h = this.scheduler, k = this.eventStore, e = h.getStart(), i = h
						.getEnd(), c = 0, b = this.calculate(f.getEvents(), e,
						i);
				if (b <= 0) {
					return ""
				}
				if (this.showPercent) {
					var d = Sch.util.Date.getDurationInMinutes(e, i);
					return (Math.round((b * 100) / d)) + " %"
				} else {
					if (b > 1440) {
						return (b / 1440).toFixed(this.nbrDecimals) + " "
								+ Sch.util.Date.getShortNameOfUnit("DAY")
					}
					if (b >= 30) {
						return (b / 60).toFixed(this.nbrDecimals) + " "
								+ Sch.util.Date.getShortNameOfUnit("HOUR")
					}
					return b + " " + Sch.util.Date.getShortNameOfUnit("MINUTE")
				}
			}
        });
      var  eventStore = Ext.create('Sch.data.EventStore', {
            model : MyModel,
            data:eventdata
        });
      
          var sched = Ext.create("Sch.panel.SchedulerGrid", {
             id:'sched',
            title : '员工任务安排情况',
            border : true,
            readOnly:true,
            viewPreset: 'weekAndDayLetter',
            renderTo : Ext.getBody(),
            //viewPreset : 'fiscalYear',
            startDate : new Date(2012, 3, 1),
            endDate : new Date(2012, 12, 1),
            rowHeight : 40,
            columnLines     : true,
            split : true,
            viewConfig : {
								forceFit : false
							},
            eventBodyTemplate : new Ext.XTemplate(
               '<div class="sch-percent-allocated-bar" style="height:{PercentAllocated}% " ></div><span class="sch-percent-allocated-text">{[values.PercentAllocated||0]}%</span>'
            ).compile(),
            //eventRenderer : function(ev, res, tplData) {
               // tplData.cls = ev.get('Type') || 'Task';
             //  return ev.getName(); 
           // },
            // Define static columns
            columnLines:true,
            columns : [
               {header : '成员名称', width:100, dataIndex : 'Name'},
                summaryCol,summaryCol2
            ],
            tbar : [
                {   text:'上一阶段',
                    iconCls : 'icon-previous',
                    handler : function() {
                        sched.shiftPrevious();
                    }
                },
                {   text:'下一阶段',
                    iconCls : 'icon-next',
                    handler : function() {
                        sched.shiftNext();
                    }
                },
                {
                    text : '时',
                    toggleGroup : 'presets',
                    enableToggle : true,
                    pressed : true,
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('hourAndDay', new Date(2012, 9, 1, 8), new Date(2012, 9, 1, 18));
                    }
                },
                {
                    text : '天',
                    toggleGroup : 'presets',
                    enableToggle : true,
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('weekAndDay', new Date(2012, 9, 1), new Date(2012, 9, 21));
                    }
                },
                {
                    text : '周/月',
                    toggleGroup : 'presets',
                    enableToggle : true,
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('weekAndMonth');
                    }
                },
                {
                    text : '周/日',
                    toggleGroup : 'presets',
                    enableToggle : true,
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('weekAndDayLetter');
                    }
                },
                {
                    text : '周/日/月',
                    toggleGroup : 'presets',
                    enableToggle : true,
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('weekDateAndMonth');
                    }
                },
                {
                    text : '月/年',
                    toggleGroup : 'presets',
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('monthAndYear');
                    }
                },
                {
                    text : '年',
                    enableToggle : true,
                    toggleGroup : 'presets',
                    iconCls : 'icon-calendar',
                    handler : function() {
                        sched.switchViewPreset('year', new Date(2012, 0, 1), new Date(2015, 0, 1));
                    }
                }
            ],
            //tooltipTpl:'hahah',
             plugins : [summaryCol,summaryCol2],
            resourceStore : resourceStore,
            eventStore : eventStore
        });
        sched.on('eventmouseenter',function(view,Record,e, eOpts){
         view.tip = Ext.create('Ext.tip.ToolTip', {
         bodyStyle: {
           background: '#C5C1AA'
        },
       bodyCls :'sch-alert',
        target: view.el,
        delegate: view.itemSelector,
        trackMouse: true,
        renderTo: Ext.getBody(),
        listeners: {
            beforeshow: function updateTipBody(tip) {
                tip.update('在进行任务:   "' + Record.data.Name + '"');
            }
        }
    });
        });
          var vp = Ext.create("Ext.Viewport", {
            layout  : 'border',
            items   : [
                {
                     region      : 'north',
                     contentEl   : 'north',
                     bodyStyle   : 'padding:0px'
                    //anchor:'100% 100%',
                }
                //anchor:'100% 100%',
              // sched
            ]
        });
    			}
    		}
    	});
    }
};
