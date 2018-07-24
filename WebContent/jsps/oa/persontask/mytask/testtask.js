Ext.onReady(function() {
    SchedulerDemo.init();
});

SchedulerDemo = {
    
    // Initialize application
    init : function() {
        
        Ext.define('MyResource', {
            extend: 'Sch.model.Resource',
            idProperty : 'EmCode', 
            nameField : 'Name',
            fields: [
                'EmCode',
                'Name',
                'Id' ,
                'EmId'
            ]
        });

       /* var resourceStore = Ext.create('Sch.data.ResourceStore', {
            model : 'MyResource',
            data : [
                {"Id":3097,"Name":"周袁","EmCode":"A016","EmId":3014},
                {"Id":3099,"Name":"马丹","EmCode":"A014","EmId":3012},
                {"Id":3101,"Name":"李瑞娟","EmCode":"A012","EmId":3010},
                {"Id":3103,"Name":"陈劲松","EmCode":"A006","EmId":3004},
                {"Id":3105,"Name":"陈正亮","EmCode":"A003","EmId":3001},
                {"Id":3113,"Name":"胡兴文","EmCode":"A005","EmId":3003},
                {"Id":3221,"Name":"姚振兴","EmCode":"A011","EmId":3009},
                {"Id":3096,"Name":"彭龙","EmCode":"A017","EmId":3015},
                {"Id":3098,"Name":"应鹏","EmCode":"A015","EmId":3013},
                {"Id":3100,"Name":"游凡","EmCode":"A013","EmId":3011},
                {"Id":3102,"Name":"钟燕玲","EmCode":"A007","EmId":3005},
                {"Id":3104,"Name":"陈正明","EmCode":"A008","EmId":3006},
                {"Id":3112,"Name":"吴伟","EmCode":"A024","EmId":3023},
                {"Id":3220,"Name":"蒋浏洋","EmCode":"A027","EmId":3025},
                {"Id":3222,"Name":"吉伟宁","EmCode":"A018","EmId":3016}
            ]
        });*/
        
        
        var resourceStore = Ext.create("Sch.data.ResourceStore", {
            model : 'MyResource',
            autoLoad: true,
            autoSync: true,
            proxy : {
                type : 'ajax',
                method: 'POST',
                reader: {
                	root: 'resources',
                    type : 'json'                   
                }, 
                extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
                api: {
                    read: basePath + 'task/resource.action',
                    create: 'e-create.php',
                    destroy: 'e-destroy.php',
                    update: 'e-update.php'
                },
                writer : {
                    type : 'json',
                    root: 'resources',
                    encode: true,
                    writeAllFields: true,
                    listful : true,
                    allowSingle: false              
                }
            }                     
        });

        Ext.define('MyEvent', {
            extend: 'Sch.model.Event',
            nameField : 'Name',
            startDateField : 'StartDate',
            endDateField : 'EndDate',
            resourceIdField : 'ResourceId',
            fields: [
                // Define your own model fields 
                {name : 'StartDate', type : 'date', dateFormat : 'Y-m-d'},
                {name : 'EndDate', type : 'date', dateFormat : 'Y-m-d'},
                'Name',
                'ResourceId',
                'TaskId'
            ]
        });
        /*var eventStore = Ext.create('Sch.data.EventStore', {
            resourceStore : resourceStore,
            data: [
                { MyResourceId: 'A016', MyName: 'Some task', MyStartDate: "2010-11-09 10:00", MyEndDate: "2010-11-09 13:00" },
                { MyResourceId: 'A014', MyName: 'Foo task', MyStartDate: "2010-11-09 14:00", MyEndDate: "2010-11-09 17:00" }
                {"ResourceId":"A007","Name":"MRP/MPS","StartDate":"2012-10-17","EndDate":"2012-10-27"},
                {"ResourceId":"A005","Name":"财务表结构","StartDate":"2012-10-17","EndDate":"2012-10-20"},
                {"ResourceId":"A014","Name":"供应链","StartDate":"2012-10-16","EndDate":"2012-10-20"},
                {"ResourceId":"A012","Name":"邮箱","StartDate":"2012-10-17","EndDate":"2012-10-20"},
                {"ResourceId":"A015","Name":"基本技术开发","StartDate":"2012-10-17","EndDate":"2012-10-27"},
                {"ResourceId":"A015","Name":"BUG管理、测试","StartDate":"2012-10-17","EndDate":"2012-10-20"},
                {"ResourceId":"A012","Name":"车辆管理","StartDate":"2012-10-12","EndDate":"2012-10-16"},
                {"ResourceId":"A012","Name":"请假管理","StartDate":"2012-10-22","EndDate":"2012-10-24"},
                {"ResourceId":"A024","Name":"加班申请","StartDate":"2012-10-24","EndDate":"2012-10-27"},
                {"ResourceId":"A015","Name":"特殊界面处理","StartDate":"2012-10-18","EndDate":"2012-10-27"},
                {"ResourceId":"A015","Name":"自定义流程问题","StartDate":"2012-10-22","EndDate":"2012-10-27"}
                	
            ],
            model: 'MyEvent'
        });*/
        
         var eventStore = Ext.create("Sch.data.EventStore", {
         	model: 'MyEvent',
         	resourceStore : resourceStore,
            autoLoad: true,
            autoSync: true,
            proxy : {
                type : 'ajax',
                method: 'POST',
                reader: {
                	root: 'assignments',
                    type : 'json'                   
                }, 
                extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
                api: {
                    read: basePath + 'task/assignment.action',
                    create: 'e-create.php',
                    destroy: 'e-destroy.php',
                    update: 'e-update.php'
                },
                writer : {
                    type : 'json',
                    root: 'assignments',
                    encode: true,
                    writeAllFields: true,
                    listful : true,
                    allowSingle: false              
                }
            }                
        });      
     
        var sched = Ext.create("Sch.panel.SchedulerGrid", {
            height : 600, 
            width : 1000,
            eventBarTextField : 'Name',
            viewPreset : 'weekAndDay',
            startDate : new Date(2012, 9, 9),
            endDate : new Date(2012, 12, 12),
            rowHeight: 30,
                
            // Setup static columns
            columns : [
           		{header : '编号', sortable:true, width:50, dataIndex : 'EmCode'},
                {header : '姓名', sortable:true, width:60, dataIndex : 'Name'},
                {header : '员工序号', sortable:true, width:50, dataIndex : 'EmId'},
                {header : '序号', sortable:true, width:50, dataIndex : 'Id'}
            ],
                            
            // Store holding all the resources
            resourceStore : resourceStore,
        
            // Store holding all the events
            eventStore : eventStore,
                
            onEventCreated : function(newEventRecord) {
                // Overridden to provide some defaults before adding it to the store
                newEventRecord.set('Name', 'New task...');
            }
        });

        sched.render(Ext.getBody());
    }
};


























/*Ext.onReady(function() {
    SchedulerDemo.init();
});

SchedulerDemo = {
    
    // Initialize application
    init : function() {
        var summaryCol = Ext.create("Sch.plugin.SummaryColumn", { 
            header : 'Time allocated', 
            width: 80,
            showPercent : false 
        });

        var summaryCol2 = Ext.create("Sch.plugin.SummaryColumn", { 
            header : '% allocated', 
            showPercent : true,
            align : 'center',
            width: 60
        });

        var resourceStore = Ext.create("Sch.data.ResourceStore", {
            model : 'Sch.model.Resource',

            //limit resources to 5 per page
            pageSize : 5,
            autoSync: true,
            proxy : {
                type : 'ajax',
                method: 'POST',
                reader: {
                    type : 'json',
                    root : 'resources',

                    //name of the response property containing nuber of all records
                    totalProperty : 'total'                    
                }, 
                extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
                api: {
                    read: basePath + 'plm/resourceassignment.action'
                   
                },
                writer : {
                    type : 'json',
                    root: 'data',
                    encode: true,
                    writeAllFields: true,
                    listful : true,
                    allowSingle: false              
                }
            }            
        });

        var eventStore = Ext.create("Sch.data.EventStore", {
            autoLoad: true,
            autoSync: true,
            proxy : {
                type : 'ajax',
                method: 'POST',
                reader: {
                    type : 'json'                   
                }, 
                extraParams : {
							condition : 'prjplan_id=3147'// formCondition
						},
                api: {
                    read: basePath + 'plm/resourceassignment.action',
                    create: 'e-create.php',
                    destroy: 'e-destroy.php',
                    update: 'e-update.php'
                },
                writer : {
                    type : 'json',
                    root: 'assignments',
                    encode: true,
                    writeAllFields: true,
                    listful : true,
                    allowSingle: false              
                }
            }                
        });        
        
        var vp = new Ext.Viewport({
            layout : 'border',
            items : [
                
                {   
                    xtype : 'tabpanel',
                    region : 'center',
                    activeTab : 1,
                    items : [
                        
                        {
                            xtype : 'schedulergrid',
                            eventBarTextField : 'Name',
                            viewPreset : 'dayAndWeek',
                            startDate : new Date(2010, 11, 1),
                            endDate : new Date(2010, 11, 14),
                            rowHeight : 36,
                            title : 'Tab 2 - Scheduler',
                            //snapToIncrement : true,
                            eventResizeHandles : 'both',

                            // Setup static columns
                            columns : [
                               {header : 'Name', sortable:true, width:200, dataIndex : 'Name'},
                               {header : 'Some link', sortable:true, width:80, locked : true, renderer : function(v) { return '<a class="mylink" href="#">Click me!</a>'; }},
                               summaryCol,
                               summaryCol2
                            ],

                            // Store holding all the resources
                            resourceStore : Ext.create("Sch.data.ResourceStore", {
                                model : 'Sch.model.Resource',
                                data : [
                                    {Id : 'r1', Name : 'Mike'},
                                    {Id : 'r2', Name : 'Linda'},
                                    {Id : 'r3', Name : 'Don'},
                                    {Id : 'r4', Name : 'Karen'},
                                    {Id : 'r5', Name : 'Doug'},
                                    {Id : 'r6', Name : 'Peter'}
                                ]
                            }),
                            
                      
                            // Store holding all the events
                            eventStore : Ext.create("Sch.data.EventStore", {
                                data : [
                                    {Id : 'e10', ResourceId: 'r1', Name : 'Assignment 1', StartDate : "2010-12-02", EndDate : "2010-12-03"},
                                    {Id : 'e11', ResourceId: 'r2', Name : 'Assignment 2', StartDate : "2010-12-04", EndDate : "2010-12-07"},
                                    {Id : 'e21', ResourceId: 'r3', Name : 'Assignment 3', StartDate : "2010-12-01", EndDate : "2010-12-04"},
                                    {Id : 'e22', ResourceId: 'r4', Name : 'Assignment 4', StartDate : "2010-12-05", EndDate : "2010-12-07"},
                                    {Id : 'e32', ResourceId: 'r5', Name : 'Assignment 5', StartDate : "2010-12-07", EndDate : "2010-12-11"},
                                    {Id : 'e33', ResourceId: 'r6', Name : 'Assignment 6', StartDate : "2010-12-09", EndDate : "2010-12-11"}
                                ]
                            }),
                            resourceStore:resourceStore,
                            eventStore:eventStore,
        
                            plugins : [ summaryCol, summaryCol2 ],
                            
                            onEventCreated : function(newEventRecord) {
                                newEventRecord.setName('New task...');
                            }
                        }
                    ]
                }
            ]
        });

        var sched = Ext.ComponentQuery.query('schedulergrid[lockable=true]')[0],
            lockedSection = sched.lockedGrid,
            view = lockedSection.getView();

        lockedSection.el.on('click', function(e, t) {   
            var rowNode = view.findItemByChild(t);
            var resource = view.getRecord(rowNode);
            Ext.Msg.alert('Hey', 'You clicked ' + resource.get('Name'));
            e.stopEvent();
        }, null, { delegate : '.mylink' });
    }
};
*/