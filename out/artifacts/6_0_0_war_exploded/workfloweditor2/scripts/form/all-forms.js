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
	             classNames:[['com.uas.erp.service.scm.impl.ScmBeforeEventListener','SCM-Before'],
	                         ['com.uas.erp.service.scm.impl.ScmAfterEventListener','SCM-After'],
	                         ['com.uas.erp.service.pm.impl.PmBeforeEventListener','PM-Before'],
	                         ['com.uas.erp.service.pm.impl.PmAfterEventListener','PM-After'],
	                         ['com.uas.erp.service.pm.impl.HrBeforeEventListener','HR-Before'],
	                         ['com.uas.erp.service.pm.impl.HrAfterEventListener','HR-After'],
	                         ['com.uas.erp.service.pm.impl.OaBeforeEventListener','OA-Before'],
	                         ['com.uas.erp.service.pm.impl.OaAfterEventListener','OA-After'],
	                         ['com.uas.erp.service.pm.impl.CrmBeforeEventListener','CRM-Before'],
	                         ['com.uas.erp.service.pm.impl.CrmAfterEventListener','CRM-After'],
	                         ['com.uas.erp.service.pm.impl.FaBeforeEventListener','FA-Before'],
	                         ['com.uas.erp.service.pm.impl.FaAfterEventListener','FA-After'],
	                         ['com.uas.erp.service.pm.impl.DrpBeforeEventListener','DRP-Before'],
	                         ['com.uas.erp.service.pm.impl.DrpAfterEventListener','DRP-After']
	                         ], 
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
	                        			 type: elem.getAttribute('type')
	                        		 });
	                        	 });

	                        	 return data;
	                         },

	                         resetParticipant: function(tabPanel, model) {   	
	                        	 var data = this.getParticipantData(model);        
	                        	 var Record = Ext.data.Record.create(['name', 'type']);
	                        	 var store = new Ext.data.JsonStore({
	                        		 fields: Record,
	                        		 data: data,
	                        		 listeners: {
	                        			 add: function(store, records, index) {
	                        				 var record = records[0];
	                        				 var participantName = record.get('name');
	                        				 var participantType = record.get('type');
	                        				 if ((!participantName) || participantName == '' || (!participantType) || participantType == '') {
	                        					 return;
	                        				 }
	                        				 var onDom = new Gef.model.Dom('participant');
	                        				 onDom.setAttribute('name', participantName);
	                        				 onDom.setAttribute('type', participantType);
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
	                        					 model.dom.addElement(onDom);
	                        				 } else {
	                        					 element.setAttribute('name', participantName);
	                        					 element.setAttribute('type', participantType);
	                        				 }

	                        				 this.commitChanges();
	                        			 }
	                        		 }
	                        	 });

	                        	 var grid = new Ext.grid.EditorGridPanel({
	                        		 title: '参与者配置',
	                        		 store: store,
	                        		 viewConfig: {
	                        			 forceFit: true
	                        		 },
	                        		 columns: [{
	                        			 header: '名称',
	                        			 dataIndex: 'name',
	                        			 editor: new Ext.grid.GridEditor(new Ext.form.TextField())
	                        		 }, {
	                        			 header: '类型',
	                        			 dataIndex: 'type',
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

	                        	 /*tabPanel.add(grid);*/
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

App.form.AutoForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'classname',
				fieldLabel: '类名',
				value: model.dom.getAttribute('class'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('class', newValue);
					}
				}
			}, {
				name: 'method',
				fieldLabel: '方法',
				value: model.dom.getAttribute('method'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('method', newValue);
					}
				}
			}, {
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


Ext.ns('App.form');

App.form.CancelForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			}, {
				name: 'ends',
				fieldLabel: '结束方式',
				xtype: 'onecombo',
				data: ['processinstance', 'execution'],
				value: model.dom.getAttribute('ends'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('ends', newValue);
					}
				}
			}, {
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


Ext.ns('App.form');

App.form.CounterSignForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetParticipant(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								//model.dom.setProperty('name', newValue, 'string');
							}
						}
			}, {
				name: 'counterSignType',
				fieldLabel: '会签方式',
				xtype: 'twocombo',
				data: [['all', '一票否决'], ['rate', '百分比']],
				//value: model.dom.getProperty('form', 'string'),
				value: model.dom.getAttribute('counter-sign-type'),
				listeners: {
					'select': function(field) {
						var newValue = field.getValue();
						//model.dom.setProperty('form', newValue, 'string');
						model.dom.setAttribute('counter-sign-type', newValue);
						if (newValue == 'rate') {
							var field = p.getForm().findField('counterSignValue');
							field.el.dom.parentNode.parentNode.style.display = '';
						} else {
							var field = p.getForm().findField('counterSignValue');
							field.setValue('');
							field.el.dom.parentNode.parentNode.style.display = 'none';
							model.dom.removeProperty('counter-sign-value');
						}
					}
				}
			}, {
				name: 'counterSignValue',
				fieldLabel: '会签通过百分比',
				//value: model.dom.getProperty('form', 'string'),
				value: model.dom.getAttribute('counter-sign-value'),
				xtype: 'numberfield',
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						//model.dom.setProperty('form', newValue, 'string');
						model.dom.setAttribute('counter-sign-value', newValue);
					}
				}
			}, {
				name: 'form',
				fieldLabel: '表单',
				//value: model.dom.getProperty('form', 'string'),
				value: model.dom.getAttribute('form'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						//model.dom.setProperty('form', newValue, 'string');
						model.dom.setAttribute('form', newValue);
					}
				}
			}, {
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

		if (model.dom.getAttribute('counter-sign-type') != 'rate') {
			var fn = function() {
				var field = p.getForm().findField('counterSignValue');
				field.el.dom.parentNode.parentNode.style.display = 'none';
				p.un('afterlayout', fn);
			};
			p.on('afterlayout', fn);
		}

		tabPanel.add(p);
		tabPanel.activate(p);
	}
});



Ext.ns('App.form');

App.form.CustomForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'classname',
				fieldLabel: '类名',
				value: model.dom.getAttribute('class'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('class', newValue);
					}
				}
			}, {
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


Ext.ns('App.form');

App.form.DecisionForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			},{
    		 	 name: 'flowtype',
    		 	 fieldLabel: '类型',
    			 value: 'judge',
    			 readOnly:true
			}]
		});
		tabPanel.add(p);
		tabPanel.activate(p);
	}
});


Ext.ns('App.form');

App.form.EndForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
	},
	/**
	 * 结束*/
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
				value: 'END',
				readOnly:true,
				listeners: {
					beforerender:function(){
						model.text = 'END';
					}
				}
			}]
		});

		tabPanel.add(p);
		tabPanel.activate(p);
	}
});

Ext.ns('App.form');

App.form.ErrorForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			}, {
				name: 'ends',
				fieldLabel: '结束方式',
				xtype: 'onecombo',
				data: ['processinstance', 'execution'],
				value: model.dom.getAttribute('ends'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('ends', newValue);
					}
				}
			}, {
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


Ext.ns('App.form');

App.form.ForeachForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			}, {
				name: 'var',
				fieldLabel: '变量',
				value: model.dom.getAttribute('var'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('var', newValue);
					}
				}
			}, {
				name: 'in',
				fieldLabel: '集合',
				value: model.dom.getAttribute('in'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('in', newValue);
					}
				}
			}, {
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


Ext.ns('App.form');

App.form.ForkForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			}, {
				name: 'description',
				fieldLabel: '备注',
				xtype: 'textarea',
				value: model.dom.getElementContent('description'), /**获取dom的值**/
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


Ext.ns('App.form');

App.form.HqlForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'var',
				fieldLabel: '变量',
				value: model.dom.getAttribute('var'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('var', newValue);
					}
				}
			}, {
				name: 'unique',
				fieldLabel: '是否唯一',
				value: model.dom.getAttribute('unique'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('unique', newValue);
					}
				}
			}, {
				name: 'query',
				fieldLabel: '查询语句',
				value: model.dom.setElementContent('query'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementContent('query', newValue);
					}
				}
			}, {
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



/**Ext.ns('App.form');

App.form.HumanForm = Ext.extend(App.form.AbstractForm, {
    assigneeXtype: 'orgfield',

    decorate: function(tabPanel, model) {

        this.clearItem(tabPanel);
        this.resetBasic(tabPanel, model);
        this.resetParticipant(tabPanel, model);
        this.resetEvent(tabPanel, model);
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
            }, {
                name: 'form',
                fieldLabel: '表单',
                value: model.dom.getAttribute('form'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('form', newValue);
                    }
                }
            }, {
                name: 'assignee',
                xtype: this.assigneeXtype,
                fieldLabel: '分配人',
                value: model.dom.getAttribute('assignee'),
                listeners: {
                    'select': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('assignee', newValue);
                    }
                }
            }, {
                name: 'duedate',
                fieldLabel: '任务时限',
                value: model.dom.getAttribute('duedate'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('duedate', newValue);
                    }
                }
            }, {
                name: 'timeoutType',
                fieldLabel: '超时处理方式',
                value: model.dom.getAttribute('timeout-type'),
                xtype: 'onecombo',
                data: ['message', 'continue', 'end'],
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('timeout-type', newValue);
                    }
                }
            }, {
                name: 'swimlaneName',
                fieldLabel: '泳道',
                value: model.dom.getProperty('swimlaneName', 'string'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setProperty('swimlaneName', newValue, 'string');
                    }
                }
            }, {
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
 **/


Ext.ns('App.form');

App.form.JavaForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'classname',
				fieldLabel: '类名',
				value: model.dom.getAttribute('class'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('class', newValue);
					}
				}
			}, {
				name: 'method',
				fieldLabel: '方法',
				value: model.dom.getAttribute('method'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('method', newValue);
					}
				}
			}, {
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

Ext.ns('App.form');

App.form.JmsForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'connectionFactory',
				fieldLabel: '连接工厂',
				value: model.dom.getAttribute('connection-factory'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('connection-factory', newValue);
					}
				}
			}, {
				name: 'destination',
				fieldLabel: '消息目标',
				value: model.dom.getAttribute('destination'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('destination', newValue);
					}
				}
			}, {
				name: 'transacted',
				fieldLabel: '事务性',
				xtype: 'onecombo',
				data: ['true', 'false'],
				value: model.dom.getAttribute('transacted'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('transacted', newValue);
					}
				}
			}, {
				name: 'acknowledge',
				fieldLabel: '提醒方式',
				xtype: 'onecombo',
				data: ['auto', 'client', 'dups-ok'],
				value: model.dom.getAttribute('acknowledge'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('acknowledge', newValue);
					}
				}
			}, {
				name: 'textObject',
				fieldLabel: '内容',
				xtype: 'textarea',
				value: model.dom.getElementContent('text'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementContent('text', newValue);
					}
				}
			}, {
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



Ext.ns('App.form');

App.form.JoinForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			}, {
				name: 'multiplicity',
				fieldLabel: '汇聚数目',
				value: model.dom.getAttribute('multiplicity'),
				xtype: 'numberfield',
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('multiplicity', newValue);
					}
				}
			}/*, {
                name: 'lockmode',
                fieldLabel: '锁定模式',
                xtype: 'onecombo',
                data: ['none', 'read', 'upgrade', 'upgrade_nowait', 'write'],
                value: model.dom.getAttribute('lockmode'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('lockmode', newValue);
                    }
                }
            }*/, {
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


Ext.ns('App.form');

App.form.MailForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'template',
				fieldLabel: '模板',
				value: model.dom.getAttribute('template'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('template', newValue);
					}
				}
			}, {
				name: 'from',
				fieldLabel: '发信人',
				value: model.dom.getElementContent('from'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementContent('from', newValue);
					}
				}
			}, {
				name: 'to',
				fieldLabel: '收信人',
				value: model.dom.getElementContent('to'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementContent('to', newValue);
					}
				}
			}, {
				name: 'subject',
				fieldLabel: '标题',
				value: model.dom.getElementContent('subject'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementContent('subject', newValue);
					}
				}
			}, {
				name: 'content',
				fieldLabel: '内容',
				value: model.dom.getElementContent('html'),
				xtype: 'htmleditor',
				listeners: {
					'sync': function(field, newValue) {
						model.dom.setElementContent('html', newValue);
					}
				}
			}, {
				name: 'description',
				fieldLabel: '备注',
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
		var k=model.dom.getAttribute("data");
		var flowName = k.flowName;
    	var flowCaller = k.flowCaller;
    	var flowRemark = k.flowRemark;
		var cstore = new Ext.data.SimpleStore({ 
			fields : ["value"],
			data : [["是"],["否"]]
		});
		var combo = new Ext.form.ComboBox({
			name:'pr_enabled',
			fieldLabel:'启用',
			editable:false,
			allowBlanmodel: false,
			store:cstore, 
			hidden:true,
			hideLabel:true,
			/*style:'background:#D3D3D3',*/
			value:model.pr_enabled?model.pr_enabled:'否',
			valueField : "value",
			displayField : "value",
			mode : "local",
			triggerAction : "all",
			/*value:model.pr_id?model.pr_id:'',*/
			listeners:{
				'beforerender':function(field){
			  	  var newValue = field.getValue();
				  model.pr_enabled = newValue;
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
				readOnly:true,
				value: flowName,
				listeners: {
					beforerender: function(field){
						if(getUrlParam('shortName')){
							model.pr_defname = getUrlParam('shortName');
						}
					}
				}
			},{
        	   name: 'pr_caller',
        	   fieldLabel: '关联表单(caller)',
        	   readOnly:true,
        	   allowBlanmodel: false,
        	   value: flowCaller,
			   listeners: {
				  'beforerender':function(field){
				  	  var newValue = field.getValue();
					  model.pr_caller = newValue;
					  /*var condition=getUrlParam('formCondition');
					  if(condition){
						  field.setValue(condition.split("IS")[1]);
					  }*/
				  }
			   }
            },combo,{
            	xtype:'combo',
            	name:'pr_ressubmit',
            	fieldLabel:'限制反提交',
				editable:false,
				allowBlanmodel: false,
				store:cstore, 
				hidden:true,
				hideLabel:true,
				value:model.pr_ressubmit?model.pr_ressubmit:'',
				valueField : "value",
				displayField : "value",
				mode : "local",
				triggerAction : "all",
				listeners:{
					'beforerender':function(field){
    				  	  var newValue = field.getValue();
    					  model.pr_ressubmit = newValue;
    				  }
				}
            },{
            	name: 'pr_descn',
            	fieldLabel: '备注',
            	xtype: 'textarea',
            	height:300,
            	readOnly:true,
            	value:flowRemark,
    			listeners: {
    				'beforerender':function(field){
    				  	  var newValue = field.getValue();
    					  model.pr_descn = newValue;
    					  model.dom.setElementContent('description', newValue);
    				  }
    			}
            }]
		});
		tabPanel.add(p);
		tabPanel.activate(p);
	}
});
Ext.ns('App.form');

App.form.RuleDecisionForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
								model.text = newValue;
							}
						}
			}, {
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


Ext.ns('App.form');

App.form.RulesForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'factVar',
				fieldLabel: '变量',
				value: model.dom.getElementAttribute('fact', 'var'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementAttribute('fact', 'var', newValue);
					}
				}
			}, {
				name: 'factExpr',
				fieldLabel: '表达式',
				value: model.dom.getElementAttribute('fact', 'expr'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementAttribute('fact', 'expr', newValue);
					}
				}
			}, {
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

Ext.ns('App.form');

App.form.ScriptForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'expr',
				fieldLabel: '表达式',
				value: model.dom.getAttribute('expr'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('expr', newValue);
					}
				}
			}, {
				name: 'lang',
				fieldLabel: '脚本语言',
				value: model.dom.getAttribute('lang'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('lang', newValue);
					}
				}
			}, {
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

Ext.ns('App.form');

App.form.SqlForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
	},

	resetBasic: function(tabPanel, model) {
		// 对象型参数
		var objFS = new Ext.form.FieldSet({
			title: '对象型参数设置',
			collapsible: true,
			autoHeight : true,
			checkboxToggle : true,
			id:'objFS',
			checkboxName :'',
			defaults: {
				anchor: '90%'
			},
			/*layout:'column',*/
			/*defaultType: 'textfield',*/
			items :[new Ext.form.TextField({
				fieldLabel: '名称',
				name: 'field1',
				disabled:true,
				listeners: {
					'blur': function(field) {
						var para = model.dom.getElementByTagName('parameters');
						var newValue = field.getValue();
						console.log(model);

						var el = para.getElementByTagName('object');
						if(!el){
							var obj = new Gef.model.Dom('object');
							obj.setAttribute('name',newValue);
							para.addElement(obj);
						}else{
							el.setAttribute('name',newValue);
						}

					},
					'beforerender':function(field){
						var para = model.dom.getElementByTagName('parameters');
						if(para){
							var el = para.getElementByTagName('object');
							if(el){
								var v = para.getElementByTagName('object').getAttribute('name');
								field.setValue(v);
							}
						}else{
							return;
						}

					}

				}


			}), new Ext.form.TextField({
				fieldLabel: '表达式',
				name: 'field2',
				disabled:true,
				listeners: {
					'blur': function(field) {
						var para = model.dom.getElementByTagName('parameters');
						var newValue = field.getValue();
						var el = para.getElementByTagName('object');
						if(!el){
							var obj = new Gef.model.Dom('object');
							obj.setAttribute('expr',newValue);
							para.addElement(obj);

						}else{
							el.setAttribute('expr',newValue);
						}

					},
					'beforerender':function(field){
						var para = model.dom.getElementByTagName('parameters');
						if(para){
							var el = para.getElementByTagName('object');
							if(el){
								var v = para.getElementByTagName('object').getAttribute('expr');
								field.setValue(v);
							}
						}else{
							return;
						}

					}

				}

			})]
		});
		var stringFS = new Ext.form.FieldSet({
			title: '字符串型参数设置',
			collapsible: true,
			id:'stringFS',
			autoHeight : true,
			checkboxToggle : true,
			checkboxName :'',
			defaults: {
				anchor: '90%'
			},
			/*layout:'column',*/
			/*defaultType: 'textfield',*/
			items :[new Ext.form.TextField({
				fieldLabel: '名称',
				name: 'field3',
				disabled:true,
				/*value:  0, */ /*model.dom.getElementByTagName('reminder').getAttribute('duedate'),*/
				listeners: {
					'blur': function(field) {
						var para = model.dom.getElementByTagName('parameters');
						var newValue = field.getValue();
						console.log(model);

						var el = para.getElementByTagName('string');
						if(!el){
							var str = new Gef.model.Dom('string');
							str.setAttribute('name',newValue);
							para.addElement(str);
						}else{
							el.setAttribute('name',newValue);
						}
					},
					'beforerender':function(field){
						var para = model.dom.getElementByTagName('parameters');
						if(para){
							var el = para.getElementByTagName('string');
							if(el){
								var v = para.getElementByTagName('string').getAttribute('name');
								field.setValue(v);
							}
						}else{
							return;
						}
					}

				}


			}), new Ext.form.TextField({
				fieldLabel: '值',
				name: 'field4',
				disabled:true,
				listeners: {
					'blur': function(field) {
						var para = model.dom.getElementByTagName('parameters');
						var newValue = field.getValue();
						console.log(model);

						var el = para.getElementByTagName('string');
						if(!el){
							var str = new Gef.model.Dom('string');
							str.setAttribute('value',newValue);
							para.addElement(str);
						}else{
							el.setAttribute('value',newValue);
						}
					},
					'beforerender':function(field){
						var para = model.dom.getElementByTagName('parameters');
						if(para){
							var el = para.getElementByTagName('string');
							if(el){
								var v = para.getElementByTagName('string').getAttribute('value');
								field.setValue(v);
							}
						}else{
							return;
						}


					}

				}

			})]
		});
		var p1 =  new Ext.form.Checkbox({
			id:'p1',
			fieldLabel :'参数设置',
			boxLabel:'对象型',
			name:'parameter',
			listeners: {
				'beforerender':function(){

				},
				'check':function(p1,b){
					if(b){
						var para = model.dom.getElementByTagName('parameters');
						if(!para){
							para = new Gef.model.Dom('parameters');
							model.dom.addElement(para);
							var items= Ext.getCmp('objFS').items.items;
							Ext.each(items,function(item){
								if(item.disable){
									item.enable();
								}
							});
						}else{
							var items = Ext.getCmp('objFS').items.items;
							Ext.each(items,function(item){
								/*item.enable();*/
								if(item.disable){
									item.enable();
								}
							});
						}
					}
				}
			}
		});
		var p2 =  new Ext.form.Checkbox({
			id:'p2',
			fieldLabel :'',
			boxLabel:'字符串型',
			name:'parameter',
			listeners: {
				'beforerender':function(){

				},
				'check':function(p2,b){
					if(b){
						var para = model.dom.getElementByTagName('parameters');
						if(!para){
							para = new Gef.model.Dom('parameters');
							model.dom.addElement(para);
							var items = Ext.getCmp('stringFS').items.items;
							Ext.each(items,function(item){
								/*item.enable();*/
								if(item.disable){
									item.enable();
								}
							});

						}else{
							var items = Ext.getCmp('stringFS').items.items;
							Ext.each(items,function(item){
								/*item.enable();*/
								if(item.disable){
									item.enable();
								}
							});

						}
					}
				}
			}
		}); 
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
			}, {
				name: 'var',
				fieldLabel: '变量',
				value: model.dom.getAttribute('var'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('var', newValue);
					}
				}
			}, {
				name: 'unique',
				fieldLabel: '是否唯一',
				value: model.dom.getAttribute('unique'),
				// hidden:true,
				//hideLabel:true,
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('unique', newValue);
					}

				}
			}, {
				name: 'query',
				fieldLabel: '查询语句',
				xtype: 'textarea',
				value: model.dom.getElementContent('query'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setElementContent('query', newValue);
					}
				}
			}/*, {
            	name:'parameters',
            	fieldLabel: '参数表达式',	
            	 value: model.dom.getElementContent('parameters'),
            	 xtype: 'textarea',
            	 validate:function(value){
            		 if(value==null||value==''){
            			 return true;
            		 }else if(value.indexOf("<")==0&&value.lastIndexOf(">")==value.length-1){
            			return true;
            		 }

            		 return false;
            	 }*/,p1,p2,objFS,stringFS,{
            		 name:'parameters',
            		 fieldLabel: '参数表达式',	
            		 value: model.dom.getElementContent('parameters'),
            		 xtype: 'textarea',
            		 validate:function(value){
            			 if(value==null||value==''){
            				 return true;
            			 }else if(value.indexOf("<")==0&&value.lastIndexOf(">")==value.length-1){
            				 return true;
            			 }

            			 return false;
            		 },
            		 listeners: {
            			 'blur': function(field) {
            				 var newValue = field.getValue();
            				 /*if(!this.validate(newValue)){
                        	 alert("请填写正确的参数表达式！");
                        	 field.focus();
                        	 return;
                         }*/

            				 model.dom.setElementContent('parameters',newValue);
            				 /*   model.dom.setElementAttribute('object', 'expr', newValue);*/
            				 /*model.dom.setElementAttribute('parameters', 'object', newValue);*/
            				 /* console.log(model.dom);*/
            				 /* var o = model.dom.getElementByTagName('object').getElementContent();
                       console.log(o);*/
            				 /*   var p = model.dom.getElementByTagName('parameters');
                         console.log(p);*/
            				 /* var items =[];
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
                         });*/
            			 }

            		 }
            	 }/*,{
            	name:'parameters',
            	fieldLabel: '参数',	
            	 value: model.dom.getElementContent('parameters'),
                 listeners: {
                     'blur': function(field) {
                         var newValue = field.getValue();
                         model.dom.setElementContent('parameters', newValue);
                     }
                 }

            }*/,{
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


Ext.ns('App.form');

App.form.StartForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
				value: 'START',
				readOnly:true,
				listeners: {
					beforerender:function(){
						model.text = 'START';
					}
				}
			}]
		});

		tabPanel.add(p);
		tabPanel.activate(p);
	}
});

Ext.ns('App.form');

App.form.StateForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
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

Ext.ns('App.form');

App.form.SubProcessForm = Ext.extend(App.form.AbstractForm, {
	decorate: function(tabPanel, model) {
		this.clearItem(tabPanel);
		this.resetBasic(tabPanel, model);
		this.resetAdvance(tabPanel, model);
		this.resetEvent(tabPanel, model);
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
			}, {
				name: 'subProcessId',
				fieldLabel: '子流程ID',
				value: model.dom.getAttribute('sub-process-id'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('sub-process-id', newValue);
					}
				}
			}, {
				name: 'subProcessKey',
				fieldLabel: '子流程KEY',
				value: model.dom.getAttribute('sub-process-key'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('sub-process-key', newValue);
					}
				}
			}, {
				name: 'outcome',
				fieldLabel: '外出转移',
				value: model.dom.getAttribute('outcome'),
				listeners: {
					'blur': function(field) {
						var newValue = field.getValue();
						model.dom.setAttribute('outcome', newValue);
					}
				}
			}, {
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
	},

	getParameterInData: function(model) {
		var data = [];
		var elements = model.dom.getElementsByTagName('parameter-in');
		Gef.each(elements, function(item) {
			data.push({
				'var': item.getAttribute('var'),
				subvar: item.getAttribute('subvar')
			});
		});

		return data;
	},

	getParameterOutData: function(model) {
		var data = [];
		var elements = model.dom.getElementsByTagName('parameter-out');
		Gef.each(elements, function(item) {
			data.push({
				'var': item.getAttribute('var'),
				subvar: item.getAttribute('subvar')
			});
		});

		return data;
	},

	resetAdvance: function(tabPanel, model) {
		var Record = Ext.data.Record.create(['var', 'subvar']);

		var inData = this.getParameterInData(model);

		var inStore = new Ext.data.JsonStore({
			fields: Record,
			data: inData,
			listeners: {
				add: function(store, records, index) {
					var record = records[0];
					var onDom = new Gef.model.Dom('parameter-in');
					onDom.setAttribute('var', record.get('var'));
					onDom.setAttribute('subvar', record.get('subvar'));
					model.dom.addElement(onDom);
				},
				remove: function(store, record, index) {
					var elements = model.dom.getElementsByTagName('parameter-in');
					var element = elements[index];
					model.dom.elements.remove(element);
				},
				update: function(store, record, operation) {
					var index = store.indexOf(record);
					var elements = model.dom.getElementsByTagName('parameter-in');
					var element = elements[index];
					element.setAttribute('var', record.get('var'));
					element.setAttribute('subvar', record.get('subvar'));

					this.commitChanges();
				}
			}
		});

		var gridIn = new Ext.grid.EditorGridPanel({
			title: '输入变量',
			store: inStore,
			viewConfig: {
				forceFit: true
			},
			columns: [{
				header: '变量名',
				dataIndex: 'var',
				editor: new Ext.grid.GridEditor(new Ext.form.TextField())
			}, {
				header: '子流程变量',
				dataIndex: 'subvar',
				editor: new Ext.grid.GridEditor(new Ext.form.TextField())
			}],
			tbar: [{
				text: '添加',
				iconCls: 'tb-add',
				handler: function() {
					var p = new Record({
						'var': '',
						subvar: ''
					});
					gridIn.stopEditing();
					var index = gridIn.getStore().getCount();
					gridIn.getStore().insert(index, p);
					gridIn.startEditing(index, 0);
				}
			}, {
				text: '删除',
				iconCls: 'tb-delete',
				handler: function() {
					Ext.Msg.confirm('信息', '确定删除？', function(btn){
						if (btn == 'yes') {
							var sm = gridIn.getSelectionModel();
							var cell = sm.getSelectedCell();

							var record = gridIn.getStore().getAt(cell[0]);
							gridIn.getStore().remove(record);
						}
					});
				}
			}]
		});

		var outData = this.getParameterOutData(model);

		var outStore = new Ext.data.JsonStore({
			fields: Record,
			data: outData,
			listeners: {
				add: function(store, records, index) {
					var record = records[0];
					var onDom = new Gef.model.Dom('parameter-out');
					onDom.setAttribute('var', record.get('var'));
					onDom.setAttribute('subvar', record.get('subvar'));
					model.dom.addElement(onDom);
				},
				remove: function(store, record, index) {
					var elements = model.dom.getElementsByTagName('parameter-out');
					var element = elements[index];
					model.dom.elements.remove(element);
				},
				update: function(store, record, operation) {
					var index = store.indexOf(record);
					var elements = model.dom.getElementsByTagName('parameter-out');
					var element = elements[index];
					element.setAttribute('var', record.get('var'));
					element.setAttribute('subvar', record.get('subvar'));

					this.commitChanges();
				}
			}
		});

		var gridOut = new Ext.grid.EditorGridPanel({
			title: '输出变量',
			store: outStore,
			viewConfig: {
				forceFit: true
			},
			columns: [{
				header: '子流程变量',
				dataIndex: 'subvar',
				editor: new Ext.grid.GridEditor(new Ext.form.TextField())
			}, {
				header: '变量名',
				dataIndex: 'var',
				editor: new Ext.grid.GridEditor(new Ext.form.TextField())
			}],
			tbar: [{
				text: '添加',
				iconCls: 'tb-add',
				handler: function() {
					var p = new Record({
						'var': '',
						subvar: ''
					});
					gridOut.stopEditing();
					var index = gridOut.getStore().getCount();
					gridOut.getStore().insert(index, p);
					gridOut.startEditing(index, 0);
				}
			}, {
				text: '删除',
				iconCls: 'tb-delete',
				handler: function() {
					Ext.Msg.confirm('信息', '确定删除？', function(btn){
						if (btn == 'yes') {
							var sm = gridOut.getSelectionModel();
							var cell = sm.getSelectedCell();

							var record = gridOut.getStore().getAt(cell[0]);
							gridOut.getStore().remove(record);
						}
					});
				}
			}]
		});

		var p = new Ext.TabPanel({
			title: ' 变量映射',
			activeItem: 0,
			items: [gridIn, gridOut]
		});
		tabPanel.add(p);

		this.gridIn = gridIn;
		this.gridOut = gridOut;
	}
});

Ext.ns('App.form');

App.form.TaskForm = Ext.extend(App.form.AbstractForm, {
	assigneeXtype: 'orgfield',
	assigneeXtype2: 'orgTrigger',
	eventNames: [
	    ['start', '开始'],
	    ['end', '结束']
	],
	classNames:[
		['com.uas.erp.service.scm.impl.ScmBeforeEventListener','SCM-Before'],
        ['com.uas.erp.service.scm.impl.ScmAfterEventListener','SCM-After'],
        ['com.uas.erp.service.pm.impl.PmBeforeEventListener','PM-Before'],
        ['com.uas.erp.service.pm.impl.PmAfterEventListener','PM-After'],
        ['com.uas.erp.service.pm.impl.HrBeforeEventListener','HR-Before'],
        ['com.uas.erp.service.pm.impl.HrAfterEventListener','HR-After'],
        ['com.uas.erp.service.pm.impl.OaBeforeEventListener','OA-Before'],
        ['com.uas.erp.service.pm.impl.OaAfterEventListener','OA-After'],
        ['com.uas.erp.service.pm.impl.CrmBeforeEventListener','CRM-Before'],
        ['com.uas.erp.service.pm.impl.CrmAfterEventListener','CRM-After'],
        ['com.uas.erp.service.pm.impl.FaBeforeEventListener','FA-Before'],
        ['com.uas.erp.service.pm.impl.FaAfterEventListener','FA-After'],
        ['com.uas.erp.service.pm.impl.DrpBeforeEventListener','DRP-Before'],
        ['com.uas.erp.service.pm.impl.DrpAfterEventListener','DRP-After']
    ], 
	decorate: function(tabPanel, model) {
		 this.clearItem(tabPanel);
		 this.resetBasic(tabPanel, model);
		 /*this.resetAdvance(tabPanel, model);*/
		 this.resetEvent(tabPanel, model);
	},	                         	               
	                         
    resetBasic: function(tabPanel, model) {
    	 /**任务基本配置 --------*/   	
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
    			 padding: '1px 0 0'
    		 },
    		 buttonAlign :'left',
    		 collapsible :true,
    		 buttons :[],
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
    		 	 name: 'flowtype',
    		 	 fieldLabel: '类型',
    			 value: '',
    			 readOnly:true,
				 listeners: {
				 	 'beforerender':function(field){
				 	 	
				 	 	//初始化类型
				 	 	field.setValue(model.dom.getAttribute('flowtype'));
				 	 	
				 	 	//根据类型分辨
			 	 		if(model.id.indexOf('derive')>=0){//派生流程
			 	 			model.dom.setAttribute('flowtype', 'derive');
			 	 			field.setValue('derive');
			 	 		}
			 	 		if(model.id.indexOf('mission')>=0){//派生任务
			 	 			model.dom.setAttribute('flowtype', 'mission');
			 	 			field.setValue('mission');
			 	 		}
			 	 		if(model.id.indexOf('idea')>=0){//派生意见
			 	 			model.dom.setAttribute('flowtype', 'idea');
			 	 			field.setValue('idea');
			 	 		}
			 	 		if(model.id.indexOf('task')>=0){//普通任务节点
			 	 			model.dom.setAttribute('flowtype', 'task');
			 	 			field.setValue('task');
			 	 		}
				 	 },
					 'blur': function(field) {
						 var newValue = field.getValue();
						 model.dom.setAttribute('flowtype', newValue);
					 }
				 }
    		 }]
    	 });
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
	            	 	 id:'lineName',
	            		 name: 'name',
	            		 fieldLabel: '名称',
	            		 value: model.text ? model.text : '',
        				 listeners: {
        					 'change': function(field) {
        						 var newValue = field.getValue();
        						 // FIXME: use command
        						 if(model.getSource().type == 'task'){
        						 }
        						 model.text = newValue;
        						 model.editPart.figure.updateAndShowText(newValue);
        					 }
        				 }
	            	 });
	            	 var cstore = new Ext.data.SimpleStore({ 
						 fields : ["display", "value"],
						 data : [["普通操作",'Turn'],["提交操作",'Commit'],["派生流程操作",'Flow'],["派生意见操作",'Update'],
						 	     ["派生任务操作",'Task'],["决策操作",'Judge']]
					 });
	            	 items.push({
	            		 name: 'linetype',
	            		 fieldLabel: '类型',
	            		 xtype:'combo',
						 editable:false,
						 allowBlanmodel: false,
						 store:cstore, 
						 value:model.pr_ressubmit?model.pr_ressubmit:'',
						 valueField : "value",
						 displayField : "display",
						 mode : "local",
						 triggerAction : "all",
						 listeners:{
							 'beforerender':function(field){
						 	 	//初始化类型
						 	 	field.setValue(model.dom.getAttribute('linetype'));
						 	 	model.linetype=model.dom.getAttribute('linetype');
						 	 },
							 'blur': function(field) {
								 var newValue = field.getValue();
								 model.dom.setAttribute('linetype', newValue);
								 model.linetype=newValue;
								 if(newValue=='Commit'){
								 	Ext.getCmp('lineName').setValue('提交');
								 	model.text = '提交';
        						 	model.editPart.figure.updateAndShowText('提交');
								 }
							 }
						 }
	            	 });

	            	 if (model.getSource().type == 'decision') {
	            		 items.push({
	            		 	 hidden:true,
	            		 	 hideLabel:true,
	            			 name: 'condition',
	            			 fieldLabel: '条件',
	            			 // <condition expr="#{ACCOUNT&gt;=10000}"/>
	            			 value: 'default',
	            			 listeners: {
	            				 'beforerender': function(field) {
	            					 var newValue = field.getValue();
            						 model.dom.setElementAttribute('condition', 'expr', newValue);
	            				 }
	            			 }
	            		 });
	            	 }

	            	 /* items.push({
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
        });*/   /*transition 的备注域注释掉  */

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

	            	 /*tabPanel.add(grid);*/
	             }
});
