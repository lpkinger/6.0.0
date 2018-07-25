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
	                         ['com.uas.erp.service.pm.impl.DrpAfterEventListener','DRP-After'],
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
			},/* {
                name: 'handler',
                fieldLabel: '决策处理器',
                value: model.dom.getElementAttribute('handler', 'class'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setElementAttribute('handler', 'class', newValue);
                    }
                }
            }, */{
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
				value: model.text ? model.text : '',
						listeners: {
							'blur': function(field) {
								var newValue = field.getValue();
								model.text = newValue;
							}
						}
			},/* {
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
                name: 'state',
                fieldLabel: '结束状态',
                value: model.dom.getAttribute('state'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('state', newValue);
                    }
                }
            },*/ {
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
		/* this.resetEvent(tabPanel, model);*/
		/* this.resetSwimlane(tabPanel, model);*/
		/*  this.resetTimer(tabPanel, model);*/
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
				/*style:'background:#D3D3D3',*/
				value:model.pr_enabled?model.pr_enabled:'',
						valueField : "value",
						displayField : "value",
						mode : "local",
						triggerAction : "all",
						/*value:model.pr_id?model.pr_id:'',*/
						listeners:{
							'blur':function(field){
								var newValue=field.getValue();
								/*model.dom.setElementContent('pr_id', newValue);*/
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
				items: [
//{
//name: 'pr_CatId',
//fieldLabel: '业务目录',
//value: model.pr ? model.procCatName : '',
//allowBlanmodel: false,
//xtype: 'treefield',
//treeConfig: {
//title: '业务目录',
//dataTag: '../console/proccat!getEditTree.do',
//hiddenId: 'processCatalogId',
//hiddenName: 'procCatId',
//handler: function(picmodeler, node) {
//var form = p.getForm();
//var field = form.findField("procCatId");
//field.setValue(node.attributes.dbid);
//field.setRawValue(node.text);

//this.procCatId = node.attributes.dbid;

//model.procCatId = this.procCatId;
//model.pr_CatId = node.text;
//},
//scope: this
//}
//}, 
{
	name: 'pr_defname',
	fieldLabel: '流程名称',
	allowBlanmodel: false,
	value: model.pr_defname ? model.pr_defname : '',
			listeners: {
				'blur': function(field) {
					var newValue = field.getValue();
					/*model.dom.setAttribute('name',newValue);*/
					/*    var editor = Gef.activeEditor;
	                                 var xml = editor.serial();
	                                 console.log(xml);*/
					model.pr_defname = newValue;
					/* model.text = newValue;*/
				}
/*                 'beforerender':function(field){
	                            	   var dataListId=getUrlParam('datalistId');
	                            	   if(dataListId){
	                            		   console.log(parent.Ext.getCmp(dataListId));

	                            	   }
	                               }*/
			}
//xtype: 'combo',
//displayField: 'name',
//valueField: 'name',
//triggerAction: 'all',
//store: new Ext.data.Store({
//url: '../console/procver!getProcDefByProcCat.do',
//reader: new Ext.data.JsonReader({
//root: ''
//}, ['id', 'name', 'code']),
//listeners: {
//beforeload: function() {
//var form = p.getForm();
//var field = form.findField("procDefName");
//field.store.baseParams.procCatId = this.procCatId;
//},
//scope: this
//}
//}),
//listeners: {
//select: function(combo, record) {
//var form = p.getForm();
//this.procDefId = record.get('id');
//form.findField('procDefCode').setValue(record.get('code'));

//model.procDefId = record.get('id');
//model.procDefCode = record.get('code');
//},
//scope: this
//}
}/*,{
	            	name:'pr_constructor',
	            	id:'pr_constructor',
	            	fieldLabel:'操作人',
	            	readOnly:true,
                	style:'background:#D3D3D3',
	            	//allowBlanmodel: false,
	            	value:model.pr_constructor?model.pr_constructor:'',
	            	listeners: {
	            				'blur': function(field) {
	            				var newValue = field.getValue();
	            				model.pr_constructor = newValue;


	            			},
	            	}		

	              }*/,{
	            	  name: 'pr_caller',
	            	  fieldLabel: '关联表单(caller)',
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
	              }/*,{
	                name: 'pr_vername',
	                fieldLabel: '版本号',
	                value: model.pr_vername ? model.pr_verName : '',
	                xtype: 'combo',
	                displayField: 'name',
	                valueField: 'name',
	                triggerAction: 'all',
	                store: new Ext.data.Store({
	                    url: '../console/procver!getProcVerByProcDef.do',
	                    reader: new Ext.data.JsonReader({
	                        root: ''
	                    }, ['name']),
	                    listeners: {
	                        beforeload: function() {
	                            var form = p.getForm();
	                            var field = form.findField("pr_VerName");
	                            field.store.baseParams.procDefId = this.procDefId;
	                        },
	                        scope: this
	                    }
	                }),
	                listeners: {
	                    'blur': function(field) {
	                        var newValue = field.getValue();
	                        model.pr_VerName = newValue;
	                    }
	                }
	            }*/,combo, 
                {
	            	xtype:'combo',
                	name:'pr_ressubmit',
                	fieldLabel:'限制反提交',
					editable:false,
					allowBlanmodel: false,
					store:cstore, 
					value:model.pr_ressubmit?model.pr_ressubmit:'',
							valueField : "value",
							displayField : "value",
							mode : "local",
							triggerAction : "all",
							listeners:{
								'blur':function(field){
									var newValue=field.getValue();
									model.pr_ressubmit=newValue;
								}
							}
                },{
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
				/*style:'background:#D3D3D3',*/
				value:k.pr_enabled?k.pr_enabled:'',
						valueField : "value",
						displayField : "value",
						mode : "local",
						triggerAction : "all",
						/*value:model.pr_id?model.pr_id:'',*/
						listeners:{
							'blur':function(field){
								var newValue=field.getValue();
								/*model.dom.setElementContent('pr_id', newValue);*/
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
				items: [
//{
//name: 'pr_CatId',
//fieldLabel: '业务目录',
//value: model.pr ? model.procCatName : '',
//allowBlank: false,
//xtype: 'treefield',
//treeConfig: {
//title: '业务目录',
//dataTag: '../console/proccat!getEditTree.do',
//hiddenId: 'processCatalogId',
//hiddenName: 'procCatId',
//handler: function(picker, node) {
//var form = p.getForm();
//var field = form.findField("procCatId");
//field.setValue(node.attributes.dbid);
//field.setRawValue(node.text);

//this.procCatId = node.attributes.dbid;

//model.procCatId = this.procCatId;
//model.pr_CatId = node.text;
//},
//scope: this
//}
//}, 
{
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
//xtype: 'combo',
//displayField: 'name',
//valueField: 'name',
//triggerAction: 'all',
//store: new Ext.data.Store({
//url: '../console/procver!getProcDefByProcCat.do',
//reader: new Ext.data.JsonReader({
//root: ''
//}, ['id', 'name', 'code']),
//listeners: {
//beforeload: function() {
//var form = p.getForm();
//var field = form.findField("procDefName");
//field.store.baseParams.procCatId = this.procCatId;
//},
//scope: this
//}
//}),
//listeners: {
//select: function(combo, record) {
//var form = p.getForm();
//this.procDefId = record.get('id');
//form.findField('procDefCode').setValue(record.get('code'));

//model.procDefId = record.get('id');
//model.procDefCode = record.get('code');
//},
//scope: this
//}
},/*{
                	name:'pr_constructor',
                	id:'pr_constructor',
                	fieldLabel:'操作人',
                	readOnly:true,
                	style:'background:#D3D3D3',
                	//allowBlank: false,
                	value:k.pr_constructor?k.pr_constructor:'',
                	listeners: {
                				'blur': function(field) {
                				var newValue = field.getValue();
                				k.pr_constructor = newValue;


                			},
                	}		

                  },{
                	name:'pr_id',
                	fieldLabel:'流程Id',
                	//disabled:true,
                	readOnly:true,
                	style:'background:#D3D3D3',
                	//value:model.pr_Id?model.pr_Id:'',
                	value:k.pr_id?k.pr_id:'',
                	listeners:{
                		'blur':function(field){
                			var newValue=field.getValue();
                			 model.dom.setElementContent('pr_id', newValue);
                			k.pr_id=newValue;
                		}
                	}
                 },*/{
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
},/*{
                    name: 'pr_vername',
                    fieldLabel: '版本号',
                    value: k.pr_vername ? k.pr_verName : '',
                    xtype: 'combo',
                    displayField: 'name',
                    valueField: 'name',
                    triggerAction: 'all',
                    store: new Ext.data.Store({
                        url: '../console/procver!getProcVerByProcDef.do',
                        reader: new Ext.data.JsonReader({
                            root: ''
                        }, ['name']),
                        listeners: {
                            beforeload: function() {
                                var form = p.getForm();
                                var field = form.findField("pr_VerName");
                                field.store.baseParams.procDefId = this.procDefId;
                            },
                            scope: this
                        }
                    }),
                    listeners: {
                        'blur': function(field) {
                            var newValue = field.getValue();
                            model.pr_VerName = newValue;
                        }
                    }
                },*/ combo, {
					xtype:'combo',
					name:'pr_ressubmit',
					fieldLabel:'限制反提交',
					editable:false,
					allowBlanmodel: false,
					store:cstore, 			
					value:k.pr_ressubmit?k.pr_ressubmit:'',
					valueField : "value",
					displayField : "value",
					mode : "local",
					triggerAction : "all",			
					listeners:{
						'blur':function(field){
							var newValue=field.getValue();
							k.pr_ressubmit=newValue;
				}
			}
		},           
    {
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
				value: model.text ? model.text : '',
						listeners: {
							'blur': function(field) {
								var newValue = field.getValue();
								model.text = newValue;
							}
						}
			}/*, {
                name: 'form',
                fieldLabel: '表单',
                value: model.dom.getAttribute('form'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('form', newValue);
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
	             /*['assign', '任务分配']*/  //  查找一下 看有没有这个事件，有的话最好。
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
	                         decorate: function(tabPanel, model) {
	                        	 this.clearItem(tabPanel);
	                        	 this.resetBasic(tabPanel, model);
	                        	 this.resetAdvance(tabPanel, model);
	                        	 this.resetEvent(tabPanel, model);
	                         },	                         	               
	                         
	                         resetBasic: function(tabPanel, model) {
	                        	 
	                             function resetReceiver(newValue,attribute){
	                				 if(typeof (newValue) == 'string'){
	                					 if(newValue.search(/,/)!=-1){
	                						 var arr2 = []; 
	                						 var arr = newValue.split(",");
	                						 var l = arr.length;
	                						 for(var i=0;i<l;i++){
	                							 var start = arr[i].indexOf("(");
	                							 var end= arr[i].indexOf(")");
	                							 fixedValue =(arr[i].indexOf("(")>0 && arr[i].indexOf(")")>0) ? arr[i].substring(start+1,end) : arr[i];
	                							 arr2.push(fixedValue);
	                						 }
	                						 newValue=arr2.join(",");
	                					 }else{
	                						 var start = newValue.indexOf("(");
	                						 var end= newValue.indexOf(")");
	                						 fixedValue =(newValue.indexOf("(")>0 && newValue.indexOf(")")>0) ? newValue.substring(start+1,end) : newValue;
	                						 newValue=fixedValue;
	                					 }
	                					 if(newValue!=''){                						  
	                						 model.dom.removeAttribute('assignee'); 
	                    					 model.dom.removeAttribute('candidate-groups');
	                    					 model.dom.removeAttribute('departjob-groups');
	                    					 model.dom.removeAttribute('rolAssignee');
	                    					 model.dom.removeAttribute('sqlAssignee'); 
	                    					 model.dom.setAttribute(attribute,newValue);	                      				                    					
	                					 }
	                					 Ext.getCmp("assignee").setValue(model.dom.getAttribute('assignee'));
                    					 Ext.getCmp("candidate").setValue(model.dom.getAttribute('candidate-groups'));
                    					 Ext.getCmp("departjob").setValue(model.dom.getAttribute('departjob-groups'));
                    					 Ext.getCmp("rolAssignee").setValue('');
                    					 Ext.getCmp("sqlAssignee").setValue(null);	
	                				 }               			  	                        	 
		                         }
	                        	 
	                        	 var r1 =  new Ext.form.Radio({
	                        		 xtype:'radio',
	                        		 id:'r1',
	                        		 fieldLabel :'接收人',
	                        		 boxLabel:'人员',
	                        		 id:"r1",
	                        		 name : "receiver",
	                        		 listeners: {
	                        			 'beforerender':function(){
	                        				 if(!model.dom.getAttribute('candidate-groups')&& !model.dom.getAttribute('departjob-groups') && !model.dom.getAttribute('rolAssignee') && !model.dom.getAttribute('sqlAssignee')){
	                        					 this.checked = true;
	                        				 }
	                        			 }
	                        		 }
	                        	 });

	                        	 var r2 = new Ext.form.Radio({
	                        		 xtype:'radio',
	                        		 id:'r2',
	                        		 boxLabel:'岗位',
	                        		 name : "receiver",
	                        		 listeners: {
	                        			 'beforerender':function(){
	                        				 if(model.dom.getAttribute('candidate-groups')){
	                        					 this.checked = true;
	                        				 }
	                        			 }
	                        		 }
	                        	 });

	                        	 var r5 = new Ext.form.Radio({
	                        		 xtype:'radio',
	                        		 id:'r5',
	                        		 boxLabel:'组织->岗位',
	                        		 name : "receiver",
	                        		 listeners: {
	                        			 'beforerender':function(){
	                        				 if(model.dom.getAttribute('departjob-groups')){
	                        					 this.checked = true;
	                        				 }
	                        			 }
	                        		 }
	                        	 });
	                        	 
	                        	 var r3 = new Ext.form.Radio({
	                        		 xtype:'radio',
	                        		 id:'r3',
	                        		 boxLabel:'角色',
	                        		 name : "receiver",
	                        		 listeners: {
	                        			 'beforerender':function(){
	                        				 if(model.dom.getAttribute('rolAssignee')){
	                        					 this.checked = true;
	                        				 }
	                        			 }
	                        		 }
	                        	 });
	                        	 var r4 =  new Ext.form.Radio({
	                        		 xtype:'radio',
	                        		 id:'r4',
	                        		 fieldLabel :'',
	                        		 boxLabel:'SQL选人设置',
	                        		 name : "receiver",
	                        		 listeners: {
	                        			 'beforerender':function(){
	                        				 if(model.dom.getAttribute('sqlAssignee')){
	                        					 this.checked = true;
	                        				 }
	                        			 }
	                        		 }
	                        	 });
	                        	 var assignee = new Gef.org.OrgField ({
	                        		 name: 'assignee',
	                        		 fieldLabel: '分配人',
	                        		 xtype: this.assigneeXtype,
	                        		 id:'assignee',
	                        		 /* hidden:'true',*/
	                        		 value: model.dom.getAttribute('assignee'),
	                        		 listeners: {
	                        			 'select': function(field) {
	                        				 resetReceiver(field.getValue(),'assignee');
	                        			 },
	                        			 'change':function(field){
	                        				 resetReceiver(field.getValue(),'assignee');	                      				
	                        			 },
	                        			 'beforerender':function(){
	                        				 if(!model.dom.getAttribute('assignee')){
	                        					 //Ext.getCmp("assignee").setVisible(false);
	                        				 }

	                        			 }
	                        		 }
	                        	 });
	                        	 var candidateGroups = new Gef.org.OrgField({
	                        		 name: 'candidateGroups',
	                        		 fieldLabel: '岗位',
	                        		 xtype: this.assigneeXtype,
	                        		 id:'candidate',
	                        		 /* xtype: 'orgTrigger',*/
	                        		 value: model.dom.getAttribute('candidate-groups'),
	                        		 listeners: {
	                        			 'select': function(field) {
	                        				 resetReceiver(field.getValue(),'candidate-groups');
	                        			 },
	                        			 'change':function(field){
	                        				 resetReceiver(field.getValue(),'candidate-groups');	                      				
	                        			 },
	                        			 'beforerender':function(){
	                        				 if(!model.dom.getAttribute('candidate-groups')){
	                        					 this.setVisible(false);
	                        				 }else{
	                        					 /*r2.fireEvent('check',r2,true);*/
	                        				 }

	                        			 }
	                        		 }

	                        	 });
	                        	 //部门岗位选择
	                         	 var departjobGroups = new Gef.org.OrgField({
	                        		 name: 'departjobGroups',
	                        		 fieldLabel: '组织->岗位',
	                        		 xtype: this.assigneeXtype,
	                        		 id:'departjob',
	                        		 value: model.dom.getAttribute('departjob-groups'),
	                        		 listeners: {
	                        			 'select': function(field) {
	                        				 resetReceiver(field.getValue(),'departjob-groups');
	                        			 },
	                        			 'change':function(field){
	                        				 resetReceiver(field.getValue(),'departjob-groups');	                      				
	                        			 },
	                        			 'beforerender':function(){
	                        				 if(!model.dom.getAttribute('departjob-groups')){
	                        					 this.setVisible(false);
	                        				 }else{
	                        					 /*r2.fireEvent('check',r2,true);*/
	                        				 }
	                        			 }
	                        		 }

	                        	 });
	                        	 //角色人员选择
	                        	 var candidateUsers = new Ext.form.ComboBox({
	                        		 name: 'rolAssignee',
	                        		 fieldLabel: '角色',
	                        		 id:'rolAssignee',
	                        		 editable:false,
	                        		 value: model.dom.getAttribute('rolAssignee'),
	                        		 displayField: 'display',
	                        		 valueField: 'value',
	                        		 //editable:true,
	                        		 mode : "local",    
	                        		 triggerAction : "all", 
	                        		 readOnly:true,
	                        		 store: new Ext.data.SimpleStore({ 
	                        			 fields : ["display", "value"],
	                        			 data :[['上节点组织领导','组织负责领导'],
	                        			        ['上节点父组织负责领导','上一步父组织负责领导'],
	                        			        ['上节点岗位领导','岗位直属领导'],
	                        			        ['上上节点岗位领导','上一步岗位直属领导'],
	                        			        ['部门领导人','部门领导人'],
	                        			        ['<font color="red">取消选择</font>',null]
	                        			 ]
	                        		 }),
	                        		 listeners: {
	                        			 'beforerender':function(){
	                        				 if(!model.dom.getAttribute('rolAssignee')){
	                        					 this.setVisible(false);                		
	                        				 };

	                        			 },
	                        			 'select': function(field) {
	                        				 var newValue = field.getValue(); 
	                        				 if(newValue){
	                        					 model.dom.setAttribute('rolAssignee',newValue); 
	                        					 model.dom.removeAttribute('assignee');
	                        					 model.dom.removeAttribute('candidate-groups');
	                        					 model.dom.removeAttribute('departjob-groups');
	                        					 model.dom.removeAttribute('sqlAssignee');
	                        					 Ext.getCmp("assignee").setValue(model.dom.getAttribute('assignee'));
		                    					 Ext.getCmp("candidate").setValue(model.dom.getAttribute('candidate-groups'));
		                    					 Ext.getCmp("departjob").setValue(model.dom.getAttribute('departjob-groups'));
		                    					 Ext.getCmp("sqlAssignee").setValue(null);
	                        				 } else {
                        						 field.setValue('');
                        						 model.dom.removeAttribute('rolAssignee');
                        					 }	                       					 
	                        			 }
	                        		 }
	                        	 });
	                        	 var sqlAssignee = new Ext.form.TextField({
	                        		 fieldLabel: 'SQL取人设置',
	                        		 name: 'field4',
	                        		 id:'sqlAssignee',
	                        		 value:model.dom.getAttribute('sqlAssignee'),
	                        		 listeners: {
	                        			 'blur': function(field) {
	                        				 var newValue = field.getValue();
	                        				 if(newValue!='' && (model.dom.getAttribute('assignee')||model.dom.getAttribute('candidate-groups')||model.dom.getAttribute('rolAssignee')||model.dom.getAttribute('departjob-groups'))){
	                        					 model.dom.setAttribute('sqlAssignee',newValue); 
	                        					 model.dom.removeAttribute('assignee');
	                        					 model.dom.removeAttribute('candidate-groups');
	                        					 model.dom.removeAttribute('rolAssignee');
	                        					 model.dom.removeAttribute('departjob-groups');
	                        					 Ext.getCmp("assignee").setValue(model.dom.getAttribute('assignee'));
		                    					 Ext.getCmp("candidate").setValue(model.dom.getAttribute('candidate-groups'));
		                    					 Ext.getCmp("departjob").setValue(model.dom.getAttribute('departjob-groups'));
		                    					 Ext.getCmp("rolAssignee").setValue('');
	                        				 } else  model.dom.setAttribute('sqlAssignee',newValue); 		                       				
	                        			 },
	                        			 'beforerender':function(field){
	                        				 if(!model.dom.getAttribute('sqlAssignee')){
	                        					 this.setVisible(false);

	                        				 };
	                        			 }

	                        		 }


	                        	 });	
	                        	 var fieldSet = new Ext.form.FieldSet({
	                        		 title: '特殊设置',
	                        		 collapsible: true,
	                        		 autoHeight : true,
	                        		 checkboxToggle : true,
	                        		 checkboxName :'',
	                        		 defaults: {
	                        			 anchor: '90%'
	                        		 },
	                        		 /*layout:'column',*/
	                        		 /*defaultType: 'textfield',*/
	                        		 items :[/*new Ext.form.NumberField({
                		fieldLabel: '限办时间(小时)',
                		name: 'field1',
                		value:1,
                		value:  0,  model.dom.getElementByTagName('reminder').getAttribute('duedate'),
                		listeners: {
                            'blur': function(field) {
                            	var newValue = field.getValue();
                            	console.log(model);
                            	var el = model.dom.getElementByTagName('reminder');
                            	if(!el){
                            		var reminder = new Gef.model.Dom('reminder');
                                	reminder.setAttribute('duedate',newValue);
                                	model.dom.addElement(reminder);
                                	var v = model.dom.getElementByTagName('reminder').getAttribute('duedate');
                                	console.log(typeof(v));


                            	}else{
                            		el.setAttribute('duedate',newValue);
                            	}

                            },
                            'beforerender':function(field){
                            	var el = model.dom.getElementByTagName('reminder');
                            	if(el){
                            		var v = model.dom.getElementByTagName('reminder').getAttribute('duedate');
                            		field.setValue(v);
                            	}
                            }

                        }


            }), new Ext.form.NumberField({
                	fieldLabel: '提醒间隔(小时)',
                	name: 'field2',
                	value:1,
                	listeners: {
                        'blur': function(field) {
                        	var newValue = field.getValue();
                        	var el = model.dom.getElementByTagName('reminder');
                        	if(!el){
                        		var reminder = new Gef.model.Dom('reminder');
                            	reminder.setAttribute('repeat',newValue);
                            	model.dom.addElement(reminder);
                            	var v = model.dom.getElementByTagName('reminder').getAttribute('repeat');                            	                            	
                        	}else{
                        		el.setAttribute('repeat',newValue);
                        	}

                        },
                        'beforerender':function(field){
                        	var el = model.dom.getElementByTagName('reminder');
                        	if(el){
                        		var v = model.dom.getElementByTagName('reminder').getAttribute('repeat');
                        		console.log(v);
                        		field.setValue(v);
                        	}
                        }

                    }

            }),*/new Ext.form.NumberField({
        		fieldLabel: '限办时间(小时)',
        		name: 'field1',
        		value:model.dom.getAttribute('duration'),
        		//value:  0,  model.dom.getElementByTagName('reminder').getAttribute('duedate'),
        		listeners: {
                    'afterrender': function(field) {
                    	var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('duration');
            			if(!el){
            				var button = new Gef.model.Dom('duration');
            				button.setAttribute('duration',button);
            				model.dom.addElement(button);
            			}else{
            				el.setAttribute('duration',newValue);
            			}
                    },
                    'blur':function(field){
                    	var newValue = field.getValue(); 
            			model.dom.setAttribute('duration',newValue);
                    }
                }
    }),{
            	xtype:'combo',
            	id:'approve',
            	name:'approve',
            	fieldLabel:'只能同意',
            	store: new Ext.data.SimpleStore({ 
            		fields : ["display", "value"],
            		data :[['是',1],['否',0]]
            	}),
            	value: model.dom.getAttribute('approve'),
            	displayField: 'display',
            	valueField: 'value',
            	editable:false,
            	mode : "local",    
            	triggerAction : "all", 
            	readOnly:true,
            	listeners:{
            		'afterrender':function(field){
            			var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('approve');
            			if(!el){
            				var button = new Gef.model.Dom('approve');
            				button.setAttribute('approve',button);
            				model.dom.addElement(button);

            			}else{
            				el.setAttribute('approve',newValue);
            			}
            		},
            		'select': function(field) {
            			var newValue = field.getValue(); 
            			model.dom.setAttribute('approve',newValue);
            		}
            	}
            },{
            	xtype:'combo',
            	id:'sendmsg',
            	name:'sendmsg',
            	fieldLabel:'短信提醒',
            	store: new Ext.data.SimpleStore({ 
            		fields : ["display", "value"],
            		data :[['是',1],['否',0]]
            	}),
            	value: model.dom.getAttribute('sendmsg'),
            	displayField: 'display',
            	valueField: 'value',
            	editable:false,
            	/*  width:100,*/
            	mode : "local",    
            	triggerAction : "all", 
            	readOnly:true,
            	listeners:{
            		'afterrender':function(field){
            			var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('sendmsg');
            			if(!el){
            				var button = new Gef.model.Dom('sendmsg');
            				button.setAttribute('sendmsg',button);
            				model.dom.addElement(button);

            			}else{
            				el.setAttribute('sendmsg',newValue);
            			}
            		},
            		'select': function(field) {
            			var newValue = field.getValue(); 
            			model.dom.setAttribute('sendmsg',newValue);
            		}
            	}
            },{
            	xtype:'combo',
            	id:'extra',
            	name:'extra',
            	fieldLabel:'额外指定',
            	store: new Ext.data.SimpleStore({ 
            		fields : ["display", "value"],
            		data :[['是',1],['否',0]]
            	}),
            	value: model.dom.getAttribute('extra'),
            	displayField: 'display',
            	valueField: 'value',
            	editable:false,
            	mode : "local",    
            	triggerAction : "all", 
            	readOnly:true,
            	listeners:{
            		'afterrender':function(field){
            			var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('extra');
            			if(!el){
            				var button = new Gef.model.Dom('smsalert');
            				button.setAttribute('extra',button);
            				model.dom.addElement(button);

            			}else{
            				el.setAttribute('extra',newValue);
            			}
            		},
            		'select': function(field) {
            			var newValue = field.getValue(); 
            			model.dom.setAttribute('extra',newValue);
            		}
            	}
            },/*{
            	xtype:'combo',
            	id:'smsalert',
            	name:'smsalert',
            	fieldLabel:'逻辑判定',
            	store: new Ext.data.SimpleStore({ 
            		fields : ["display", "value"],
            		data :[['是',1],['否',0]]
            	}),
            	value: model.dom.getAttribute('smsalert'),
            	displayField: 'display',
            	valueField: 'value',
            	editable:false,
//            	width:0,
            	hidden:true,
            	hiddenLable:true,
            	mode : "local",    
            	triggerAction : "all", 
            	readOnly:true,
            	listeners:{
            		'afterrender':function(field){
            			var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('smsalert');
            			if(!el){
            				var button = new Gef.model.Dom('smsalert');
            				button.setAttribute('smsalert',button);
            				model.dom.addElement(button);

            			}else{
            				el.setAttribute('smsalert',newValue);
            			}
            		},
            		'select': function(field) {
            			var newValue = field.getValue(); 
            			model.dom.setAttribute('smsalert',newValue);
            		}
            	}
            },*/{
            	xtype:'textarea',
            	fieldLabel:'开始',
            	id:'exebefore',
            	name:'exebefore',
            	value: model.dom.getAttribute('exebefore'),
            	listeners: {
            		'afterrender':function(field){
            			var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('exebefore');
            			if(!el){
            				var neccessaryField = new Gef.model.Dom('exebefore');
            				neccessaryField.setAttribute('exebefore',button);
            				model.dom.addElement(exeafter);

            			}else{
            				el.setAttribute('exebefore',newValue);
            			}
            		},
            		'blur': function(field) {
            			var newValue = field.getValue();
            			model.dom.setAttribute('exebefore', newValue);
            		}
            	}
            },{
            	xtype:'textarea',
            	fieldLabel:'结束',
            	id:'exeafter',
            	name:'exeafter',
            	value: model.dom.getAttribute('exeafter'),
            	listeners: {
            		'afterrender':function(field){
            			var newValue = field.getValue();   	             
            			var el = model.dom.getElementByTagName('exeafter');
            			if(!el){
            				var neccessaryField = new Gef.model.Dom('exeafter');
            				alert(button);
            				neccessaryField.setAttribute('exeafter',button);
            				model.dom.addElement(exeafter);

            			}else{
            				el.setAttribute('exeafter',newValue);
            			}
            		},
            		'blur': function(field) {
            			var newValue = field.getValue();
            			model.dom.setAttribute('exeafter', newValue);
            		}
            	}
            }]
	                        	 });
	                        	 var notifySet = new Ext.form.FieldSet({
	                        		 title: '知会设置',
	                        		 collapsible: true,
	                        		 autoHeight : true,
	                        		 checkboxToggle : true,
	                        		 checkboxName :'',
	                        		 defaults: {
	                        			 anchor: '90%'
	                        		 },
	                        		 /*layout:'column',*/
	                        		 /*defaultType: 'textfield',*/
	                        		 items :[new Gef.org.OrgField({
	                        			 name: 'notifyGroups',
	                        			 fieldLabel: '岗位',
	                        			 xtype: this.assigneeXtype,
	                        			 id:'notifyGroups',
	                        			 /* xtype: 'orgTrigger',*/
	                        			 value: model.dom.getAttribute('notifyGroups'),
	                        			 listeners: {
	                        				 'select': function(field) {

	                        					 var newValue = field.getValue();

	                        					 if(typeof (newValue) == 'string'){
	                        						 if(newValue.search(/,/)!=-1){
	                        							 var arr2 = []; 
	                        							 var arr = newValue.split(",");
	                        							 var l = arr.length;
	                        							 for(var i=0;i<l;i++){
	                        								 console.log(arr[i]);
	                        								 var start = arr[i].indexOf("(");
	                        								 var end= arr[i].indexOf(")");
	                        								 fixedValue = arr[i].substring(start+1,end);
	                        								 arr2.push(fixedValue);
	                        							 }
	                        							 model.dom.setAttribute('notifyGroups', arr2.join(","));
	                        						 }else{
	                        							 var start = newValue.indexOf("(");
	                        							 var end= newValue.indexOf(")");
	                        							 fixedValue = newValue.substring(start+1,end);
	                        							 model.dom.setAttribute('notifyGroups',fixedValue);
	                        						 }
	                        					 }

	                        				 },
	                        				 'afterrender':function(field){
	                        					 var newValue = field.getValue();                	
	                        					 var el = model.dom.getElementByTagName('notifyGroups');
	                        					 if(!el){
	                        						 var notifyGroups = new Gef.model.Dom('notifyGroups');
	                        						 notifyGroups.setAttribute('notifyGroups',notifyGroups);
	                        						 model.dom.addElement(notifyGroups);
	                        						 /*	var v = model.dom.getElementByTagName('reminder').getAttribute('duedate');
                    	console.log(typeof(v));*/
	                        					 }else{
	                        						 el.setAttribute('notifyGroups',newValue);
	                        					 }
	                        				 },
	                        				 'blur': function(field) {
	                        					 var newValue = field.getValue();
	                        					 model.dom.setAttribute('notifyGroups', newValue);
	                        				 }
	                        
	                        			 }

	                        		 }),new Gef.org.OrgField({
	                        			 name: 'notifyPeople',
	                        			 fieldLabel: '知会人员',
	                        			 xtype: this.assigneeXtype,
	                        			 id:'notifyPeople',
	                        			 /* xtype: 'orgTrigger',*/
	                        			 value: model.dom.getAttribute('notifyPeople'),
	                        			 listeners: {
	                        				 'select': function(field) {
	                        					 var newValue = field.getValue();
	                        					 if(typeof (newValue) == 'string'){
	                        						 if(newValue.search(/,/)!=-1){
	                        							 var arr2 = []; 
	                        							 var arr = newValue.split(",");
	                        							 var l = arr.length;
	                        							 for(var i=0;i<l;i++){
	                        								 console.log(arr[i]);
	                        								 var start = arr[i].indexOf("(");
	                        								 var end= arr[i].indexOf(")");
	                        								 fixedValue = arr[i].substring(start+1,end);
	                        								 arr2.push(fixedValue);
	                        							 }
	                        							 model.dom.setAttribute('notifyPeople', arr2.join(","));
	                        						 }else{
	                        							 var start = newValue.indexOf("(");
	                        							 var end= newValue.indexOf(")");
	                        							 fixedValue = newValue.substring(start+1,end);
	                        							 model.dom.setAttribute('notifyPeople',fixedValue);
	                        						 }
	                        					 }
	                        				 },
	                        				 'afterrender':function(field){
	                        					 var newValue = field.getValue();                	
	                        					 var el = model.dom.getElementByTagName('notifyPeople');
	                        					 if(!el){
	                        						 var notifyPeople = new Gef.model.Dom('notifyPeople');
	                        						 notifyPeople.setAttribute('notifyPeople',notifyPeople);
	                        						 model.dom.addElement(notifyPeople);
	                        						 /*	var v = model.dom.getElementByTagName('reminder').getAttribute('duedate');
                     	console.log(typeof(v));*/
	                        					 }else{
	                        						 el.setAttribute('notifyPeople',newValue);
	                        					 }
	                        				 },
	                        				 'blur': function(field) {
	                        					 var newValue = field.getValue();
	                        					 model.dom.setAttribute('notifyPeople', newValue);
	                        				 }
	                        			 }
	                        		 }),{	name: 'notifySql',
	                     				fieldLabel: 'SQL知会设置',
	                    				xtype: 'textarea',
	                    				value: model.dom.getAttribute('notifySql'),
	                    				listeners: {
	                    					'blur': function(field) {
	                    						var newValue = field.getValue();
	                    						model.dom.setAttribute('notifySql', newValue);
	                    					}
	                    				}
	                    			}]
	                        	 });
	                        	 var buttonSet = new Ext.form.FieldSet({
	                        		 title: '按钮设置',
	                        		 collapsible: true,
	                        		 autoHeight : true,
	                        		 checkboxToggle : true,
	                        		 checkboxName :'',
	                        		 defaults: {
	                        			 anchor: '90%'
	                        		 },
	                        		 /*layout:'column',*/
	                        		 /*defaultType: 'textfield',*/

	                        		 items :[{
	                        			 xtype:'combo',
	                        			 id:'specialbutton',
	                        			 name:'specialbutton',
	                        			 fieldLabel:'选择按钮',
	                        			 store: new Ext.data.SimpleStore({ 
	                        				 fields : ["display", "value"],
	                        				 data :comboxdata
	                        			 }),
	                        			 value: model.dom.getAttribute('specialbutton'),
	                        			 displayField: 'display',
	                        			 valueField: 'value',
	                        			 editable:true,
	                        			 /*  width:100,*/
	                        			 mode : "local",    
	                        			 triggerAction : "all", 
	                        			 listeners:{
	                        				 'afterrender':function(field){
	                        					 var newValue = field.getValue();   	             
	                        					 var el = model.dom.getElementByTagName('specialbutton');
	                        					 if(!el){
	                        						 var button = new Gef.model.Dom('specialbutton');
	                        						 button.setAttribute('specialbutton',button);
	                        						 model.dom.addElement(button);
	                        					 }else{
	                        						 el.setAttribute('specialbutton',newValue);
	                        					 }
	                        				 },
	                        				 'select': function(field) {
	                        					 var newValue = field.getValue(); 
	                        					 if(!newValue) {
	                        						 field.setValue('');
	                        						 model.dom.removeAttribute('specialbutton');
	                        					 }
	                        					 else model.dom.setAttribute('specialbutton',newValue);
	                        				 }
	                        			 }
	                        		 },{
	                        			 xtype:'textarea',
	                        			 fieldLabel:'必填字段',
	                        			 id:'necessaryField',
	                        			 name:'neccessaryField',
	                        			 value: model.dom.getAttribute('neccessaryField'),
	                        			 listeners: {
	                        				 'afterrender':function(field){
	                        					 var newValue = field.getValue();   	             
	                        					 var el = model.dom.getElementByTagName('neccessaryField');
	                        					 if(!el){
	                        						 var neccessaryField = new Gef.model.Dom('neccessaryField');
	                        						 neccessaryField.setAttribute('neccessaryField',button);
	                        						 model.dom.addElement(neccessaryField);

	                        					 }else{
	                        						 el.setAttribute('neccessaryField',newValue);
	                        					 }
	                        				 },
	                        				 'blur': function(field) {
	                        					 var newValue = field.getValue();
	                        					 model.dom.setAttribute('neccessaryField', newValue);
	                        				 }
	                        			 }
	                        		 }]
	                        	 });
	                        	 
	                        	 var jprocessRuleSet = new Ext.form.FieldSet({
	                        		 title: '智能审批设置',
	                        		 collapsible: true,
	                        		 autoHeight : true,
	                        		 checkboxToggle : true,
	                        		 checkboxName :'',
	                        		 defaults: {
	                        			 anchor: '90%'
	                        		 },
	                        		 items :[/*{
	                        		 	 fieldLabel:'规则选择',
	                        		 	 xtype:'textfield',
	                        		 	 id:'RU_ID',
	                        		 	 value:model.dom.getAttribute('jprocessRuleId'),
	                        		 	 listeners:{
	                        		 	 	datachange:function(value){
	                        		 	 		var me = this;
	                        		 	 		me.setValue(value);
	                        		 	 		model.dom.setAttribute('jprocessRuleId',value);
	                        		 	 	}
	                        		 	 }
	                        		 },*/new Ext.form.ComboBox({
	                        		 	 fieldLabel:'规则选择',
	                        		 	 triggerClass:'x-form-search-trigger',
	                        		 	 editable:false,
	                        		 	 id:'RU_ID',
	                        		 	 value:model.dom.getAttribute('jprocessRuleId'),
	                        		 	 onTriggerClick:function(trigger){
	                        		 		 var me = this;
	                        		 		 var jprocessRuleSet =  me.ownerCt;
	                        		 		 var ruleid = Ext.getCmp('RU_ID').value;
	                						 jprocessRuleSet.getRules(formCaller,ruleid);
	                        		 	 },
	                        		 	 listeners:{
	                        		 	 	datachange:function(value){
	                        		 	 		var me = this;
	                        		 	 		me.setValue(value);
	                        		 	 		model.dom.setAttribute('jprocessRuleId',value);
	                        		 	 	}
	                        		 	 }
	                        		 })],
	                        		 showRules:function(rules,ruleid){
										rules = this.pushSelectedRuleFirst(rules,ruleid);
										var searchKey = "";
	                        		 	var win = new Ext.Window({
	                        		 		title:'规则选择',
								            height:window.innerHeight*0.65,
								            width: 600,
	                        		 		layout:'border',
	                        		 		bodyStyle:'background:white',
	                        		 		modal: true,
	                        		 		items:[{
												xtype:'form',
												layout:'form',
												id:'ruleSearch',
												region:'north',
												//height:80,
												width:100,
												autoHeight:true,
												margin:2,
												items:[new Ext.form.TriggerField({
													id:'searchKey',
													emptyText:'搜索',
				                        		 	fieldLabel:'',
				                        		 	labelSeparator:'',
				                        		 	labelStyle:'width:0!important;',
				                        		 	triggerClass:'x-form-search-trigger',
				                        		 	onTriggerClick:function(trigger){
				                        		 		var me = this;
				                        		 		me.search(me.getValue());
				                        		 	},
				                        		 	//hideTrigger:true,
				                        		 	listeners:{
				                        		 		render:function(){
				                        		 			var me = this;
				                        		 			this.el.on('keyup',function(e,input){
				                        		 				me.search(input.value)
				                        		 			});
				                        		 		}
				                        		 	},
				                        		 	search:function(key){
				                        		 		Ext.Msg.wait('获取数据中...');
				                        		 		if(key){
				                        		 			key = key.trim();
				                        		 		}
				                        		 		searchKey = key;
				                        		 		var result = new Array();
				                        		 		var grid = Ext.getCmp('ruleGrid');
				                        		 		var index = -1;
				                        		 		rules.forEach(function(rule,arrIndex){
				                        		 			if(rule.RU_NAME&&rule.RU_NAME.indexOf(key)>-1){
				                        		 				result.push(rule);
				                        		 			}else if(rule.RU_DESC&&rule.RU_DESC.indexOf(key)>-1){
				                        		 				result.push(rule);
				                        		 			}
				                        		 		});
				                        		 		if(result.length>0){
				                        		 			result.forEach(function(res,arrIndex){
				                        		 				if(res.RU_ID==ruleid){
	                        		 								index = arrIndex;
	                        		 								return false;
	                        		 							}
				                        		 			});
				                        		 		}
				                        		 		grid.store.loadData(result);
				                        		 		if(index!=-1){
	                        		 						grid.getSelectionModel().selectRow(index,true); 
	                        		 					}
	                        		 					Ext.Msg.hide();
				                        		 	}
				                        		})]
	                        		 		},
                        		 			new Ext.grid.GridPanel({
	                        		 			id:'ruleGrid',
	                        		 			region:'center',
				                        		viewConfig: {
										            forceFit:true
										        },
										        store: new Ext.data.JsonStore({ 
				                        			 fields : ["RU_ID","RU_NAME","RU_DESC"],
				                        			 data :rules
				                        		}),
										        enableDD: false,
										        autoScroll:true,
										        sm:new Ext.grid.CheckboxSelectionModel({
										        	singleSelect:true
										        }),
										        processEvent : function(C, E) {
													this.fireEvent(C, E);
													var D = E.getTarget();
													var B = this.view;
													var G = B.findHeaderIndex(D);
													if (G !== false) {
														this.fireEvent("header" + C, this, G, E)
													} else {
														var F = B.findRowIndex(D);
														var A = B.findCellIndex(D);
														if (F !== false) {
															//this.fireEvent("row" + C, this, F, E); //取消row事件，防止列移动
															if (A !== false) {
																this.fireEvent("cell" + C, this, F, A, E)
															}
														}
													}
												},
	                        		 			columns:[new Ext.grid.CheckboxSelectionModel(),new Ext.grid.RowNumberer(),{
	                        		 				header:'ID',
	                        		 				dataIndex:'RU_ID',
	                        		 				width:30
	                        		 			},{
	                        		 				header:'规则名称',
	                        		 				dataIndex:'RU_NAME',
	                        		 				flex:1,
	                        		 				renderer:function(val,meta,rec){
	                        		 					if(val&&searchKey){
	                        		 						return val.replace(searchKey,'<font color="red">'+searchKey+'</font>');
	                        		 					}
	                        		 					return val;
	                        		 				}
	                        		 			},{
	                        		 				header:'规则描述',
	                        		 				dataIndex:'RU_DESC',
	                        		 				flex:1,
	                        		 				id:'RU_DESC',
	                        		 				renderer:function(val,meta,rec){
	                        		 					if(val&&searchKey){
	                        		 						return val.replace(searchKey,'<font color="red">'+searchKey+'</font>');
	                        		 					}
	                        		 					return val;
	                        		 				}
	                        		 			}],
	                        		 			listeners:{
	                        		 				render:function(grid){
	                        		 					if(ruleid){
	                        		 						var index = -1;
	                        		 						rules.forEach(function(rule,arrIndex){
	                        		 							if(rule.RU_ID==ruleid){
	                        		 								index = arrIndex;
	                        		 								return false;
	                        		 							}
	                        		 						});
	                        		 						if(index!=-1){
	                        		 							grid.getSelectionModel().selectRow(index,true); 
	                        		 						}
	                        		 					}
	                        		 					 
	                        		 				}
	                        		 			}
	                        		 		})],
								            listeners:{
								            	'beforehide':function(c){
								            		Gef.activeEditor.existWin=false;
								            		Gef.activeEditor.enable();
								            	},
								            	'beforeshow':function(c){ 
								            		Gef.activeEditor.existWin=true;
								            		Gef.activeEditor.disable();
								            	}
								            	
								            },
								            buttons:[{
								            	text:'确定',
								            	handler:function(){
								            		var me = this;
								            		var grid = Ext.getCmp('ruleGrid');
								            		var rec = grid.getSelectionModel().getSelections()[0];
	                        		 				//var ruleName = Ext.getCmp('RU_NAME');
	                        		 				var ruleId = Ext.getCmp('RU_ID');
	                        		 				//ruleName.fireEvent('datachange',rec?rec.data['RU_NAME']:null);
	                        		 				ruleId.fireEvent('datachange',rec?rec.data['RU_ID']:null);
	                        		 				me.ownerCt.close();
								            	}
								            },{
								            	text:'取消',
								            	handler:function(){
								            		var me = this;
								            		me.ownerCt.close();
								            	}
								            }]
	                        		 	});
	                        		 	win.show();
	                        		 },
	                        		 getRules:function(formCaller,ruleid){
	                        		 	var me = this;
	                        		 	Ext.Msg.wait('获取数据中...');
	                        		 	Ext.Ajax.request({//拿到tree数据       	
								    		url:basePath + 'common/getAllJprocessRules.action',
								    		params:{
								    			caller:formCaller
								    		},
								        	method:'post',
								        	timeout:60000,
								        	success: function(response){
								        		Ext.Msg.hide();
								        		var rules;
								        		res = new Ext.decode(response.responseText);
								        		if(res.success){
								        			rules = res.rules;
								        			if(rules.length>0){
								        				me.showRules(rules,ruleid);
								        			}else{
								        				Ext.Msg.alert('提示','<font color="red">该流程暂未设置审核规则</font>');
								        			}
								        		}else if(res.exceptionInfo){
								        			alert(res.exceptionInfo);
								        		}
								        	} 
								        });
	                        		 },
	                        		 pushSelectedRuleFirst:function(rules,ruleid){
	                        		 	 var data = new Array();
	                        		 	 var selectedRule = new Array();
        		 						 rules.forEach(function(rule,arrIndex){
        		 							 if(rule.RU_ID==ruleid){
        		 								 index = arrIndex;
        		 								 selectedRule.push(rule);
        		 							 }else{
        		 								 data.push(rule);
        		 							 }
        		 						 });
            		 				 	 if(selectedRule.length>0){
            		 				 	 	data = selectedRule.concat(data);
            		 				 	 }
            		 				 	 return data;
	                        		 }
	                        	 });
	                        	 
	                        	 //-----------------------------------------------  以下 w2 之前的组件 都是 w2 item 中的东西……
	                        	 var simpleStore =  new Ext.data.SimpleStore({ 
	                        		 fields : ["text", "value"],
	                        		 data : [["办理人决定", "1"], ["单人办理", "2"],["顺序办理","3"],["并行办理","4"]]
	                        	 });
	                        	 var combox2 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",
	                        		 id:"isMsgAdviceCombo",
	                        		 editable:false,
	                        		 /*  width:100,*/
	                        		 mode : "local",    
	                        		 triggerAction : "all", 
	                        		 store : simpleStore,
	                        		 value:"1",  
	                        		 valueField : "value", 
	                        		 displayField : "text", 
	                        		 readOnly:true
	                        	 });
	                        	 var fs = new Ext.form.FieldSet({
	                        		 title: '催办设置',
	                        		 collapsible: true,
	                        		 autoHeight : true,
	                        		 checkboxToggle : true,
	                        		 checkboxName :'',
	                        		 /*layout:'column',*/
	                        		 layout:'table',
	                        		 defaults: {
	                        			 // applied to each contained panel
	                        			 bodyStyle:'padding:20px'
	                        		 },
	                        		 layoutConfig: {
	                        			 // The total column count must be specified here
	                        			 columns: 3
	                        		 },

	                        		 items :[new Ext.form.Checkbox({fieldLabel:'多人接收', rowspan: 1}),
	                        		         combox2,
	                        		         new Ext.form.TextField({emptyText:'办理完成'}),    
	                        		         new Ext.form.Checkbox({boxLabel :'和组织关联'}),
	                        		         new Ext.form.Checkbox({boxLabel :'必须经过这一步'}),
	                        		         new Ext.form.Checkbox({boxLabel :'决定性的这一步'}),
	                        		         new Ext.form.Checkbox({boxLabel :'部门所有成员'}),
	                        		         new Ext.form.Checkbox({boxLabel :'是否发寻呼'}),
	                        		         new Ext.form.Checkbox({boxLabel :'发短信通知'}),
	                        		         new Ext.form.Checkbox({boxLabel :'部门解析到人'})
	                        		 ]
	                        	 });
	                        	 var w2 = new Ext.Window({
	                        		 /*  contentEl:"win",*/
	                        		 closeAction:'hide',
	                        		 width:500,
	                        		 height:500,
	                        		 modal:false,
	                        		 items:[new Ext.Toolbar({ items:[new Ext.form.Radio({
	                        			 boxLabel : '条件接收人',
	                        			 xtype:'radio',
	                        			 /*checked:true,*/
	                        			 name :'2'
	                        		 }),
	                        		 {text: '设置'}]

	                        		 }),fs],
	                        		 plain:true,
	                        		 title:"标题"
	                        	 });
	                        	 var button = new Ext.Button({ //此 button 暂时无用！
	                        		 text:'更多设置>>',
	                        		 height:40,
	                        		 handler:function(){
	                        			 w2.show();

	                        		 }
	                        	 });
	                        	 var windowWidth = 550;
	                        	 var ms1 = new Ext.form.TextField({fieldLabel:'要点1',width:windowWidth*0.65,id:'ms1',emptyText: '自定义要点1', columnWidth: .45});
	                        	 var ms2 = new Ext.form.TextField({fieldLabel:'要点2',width:windowWidth*0.65,id:'ms2',emptyText: '自定义要点2'});
	                        	 var ms3 = new Ext.form.TextField({fieldLabel:'要点3',width:windowWidth*0.65,id:'ms3',emptyText: '自定义要点3'});
	                        	 var ms4 = new Ext.form.TextField({fieldLabel:'要点4',width:windowWidth*0.65,id:'ms4',emptyText: '自定义要点4'});
	                        	 var ms5 = new Ext.form.TextField({fieldLabel:'要点5',width:windowWidth*0.65,id:'ms5',emptyText: '自定义要点5'});
	                        	 var ms6 = new Ext.form.TextField({fieldLabel:'要点6',width:windowWidth*0.65,id:'ms6',emptyText: '自定义要点6'});
	                        	 var ms7 = new Ext.form.TextField({fieldLabel:'要点7',width:windowWidth*0.65,id:'ms7',emptyText: '自定义要点7'});
	                        	 var ms8 = new Ext.form.TextField({fieldLabel:'要点8',width:windowWidth*0.65,id:'ms8',emptyText: '自定义要点8'});
	                        	 var ms9 = new Ext.form.TextField({fieldLabel:'要点9',width:windowWidth*0.65,id:'ms9',emptyText: '自定义要点9'});
	                        	 var ms10 = new Ext.form.TextField({fieldLabel:'要点10',width:windowWidth*0.65,id:'ms10',emptyText: '自定义要点10'});
	                        	 var des1 = new Ext.form.TextField({width:windowWidth*0.15,id:'des1',emptyText: '决策字段',columnWidth: .15});
	                        	 var des2 = new Ext.form.TextField({width:windowWidth*0.15,id:'des2',emptyText: '决策字段'});
	                        	 var des3 = new Ext.form.TextField({width:windowWidth*0.15,id:'des3',emptyText: '决策字段'});
	                        	 var des4 = new Ext.form.TextField({width:windowWidth*0.15,id:'des4',emptyText: '决策字段'});
	                        	 var des5 = new Ext.form.TextField({width:windowWidth*0.15,id:'des5',emptyText: '决策字段'});
	                        	 var des6 = new Ext.form.TextField({width:windowWidth*0.15,id:'des6',emptyText: '决策字段'});
	                        	 var des7 = new Ext.form.TextField({width:windowWidth*0.15,id:'des7',emptyText: '决策字段'});
	                        	 var des8 = new Ext.form.TextField({width:windowWidth*0.15,id:'des8',emptyText: '决策字段'});
	                        	 var des9 = new Ext.form.TextField({width:windowWidth*0.15,id:'des9',emptyText: '决策字段'});
	                        	 var des10 = new Ext.form.TextField({width:windowWidth*0.15,id:'des10',emptyText: '决策字段'});
	                        	 var op1 = new Ext.form.TextField({width:windowWidth*0.35,id:'op1',emptyText: '可选项',columnWidth: .35});
	                        	 var op2 = new Ext.form.TextField({width:windowWidth*0.35,id:'op2',emptyText: '可选项'});
	                        	 var op3 = new Ext.form.TextField({width:windowWidth*0.35,id:'op3',emptyText: '可选项'});
	                        	 var op4 = new Ext.form.TextField({width:windowWidth*0.35,id:'op4',emptyText: '可选项'});
	                        	 var op5 = new Ext.form.TextField({width:windowWidth*0.35,id:'op5',emptyText: '可选项'});
	                        	 var op6 = new Ext.form.TextField({width:windowWidth*0.35,id:'op6',emptyText: '可选项'});
	                        	 var op7 = new Ext.form.TextField({width:windowWidth*0.35,id:'op7',emptyText: '可选项'});
	                        	 var op8 = new Ext.form.TextField({width:windowWidth*0.35,id:'op8',emptyText: '可选项'});
	                        	 var op9 = new Ext.form.TextField({width:windowWidth*0.35,id:'op9',emptyText: '可选项'});
	                        	 var op10 = new Ext.form.TextField({width:windowWidth*0.35,id:'op10',emptyText: '可选项'});
	                        	 var n1= new Ext.form.Checkbox ({boxLabel :'必填',id:'n1',rowspan: 1 ,columnWidth: .25/*,width:75*/});
	                        	 var n2= new Ext.form.Checkbox ({boxLabel :'必填',id:'n2',rowspan: 1});
	                        	 var n3= new Ext.form.Checkbox ({boxLabel :'必填',id:'n3'/*,width:75*/});
	                        	 var n4= new Ext.form.Checkbox ({boxLabel :'必填',id:'n4'});
	                        	 var n5= new Ext.form.Checkbox ({boxLabel :'必填',id:'n5'});
	                        	 var n6= new Ext.form.Checkbox ({boxLabel :'必填',id:'n6'});
	                        	 var n7= new Ext.form.Checkbox ({boxLabel :'必填',id:'n7'});
	                        	 var n8= new Ext.form.Checkbox ({boxLabel :'必填',id:'n8'});
	                        	 var n9= new Ext.form.Checkbox ({boxLabel :'必填',id:'n9'});
	                        	 var n10= new Ext.form.Checkbox ({boxLabel :'必填',id:'n10'});

	                        	 var simpleStore =  new Ext.data.SimpleStore({ 
	                        		 fields : ["text", "value"],
	                        		 data : [["布尔型", "B"], ["字符串型", "S"],["日期型","D"],["数字型","N"],["下拉框","C"]]
	                        	 });
	                        	 var combox1 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox1",editable:false,mode : "local",triggerAction : "all",emptyText:'类型', 
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75 });
	                        	 var combox2 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox2",editable:false,mode : "local",triggerAction : "all",emptyText:'类型', 
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75 });
	                        	 var combox3 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox3",editable:false,mode : "local",triggerAction : "all",emptyText:'类型',  
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width: 75 });
	                        	 var	combox4 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox4",editable:false,mode : "local",triggerAction : "all",emptyText:'类型',  
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75 });
	                        	 var combox5 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox5",editable:false,mode : "local",triggerAction : "all",emptyText:'类型',  
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true ,width:75});
	                        	 var combox6 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox6",editable:false,mode : "local",triggerAction : "all",emptyText:'类型',  
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75 });
	                        	 var combox7 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox7",editable:false,mode : "local",triggerAction : "all",emptyText:'类型',  
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75 });
	                        	 var combox8 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox8",editable:false,mode : "local",triggerAction : "all",emptyText:'类型', 
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75 });
	                        	 var combox9 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox9",editable:false,mode : "local",triggerAction : "all",emptyText:'类型',  
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true,width:75});
	                        	 var combox10 = new Ext.form.ComboBox({
	                        		 fieldLabel :"是否消息通知",id:"combox10",editable:false,mode : "local",triggerAction : "all",emptyText:'类型', 
	                        		 store : simpleStore,valueField : "value",displayField : "text",readOnly:true ,width:75 });

	                        	 var fst = new Ext.form.FieldSet({
	                        		 collapsible: true,
	                        		 autoHeight : true,
	                        		 checkboxToggle : true,
	                        		 checkboxName :'ssss',
	                        		 labelWidth :45,
	                        		 autoWidth :true,
	                        		 shadow : true,
	                        		 layout:'table',
	                        		 layoutConfig: {
	                        			 columns: 5
	                        		 },
	                        		 defaults: {
	                        			 bodyStyle:'padding:3333px'
	                        		 },
	                        		 items:[n1,ms1,combox1,op1,des1,n2,ms2,combox2,op2,des2,n3,ms3,combox3,op3,des3,n4,ms4,combox4,op4,des4,n5,ms5,combox5,op5,des5,n6,ms6,combox6,
	                        		        op6,des6,n7,ms7,combox7,op7,des7,n8,ms8,combox8,op8,des8,n9,ms9,combox9,op9,des9,n10,ms10,combox10,op10,des10],
	                        		        listeners:{
	                        		        	'render':function(){
	                        		        		var cu = model.dom.getAttribute('customSetup');
	                        		        		if(cu){
	                        		        			var vs = [];
	                        		        			vs = cu.split("#");
	                        		        			for(var i=0;i<vs.length;i++){
	                        		        				var ii = vs[i].indexOf("-");
	                        		        				var i2 = vs[i].indexOf("^");
	                        		        				var i3 = vs[i].indexOf("$");
	                        		        				var i4 = vs[i].indexOf('@');
	                        		        				var i5 = vs[i].indexOf('[');
	                        		        				var index = vs[i].substring(0,ii);
	                        		        				var value = vs[i].substring(ii+1,i2);
	                        		        				Ext.getCmp('ms'+index.toString()).value = value;   						
	                        		        				var type = vs[i].substring(i2+1,i3);
	                        		        				Ext.getCmp('combox'+index.toString()).value = type; 
	                        		        				var neccessary = vs[i].substring(i3+1,i3+2);
	                        		        				if(neccessary=='Y'){
	                        		        					Ext.getCmp('n'+index.toString()).checked = true;
	                        		        				}else{
	                        		        					Ext.getCmp('n'+index.toString()).checked = false;
	                        		        				}
	                        		        				if(i5>0)
	                        		        				  Ext.getCmp('op'+index.toString()).value = vs[i].substring(i5+1,vs[i].indexOf(']'));
	                        		        				if(i4>0)  Ext.getCmp('des'+index.toString()).value = vs[i].substring(vs[i].indexOf("@")+1);
	                        		        			}   				    					
	                        		        		}

	                        		        	}
	                        		        }
	                        	 });
	                        	 var btn = new Ext.Button({text:'确定',handler:function(){    		
	                        		 var desValues = [];
	                        		 for(var i=1;i<11;i++){
	                        			 var v = Ext.getCmp('ms'+i.toString()).getValue();
		                        		 var cus='';
	                        			 if(v){
	                        				 var type = Ext.getCmp('combox'+i.toString()).getValue();
	                        				 var necessary = Ext.getCmp('n'+i.toString()).getValue();
	                        				 var des=Ext.getCmp('des'+i.toString()).getValue();
	                        				 var ops=Ext.getCmp('op'+i.toString()).getValue();
	                        				 if(!type){
	                        					 Ext.Msg.alert("提示","尚未选择"+"自定义描述".fontcolor("Red").fontsize(16)+i.toString().fontsize(16).fontcolor("Red")+"的类型");
	                        					 return ;
	                        				 }
	                        				 if(type=='C' && !ops){
	                        					 Ext.Msg.alert("提示","第"+i+"行 设置为下拉框 需设置完整的可选项!"); 
	                        					 return;
	                        				 }	                        			
	                        				 if(necessary){
	                        					 cus=i.toString()+"-"+v+"^"+type+"$Y";
	                        				 }else{
	                        					 cus=i.toString()+"-"+v+"^"+type+"$N";
	                        				 }	                        				
	                        				 if(ops) cus+='['+ops+']';
	                        				 if(des) cus+='@'+des;
	                        				 desValues.push(cus);
	                        			 }
	                        			 if(desValues.length>0){
	                        				 model.dom.setAttribute("customSetup",desValues.join("#")); 
	                        			 }else{
	                        				 if(model.dom.getAttribute("customSetup")){ 
	                        					 model.dom.removeAttribute("customSetup");
	                        				 }
	                        			 }
	                        		 }
	                        		 w3.hide();

	                        	 }});
	                        	 var w3=new Ext.Window({
	                        		 closeAction:'hide',
	                        		 autoWidth :true,
	                        		 autoHeight : true,
	                        		 modal:false,
	                        		 buttons:[btn],
	                        		 items:[fst],
	                        		 plain:true,
	                        		 title:"设置"
	                        	 });
	                        	 var set = new Ext.form.Checkbox({
	                        		 boxLabel:'设置',
	                        		 id:'set',
	                        		 clearCls :"x-form-clear-left",
	                        		 fieldLabel :'审批要点',
	                        		 listeners:{
	                        			 'check':function(c,checked){
	                        				 if(checked){ 
	                        					 w3.show();
	                        				 }else{
	                        					 w3.hide();
	                        				 }
	                        			 }
	                        		 }
	                        	 });
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
	                        		 /* listeners:{
            	'beforerender':function(){
            		console.log("浏览器类型 " +window.Ext.isIE());
            	}
            },*/
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
	                        		 },r1,r2,r5,r3,r4,assignee, candidateGroups,departjobGroups,candidateUsers,sqlAssignee,fieldSet,notifySet,buttonSet,jprocessRuleSet,set,{
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
	                        	 function radioHandler(radio,bool){
	                        		 if(bool){
	                        			 switch(radio.getId()){
	                        			 case "r1" :{
	                        					 Ext.getCmp('candidate').setVisible(false);
	                        					 Ext.getCmp('departjob').setVisible(false);	                       					 
	                        					 Ext.getCmp('rolAssignee').setVisible(false);
	                        					 Ext.getCmp('sqlAssignee').setVisible(false);
	                        					 Ext.getCmp('assignee').setVisible(true);
	                        					 p.ownerCt.setWidth(p.ownerCt.getInnerWidth()+1);
	                        			 };break;
	                        			 case "r2" :{	
	                        					 Ext.getCmp('assignee').setVisible(false);
	                        					 Ext.getCmp('departjob').setVisible(false);	
	                        					 Ext.getCmp('rolAssignee').setVisible(false);
	                        					 Ext.getCmp('sqlAssignee').setVisible(false);
	                        					 Ext.getCmp('candidate').setVisible(true);
	                        					 p.ownerCt.setWidth(p.ownerCt.getInnerWidth()+1);	                        
	                        			 } break;
	                        			 case "r3" :{
	                        					 Ext.getCmp('assignee').setVisible(false);
	                        					 Ext.getCmp('candidate').setVisible(false);
	                        					 Ext.getCmp('departjob').setVisible(false);	
	                        					 Ext.getCmp('sqlAssignee').setVisible(false);
	                        					 Ext.getCmp('rolAssignee').setVisible(true);
	                        					 p.ownerCt.setWidth(p.ownerCt.getInnerWidth()+1);
	                        			 } break;
	                        			 case "r4" :{
	                        				 Ext.getCmp('assignee').setVisible(false);
	                        				 Ext.getCmp('candidate').setVisible(false);
	                        				 Ext.getCmp('departjob').setVisible(false);	
	                        				 Ext.getCmp('rolAssignee').setVisible(false);
	                        				 Ext.getCmp('sqlAssignee').setVisible(true);
	                        				 p.ownerCt.setWidth(p.ownerCt.getInnerWidth()+1);
	                        			 } break;
	                        			 case "r5" :{ 
	                        				 Ext.getCmp('assignee').setVisible(false);
	                        				 Ext.getCmp('candidate').setVisible(false);
	                        				 Ext.getCmp('departjob').setVisible(true);	
	                        				 Ext.getCmp('rolAssignee').setVisible(false);
	                        				 Ext.getCmp('sqlAssignee').setVisible(false);
	                        				 p.ownerCt.setWidth(p.ownerCt.getInnerWidth()+1);} break;
	                        			 }
	                        		 }
	                        	 };
	                        	 r1.on('check',function(r,b){
	                        		 radioHandler(r,b);
	                        	 });
	                        	 r2.on('check',function(r,b){
	                        		 radioHandler(r,b);
	                        	 });
	                        	 r3.on('check',function(r,b){
	                        		 radioHandler(r,b);
	                        	 });
	                        	 r4.on('check',function(r,b){
	                        		 radioHandler(r,b);
	                        	 });
	                        	 r5.on('check',function(r,b){
	                        		 radioHandler(r,b);
	                        	 });
	                        	 /*  panel.hide();*/
	                        	 /* assignee.setVisible(false);
        candidate.setVisible(false);
        candidateGroups.setVisible(false);*/
	                        	 tabPanel.add(p);
	                        	 tabPanel.activate(p);
	                         },

	                         resetAdvance: function(tabPanel, model) {
	                        	 // 以下内容  为 task的 属性 panel的 高级配置选项……
	                        	 /* var p = new Ext.form.FormPanel({
            title: '高级配置',
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
                name: 'swimlane',
                fieldLabel: '泳道',
                value: model.dom.getAttribute('swimlane'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setAttribute('swimlane', newValue);
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
                name: 'duedate',
                fieldLabel: '持续时间',
                value: model.duedate ? model.duedate : ''
            }, {
                name: 'onTransition',
                fieldLabel: '转移',
                value: model.onTransition ? model.onTransition : ''
            }, {
                name: 'completion',
                fieldLabel: '完成',
                value: model.completion ? model.completion : ''
            }, {
                name: 'notification',
                fieldLabel: '邮件提示',
                value: model.dom.getElementAttribute('notification', 'template'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setElementAttribute('notification', 'template', newValue);
                    }
                }
            }, {
                name: 'reminder',
                fieldLabel: '邮件提醒',
                value: model.dom.getElementAttribute('reminder', 'template'),
                listeners: {
                    'blur': function(field) {
                        var newValue = field.getValue();
                        model.dom.setElementAttribute('reminder', 'template', newValue);
                    }
                }
            }]
        });

        tabPanel.add(p);*/
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
        					 'change': function(field) {
        						 var newValue = field.getValue();
        						 // FIXME: use command
        						 if(model.getSource().type == 'task'){
        							 if(newValue!="同意"&&newValue!="不同意"){
        								 Ext.Msg.alert('提示','任务节点的流出连线名称必须为“同意”或“不同意！”');
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

	            	 tabPanel.add(grid);
	             }
});
