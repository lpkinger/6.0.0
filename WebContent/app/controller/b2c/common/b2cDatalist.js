Ext.QuickTips.init();
Ext.define('erp.controller.b2c.common.b2cDatalist', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views:[
	       'b2c.common.datalistViewport','b2c.common.b2cDatalistPanel','b2c.common.b2cDatalistGrid','common.datalist.Toolbar','core.button.VastAudit','core.button.VastDelete',
	       'core.button.VastPrint','core.button.VastReply','core.button.VastSubmit','core.button.ResAudit','core.form.FtField',
	       'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
	       'core.form.FtNumberField', 'core.form.MonthDateField','core.form.BtnDateField'
	       ],
	       init:function(){
	    	   this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   this.FormUtil = Ext.create('erp.util.FormUtil');
	    	   this.GridUtil = Ext.create('erp.util.GridUtil');
	    	   this.control({
	    		   'erpDatalistGridPanel': { 
	    			   itemclick: this.onGridItemClick
	    		   },
	    		   'erpVastDeleteButton': {
	    			   click: function(btn){
	    				   var dlwin = new Ext.window.Window({
	    					   id : 'dlwin',
	    					   title: btn.text,
	    					   height: "100%",
	    					   width: "80%",
	    					   maximizable : true,
	    					   buttonAlign : 'center',
	    					   layout : 'anchor',
	    					   items: [{
	    						   tag : 'iframe',
	    						   frame : true,
	    						   anchor : '100% 100%',
	    						   layout : 'fit',
	    						   html : '<iframe id="iframe_dl_'+caller+'" src="'+basePath+'jsps/common/vastDatalist.jsp?urlcondition='+condition+'&whoami='+caller+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	    					   }],
	    					   buttons : [{
	    						   text: btn.text,
	    						   iconCls: btn.iconCls,
	    						   cls: 'x-btn-gray-1',
	    						   handler: function(){

	    						   }
	    					   },{
	    						   text : '关  闭',
	    						   iconCls: 'x-button-icon-close',
	    						   cls: 'x-btn-gray',
	    						   handler : function(){
	    							   Ext.getCmp('dlwin').close();
	    						   }
	    					   }]
	    				   });
	    				   dlwin.show();
	    			   }
	    		   },
	    		   'button[id=searchlist]': {
	    			   click: function(){
	    				   this.showSearchListWin();
	    			   }
	    		   },
	    		   'button[id=customize]': {
	    			   click: function(){
	    				   this.showCustomizeWin();
	    			   }
	    		   },
	    		   'dbfindtrigger[name=sl_label]': {
	    			   afterrender: function(t){
	    				   t.dbBaseCondition = 'sl_caller=\'' + caller + '\'';
	    			   }
	    		   }
	    	   });
	       }, 
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   if(typeof parentDoc !== 'undefined' && parentDoc) {
	    		   var doc = parent.Ext.getCmp(parentDoc);
	    		   if(doc) {
	    			   doc.fireEvent('itemselect', doc, record.data);
	    		   }
	    	   } else {
	    		   if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
		    		   if(keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
		    			   this.openQueryUrl(record, selModel.ownerCt);
		    		   } else {
		    			   this.openUrl(record);
		    		   }
		    	   }
	    	   }
	       }, 
	       openUrl: function(record) {
	    	   var me = this, value = record.data[keyField];
	    	   var formCondition = keyField + "IS" + value ;
	    	   var gridCondition = pfField + "IS" + value;
	    	   var newmaster = record.data['CURRENTMASTER'];
	    	   if(!Ext.isEmpty(pfField) && pfField.indexOf('+') > -1) {//多条件传入维护界面//vd_vsid@vd_id+vd_class@vd_class
	    		   var arr = pfField.split('+'),ff = [],k = [];
	    		   Ext.Array.each(arr, function(r){
	    			   ff = r.split('@');
	    			   k.push(ff[0] + 'IS\'' + record.get(ff[1]) + '\'');
	    		   });
	    		   gridCondition = k.join(' AND ');
	    	   }
	    	   var panelId = caller + keyField + "_" + value + gridCondition;
	    	   var panel = Ext.getCmp(panelId); 
	    	   var main = parent.Ext.getCmp("content-panel");
	    	   if(!main){
	    		   main = parent.parent.Ext.getCmp("content-panel");
	    	   }
	    	   if(!panel){ 
	    		   var title = "";
	    		   if (value.toString().length>4) {
	    			   title = value.toString().substring(value.toString().length-4);	
	    		   } else {
	    			   title = value;
	    		   }
	    		   var myurl = '';
	    		   if(me.BaseUtil.contains(url, '?', true)){
	    			   myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
	    		   } else {
	    			   myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition;
	    		   }
	    		   myurl += "&datalistId=" + main.getActiveTab().id;
	    		   /**
	    		    * 优软云界面配置 
	    		    * */
	    		   if(getUrlParam('_config')){
	    			   myurl +='&_config='+getUrlParam('_config');
	    		   }
	    		   if( newmaster ){
	    			   var currentMaster = parent.window.sob;
	    			   if ( currentMaster && currentMaster != newmaster) {// 与当前账套不一致
	    				   me.openModalWin(newmaster, currentMaster, myurl);return;
	    			   }
	    		   }
	    		   main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页
	    		   main.getActiveTab().currentRecord=record;
	    		   if(main._mobile) {
	    			   main.addPanel(me.BaseUtil.getActiveTab().title+'('+title+')', myurl, panelId);
	    		   } else {
	    			   panel = {       
	    					   title : me.BaseUtil.getActiveTab().title+'('+title+')',
	    					   tag : 'iframe',
	    					   tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
	    					   border : false,
	    					   layout : 'fit',
	    					   iconCls : 'x-tree-icon-tab-tab1',
	    					   html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
	    					   closable : true,
	    					   listeners : {
	    						   close : function(){
	    							   if(!main){
	    								   main = parent.parent.Ext.getCmp("content-panel");
	    							   }
	    							   main.setActiveTab(main.getActiveTab().id); 
	    						   }
	    					   } 
	    			   };
	    			   this.openTab(panel, panelId);
	    		   }
	    	   }else{ 
	    		   main.setActiveTab(panel); 
	    	   }
	       },
	       openQueryUrl: function(record, grid) {
	    	   var me = this, arr = keyField.split('+'), ff = [], k = [], val, fields = Ext.Object.getKeys(record.data);//vd_vsid@vd_id+vd_class@vd_class
	    	   Ext.Array.each(arr, function(r){
	    		   ff = r.split('@');
	    		   if(fields.indexOf(ff[1]) > -1) {
	    			   val = record.get(ff[1]);
		    		   if(val instanceof Date)
		    			   val = Ext.Date.format(val, 'Y-m-d');
	    		   } else {
	    			   val = ff[1];
	    		   }
	    		   k.push(ff[0] + '=' + val);
	    	   });
	    	   var myurl = k.join('&');
	    	   var panelId = caller +  "_" + myurl;
	    	   var panel = Ext.getCmp(panelId); 
	    	   var main = parent.Ext.getCmp("content-panel");
	    	   if(!main){
	    		   main = parent.parent.Ext.getCmp("content-panel");
	    	   }
	    	   if(!panel){ 
	    		   var title = me.BaseUtil.getActiveTab().title + '-查询';
	    		   if(contains(url, '?', true)){
	    			   myurl = url + '&' + myurl;
	    		   } else {
	    			   myurl = url + '?' + myurl;
	    		   }
	    		   if (main._mobile) {
	    			   main.addPanel(title, myurl, panelId);
	    		   } else {
	    			   panel = {       
	    					   title : title,
	    					   tag : 'iframe',
	    					   tabConfig: {tooltip: title},
	    					   border : false,
	    					   layout : 'fit',
	    					   iconCls : 'x-tree-icon-tab-tab1',
	    					   html : '<iframe src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
	    					   closable : true,
	    					   listeners : {
	    						   close : function(){
	    							   if(!main){
	    								   main = parent.parent.Ext.getCmp("content-panel");
	    							   }
	    							   main.setActiveTab(main.getActiveTab().id); 
	    						   }
	    					   } 
	    			   };
	    			   this.openTab(panel, panelId);
	    		   }
	    	   } else { 
	    		   main.setActiveTab(panel); 
	    	   }
	       },
	       openTab : function (panel,id){ 
	    	   var o = (typeof panel == "string" ? panel : id || panel.id); 
	    	   var main = parent.Ext.getCmp("content-panel"); 
	    	   /*var tab = main.getComponent(o); */
	    	   if(!main) {
	    		   main =parent.parent.Ext.getCmp("content-panel"); 
	    	   }
	    	   var tab = main.getComponent(o); 
	    	   if (tab) { 
	    		   main.setActiveTab(tab); 
	    	   } else if(typeof panel!="string"){ 
	    		   panel.id = o; 
	    		   var p = main.add(panel); 
	    		   main.setActiveTab(p); 
	    	   } 
	       },
	       openModalWin: function(master, current, url) {
	    	   if (parent.Ext) {
	    		   Ext.Ajax.request({
	    			   url: basePath + 'common/changeMaster.action',
	    			   params: {
	    				   to: master
	    			   },
	    			   callback: function(opt, s, r) {
	    				   if (s) {
	    					   var localJson = new Ext.decode(r.responseText);
	    					   var win = parent.Ext.create('Ext.Window', {
	    						   width: '100%',
	    						   height: '100%',
	    						   draggable: false,
	    						   closable: false,
	    						   modal: true,
	    						   id:'modalwindow',
	    						   historyMaster:current,
	    						   title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
	    						   html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
	    						   buttonAlign: 'center',
	    						   buttons: [{
	    							   text: $I18N.common.button.erpCloseButton,
	    							   cls: 'x-btn-blue',
	    							   id: 'close',
	    							   handler: function(b) {
	    								   Ext.Ajax.request({
	    									   url: basePath + 'common/changeMaster.action',
	    									   params: {
	    										   to: current
	    									   },
	    									   callback: function(opt, s, r) {
	    										   if (s) {
	    											   b.up('window').close();
	    										   } else {
	    											   alert('切换到原账套失败!');
	    										   }
	    									   }
	    								   });
	    							   }
	    						   }]
	    					   });
	    					   win.show();
	    				   } else {
	    					   alert('无法创建到账套' + master + '的临时会话!');
	    				   }
	    			   }
	    		   });
	    	   }
	       },
	       getCurrentStore: function(value){
	    	   var grid = Ext.getCmp('grid');
	    	   var items = grid.store.data.items;
	    	   var array = new Array();
	    	   var o = null;
	    	   Ext.each(items, function(item, index){
	    		   o = new Object();
	    		   o.selected = false;
	    		   if(index == 0){
	    			   o.prev = null;
	    		   } else {
	    			   o.prev = items[index-1].data[keyField];
	    		   }
	    		   if(index == items.length - 1){
	    			   o.next = null;
	    		   } else {
	    			   o.next = items[index+1].data[keyField];
	    		   }
	    		   var v = item.data[keyField];
	    		   o.value = v;
	    		   if(v == value)
	    			   o.selected = true;
	    		   array.push(o);
	    	   });
	    	   return array;
	       },
	       showSearchListWin: function(){
	    	   var me = this, win = this.searchWin;
	    	   if (!win){
	    		   win = this.searchWin = Ext.create('Ext.window.Window', {
	    			   title: '高级查询',
	    			   height: screen.height*0.7*0.8,
	    			   width: screen.width*0.7*0.6,
	    			   maximizable : true,
	    			   closable: false,
	    			   buttonAlign : 'center',
	    			   layout : 'border',
	    			   bodyStyle: 'background:#f1f1f1;',
	    			   tools: [{
	    				   type: 'close',
	    				   handler: function(e, el, header, tool){
	    					   tool.ownerCt.ownerCt.down('grid').setEffectData();//保留已选择的条件
	    					   tool.ownerCt.ownerCt.hide();
	    				   }
	    			   }],
	    			   items: [{
	    				   xtype: 'form',
	    				   region: 'north',
	    				   layout: 'column',
	    				   bodyStyle: 'background:#f1f1f1;',
	    				   maxHeight: 100,
	    				   buttonAlign: 'center',
	    				   buttons: [{
	    					   name: 'query',
	    					   id: 'query',
	    					   text: $I18N.common.button.erpQueryButton,
	    					   iconCls: 'x-button-icon-query',
	    					   cls: 'x-btn-gray',
	    					   handler: function(btn){
	    						   Ext.getCmp('grid').getCount(caller);
	    						   btn.ownerCt.ownerCt.ownerCt.hide();
	    					   }
	    				   },{
	    					   cls: 'x-btn-gray',
	    					   text: '清空',
	    					   handler: function(btn){
	    						   btn.ownerCt.ownerCt.ownerCt.down('grid').store.loadData([{},{},{},{},{},{},{},{},{},{}]);
	    						   Ext.getCmp('grid').getCount(caller);
	    					   }
	    				   },{
	    					   cls: 'x-btn-gray',
	    					   text: '关闭',
	    					   handler: function(btn){
	    						   btn.ownerCt.ownerCt.ownerCt.down('grid').setEffectData();
	    						   btn.ownerCt.ownerCt.ownerCt.hide();
	    					   }
	    				   },{
	    					   xtype: 'radio',
	    					   name: 'separator',
	    					   boxLabel: '与',
	    					   checked: true,
	    					   inputValue: 'AND',
	    					   getCheckValue: function(){
	    						   return this.checked ? 'AND' : 'OR';
	    					   }
	    				   },{
	    					   xtype: 'radio',
	    					   name: 'separator',
	    					   boxLabel: '或',
	    					   inputValue: 'OR'
	    				   }]
	    			   }, me.getSearchListGrid(), me.getTemplateForm() ]
	    		   });
	    		   Ext.getCmp('grid').searchGrid = win.down('grid');
	    		   Ext.getCmp('grid').tempalteForm = win.down('form[name=template]');
	    		   this.getTemplates(caller);
	    	   }
	    	   win.show();
	    	   win.down('grid').loadData();
	       },
	       showCustomizeWin:function(){
	    	   var me = this, win = this.CustomizeWin, grid = Ext.getCmp('grid');
	    	   if(!win){
	    		   var ablecolumns=new Array(),unselectcolumns=grid.basecolumns;
	    		   Ext.Array.each(grid.columns,function(item){
	    			   if(item.text && item.text.indexOf('&#160')<0){
	    				   ablecolumns.push(item);
	    			   }
	    		   });
	    		   unselectcolumns.splice(0,ablecolumns.length);
	    		   this.CustomizeWin=win = Ext.create('Ext.window.Window', {
	    			   title: '<div align="center">个性设置</div>',
	    			   height: screen.height*0.7,
	    			   width: screen.width*0.7*0.9,
	    			   layout:'border',
	    			   closeAction:'hide',
	    			   items:[{
	    				   region:'center',
	    				   layout:{
	    					   type: 'hbox',
	    					   align: 'stretch',
	    					   padding: 5
	    				   },
	    				   defaults     : { flex : 1 },
	    				   items:[{
	    					   xtype:'grid',
	    					   multiSelect: true,
	    					   id: 'fromgrid',
	    					   title:'不显示列',
	    					   flex:0.7,
	    					   cls: 'custom-grid',	    		
	    					   store:Ext.create('Ext.data.Store', {
	    						   fields: [{name:'fullName',type:'string'},{name:'text',type:'string'},{name:'width',type:'number'}],
	    						   data: unselectcolumns,
	    						   filterOnLoad: false 
	    					   }),
	    					   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
	    					   viewConfig: {
	    						   plugins: {
	    							   ptype: 'gridviewdragdrop',
	    							   dragGroup: 'togrid',
	    							   dropGroup: 'togrid'
	    						   }	    					
	    					   },
	    					   stripeRows: false,
	    					   columnLines:true,
	    					   columns:[{
	    						   dataIndex:'fullName',
	    						   cls :"x-grid-header-1",
	    						   text:'字段名称',
	    						   width:120,
	    						   filter: {
	    							   xtype : 'textfield'
	    						   }
	    					   },{
	    						   dataIndex:'text',
	    						   text:'描述',
	    						   cls :"x-grid-header-1",
	    						   flex:1,
	    						   filter: {
	    							   xtype : 'textfield'
	    						   }
	    					   },{
	    						   dataIndex:'width',
	    						   text:'宽度',
	    						   width:60,
	    						   cls :"x-grid-header-1",
	    						   align:'right',
	    						   editor: {
	    							   xtype: 'numberfield',
	    							   format:0
	    						   },
	    						   filter: {
	    							   xtype : 'textfield'
	    						   }
	    					   }]
	    				   },{
	    					   xtype:'grid',
	    					   multiSelect: true,
	    					   id: 'togrid',
	    					   stripeRows: true,
	    					   columnLines:true,
	    					   title:'显示列',
	    					   store:Ext.create('Ext.data.Store', {
	    						   fields: [{name:'fullName',type:'string'},{name:'text',type:'string'},{name:'width',type:'number'},
	    						            {name:'orderby',type:'string'},{name:'priority',type:'string'}],
	    						            data:ablecolumns,
	    						            filterOnLoad: false 
	    					   }),
	    					   necessaryField:'fullName',
	    					   plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),
	    					             Ext.create('Ext.grid.plugin.CellEditing', {
	    					            	 clicksToEdit: 1
	    					             })],
	    					             viewConfig: {
	    					            	 plugins: {
	    					            		 ptype: 'gridviewdragdrop',
	    					            		 dragGroup: 'togrid',
	    					            		 dropGroup: 'togrid'
	    					            	 }
	    					             },
	    					             columns:[{
	    					            	 dataIndex:'fullName',
	    					            	 text:'字段名称',
	    					            	 cls :"x-grid-header-1",
	    					            	 width:120,
	    					            	 filter: {
	    					            		 xtype : 'textfield'
	    					            	 }
	    					             },{
	    					            	 dataIndex:'text',
	    					            	 text:'描述',
	    					            	 cls :"x-grid-header-1",
	    					            	 flex:1,
	    					            	 filter: {
	    					            		 xtype : 'textfield'
	    					            	 }
	    					             },{
	    					            	 dataIndex:'width',
	    					            	 text:'宽度',
	    					            	 width:60,
	    					            	 xtype:'numbercolumn',
	    					            	 align:'right',
	    					            	 cls :"x-grid-header-1",
	    					            	 filter: {
	    					            		 xtype : 'textfield'
	    					            	 },
	    					            	 editable:true,
	    					            	 format: '0',
	    					            	 editor: {
	    					            		 xtype: 'numberfield',
	    					            		 hideTrigger: true
	    					            	 },
	    					             },
	    					             {
	    					            	 dataIndex:'orderby',
	    					            	 text:'排序',
	    					            	 width:60,
	    					            	 xtype:'combocolumn',
	    					            	 cls :"x-grid-header-1",
	    					            	 filter: {
	    					            		 xtype : 'textfield'
	    					            	 },
	    					            	 renderer:function(val){
	    					            		 if(val=='ASC'){
	    					            			 return '<img src="' + basePath + 'resource/images/16/up.png">' + 
	    					            			 '<span style="color:red;padding-left:2px">升序</span>';
	    					            		 } else if(val=='DESC') {
	    					            			 return '<img src="' + basePath + 'resource/images/16/down.png">' + 
	    					            			 '<span style="color:red;padding-left:2px">降序</span>';
	    					            		 }
	    					            	 },
	    					            	editor:{
	    					            			 xtype:'combo',
	    					            			 queryMode: 'local',
	    					            			 displayField: 'display',
	    					            			 valueField: 'value',
	    					            			 store:Ext.create('Ext.data.Store', {
	    					            				 fields: ['value', 'display'],
	    					            				 data : [{value:"ASC", display:"升序"},
	    					            				         {value:"DESC", display:"降序"}]
	    					            			 })
	    					            		 }
	    					            	 },{
	    					            		 dataIndex:'priority',
	    					            		 text:'优先级',
	    					            		 width:60,
	    					            		 align:'right',
	    					            		 cls :"x-grid-header-1",
	    					            		 filter: {
	    					            			 xtype : 'textfield'
	    					            		 },
	    					            		 editor:{
	    					            			 xtype:'combo',
	    					            			 queryMode: 'local',
	    					            			 displayField: 'display',
	    					            			 valueField: 'value',
	    					            			 store:Ext.create('Ext.data.Store', {
	    					            				 fields: ['value', 'display'],
	    					            				 data : [{value:"1", display:"1"},
	    					            				         {value:"2", display:"2"},
	    					            				         {value:"3", display:"3"},
	    					            				         {value:"4", display:"4"},
	    					            				         {value:"5", display:"5"},
	    					            				         {value:"6", display:"6"},
	    					            				         {value:"7", display:"7"},
	    					            				         {value:"8", display:"8"},
	    					            				         {value:"9", display:"9"},]
	    					            			 })
	    					            		 }
	    					            	 }] 

	    					             }]
	    				   }],
	    				   buttonAlign:'left',
	    				   buttons:[{xtype : 'tbtext',
	    					   text:'<font color=gray>*【提示】拖动显示列可设置列是否显示及显示顺序</font>' },'->',{
	    					   text:'重置',
	    					   scope:this,
	    					   handler:function(btn){
	    						   warnMsg('重置列表将还原配置，确认重置吗?', function(btn){
	    								if(btn == 'yes'){
	    									Ext.Ajax.request({
	    										url : basePath + 'common/resetEmpsDataListDetails.action?_noc=1',
	    										params: {
	    											caller:caller
	    										},
	    										method : 'post',
	    										callback : function(options,success,response){
	    											var localJson = new Ext.decode(response.responseText);
	    											if(localJson.success){
	    												showMessage('提示','重置成功!',1000);
	    													window.location.reload();
	    											} else {
	    												if(localJson.exceptionInfo){
	    													var str = localJson.exceptionInfo;
	    													if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	    														str = str.replace('AFTERSUCCESS', '');
	    														showError(str);
	    													} else {
	    														showError(str);return;
	    													}
	    												}
	    											}
	    										}
	    									});
	    								}
	    							});
	    					   }
	    				    },{
	    					   style:'margin-left:5px;',
	    					   text:'保存',
	    					   scope:this,
	    					   handler:function(btn){
	    						   var grid=Ext.getCmp('togrid'),fromgrid=Ext.getCmp('fromgrid');
	    						   var jsonGridData = new Array(),datas=new Array();
	    							var form = Ext.getCmp('form');
	    							grid.getStore().each(function(item){//将grid里面各行的数据获取并拼成jsonGridData
	    								var data = {
	    								  dde_field:item.data.fullName,
	    								  dde_width:item.data.width,
	    								  dde_orderby:item.data.orderby && item.data.orderby!='ASC' && item.data.orderby!='DESC' ? null : item.data.orderby,
	    								  dde_priority:item.data.priority
	    								}; 
	    								item.dirty=false;
                                    	jsonGridData.push(Ext.JSON.encode(data));
                                    	datas.push(item.data);
	    							});
	    						   Ext.Ajax.request({
	    								url : basePath + 'common/saveEmpsDataListDetails.action?_noc=1',
	    								params : {
	    									caller:caller,
	    									data:unescape(jsonGridData.toString())
	    								},
	    								method : 'post',
	    								callback : function(options,success,response){
	    									var localJson = new Ext.decode(response.responseText);
	    									if(localJson.success){
	    										showMessage('提示','保存成功!',1000);
	    										window.location.reload();
	    									}
	    								}

	    							});
	    						   
	    					   }
	    				   },{
	    					   style:'margin-left:5px;',
	    					   text:'关闭',
	    					   handler:function(btn){
	    						   btn.ownerCt.ownerCt.hide();
	    					   }
	    				   },'->','->']

	    			   });
	    		   }
	    		   win.show();
	    	   },
	    	   getFilterCondition: function(){
	    		   var fields = Ext.getCmp('grid').plugins[0].fields;
	    		   var items = new Array();
	    		   Ext.each(Ext.Object.getKeys(fields), function(key){
	    			   var item = fields[key];
	    			   if(item.value != null && item.value.toString().trim() != ''){
	    				   items.push({
	    					   xtype: item.xtype,
	    					   id: item.itemId,
	    					   fieldLabel: item.fieldLabel,
	    					   fieldStyle: item.fieldStyle,
	    					   value: item.value,
	    					   columnWidth: 0.5,
	    					   cls: 'form-field-border',
	    					   listeners: {
	    						   change: function(f){
	    							   Ext.getCmp(item.id).setValue(f.value);
	    						   }
	    					   }
	    				   });
	    			   }
	    		   });
	    		   return items;
	    	   },
	    	   getGridColumns : function() {
	    		   var grid = Ext.getCmp('grid'), columns = grid.headerCt.getGridColumns(), data = [];
	    		   Ext.each(columns, function(){
	    			   if(this.dataIndex && this.getWidth() > 0) {
	    				   data.push({
	    					   display : this.text,
	    					   value : this.text,
	    					   column : this
	    				   });
	    			   }
	    		   });
	    		   return data;
	    	   },
	    	   getSearchListGrid: function(){
	    		   var data = this.getGridColumns();
	    		   var grid = Ext.create('Ext.grid.Panel', {
	    			   maxHeight: 350,
	    			   region: 'center',
	    			   store: Ext.create('Ext.data.Store', {
	    				   fields:[{
	    					   name: 'sl_label',
	    					   type: 'string'
	    				   },{
	    					   name: 'sl_field',
	    					   type: 'string'
	    				   },{
	    					   name: 'sl_type',
	    					   type: 'string'
	    				   },{
	    					   name: 'sl_dbfind',
	    					   type: 'string'
	    				   },{
	    					   name: 'union',
	    					   type: 'string'
	    				   },{
	    					   name: 'value'
	    				   }],
	    				   data: []
	    			   }),
	    			   columns: [{
	    				   text: '条件',
	    				   flex: 2,
	    				   dataIndex: 'sl_label',
	    				   editor: {
	    					   xtype: 'combo',
	    					   store : Ext.create('Ext.data.Store', {
	    						   fields : [ 'display', 'value', 'column' ],
	    						   data : data
	    					   }),
	    					   editable: false,
	    					   displayField : 'display',
	    					   valueField : 'value',
	    					   queryMode : 'local'
	    				   },
	    				   renderer : function(val, meta, record, x, y, store, view) {
	    					   if (val) {
	    						   var column = view.ownerCt.headerCt.getHeaderAtIndex(y);
	    						   if(column && typeof column.getEditor != 'undefined') {
	    							   var	editor = column.getEditor(record);
	    							   if (editor && editor.lastSelection.length > 0) {
	    								   var cm = editor.lastSelection[0].get('column'),
	    								   field = cm.dataIndex;
	    								   if (record.get('sl_field') != field)
	    									   record.set('sl_field', field);
	    								   var t = 'S';
	    								   if(cm.xtype == 'datecolumn' || cm.xtype == 'datetimecolumn') {
	    									   t = 'D';
	    								   } else if(cm.xtype == 'numbercolumn') {
	    									   t = 'N';
	    								   }
	    								   if (record.get('sl_type') != t)
	    									   record.set('sl_type', t);
	    							   }
	    						   }
	    					   } else {
	    						   if (record.get('sl_field')) {
	    							   record.set('sl_field', null);
	    						   }
	    					   }
	    					   return val;
	    				   },
	    				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
	    					   if (type == 'click' || type == 'dbclick') {
	    						   return true;
	    					   }
	    					   return false;
	    				   }
	    			   },{
	    				   text: '',
	    				   hidden: true,
	    				   dataIndex: 'sl_field',
	    				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
	    					   return false;
	    				   }
	    			   },{
	    				   text: '',
	    				   hidden: true,
	    				   dataIndex: 'sl_type',
	    				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
	    					   return false;
	    				   }
	    			   },{
	    				   text: '关系',
	    				   flex: 1,
	    				   dataIndex: 'union',
	    				   xtype:'combocolumn',
	    				   editor: {
	    					   xtype: 'combo',
	    					   store: Ext.create('Ext.data.Store', {
	    						   fields: ['display', 'value'],
	    						   data : [{"display": '等于', "value": '='},
	    						           {"display": '大于', "value": '>'},
	    						           {"display": '大于等于', "value": '>='},
	    						           {"display": '小于', "value": '<'},
	    						           {"display": '小于等于', "value": '<='},
	    						           {"display": '不等于', "value": '<>'},
	    						           {"display": '介于', "value": 'Between And'},
	    						           {"display": '包含', "value": 'like'},
	    						           {"display": '不包含', "value": 'not like'},
	    						           {"display": '开头是', "value": 'begin like'},
	    						           {"display": '开头不是', "value": 'begin not like'},
	    						           {"display": '结尾是', "value": 'end like'},
	    						           {"display": '结尾不是', "value": 'end not like'}]
	    					   }),
	    					   displayField: 'display',
	    					   valueField: 'value',
	    					   queryMode: 'local',
	    					   editable: false,
	    					   value: 'like'
	    				   },
	    				  /* renderer : function(v) {
	    					   var r = v;
	    					   switch(v) {
	    					   case 'like':
	    						   r = 'Like';break;
	    					   case '=':
	    						   r = '等于';break;
	    					   case '>':
	    						   r = '大于';break;
	    					   case '>=':
	    						   r = '大于等于';break;
	    					   case '<':
	    						   r = '小于';break;
	    					   case '<=':
	    						   r = '小于等于';break;
	    					   case '<>':
	    						   r = '不等于';break;
	    					   case 'Between And':
	    						   r = '介于';break;
	    					   }
	    					   return r;
	    				   },*/
	    				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
	    					   if (type == 'click' || type == 'dbclick') {
	    						   return true;
	    					   }
	    					   return false;
	    				   }
	    			   },{
	    				   text: '值',
	    				   flex: 3,
	    				   dataIndex: 'value',
	    				   renderer: function(val){
	    					   if(Ext.isDate(val)){
	    						   return Ext.Date.format(val, 'Y-m-d');
	    					   }
	    					   return val;
	    				   },
	    				   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
	    					   if (type == 'click' || type == 'dbclick') {
	    						   var s = view.ownerCt.selModel, m = s.getSelection(), n = [];
	    						   Ext.Array.each(m, function(){
	    							   n.push(this);
	    						   });
	    						   n.push(view.ownerCt.store.getAt(recordIndex));
	    						   s.select(n);
	    						   return true;
	    					   }
	    					   return false;
	    				   }
	    			   }],
	    			   columnLines: true,
	    			   plugins: Ext.create('Ext.grid.plugin.CellEditing', {
	    				   clicksToEdit: 1,
	    				   listeners: {
	    					   beforeedit: function(e){
	    						   if(e.field == 'value'){
	    							   var record = e.record;
	    							   var column = e.column;
	    							   if(record.data['union'] == null || record.data['union'] == ''){
	    								   record.set('union', 'like');
	    							   }
	    							   var f = record.data['sl_field']+'highSearch';
	    							   switch(record.data['sl_type']){
	    							   case 'D':
	    								   switch(record.data['union']){
	    								   case 'Between And':
	    									   column.setEditor(new erp.view.core.form.FtDateField({
	    										   name: f
	    									   }));break;
	    								   default:
	    									   column.setEditor(new Ext.form.field.Date({
	    										   name: f
	    									   }));break;
	    								   }
	    								   break;
	    							   case 'S':
	    								   switch(record.data['union']){
	    								   case 'Between And':
	    									   column.setEditor(new erp.view.core.form.FtField({
	    										   name: f,
	    										   value: e.value
	    									   }));break;
	    								   default:
	    									   column.setEditor(new Ext.form.field.Text({
	    										   name: f
	    									   }));break;
	    								   }
	    								   break;
	    							   case 'N':
	    								   switch(record.data['union']){
	    								   case 'Between And':
	    									   column.setEditor(new erp.view.core.form.FtNumberField({
	    										   id: f,
	    										   name: f
	    									   }));break;
	    								   default:
	    									   column.setEditor(new Ext.form.field.Number({
	    										   id: f,
	    										   name: f
	    									   }));break;
	    								   }
	    								   break;
	    							   case 'T':
	    								   column.dbfind = record.get('sl_dbfind');
	    								   switch(record.data['union']){
	    								   case 'Between And':
	    									   column.setEditor(new erp.view.core.form.FtFindField({
	    										   id: f,
	    										   name: f
	    									   }));break;
	    								   default:
	    									   column.setEditor(new erp.view.core.trigger.DbfindTrigger({
	    										   name: f
	    									   }));break;
	    								   }
	    								   break;
	    							   default:
	    								   column.setEditor(null);
	    							   }
	    						   }
	    					   }
	    				   }
	    			   }),
	    			   selModel: Ext.create('Ext.selection.CheckboxModel',{

	    			   }),
	    			   setEffectData: function(){
	    				   var me = this;
	    				   var datas = new Array();
	    				   Ext.each(me.selModel.getSelection(), function(item){
	    					   var data = item.data;
	    					   if(!Ext.isEmpty(data.sl_label) && !Ext.isEmpty(data.union) && !Ext.isEmpty(data.value)){
	    						   datas.push(data);
	    					   }
	    				   });
	    				   me.effectdata = datas;
	    			   },
	    			   getEffectData: function(){
	    				   return this.effectdata || new Array();
	    			   },
	    			   loadData: function(){
	    				   if(!this.effectdata) {
	    					   this.store.add([{},{},{},{},{},{},{},{},{},{}]);
	    				   }
	    			   },
	    			   /**
	    			    * 将数据拼成Sql条件语句
	    			    */
	    			   getCondition: function(){
	    				   this.setEffectData();
	    				   var condition = '';
	    				   var separator = this.up('window').down('form').down('radio').getCheckValue();
	    				   Ext.each(this.effectdata, function(data){
	    					   if(data.union == 'Between And'){
	    						   var v1 = data.value.split('~')[0];
	    						   var v2 = data.value.split('~')[1];
	    						   if(data.sl_type == 'D'){
	    							   if(condition == ''){
	    								   condition = '(' + data.sl_field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
	    								   + v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
	    							   } else {
	    								   condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
	    								   + v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
	    							   }
	    						   } else if(data.sl_type == 'N'){
	    							   if(condition == ''){
	    								   condition = '(' + data.sl_field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
	    							   } else {
	    								   condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
	    							   }
	    						   } else{
	    							   if(condition == ''){
	    								   condition = '(' + data.sl_field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
	    							   } else {
	    								   condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
	    							   }
	    						   }
	    					   } else {
	    						   if(data.sl_type == 'D'){
	    							   var v = data.value, field = data.sl_field;
	    							   if(Ext.isDate(v)) {
	    								   v = Ext.Date.format(v, 'Y-m-d');
	    							   }
	    							   if(data.union == '<' || data.union == '<=' || data.union == '>' || data.union == '>='){
	    								   v = "to_date('" + v + "','yyyy-MM-dd')";
	    							   }else {
	    								   v = Ext.Date.format(data.value, 'Ymd');
	    								   field = "to_char(" + field + ",'yyyymmdd')";
	    							   }
	    							   if(condition == ''){
	    								   condition = '(' + field + data.union + v + ') ';
	    							   } else {
	    								   condition += ' ' + separator +' (' + field + data.union + v + ') ';
	    							   }
	    						   } else {
	    							   var v = data.value;
	    							   var u =data.union ;
	    							   if(data.union == 'like' || data.union=='not like'){
	    								   v = " '%" + data.value + "%'";
	    							   }else if(data.union =='begin like' || data.union =='begin not like'){
	    								   v = " '" + data.value + "%'";
	    								   u=data.union.substring(5);
	    							   }else if(data.union =='end like' || data.union=='end not like'){
	    								   v = " '%" + data.value + "'";
	    								   u=data.union.substring(3);
	    							   }else {
	    								   v = " '" + data.value + "'";
	    							   }
	    							   if(condition == ''){
	    								   condition = '(' + data.sl_field + " " +u + v + ") ";
	    							   } else {
	    								   condition += ' ' + separator +' (' + data.sl_field + " " +u  + v + ") ";
	    							   }
	    						   }
	    					   }
	    				   });
	    				   return condition;
	    			   }
	    		   });
	    		   return grid;
	    	   },
	    	   getTemplateForm : function() {
	    		   var me = this;
	    		   return Ext.create('Ext.form.Panel', {
	    			   minHeight : 150,
	    			   region : 'south',
	    			   layout : 'column',
	    			   name : 'template',
	    			   bodyStyle : 'background:#f1f2f5;',
	    			   items : [{
	    				   xtype : 'fieldcontainer',
	    				   columnWidth : .75,
	    				   defaults : {
	    					   xtype : 'radio',
	    					   flex : 1,
	    					   margin : '5 0 5 15',
	    					   labelAlign : 'right'
	    				   },
	    				   items : [{
	    					   name : 'export-type',
	    					   boxLabel : '全部列',
	    					   checked : true
	    				   }]
	    			   }, {
	    				   xtype : 'fieldcontainer',
	    				   columnWidth : .25,
	    				   items : [{
	    					   xtype : 'button',
	    					   cls : 'x-btn-gray',
	    					   iconCls : 'x-button-icon-excel',
	    					   flex : 1,
	    					   margin : '15 0 5 0',
	    					   width : 80,
	    					   text : '导&nbsp;&nbsp;&nbsp;出',
	    					   handler : function(b) {
	    						   var grid = Ext.getCmp('grid'),
	    						   tb = grid.down('erpDatalistToolbar'),
	    						   r = b.ownerCt.ownerCt.down('radio[checked=true]');
	    						   if (r.fields)
	    							   tb.exportData(grid, b, r.boxLabel, r.fields);
	    						   else
	    							   tb.exportData(grid, b);
	    					   }
	    				   }
	    				   //已存在个性化设置列功能
	    				   /*,{
	    					   xtype : 'button',
	    					   cls : 'x-btn-gray',
	    					   flex : 1,
	    					   margin : '5 0 10 0',
	    					   width : 80,
	    					   text : '新建模板',
	    					   handler : function(b, e) {
	    						   var grid = Ext.getCmp('grid');
	    						   me.addTemplate(grid);
	    					   }
	    				   }*/
	    				   ]
	    			   }]
	    		   });
	    	   },
	    	  /* addTemplate : function(grid) {
	    		   var me = this, panel = grid.templatePanel;
	    		   if (!panel) {
	    			   var columns = grid.headerCt.getGridColumns(), data = [];
	    			   Ext.each(columns, function(){
	    				   if(this.dataIndex && this.getWidth() > 0) {
	    					   data.push({
	    						   boxLabel : this.text,
	    						   inputValue : this.dataIndex,
	    						   checked : true
	    					   });
	    				   }
	    			   });
	    			   data.push({
	    				   columnWidth : .8,
	    				   labelWidth : 60,
	    				   fieldLabel : '描述',
	    				   name : 'desc',
	    				   xtype : 'textfield',
	    				   allowBlank : false,
	    				   value : this.BaseUtil.getActiveTab().title + '(模板)-' + em_name 
	    				   + '-' + Ext.Date.format(new Date(),'Ymd')
	    			   });
	    			   panel = grid.templatePanel = Ext.create('Ext.panel.Panel', {
	    				   floating : true,
	    				   shadow : 'frame',
	    				   layout : 'column',
	    				   width : 700,
	    				   bodyStyle : 'background:#f1f2f5;z-index:9999;',
	    				   defaults : {
	    					   xtype : 'checkbox',
	    					   margin : '5 5 15 15',
	    					   columnWidth : .25
	    				   },
	    				   items : data,
	    				   buttonAlign : 'center',
	    				   buttons : [{
	    					   text : '确认',
	    					   handler : function(b) {
	    						   var items = panel.query('checkbox[checked=true]'), fs = [];
	    						   Ext.each(items, function(){
	    							   fs.push(this.inputValue);
	    						   });
	    						   me.onTemplateAdd(panel, caller, fs.join(','), panel.down('textfield[name=desc]').value);
	    					   }
	    				   },{
	    					   text : '取消',
	    					   handler : function() {
	    						   panel.hide();
	    					   }
	    				   }]
	    			   });
	    		   }
	    		   panel.show();
	    		   panel.center();
	    	   },*/
	    	   onTemplateAdd : function(panel, caller, fields, desc) {
	    		   warnMsg('确定添加新模板:' + desc + '?', function(b){
	    			   if (b == 'ok' || b == 'yes') {
	    				   Ext.Ajax.request({
	    					   url : basePath + 'common/template/save.action',
	    					   params : {
	    						   _noc : 1,
	    						   caller : caller,
	    						   fields : fields,
	    						   desc : desc
	    					   },
	    					   callback : function(opt, s, res) {
	    						   var r = Ext.decode(res.responseText);
	    						   if (r.success) {
	    							   alert('添加成功!');
	    							   panel.hide();
	    							   var form = Ext.getCmp('grid').tempalteForm;
	    							   form.down('radio[value=true]').setValue(false);
	    							   form.items.first().add({
	    								   name : 'export-type',
	    								   boxLabel : desc,
	    								   fields : fields,
	    								   checked : true
	    							   });
	    						   }
	    					   }
	    				   });
	    			   }
	    		   });
	    	   },
	    	   getTemplates : function(caller) {
	    		   Ext.Ajax.request({
	    			   url : basePath + 'common/getFieldsDatas.action',
	    			   async: false,
	    			   params: {
	    				   caller: 'DataTemplate',
	    				   fields: 'dt_desc,dt_fields',
	    				   condition: 'dt_caller=\'' + caller + '\''
	    			   },
	    			   method : 'post',
	    			   callback : function(opt, s, res){
	    				   var r = new Ext.decode(res.responseText);
	    				   if(r.exceptionInfo){
	    					   showError(r.exceptionInfo);return;
	    				   }
	    				   if (r.success && r.data) {
	    					   var f = [], dd = Ext.decode(r.data);
	    					   if(dd.length > 0) {
	    						   Ext.each(dd, function(){
	    							   f.push({
	    								   name : 'export-type',
	    								   boxLabel : this.DT_DESC,
	    								   fields : this.DT_FIELDS
	    							   });
	    						   });
	    						   var form = Ext.getCmp('grid').tempalteForm;
	    						   form.items.first().add(f);
	    					   }
	    				   }
	    			   }
	    		   });
	    	   },
	    	   indexOf: function(array, item, from) {
	    		   if (supportsIndexOf) {
	    			   return array.indexOf(item, from);
	    		   }

	    		   var i, length = array.length;

	    		   for (i = (from < 0) ? Math.max(0, length + from) : from || 0; i < length; i++) {
	    			   if (array[i] === item) {
	    				   return i;
	    			   }
	    		   }

	    		   return -1;
	    	   }
	       });