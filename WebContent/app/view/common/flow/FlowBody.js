Ext.define('erp.view.common.flow.FlowBody',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.flowbody',
	style:'background: #fff',
	id:'flowbody',
	hideBorders: true,
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	height:window.innerHeight,
	caller:'',
	currentnode:'',
	processtitle:'',
	dockedItems: [/*{
		xtype: 'toolbar',
		readOnly: ISexecuted,
		disabled: ISexecuted,
		hidden:disagree,
		style: {
			background: 'transparent',
			border: 'none'
		},
		anchor: '100%',
		layout: 'hbox',
		items: [{
			xtype: 'splitter',
			width: 20
		},
		{
			xtype: 'button',
			text: '同&nbsp;&nbsp;&nbsp;&nbsp;意',
			id:'agree-task',
			iconCls: 'x-button-icon-agree',
			cls: 'x-btn-gray',
			width: 90
		},
		{
			xtype: 'splitter',
			width: 10
		},
		{
			xtype: 'button',
			id:'disagree-task',
			text: '不 &nbsp;同 &nbsp;意',
			iconCls: 'x-button-icon-unagree',
			cls: 'x-btn-gray',
			width: 90
		},{
			xtype: 'splitter',
			width: 10
		},{
			xtype: 'button',
			id:'skip-task',
			iconCls: 'x-button-icon-turn',
			text: '下一条',
			cls: 'x-btn-gray',
			width: 90
		},
		{
			xtype: 'splitter',
			width: 10
		},
		{
			xtype:'button',
			text: '智能审批',
			iconCls: 'x-button-icon-autodeal',
			cls: 'x-btn-gray',
			id:'autoaudit',
			width: 90,
			handler: function() {
				this.ownerCt.ownerCt.autoAudit();							
			}
		},
		{
			xtype: 'splitter',
			width: 10
		},
		{
			xtype: 'button',
			text: '关&nbsp;&nbsp;&nbsp;&nbsp;闭',
			iconCls: 'x-button-icon-newclose',
			cls: 'x-btn-gray',
			id:'closeProcess',
			width: 90,
			handler: function() {			
				if(parent.Ext.getCmp('modalwindow')){
					Ext.Ajax.request({
						url: basePath + 'common/changeMaster.action',
						params: {
							to: parent.Ext.getCmp('modalwindow').historyMaster
						},
						callback: function(opt, s, r) {
							if (s) {
								var formUtil = Ext.create('erp.util.FormUtil');
								var tab = formUtil.getActiveTab();
								if(tab){
									tab.fireEvent('activate',tab);
								}
								parent.Ext.getCmp('modalwindow').close();
							} else {
								alert('切换到原账套失败!');
							}
						}
					}); 							  									
				}								
				else parent.Ext.getCmp('content-panel').getActiveTab().close();
			}
		}
		]
	}*/],
	initComponent : function(){
		this.callParent(arguments);
		this.loadPage();
		this.addEvents('pageLoad');
	},
	loadPage:function(){
		var me = this;
		formCondition = (formCondition == null) ? "": formCondition.replace(/IS/g, "=");
		nodeId = nodeId != null ? nodeId:getUrlParam('nodeId');
		nodeId =nodeId && nodeId!="null"? nodeId : formCondition.split("=")[1];
		Ext.Ajax.request({ 
			url: basePath + 'common/getProcessInstanceId.action',
			params: {
				jp_nodeId: nodeId,
				master: master,
				_noc: 1
			},
			success: function(response) {
				var res = response.responseText;
				processInstanceId = Ext.decode(res).processInstanceId;
				Ext.Ajax.request({ //获取当前节点对应的JProcess对象
					url: basePath + 'common/getCurrentNode.action',
					params: {
						jp_nodeId: nodeId,
						master: master,
						_noc: 1
					},
					success: function(response) {
						var res = new Ext.decode(response.responseText),forknode=res.info.forknode;
						ProcessData = res.info.currentnode;
						dealmanname = res.info.dealmanname;
						if(res.info.button!=null&&res.info.button!=""){
							requiredFields = res.info.button.jt_neccessaryfield;
						}
						/*Ext.getCmp('agree-task').setDisabled(false);*/
						me.setHeader(ProcessData,dealmanname);
						me.loadNodeGrid(processInstanceId);
						

						Ext.getCmp('processtitle').setText('<span style="font-weight: bold !important;font-size:18px">' + ProcessData.jp_name + '</span>');
						
						
						if(res.info.communicates){ //如果有沟通内容则展开
							Ext.getCmp('com_record').setValue('<ul style="font-size:90%;color:#3B3B3B;list-style-type:none;margin:0;padding:0;margin-left:10px;overflow:autoscroll;">'+res.info.communicates+'</ul>');
							Ext.getCmp('extraCompoent').expand();
							
							var iframe = Ext.get('com_record-iframeEl');
						
							if(iframe){
								iframe.dom.classList.add('iframe-background');
								Ext.defer(function(){
									iframe.dom.classList.remove('iframe-background');
								
								},800);								
							}
						}
						
						var formCondition = ProcessData.jp_keyName + "IS" + ProcessData.jp_keyValue;
						var gridCondition = '';
						if (ProcessData.jp_keyName) {
							gridCondition = ProcessData.jp_formDetailKey + 'IS' + ProcessData.jp_keyValue;
						}

						var caller = ProcessData.jp_caller;
						var url = basePath + ProcessData.jp_url;
						var queryType='form';
						var myurl;
						if (me.BaseUtil.contains(url, '?', true)) {
							myurl = url + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
						} else {
							myurl = url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition;
						}
						myurl += '&_noc=1&datalistId=NaN'; // 不限制权限
						if (master) {
							myurl += '&newMaster=' + master;
						}
						if(myurl.indexOf('jsps/ma/jprocess/AutoJprocess.jsp?type=1')>0){
							myurl+='&caller='+caller;
							queryType='tabpanel';
						}
						me.addPage(queryType, myurl,res.info.button,caller,res.info.currentnode.jp_status);
					}
				});
			}
		});
	},
	setHeader:function(data,name){
		Ext.getCmp('currentnode').setText('当前节点:<font size=2 color="red">' + data.jp_nodeName + '</font>');
		Ext.getCmp('currentnodename').setText('当前节点处理人:<font size=2 color="red">' + name + '</font>');
		Ext.getCmp('launchername').setText('发起人:<font size=2 color="red">' + data.jp_launcherName + '</font>');
		Ext.getCmp('launchtime').setText('发起时间:<font size=2 color="red">' + Ext.Date.format(new Date(data.jp_launchTime), "Y-m-d H:i:s") + '</font>');
		this.caller=data.jp_caller;
		this.currentnode=data.jp_nodeName;
		//this.launchername=data.jp_launcherName;
		//this.launchtime=Ext.Date.format(new Date(data.jp_launchTime), "Y-m-d H:i:s");
		this.processtitle= data.jp_name;
		
	},
	loadNodeGrid:function(processInstanceId){
		Ext.getCmp('historyGrid').getOwnStore(processInstanceId);
	},
	addPage:function(queryType,url,btn,caller,jp_status){
		var me=this,height=window.innerHeight-26,IntervalTask=null;
		function findToolbar() {
			var childpanel,childtoolbar;
			var w = iframe_maindetail.contentWindow||iframe_maindetail.window;
			if (w.Ext) {
				//childpanel = w.Ext.ComponentQuery.query(queryType)[0];
				childpanels = w.Ext.ComponentQuery.query(queryType);
				Ext.Array.each(childpanels,function(item,index){
					if(item.dockedItems){
						if(item.dockedItems.items.length>0){
							childpanel = item;
							return false;
						}
					}
				});
				
				childtoolbar = w.Ext.ComponentQuery.query(queryType+'>toolbar')[0];
				var grid=w.Ext.ComponentQuery.query('grid')[0];
				if (!childpanel || !childpanel.dockedItems) return;
				Ext.Array.each(childpanel.dockedItems.items,
						function(item) {
					if (item.dock == 'top' && item.id == 'form_toolbar') {
						/*item.removeAll();*/
						toolbar = item;
					}
				});
				if (childtoolbar != null && (grid==null || (grid!=null && ((grid.columns && grid.columns.length>0)||grid.hidden)))) {
					window.clearInterval(IntervalTask);	
					//var button = res.info.button;
					if (btn != null && !ISexecuted) {
						//带XTYPE的BUTTON 
						var buttontype = btn.jb_fields;
						var neccessaryField = btn.jt_neccessaryfield;
						if(buttontype=='updatedetailRequired'){
							grid=w.Ext.ComponentQuery.query('grid')[0];
							if (neccessaryField != null) {
								grid.readOnly=false;
								grid.NoAdd=true;
								var fields = neccessaryField.split(","),addItems=new Array(),fieldtype=null,editable=false;														
								Ext.Array.each(grid.columns,function(column){
									editable=false;
									Ext.Array.each(fields,function(field) {
										var f =column.xtype;													
										if(column.dataIndex==field){
											//明细可编辑列头背景颜色
											column.el.dom.getElementsByClassName('x-column-header-inner')[0].classList.add('x-edit-column');
											//明细可编辑列头字体颜色
											column.getEl().applyStyles('color:#fff');
											column.neccessaryField=true;
											editable=true;
											if (f=="numbercolumn") {																
												column.editor={
														xtype:'numberfield',
														format:'0',
														hideTrigger:true
												};
											} else if (f=="floatcolumn") {
												column.editor={
														xtype:'numberfield',
														format:'0.00',
														hideTrigger:true
												};
											} else if (f.indexOf("floatcolumn")>-1) {							
												var format = "0.";
												var length =parseInt(f.substring(11));
												for (var i = 0; i < length; i++) {
													format += "0";
												}
												column.editor={
														xtype:'numberfield',
														format:format,
														hideTrigger:true
												};
											} else if (f =="datecolumn") {
												column.editor={
														xtype:'datefield',
														format:"Y-m-d",
														hideTrigger:false
												};
											} else if (f =="datetimecolumn") {
												column.editor={
														xtype:'datetimefield',
														format:"Y-m-d H:i:s",
														hideTrigger:false
												};
											} else if (f =="timecolumn") {
												column.editor={
														xtype:'timefield',
														format:"H:i",
														hideTrigger:false
												};
											} else if (f =="monthcolumn") {
												column.editor={
														xtype:'monthdatefield',
														hideTrigger:false
												};
											} else if (f =="textcolumn" || f=="textfield" || f=="text") {	
												column.editor={
														xtype:'textfield'
												};
											} else if (f =="textareafield") {			
												column.editor={
														xtype:'textareafield'
												};
											} else if (f=="textareatrigger") {
												column.editor={
														xtype:'textareatrigger',
														hideTrigger:false
												};
											} else if (f=="dbfindtrigger") {					
												column.editor={
														xtype:'dbfindtrigger',
														hideTrigger:false
												};
											} else if (f =="multidbfindtrigger") {
												column.editor={
														xtype:'multidbfindtrigger',
														hideTrigger:false
												};
											} else if (f=="datehourminutefield") {					
												column.editor={
														xtype:'datehourminutefield',
														hideTrigger:false
												};
											} else if (f=="checkbox") {
												column.editor={
														xtype:'checkbox',
														cls:'x-grid-checkheader-editor',
														hideTrigger:false
												};
											}
											return false;
										}
									});
									if(!editable) {
										column.editor=null;
									}
								});
								toolbar.insert(0, [{
									xtype: 'button',
									text:'修改明细',
									iconCls: 'x-button-icon-save',
									cls: 'x-btn-gray',
									handler:function(btn){
										var values = {};													
										var jsonGridData = new Array();
										var s = grid.getStore().data.items;//获取store里面的数据
										var dd;
										for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
											var data = s[i].data;
											dd = new Object();
											var count = 0;
											Ext.each(fields,function(f){
												var	col = grid.down('gridcolumn[dataIndex=' + f + ']');
												var useNull=false;
												if(col && col.useNull){
													useNull=true;
												}
												if(col){
													Ext.each(s, function(c){
														if((c.data[f]==null || c.data[f]=='') && !useNull ){
															if(col.xtype=='numbercolumn'){
																if(c.data[f]!=0){
																	count++;
																}
															}else{
																count++;
															}
														}
													});
												}
											});
											if(count>0){
												showError('明细行有必填字段未填写');
												return false;
											}
											if(s[i].dirty && !grid.GridUtil.isBlank(grid, data)){
												Ext.each(grid.columns, function(c){
													if((c.neccessaryField && c.logic!='ignore')|| c.logic=='keyField'){
														if(c.xtype == 'datecolumn'){
															c.format = c.format || 'Y-m-d';
															if(Ext.isDate(data[c.dataIndex])){
																dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
															} 
														} else if(c.xtype == 'datetimecolumn'){
															if(Ext.isDate(data[c.dataIndex])){
																dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');
															} 
														} else if(c.xtype == 'numbercolumn'){																					
															dd[c.dataIndex] = "" + s[i].data[c.dataIndex];																					
														} else {
															dd[c.dataIndex] = s[i].data[c.dataIndex];
														}
													}
												});															
												jsonGridData.push(Ext.JSON.encode(dd));
											}
										}																												
										childpanel.getForm().getFields().each(function(field) {
											if (field.isDirty() &&  field.logic!='ignore' ) {
												var data = field['getSubmitData'](true);
												if (Ext.isObject(data)) {
													Ext.iterate(data,
															function(name, val) {
														if (true && val === '') {
															val = field.emptyText || '';
														}
														if (name in values) {
															var bucket = values[name],
															isArray = Ext.isArray;
															if (!isArray(bucket)) {
																bucket = values[name] = [bucket];
															}
															if (isArray(val)) {
																values[name] = bucket.concat(val);
															} else {
																bucket.push(val);
															}
														} else {
															values[name] = val;
														}
													});
												}
											}
										});
										values[ProcessData.jp_keyName] = ProcessData.jp_keyValue;
										Ext.Ajax.request({
											url: basePath + '/common/processUpdate.action',
											method: 'post',
											params: {
												caller: caller,
												processInstanceId:ProcessData.jp_processInstanceId,
												formStore: unescape(Ext.JSON.encode(values).replace(/\\/g, "%")),
												param: unescape(jsonGridData.toString()),
												_noc: 1
											},
											async:false,
											callback: function(options, success, response) {
												var localJson = new Ext.decode(response.responseText);
												canexecute = true;
												if (localJson.success) {
													conditionValidation=0;
													showMessage('提示', '更新成功!', 1000);
													grid.GridUtil.loadNewStore(grid,{
														caller:grid.caller||caller,
														condition:ProcessData.jp_formDetailKey + '=' + ProcessData.jp_keyValue
													});
												} else if (localJson.exceptionInfo) {
													var str = localJson.exceptionInfo;
													if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
														str = str.replace('AFTERSUCCESS', '');
														conditionValidation=0;
														showMessage('提示', '更新成功!', 1000);
													}else conditionValidation=1;
													showError(str);
													return;
												} else {																	
													updateFailure();
												}
											}
										});
									}
								},
								'->']);
							}
						}else if(buttontype=='updatedetail'){
							grid=w.Ext.ComponentQuery.query('grid')[0];
							if (neccessaryField != null) {
								grid.readOnly=false;
								grid.NoAdd=true;
								var fields = neccessaryField.split(","),addItems=new Array(),fieldtype=null,editable=false;														
								Ext.Array.each(grid.columns,function(column){
									editable=false;
									Ext.Array.each(fields,function(field) {
										var f =column.xtype;													
										if(column.dataIndex==field){
											//明细可编辑列头背景颜色
											column.el.dom.getElementsByClassName('x-column-header-inner')[0].classList.add('x-edit-column');
											//明细可编辑列头字体颜色
											column.getEl().applyStyles('color:#fff');
											column.neccessaryField=true;
											editable=true;
											if (f=="numbercolumn") {																
												column.editor={
														xtype:'numberfield',
														format:'0',
														hideTrigger:true
												};
											} else if (f=="floatcolumn") {
												column.editor={
														xtype:'numberfield',
														format:'0.00',
														hideTrigger:true
												};
											} else if (f.indexOf("floatcolumn")>-1) {							
												var format = "0.";
												var length =parseInt(f.substring(11));
												for (var i = 0; i < length; i++) {
													format += "0";
												}
												column.editor={
														xtype:'numberfield',
														format:format,
														hideTrigger:true
												};
											} else if (f =="datecolumn") {
												column.editor={
														xtype:'datefield',
														format:"Y-m-d",
														hideTrigger:false
												};
											} else if (f =="datetimecolumn") {
												column.editor={
														xtype:'datetimefield',
														format:"Y-m-d H:i:s",
														hideTrigger:false
												};
											} else if (f =="timecolumn") {
												column.editor={
														xtype:'timefield',
														format:"H:i",
														hideTrigger:false
												};
											} else if (f =="monthcolumn") {
												column.editor={
														xtype:'monthdatefield',
														hideTrigger:false
												};
											} else if (f =="textcolumn" || f=="textfield" || f=="text") {	
												column.editor={
														xtype:'textfield'
												};
											} else if (f =="textareafield") {			
												column.editor={
														xtype:'textareafield'
												};
											} else if (f=="textareatrigger") {
												column.editor={
														xtype:'textareatrigger',
														hideTrigger:false
												};
											} else if (f=="dbfindtrigger") {					
												column.editor={
														xtype:'dbfindtrigger',
														hideTrigger:false
												};
											} else if (f =="multidbfindtrigger") {
												column.editor={
														xtype:'multidbfindtrigger',
														hideTrigger:false
												};
											} else if (f=="datehourminutefield") {					
												column.editor={
														xtype:'datehourminutefield',
														hideTrigger:false
												};
											} else if (f=="checkbox") {
												column.editor={
														xtype:'checkbox',
														cls:'x-grid-checkheader-editor',
														hideTrigger:false
												};
											}
											return false;
										}
									});
									if(!editable) {
										column.editor=null;
									}
								});
								toolbar.insert(0, [{
									xtype: 'button',
									text:'修改明细',
									iconCls: 'x-button-icon-save',
									cls: 'x-btn-gray',
									handler:function(btn){
										var values = {};													
										var jsonGridData = new Array();
										var s = grid.getStore().data.items;//获取store里面的数据
										var dd;
										for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
											var data = s[i].data;
											dd = new Object();
											if(s[i].dirty && !grid.GridUtil.isBlank(grid, data)){
												Ext.each(grid.columns, function(c){
													if((c.neccessaryField && c.logic!='ignore')|| c.logic=='keyField'){
														if(c.xtype == 'datecolumn'){
															c.format = c.format || 'Y-m-d';
															if(Ext.isDate(data[c.dataIndex])){
																dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
															} 
														} else if(c.xtype == 'datetimecolumn'){
															if(Ext.isDate(data[c.dataIndex])){
																dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');
															} 
														} else if(c.xtype == 'numbercolumn'){																					
															dd[c.dataIndex] = "" + s[i].data[c.dataIndex];																					
														} else {
															dd[c.dataIndex] = s[i].data[c.dataIndex];
														}
													}
												});															
												jsonGridData.push(Ext.JSON.encode(dd));
											}
										}																												
										childpanel.getForm().getFields().each(function(field) {
											if (field.isDirty() &&  field.logic!='ignore' ) {
												var data = field['getSubmitData'](true);
												if (Ext.isObject(data)) {
													Ext.iterate(data,
															function(name, val) {
														if (true && val === '') {
															val = field.emptyText || '';
														}
														if (name in values) {
															var bucket = values[name],
															isArray = Ext.isArray;
															if (!isArray(bucket)) {
																bucket = values[name] = [bucket];
															}
															if (isArray(val)) {
																values[name] = bucket.concat(val);
															} else {
																bucket.push(val);
															}
														} else {
															values[name] = val;
														}
													});
												}
											}
										});
										values[ProcessData.jp_keyName] = ProcessData.jp_keyValue;
										Ext.Ajax.request({
											url: basePath + '/common/processUpdate.action',
											method: 'post',
											params: {
												caller: caller,
												processInstanceId:ProcessData.jp_processInstanceId,
												formStore: unescape(Ext.JSON.encode(values).replace(/\\/g, "%")),
												param: unescape(jsonGridData.toString()),
												_noc: 1
											},
											async:false,
											callback: function(options, success, response) {
												var localJson = new Ext.decode(response.responseText);
												canexecute = true;
												if (localJson.success) {
													conditionValidation=0;
													showMessage('提示', '更新成功!', 1000);
													grid.GridUtil.loadNewStore(grid,{
														caller:grid.caller||caller,
														condition:ProcessData.jp_formDetailKey + '=' + ProcessData.jp_keyValue
													});
												} else if (localJson.exceptionInfo) {
													var str = localJson.exceptionInfo;
													if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
														str = str.replace('AFTERSUCCESS', '');
														conditionValidation=0;
														showMessage('提示', '更新成功!', 1000);
													}else conditionValidation=1;
													showError(str);
													return;
												} else {																	
													updateFailure();
												}
											}
										});
									}
								},
								'->']);
							}
						}else if (buttontype.indexOf('#') > 0) {
							if(buttontype.indexOf(',') > 0){
								var btns = buttontype.replace(/xtype#/,'').split(',');
								Ext.Array.each(btns,function(btn){
									toolbar.insert(0,{
										xtype: btn,
										fireHandler: function(e){
											var me = this,
											handler = me.handler;
											canexecute=true;    
											if(btn!='erpEditDetailButton'){
												var form = me.ownerCt.ownerCt;
												var statusCodeField = form.statuscodeField;
												var tablename = form.tablename;
												var keyField = form.keyField;
												var updateStatus = function(from,to){
													if(statusCodeField&&tablename&&keyField){
														var w = window.frames['iframe_maindetail'].contentWindow;
														tablename = tablename.toUpperCase();
														if(tablename.indexOf('LEFT JOIN')>-1){
															tablename = tablename.substring(0,tablename.indexOf('LEFT JOIN'));
														}else if(tablename.indexOf('RIGHT JOIN')>-1){
															tablename = tablename.substring(0,tablename.indexOf('RIGHT JOIN'));
														}
														if(w.Ext&&tablename){
															var key = w.Ext.getCmp(keyField);
															if(key&&key.value){
																Ext.Ajax.request({
																	url:basePath + 'common/updateByCondition.action',
																	method:'post',
																	async:false,
																	params:{
																		table:tablename,
																		update:statusCodeField + "='"+to+"'",
																		condition:statusCodeField + "='"+from+"' and " +keyField + "='"+key.value+"'"
																	},
																	callback:function(options,success,response){
																		var res = Ext.decode(response.responseText);
																		if(res.exceptionInfo){
																			showError(res.exceptionInfo);
																		}
																	}
																});
															}
														}
													}
												};
												var status = w.Ext.getCmp(statusCodeField);
												if(status&&status.value){
													updateStatus(status.value,'ENTERING');
													me.fireEvent('click', me, e);
													Ext.defer(function(){
														updateStatus('ENTERING',status.value);
													},500);
												}else{
													updateStatus('COMMITED','ENTERING');
													me.fireEvent('click', me, e);
													Ext.defer(function(){
														updateStatus('ENTERING','COMMITED');
													},500);
												}
											}else{
												me.fireEvent('click', me, e,true);
											}
											if (handler) {
												handler.call(me.scope || me, me, e,w.Ext.ComponentQuery.query('grid'),requiredFields);
											}
											me.onBlur();

										},
										listeners:{
											afterrender:function(btn){
												Ext.defer(function(){
													if(btn.hidden){
														btn.show();
													}
												},500);														    			
											}
										}
									});	
								});
							}else{
								toolbar.insert(0,[{
									xtype: buttontype.split('#')[1],
									text: btn.jb_buttonname,
									fireHandler: function(e){
										var me = this,
										handler = me.handler;
										canexecute=true;    
										me.fireEvent('click', me, e);
										if (handler) {
											handler.call(me.scope || me, me, e);
										}
										me.onBlur();
									}
								}]);
							}

						} else {
							childtoolbar.insert(0,[{
								xtype: 'button',
								text: btn.jb_buttonname,
								id: btn.jb_id,
								group: btn.jb_fields,
								iconCls: 'x-button-icon-save',
								cls: 'x-btn-gray',
								formBind: true,
								handler: function() {
									var values = {};
									var necessaryValues = (iframe_maindetail.contentWindow||iframe_maindetail.window).Ext.getCmp('form').getValues();
									var bool = true;
									if (requiredFields != null) {
										var fields = requiredFields.split(",");
										Ext.Array.each(fields,
												function(field) {
											if (necessaryValues[field] == null || necessaryValues[field] == "") {
												bool = false;
												showError('保存或同意之前请先填写必填的信息!');
												return false;
											}
										});
									}
									if (bool) {
										childpanel.getForm().getFields().each(function(field) {
											//&& field.groupName==button.jb_fields 有些组件写的有问题
											if (field.isDirty() &&  field.logic!='ignore' ) {
												var data = field['getSubmitData'](true);
												if (Ext.isObject(data)) {
													Ext.iterate(data,
															function(name, val) {
														if (true && val === '') {
															val = field.emptyText || '';
														}
														if (name in values) {
															var bucket = values[name],
															isArray = Ext.isArray;
															if (!isArray(bucket)) {
																bucket = values[name] = [bucket];
															}
															if (isArray(val)) {
																values[name] = bucket.concat(val);
															} else {
																bucket.push(val);
															}
														} else {
															values[name] = val;
														}
													});
												}
											}
										});
										var grids = w.Ext.ComponentQuery.query('itemgrid');
										Ext.each(grids,function(g,index){
											if(g.xtype=='itemgrid' && !g.readOnly){
												g.saveValue();
											}
										});
										values[ProcessData.jp_keyName] = ProcessData.jp_keyValue;
										Ext.Ajax.request({
											url: basePath + '/common/processUpdate.action',
											method: 'post',
											async:false,
											params: {
												caller: caller,
												processInstanceId:ProcessData.jp_processInstanceId,
												formStore: unescape(Ext.JSON.encode(values).replace(/\\/g, "%")),
												_noc: 1
											},
											callback: function(options, success, response) {
												var localJson = new Ext.decode(response.responseText);
												canexecute = true;
												if (localJson.success) {
													conditionValidation=0;
													showMessage('提示', '更新成功!', 1000);
													childpanel.getForm().getFields().each(function(field) {
														if (field.isDirty() &&  field.logic!='ignore' ) {
															field.originalValue = field.rawValue;
														}
													});
												} else if (localJson.exceptionInfo) {
													var str = localJson.exceptionInfo;
													if (str.trim().substr(0, 12) == 'AFTERSUCCESS') { //特殊情况:操作成功，但是出现警告,允许刷新页面
														str = str.replace('AFTERSUCCESS', '');
														conditionValidation=0;
														showMessage('提示', '更新成功!', 1000);
													}else conditionValidation=1;
													showError(str);
													return;
												} else {																		
													updateFailure();
												}
											}
										});
									}
								}
							}]);
						}
						var items = w.Ext.ComponentQuery.query('form')[0].items.items;
						var forms = w.Ext.ComponentQuery.query('form');
						Ext.Array.each(forms,function(form){
							if(form.dockedItems){
								if(form.dockedItems.items.length>0){
									items = form.items.items;
									return false;
								}
							}
						});
						//var nameArray=button.jb_buttonname.split(";");
						var fieldsArray=btn.jb_fields.split(";");
						requiredFields = neccessaryField;
						var necFields=neccessaryField!=null?neccessaryField.split(","):[];
						//去除必填左右空格
						for(var i=0;i<necFields.length;i++){
							necFields[i]=Ext.util.Format.trim(necFields[i]);
						}
						Ext.each(items,function(item) {
							if (item.groupName == btn.jb_buttonname && btn.jb_fields.indexOf("#") > 0) {
								if(item.xtype =='itemgrid'){
									item.readOnly=false;
								}else{
									item.setReadOnly(false);
								}
							}
							if (Ext.Array.contains(fieldsArray,item.groupName)) {
								if(item.xtype =='itemgrid'){
									item.readOnly=false;
								}else{
									item.setReadOnly(false);
								}
								if (item.xtype!='checkbox' && item.xtype!='itemgrid' && !Ext.Array.contains(necFields,item.name)) {
									item.setFieldStyle("background:#fff;color:#515151;");
								} 
							}
							if(Ext.Array.contains(necFields,item.name)){
								if(item.xtype =='itemgrid'){
									item.readOnly=false;
								}else{
									item.setReadOnly(false);
								}
								if(item.xtype!='checkbox')
									item.setFieldStyle("background:#d4dce6;color:#515151;");
							}
							if(item.id=='newStyle_Tab'){
								var tabpanel = item;
								//为可读状态的tab页激活显示
								var i = 0,readStatus,overActive;
								Ext.each(item.items.items,function(panel) {
									Ext.each(panel.items.items,function(item) {
										if (item.groupName == btn.jb_buttonname && btn.jb_fields.indexOf("#") > 0) {
											if(item.xtype =='itemgrid'){
												item.readOnly=false;
											}else{
												item.setReadOnly(false);
											}
											readStatus = true;
										}
										if (Ext.Array.contains(fieldsArray,item.groupName)) {
											if(item.xtype =='itemgrid'){
												item.readOnly=false;
											}else{
												item.setReadOnly(false);
											}
											if (item.xtype!='checkbox' && item.xtype!='itemgrid' && !Ext.Array.contains(necFields,item.name)) {
												item.setFieldStyle("background:#fff;color:#515151;");
											} 
											readStatus = true;
										}
										if(Ext.Array.contains(necFields,item.name)){
											if(item.xtype =='itemgrid'){
												item.readOnly=false;
											}else{
												item.setReadOnly(false);
											}
											if(item.xtype!='checkbox'){
												item.setFieldStyle("background:#d4dce6;color:#515151;");
											}
											readStatus = true;
										}
										if(readStatus&&!overActive){
											tabpanel.setActiveTab(i);
											overActive = true;
										}
									});
									i++;
								});
							}
						});
					}
				}
			}
		};
      	var hideToolbar = '&_Virtual=1';
      	if(btn&&jp_status=='待审批'){
        	hideToolbar = '&_Virtual=0'
      	}
      	if(disagree){
      		hideToolbar = '&_Virtual=-1'
      	}
		me.add({
			xtype: 'component',							
			id:'iframe_maindetail',									
			autoEl: {
				tag: 'iframe',
				style: 'height:100%; width: 100%; border: none;',
				src: url + hideToolbar
			},
			listeners: {
				load: {
					element: 'el',
					fn: function () {
						if(!disagree){
							IntervalTask= window.setInterval(findToolbar, 1000);
						}
						if(!ISexecuted){
							Ext.getCmp('agree-task').setDisabled(false);
						}
					}
				}
			}
		});
	},
	autoAudit:function(){	
		var windows= Ext.create('Ext.window.Window', {   
     		x: Ext.getBody().getWidth()/10, 
			y: Ext.getBody().getHeight()/16,
     		width:"80%",
     		height:"90%",
     		modal:true,
     		id:'autoauditwindow',
     		closable:true,     	
     		border: false,     		
     		resizable :false,
     		header: false,
     		draggable: false,
     		title :'智能审批设置',
     		buttonAlign:'center',
     		items: {xtype: 'component',
			id:'iframe_detail',   					
			autoEl: {
				tag: 'iframe',
				style: 'height: 100%; width: 100%; border: none;',
				src: basePath +'jsps/common/jprocessDeal/jprocessAutoAudit.jsp'
			}
		},
			buttons:[
			{
				xtype:'button',
				text:'关闭',
				height:25,	
				handler:function(v){
					v.ownerCt.ownerCt.close();
				}		
		}]	
		
		 });
		 windows.show();
		
	}
});