//没找到model从哪里来的***************不知道如何调用form
/**
* 各种活动对应的属性面板
*/
Ext.ns('App.form');
/** 面向抽象编程***/
App.form.AbstractForm = Ext.extend(Object, {
    eventNames: [
        ['start', '开始'],
        ['end', '结束']
    ],
    classNames:[], 
    clearItem: function(p) {
        if (typeof p.items != 'undefined') {
            var item = null;
            while ((item = p.items.last())) {
                p.remove(item, true);
            }
        }
    },
    getParticipantData: function(model) {
        var data = [];
        var elements = model.dom.getElementsByTagName('participant');
        Gef.each(elements, function(elem) {
            data.push({
                name: elem.getAttribute('name'),
                type: elem.getAttribute('type'),
                id  : elem.getAttribute('id')
            });
        });
       
        return data;
    },

    resetParticipant: function(tabPanel, model) {   	
        var data = this.getParticipantData(model);        
        var Record = Ext.data.Record.create(['name', 'type','id']);
        var store = new Ext.data.JsonStore({
            fields: Record,
            data: data,
            listeners: {
                add: function(store, records, index) {
                    var record = records[0];
                    var participantName = record.get('name');
                    var participantType = record.get('type');
                    var participantId   = record.get('id');
                    if ((!participantName) || participantName == '' || (!participantType) || participantType == '') {
                        return;
                    }
                    var onDom = new Gef.model.Dom('participant');
                    onDom.setAttribute('name', participantName);
                    onDom.setAttribute('type', participantType);
                    onDom.setAttribute('id',participantId);
                    model.dom.addElement(onDom);
                },
                remove: function(store, record, index) {
                    var candidateElements = model.dom.getElementsByTagName('participant');
                    var elements = candidateElements;
                    var element = elements[index];
                    model.dom.elements.remove(element);
                },
                update: function(store, record, operation) {
                    var participantName = record.get('name');
                    var participantType = record.get('type');
                    var participantId   = record.get('id');
                    if ((!participantName) || participantName == '' || (!participantType) || participantType == '') {
                        return;
                    }
                    var index = store.indexOf(record);
                    var candidateElements = model.dom.getElementsByTagName('participant');
                    var elements = candidateElements;
                    var element = elements[index];
                    if (!element) {
                        var onDom = new Gef.model.Dom('participant');
                        onDom.setAttribute('name', participantName);
                        onDom.setAttribute('type', participantType);
                        onDom.setAttribute('id' ,   participantId);
                        model.dom.addElement(onDom);
                    } else {
                        element.setAttribute('name', participantName);
                        element.setAttribute('type', participantType);
                        element.setAttribute('id' ,   participantId);
                    }

                    this.commitChanges();
                }
            }
        });

        var grid = new Ext.grid.EditorGridPanel({
            title: '界面配置',
            store: store,
            viewConfig: {
                forceFit: true
            },
            id:'grid',         
            listeners:{
            	'cellclick':function(grid,rowIndex){
            		grid.lastSelectedRecord=grid.selModel.selection.record;
            	}          
            },
            columnLines:true,
            dbfinds:[{
            	field:'name',
            	dbGridField:'sn_displayname'
            },{
            	field:'type',
            	dbGridField:'sn_url'
            },{
            	field:'id',
            	dbGridField:'sn_id'
            }],
            columns: [{
                header: '页面名称',
                dataIndex: 'name',
                sortable: true,
                width:400,
                editor: new Ext.grid.GridEditor(new Gef.org.DbfindField({
                  dbtype:'grid',
                  name:'name',
                  dbfind:'Sysnavigation|sn_displayname'
                }))            
            },{
                header: '页面路径',
                dataIndex: 'type',
                sortable: true,
                width:400
            },{
            	header:'页面ID',
            	dataIndex:'id',
            	sortable:true,
            	width:0
            }],
            tbar: [{
                text: '添加',
                iconCls: 'tb-add',
                handler: function() {
                    var p = new Record({
                        name: '',
                        type: ''
                    });
                    this.addRecord(grid, p);
                },
                scope: this
            }, {
                text: '删除',
                iconCls: 'tb-delete',
                handler: function() {
                    this.removeRecord(grid);
                },
                scope: this
            }]
        });

        tabPanel.add(grid);
    },

    getEventData: function(model) {
        var data = [];
        var elements = model.dom.getElementsByTagName('on');
        Gef.each(elements, function(elem) {
            var timerElem = elem.getElementByTagName('timer');
            if (timerElem) {
                return true;
            }
            data.push({
                name: elem.getAttribute('event'),
                classname: elem.getElementAttribute('event-listener', 'class')
            });
        });

        return data;
    },

    resetEvent: function(tabPanel, model) {
        var data = this.getEventData(model);
        var Record = Ext.data.Record.create(['name', 'classname']);
        var store = new Ext.data.JsonStore({
            fields: Record,
            data: data,
            listeners: {
                add: function(store, records, index) {
                    var record = records[0];
                    var eventName = record.get('name');
                    var eventClassName = record.get('classname');
                    if ((!eventName) || eventName == '' || (!eventClassName) || eventClassName == '') {
                        return;
                    }
                    var onDom = new Gef.model.Dom('on');  // 这才是 创建一个 新元素的正确方法！
                    onDom.setAttribute('event', eventName);
                    onDom.setElementAttribute('event-listener', 'class', eventClassName);
                    model.dom.addElement(onDom);
                },
                remove: function(store, record, index) {
                    var candidateElements = model.dom.getElementsByTagName('on');
                    var elements = [];
                    Gef.each(candidateElements, function(elem) {
                        var timerElem = elem.getElementByTagName('timer');
                        if (!timerElem) {
                            elements.push(elem);
                        }
                    });
                    var element = elements[index];
                    model.dom.elements.remove(element);
                },
                update: function(store, record, operation) {
                    var eventName = record.get('name');
                    var eventClassName = record.get('classname');
                    if ((!eventName) || eventName == '' || (!eventClassName) || eventClassName == '') {
                        return;
                    }
                    var index = store.indexOf(record);
                    var candidateElements = model.dom.getElementsByTagName('on');
                    var elements = [];
                    Gef.each(candidateElements, function(elem) {
                        var timerElem = elem.getElementByTagName('timer');
                        if (!timerElem) {
                            elements.push(elem);
                        }
                    });
                    var element = elements[index];
                    if (!element) {
                        var onDom = new Gef.model.Dom('on');
                        onDom.setAttribute('event', eventName);
                        onDom.setElementAttribute('event-listener', 'class', eventClassName);
                        model.dom.addElement(onDom);
                    } else {
                        element.setAttribute('event', eventName);
                        element.setElementAttribute('event-listener', 'class', eventClassName);
                    }

                    this.commitChanges();
                }
            }
        });

        var eventNameMap = {};
        Gef.each(this.eventNames, function(item) {
            eventNameMap[item[0]] = item[1];
        });
       var classNameMap = {};
       Gef.each(this.classNames, function(item) {
           classNameMap[item[0]] = item[1];
       });
        var grid = new Ext.grid.EditorGridPanel({
            title: '事件配置',
            store: store,          
            viewConfig: {
                forceFit: true
            },
            columns: [{
                header: '事件类型',
                dataIndex: 'name',
                editor: new Ext.grid.GridEditor(new Ext.ux.TwoCombo({
                    data: this.eventNames
                })),
               /* width:'15%',*/
                renderer: function(v) {
                    return eventNameMap[v];
                }
            }, {
                header: '类名',
                dataIndex: 'classname',
               /* editor: new Ext.grid.GridEditor(new Ext.form.TextField())*/
                editor: new Ext.grid.GridEditor(new Ext.ux.TwoCombo({
                    data: this.classNames
                })),
                
                renderer: function(v) {
                    return classNameMap[v];
                }
            }],
            tbar: [{
                text: '添加',
                iconCls: 'tb-add',
                handler: function() {
                    var p = new Record({
                        name: this.eventNames[0][0],
                        classname: ''
                    });
                    this.addRecord(grid, p);
                },
                scope: this
            }, {
                text: '删除',
                iconCls: 'tb-delete',
                handler: function() {
                    this.removeRecord(grid);
                },
                scope: this
            }]
        });

        tabPanel.add(grid);
    },

    getSwimlaneData: function(model) {
        var data = [];
        var elements = model.dom.getElementsByTagName('swimlane');
        Gef.each(elements, function(item) {
            data.push({
                name: item.getAttribute('name'),
                assignee: item.getAttribute('assignee'),
                candidateUsers: item.getAttribute('candidate-users'),
                candidateGroups: item.getAttribute('candidate-groups'),
                description: item.getElementContent('description')
            });
        });

        return data;
    },

    resetSwimlane: function(tabPanel, model) {
        var data = this.getSwimlaneData(model);

        var Record = Ext.data.Record.create([
            'name',
            'assignee',
            'candidateUsers',
            'candidateGroups',
            'description'
        ]);

        var store = new Ext.data.JsonStore({
            fields: Record,
            data: data,
            listeners: {
                add: function(store, records, index) {
                    var record = records[0];
                    var swimlaneDom = new Gef.model.Dom('swimlane');
                    swimlaneDom.setAttribute('name', record.get('name'));
                    swimlaneDom.setAttribute('assignee', record.get('assignee'));
                    swimlaneDom.setAttribute('candidate-users', record.get('candidateUsers'));
                    swimlaneDom.setAttribute('candidate-groups', record.get('candidateGroups'));
                    swimlaneDom.setElementContent('description', record.get('description'));
                    model.dom.addElement(swimlaneDom);
                },
                remove: function(store, record, index) {
                    var elements = model.dom.getElementsByTagName('swimlane');
                    var element = elements[index];
                    model.dom.elements.remove(element);
                },
                update: function(store, record, operation) {
                    var index = store.indexOf(record);
                    var elements = model.dom.getElementsByTagName('swimlane');
                    var element = elements[index];
                    element.setAttribute('name', record.get('name'));
                    element.setAttribute('assignee', record.get('assignee'));
                    element.setAttribute('candidate-users', record.get('candidateUsers'));
                    element.setAttribute('candidate-groups', record.get('candidateGroups'));
                    element.setElementContent('description', record.get('description'));

                    this.commitChanges();
                }
            }
        });

        var grid = new Ext.grid.EditorGridPanel({
            title: '泳道配置',
            store: store,
            viewConfig: {
                forceFit: true
            },
            columns: [{
                header: '泳道名',
                dataIndex: 'name',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '分配人',
                dataIndex: 'assignee',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '候选人',
                dataIndex: 'candidateUsers',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '候选群组',
                dataIndex: 'candidateGroups',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '备注',
                dataIndex: 'description',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }],
            tbar: [{
                text: '添加',
                iconCls: 'tb-add',
                handler: function() {
                    var p = new Record({
                        name: '',
                        assignee: '',
                        candidateUsers: '',
                        candidateGroups: '',
                        description: ''
                    });
                    this.addRecord(grid, p);
                },
                scope: this
            }, {
                text: '删除',
                iconCls: 'tb-delete',
                handler: function() {
                    this.removeRecord(grid);
                },
                scope: this
            }]
        });

        tabPanel.add(grid);
    },

    getTimerData: function(model) {
        var data = [];
        var elements = model.dom.getElementsByTagName('on');
        Gef.each(elements, function(elem) {
            var timerElem = elem.getElementByTagName('timer');
            if (!timerElem) {
                return true;
            }
            data.push({
                name: elem.getAttribute('event'),
                duedate: timerElem.getAttribute('duedate'),
                repeat: timerElem.getAttribute('repeat'),
                //duedatetime: timerElem.getAttribute('duedatetime'),
                classname: elem.getElementAttribute('event-listener', 'class')
            });
        });

        return data;
    },

    resetTimer: function(tabPanel, model) {
        var data = this.getTimerData(model);

        var Record = Ext.data.Record.create([
            'name',
            'duedate',
            'repeat',
            //'duedatetime',
            'classname'
        ]);

        var store = new Ext.data.JsonStore({
            fields: Record,
            data: data,
            listeners: {
                add: function(store, records, index) {
                    var record = records[0];
                    var onDom = new Gef.model.Dom('on');
                    onDom.setAttribute('event', record.get('name'));
                    var timerDom = new Gef.model.Dom('timer');
                    timerDom.setAttribute('duedate', record.get('duedate'));
                    timerDom.setAttribute('repeat', record.get('repeat'));
                    //timerDom.setAttribute('duedatetime', record.get('duedatetime'));
                    onDom.addElement(timerDom);
                    onDom.setElementAttribute('event-listener', 'class', record.get('classname'));
                    model.dom.addElement(onDom);
                },
                remove: function(store, record, index) {
                    var candidateElements = model.dom.getElementsByTagName('on');
                    var elements = [];
                    Gef.each(candidateElements, function(elem) {
                        var timerElem = elem.getElementByTagName('timer');
                        if (!timerElem) {
                            return true;
                        } else {
                            elements.push(elem);
                        }
                    });
                    var element = elements[index];
                    model.dom.elements.remove(element);
                },
                update: function(store, record, operation) {
                    var index = store.indexOf(record);
                    var candidateElements = model.dom.getElementsByTagName('on');
                    var elements = [];
                    Gef.each(candidateElements, function(elem) {
                        var timerElem = elem.getElementByTagName('timer');
                        if (!timerElem) {
                            return true;
                        } else {
                            elements.push(elem);
                        }
                    });
                    var element = elements[index];
                    element.setAttribute('event', record.get('name'));
                    var timerElem = element.getElementByTagName('timer');
                    timerElem.setAttribute('duedate', record.get('duedate'));
                    timerElem.setAttribute('repeat', record.get('repeat'));
                    //element.setAttribute('duedatetime', record.get('duedatetime'));
                    element.setElementAttribute('event-listener', 'class', record.get('classname'));
                    element.setElementContent('description', record.get('description'));

                    this.commitChanges();
                }
            }
        });

        var grid = new Ext.grid.EditorGridPanel({
            title: '定时器配置',
            xtype: 'editorgrid',
            store: store,
            viewConfig: {
                forceFit: true
            },
            columns: [{
                header: '名称',
                dataIndex: 'name',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '持续日期',
                dataIndex: 'duedate',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '重复次数',
                dataIndex: 'repeat',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, /*{
                header: '持续时间',
                dataIndex: 'duedatetime',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, */{
                header: '监听器类名',
                dataIndex: 'classname',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }],
            tbar: [{
                text: '添加',
                iconCls: 'tb-add',
                handler: function() {
                    var p = new Record({
                        name: '',
                        duedate: '',
                        repeat: '',
                        //duedatetime: '',
                        classname: ''
                    });
                    this.addRecord(grid, p);
                },
                scope: this
            }, {
                text: '删除',
                iconCls: 'tb-delete',
                handler: function() {
                    this.removeRecord(grid);
                },
                scope: this
            }]
        });

        tabPanel.add(grid);
    },

    getVariableData: function(model) {
        var data = [];
        var elements = model.dom.getElementsByTagName('variable');
        Gef.each(elements, function(item) {
            data.push({
                name: item.getAttribute('name'),
                type: item.getAttribute('type'),
                history: item.getAttribute('history'),
                init: item.getAttribute('init')
            });
        });

        return data;
    },

    resetVariable: function(tabPanel, model) {
        var data = this.getVariableData(model);

        var Record = Ext.data.Record.create([
            'name',
            'type',
            'history',
            'init'
        ]);

        var store = new Ext.data.JsonStore({
            fields: Record,
            data: data,
            listeners: {
                add: function(store, records, index) {
                    var record = records[0];
                    var variableDom = new Gef.model.Dom('variable');
                    variableDom.setAttribute('name', record.get('duedate'));
                    variableDom.setAttribute('type', record.get('type'));
                    variableDom.setAttribute('history', record.get('history'));
                    variableDom.setAttribute('init', record.get('init'));
                    model.dom.addElement(variableDom);
                },
                remove: function(store, record, index) {
                    var elements = model.dom.getElementsByTagName('variable');
                    var element = elements[index];
                    model.dom.elements.remove(element);
                },
                update: function(store, record, operation) {
                    var index = store.indexOf(record);
                    var elements = model.dom.getElementsByTagName('variable');
                    var element = elements[index];
                    element.setAttribute('name', record.get('name'));
                    element.setAttribute('type', record.get('type'));
                    element.setAttribute('init', record.get('history'));
                    element.setAttribute('history', record.get('init'));

                    this.commitChanges();
                }
            }
        });

        var grid = new Ext.grid.EditorGridPanel({
            title: '变量设置',
            store: store,
            viewConfig: {
                forceFit: true
            },
            columns: [{
                header: '变量名',
                dataIndex: 'name',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '变量类型',
                dataIndex: 'type',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }, {
                header: '是否保存历史',
                dataIndex: 'history',
                editor: new Ext.grid.GridEditor(new Ext.ux.OneCombo({
                    data: ['true', 'false']
                }))
            }, {
                header: '初始值',
                dataIndex: 'init',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }],
            tbar: [{
                text: '添加',
                iconCls: 'tb-add',
                handler: function() {
                    var p = new Record({
                        name: '',
                        type: ''
                    });
                    grid.stopEditing();
                    grid.getStore().insert(0, p);
                    grid.startEditing(0, 0);
                }
            }, {
                text: '删除',
                iconCls: 'tb-delete',
                handler: function() {
                    Ext.Msg.confirm('信息', '确定删除？', function(btn){
                        if (btn == 'yes') {
                            var sm = grid.getSelectionModel();
                            var cell = sm.getSelectedCell();

                            var record = grid.getStore().getAt(cell[0]);
                            grid.getStore().remove(record);
                        }
                    });
                }
            }]
        });

        tabPanel.add(grid);
    },

    addRecord: function(grid, record) {
        grid.stopEditing();
        var index = grid.getStore().getCount();
        grid.getStore().insert(index, record);
        grid.startEditing(index, 0);
    },

    removeRecord: function(grid) {
        Ext.Msg.confirm('信息', '确定删除？', function(btn) {
            if (btn != 'yes') {
                return;
            }
            var sm = grid.getSelectionModel();
            var cell = sm.getSelectedCell();

            var record = grid.getStore().getAt(cell[0]);
            grid.getStore().remove(record);
        });
    }
});

Ext.ns('App.form');

App.form.CustomForm = Ext.extend(App.form.AbstractForm, {
    decorate: function(tabPanel, model) {
        this.clearItem(tabPanel);
        this.resetBasic(tabPanel, model);
        this.resetParticipant(tabPanel, model);
        //this.resetEvent(tabPanel, model);
    },
    resetBasic: function(tabPanel, model) {
        var p = new Ext.form.FormPanel({
            title: '基本配置',
            labelWidth: 70,
            labelAlign: 'right',
            border: false,
            defaultType: 'textfield',
            defaults: {
                anchor: '90%'
            },
            bodyStyle: {
                padding: '6px 0 0'
            },
            items: [{
                name: 'name',
                fieldLabel: '名称',
                value: model.text ? model.text : '',
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        // FIXME: use command
                        model.text = newValue;
                        model.editPart.figure.updateAndShowText(newValue);
                    }
                }
            },{
                name: 'description',
                fieldLabel: '备注',
                xtype: 'textarea',
                value: model.dom.getElementContent('description'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setElementContent('description', newValue);
                    }
                }
            }]
        });

        tabPanel.add(p);
        tabPanel.activate(p);
    }
});
App.form.ProcessForm = Ext.extend(App.form.AbstractForm, {
    decorate: function(tabPanel, model) {
        this.clearItem(tabPanel);
        this.resetBasic(tabPanel, model);
    },
    resetBasic: function(tabPanel, model) {
    	var cstore = new Ext.data.SimpleStore({ 
			fields : ["value"],
        	data : [["是"],["否"]]
    	});
    	var k=model.dom.getAttribute("data");
    	if(k.length==0){
    		var combo = new Ext.form.ComboBox({
        		name:'pr_enabled',
            	fieldLabel:'启用',
            	editable:false,
            	allowBlanmodel: false,
            	store:cstore, 
            	value:model.pr_enabled?model.pr_enabled:'',
            	valueField : "value",
            	displayField : "value",
            	mode : "local",
            	triggerAction : "all",
            	listeners:{
            		'blur':function(field){
            			var newValue=field.getValue();           		
            			model.pr_enabled=newValue;
            		}
            	}
        		
        	});
    		var p = new Ext.form.FormPanel({
	        	id:'FirstForm',
	            title: '基本配置',
	            labelWidth: 70,
	            labelAlign: 'right',
	            border: false,
	            defaultType: 'textfield',
	            defaults: {
	                anchor: '90%'
	            },
	            bodyStyle: {
	                padding: '6px 0 0'
	            },
	            items: [{
	                name: 'pr_defname',
	                fieldLabel: '流程名称',
	                allowBlanmodel: false,
	                value: model.pr_defname ? model.pr_defname : '',
	                		   listeners: {
	                               'blur': function(field) {
	                                  var newValue = field.getValue();
	                                  model.pr_defname = newValue;
	                               }}
	            },{
	                name: 'pr_caller',
	                fieldLabel: '流程CALLER',
	                allowBlanmodel: false,
	                value: model.pr_caller?model.pr_caller:'',
	                listeners: {
	                    'blur': function(field) {
	                        var newValue = field.getValue();
	                        // console.log(model);
	                        model.pr_caller = newValue;
	                    },
	                    'beforerender':function(field){
	                    	var condition=getUrlParam('formCondition');
	                    	if(condition){
	                    		field.setValue(condition.split("IS")[1]);
	                    	}
	                    	
	                    }
	                }
	            },combo, {
	                name: 'pr_descn',
	                fieldLabel: '备注',
	                xtype: 'textarea',
	                //value: model.dom.getElementContent('description'),//设置。。
	                value:model.pr_descn ? model.pr_descn:'',
	                listeners: {
	                    'blur': function(field) {
	                        var newValue = field.getValue();
	                        model.dom.setElementContent('description', newValue);
	                        var a=model;
	                        model.pr_descn=newValue;
	                    }
	                }
	            }]
	        });

    	}else{
    		var combo = new Ext.form.ComboBox({
        		name:'pr_enabled',
            	fieldLabel:'启用',
            	editable:false,
            	allowBlanmodel: false,
            	store:cstore, 
            	value:k.pr_enabled?k.pr_enabled:'',
            	valueField : "value",
            	displayField : "value",
            	mode : "local",
            	triggerAction : "all",
            	listeners:{
            		'blur':function(field){
            			var newValue=field.getValue();
            			k.pr_enabled=newValue;
            		}
            	}
        		
        	});
            var p = new Ext.form.FormPanel({
            	id:'FirstForm',
                title: '基本配置',
                labelWidth: 70,
                labelAlign: 'right',
                border: false,
                defaultType: 'textfield',
                defaults: {
                    anchor: '90%'
                },
                bodyStyle: {
                    padding: '6px 0 0'
                },
                items: [{
                    name: 'pr_defname',
                    fieldLabel: '流程名称',
                    allowBlank: false,
                    value: k.pr_defname ? k.pr_defname : '',
                    		   listeners: {
                                   'blur': function(field) {
                                       var newValue = field.getValue();
                                      k.pr_defname = newValue;
                                }
                       }
                },{
                    name: 'pr_caller',
                    fieldLabel: '流程对应表单(caller)',
                    allowBlank: false,
                    value: k.pr_caller?k.pr_caller:'',
                    listeners: {
                        'blur': function(field) {
                            var newValue = field.getValue();
                            k.pr_caller = newValue;
                        },
                        'afterrender':function(field){
                        	alert(getUrlParam('formCondition'));
                        	
                        }
                    }
                },combo,{
                    name: 'pr_descn',
                    fieldLabel: '备注',
                    xtype: 'textarea',
                    //value: model.dom.getElementContent('description'),//设置。。
                    value: k.pr_descn ? k.pr_descn : '',
                    listeners: {
                        'blur': function(field) {
                            var newValue = field.getValue();
                            model.dom.setElementContent('description', newValue);
                            var a=model;
                            k.pr_descn=newValue;
                        }
                    }
                }]
            });
    	}
        tabPanel.add(p);
        tabPanel.activate(p);
    }
});

Ext.ns('App.form');

App.form.TransitionForm = Ext.extend(App.form.AbstractForm, {
	id:'transitionform',
    eventNames: [
        ['take', '进入']
    ],

    decorate: function(tabPanel, model) {
        this.clearItem(tabPanel);
        this.resetBasic(tabPanel, model);
        this.resetEvent(tabPanel, model);
        this.resetTimer(tabPanel, model);
    },

    resetBasic: function(tabPanel, model) {
        var condition = model.dom.getElementAttribute('condition', 'expr');
        if (condition != '') {
            condition = condition.replace(/&lt;/g, '<')
                                 .replace(/&amp;/g, '&');
        }

        var items = [];
        items.push({
            name: 'name',
            fieldLabel: '名称',
            value: model.text ? model.text : '',
            listeners: {
                'blur': function(field) {
                    var newValue = field.getValue();
                    // FIXME: use command
					 if(model.getSource().type == 'task'){
                    	if(newValue!="同意"&&newValue!="不同意"){
                    		alert("任务节点的流出连线名称必须为“同意”或“不同意！”");
                    		field.focus();
						}
                    }
                    model.text = newValue;
                    model.editPart.figure.updateAndShowText(newValue);
                 }
            }
        });

        if (model.getSource().type == 'decision') {
            items.push({
                name: 'condition',
                fieldLabel: '条件',
                // <condition expr="#{ACCOUNT&gt;=10000}"/>
                value: condition,
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        if (newValue != '') {
                            model.dom.setElementAttribute('condition', 'expr', newValue);
                        } else if (model.dom.elements.length > 0) {
                            model.dom.removeElement(model.dom.elements[0]);
                        }
                    }
                }
            });
        }

        var p = new Ext.form.FormPanel({
            title: '基本配置',
            labelWidth: 70,
            labelAlign: 'right',
            border: false,
            defaultType: 'textfield',
            defaults: {
                anchor: '90%'
            },
            bodyStyle: {
                padding: '6px 0 0'
            },
            items: items
        });

        tabPanel.add(p);
        tabPanel.activate(p);
    },

    getTimerData: function(model) {
        var data = [];
        var elements = model.dom.getElementsByTagName('timer');
        Gef.each(elements, function(elem) {
            data.push({
                duedate: timerElem.getAttribute('duedate')
            });
        });

        return data;
    },

    resetTimer: function(tabPanel, model) {
        var data = this.getTimerData(model);

        var Record = Ext.data.Record.create([
            'duedate'
        ]);

        var store = new Ext.data.JsonStore({
            fields: Record,
            data: data,
            listeners: {
                add: function(store, records, index) {
                    var record = records[0];
                    var timerDom = new Gef.model.Dom('timer');
                    timerDom.setAttribute('duedate', record.get('duedate'));
                    model.dom.addElement(timerDom);
                },
                remove: function(store, record, index) {
                    var element = model.dom.getElementByTagName('timer');
                    model.dom.elements.remove(element);
                },
                update: function(store, record, operation) {
                    var index = store.indexOf(record);
                    var element = model.dom.getElementByTagName('timer');
                    element.setAttribute('duedate', record.get('duedate'));

                    this.commitChanges();
                }
            }
        });

        var grid = new Ext.grid.EditorGridPanel({
            title: '定时器配置',
            xtype: 'editorgrid',
            store: store,
            viewConfig: {
                forceFit: true
            },
            columns: [{
                header: '持续日期',
                dataIndex: 'duedate',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }/*, {
                header: '持续时间',
                dataIndex: 'duedatetime',
                editor: new Ext.grid.GridEditor(new Ext.form.TextField())
            }*/],
            tbar: [{
                text: '添加',
                iconCls: 'tb-add',
                handler: function() {
                    var count = grid.getStore().getCount();
                    if (count > 0) {
                        Ext.Msg.alert('info', '连线上只能设置一个定时器');
                        return;
                    }

                    var p = new Record({
                        name: '',
                        duedate: '',
                        repeat: '',
                        //duedatetime: '',
                        classname: ''
                    });
                    grid.stopEditing();
                    var index = grid.getStore().getCount();
                    grid.getStore().insert(index, p);
                    grid.startEditing(index, 0);
                }
            }, {
                text: '删除',
                iconCls: 'tb-delete',
                handler: function() {
                    Ext.Msg.confirm('信息', '确定删除？', function(btn){
                        if (btn == 'yes') {
                            var sm = grid.getSelectionModel();
                            var cell = sm.getSelectedCell();

                            var record = grid.getStore().getAt(cell[0]);
                            grid.getStore().remove(record);
                        }
                    });
                }
            }]
        });

        tabPanel.add(grid);
    }
});
