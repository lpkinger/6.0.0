Ext.QuickTips.init();
Ext.define('erp.controller.sys.Feedback', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	currentManfield:null,
	views:[
	       'core.form.Panel','sys.Feedback','core.form.MultiField','core.button.Reply',
	       'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
	       'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ForBidden',
	       'core.button.ResForBidden','core.form.FileField','core.button.TurnProject','core.button.TurnBuglist','core.button.Checktask','core.button.Endfeedback',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger','core.button.Canceltask','core.button.Back','core.button.Confirm',
	       'core.button.ChangeHandler','core.button.ProcessConfirm','core.button.Modify'
	       ],    
	init:function(){
	    	   var me = this;
	    	   this.control({
	    		   /*'erpFormPanel': {
	    			   afterrender:function(form){
	    				   Ext.defer(function(){
	    					   var _k=form.down('#fb_kind').getValue(),
	    					   _p=form.down('#fb_position').getValue(),
	    					   fbid=form.down('#fb_id').getValue(),
	    					   _status=form.down('#fb_statuscode').getValue(),info=null,hasfocused=false,handmanid=null;
	    					   if(_status!='ENTERING' && _status!='REPLYED'){
	    						   info=me.getCurrentFlow(_k,_p);
	    						   if(_p=='End'){
	    							   var datalistId = getUrlParam('datalistId');
	    							   var datalist = parent.Ext.getCmp(datalistId);
	    							   if(datalist){
	    								   var record = datalist.currentRecord;
	    								   if (record.get('fb_position')=='End')handmanid=record.get('em_id');
	    							   }
	    						   }else {
	    							   handmanid=form.down('#'+info['ff_handmanfield']).getValue();
	    							   me.currentManfield=info['ff_handmanfield'];
	    							   Ext.getCmp(info['ff_handmanfield']).on('change',function(){
	    								   var buttons=Ext.ComponentQuery.query('erpChangeHandlerButton');
	    								   if(buttons) buttons[0].setDisabled(false);
	    							   });
	    						   }
	    						   if(handmanid==emid){
	    							   Ext.Array.each(form.items.items,function(item){							    		  
	    								   if(item.groupName==info['ff_caption']){
	    									   console.log(item.fieldLabel);
	    									   console.log(item.value);
	    									   if(item.fieldLabel=='处理时间' || item.fieldLabel=='回复时间' ){
	    										   item.setValue(new Date());
	    										   item.setReadOnly(false);
	    									   }else if(item.fieldLabel=='预计完成时间' && item.value){
	    									   }else if(item.name=='fb_backman'){
	    										   item.setValue(em_name);
	    									   }else {
	    										   if(!hasfocused) {item.focus(false, 200); hasfocused=true;}  item.setReadOnly(false);							    				
	    										   item.setFieldStyle("background:#fffac0;color:#515151;");
	    										   if(me.RelativeFields[_p] && item.name==me.RelativeFields[_p].remarkField) item.setValue(null);
	    									   }

	    								   }
	    							   });
	    						   }else {
	    							   form.down('erpConfirmButton').hide();
	    							   form.down('erpChangeHandlerButton').hide();
	    							   form.down('erpBackButton').hide();
	    						   }
	    					   }
	    					   //插入处理日志
	    					   if(fbid){
	    						   var data= me.loadNewStore(fbid);
	    						   var index=me.getInsertIndex();
	    						   form.insert(index,{
	    							   xtype: 'fieldset',
	    							   title: '<h2><img src="' + basePath + 'resource/images/icon/communicate.png" width=20/>处理明细</h2>',
	    							   collapsible: true,
	    							   //collapsed: true,
	    							   columnWidth:1,
	    							   layout: 'anchor',
	    							   defaults: {
	    								   anchor: '100%',
	    								   labelStyle: 'padding-left:4px;'
	    							   },
	    							   items:[{
	    								   autoScroll: true,
	    								   xtype: 'dataview',
	    								   tpl:Ext.create('Ext.XTemplate',
	    										   '<tpl for=".">',
	    										   '<div class="search-item">',
	    										   '<h3><span>{fl_date}<br /> {fl_man}</span>',
	    										   '<font color="blue">{fl_position:this.formatTitle} &nbsp;&nbsp;</font></h3>',
	    										   '<p>处理方式 :<font color="green">{fl_kind:this.formatKind}</font> {fl_remark}</p>',
	    										   '</div></tpl>',
	    										   {formatKind: function(value){
	    										   if(value=='PLAN') return '回复处理';
												   else if(value=='CHANGEHANDLER') return '变更处理人';
												   else if(value=='REVIEW') return '确认处理';}
	    										   },{
	    											   formatTitle:function(value){
	    												   if(value=='TechnicalService') return '技术支持部处理信息';
	    												   else if(value=='R&D') return '研发部处理信息';
	    												   else if(value=='ProductDept') return '产品部处理信息';
	    												   else if(value=='Test') return '测试部处理信息';
	    												   else if(value=='End') return '回复处理';
	    											   }
	    										   }),
	    										   store: Ext.create('Ext.data.Store', {
	    											   fields:[{name: 'fl_man' },
	    											           {name: 'fl_date'},
	    											           {name: 'fl_position'},
	    											           {name: 'fl_remark'},
	    											           {name: 'fl_kind'}
	    											           ],
	    											           data:data
	    										   })   
	    							   }]
	    						   });
	    					   }

	    				   },200);

	    			   }
	    		   },*/	
	    		   'erpFormPanel': {
	    			   afterload:function(form){
	    				   Ext.defer(function(){
	    					   var _k=form.down('#fb_kind').getValue(),
	    					   _p=form.down('#fb_position').getValue(),
	    					   fbid=form.down('#fb_id').getValue(),
	    					   _status=form.down('#fb_statuscode').getValue(),info=null,hasfocused=false,handmanid=null;
	    					   if(_status!='ENTERING' && _status!='REPLYED'){
	    						   if(!me.info){
	    							   info=me.getCurrentFlow(_k,_p);
		    						   me.info=info;
	    						   }else info=me.info;	    		    						   
	    						   me.otherfields=info['ff_otherfields']?info['ff_otherfields'].split(','):[];
	    						   if(_p=='End'){
	    							   var datalistId = getUrlParam('datalistId');
	    							   var datalist = parent.Ext.getCmp(datalistId);
	    							   if(datalist){
	    							   	   var fbEmid = Ext.getCmp('fb_emid');
	    								   if(fbEmid){
	    								   	   handmanid = fbEmid.value;
	    								   }
	    							   }else handmanid=form.down('#'+info['ff_manidfield']).getValue() || form.down('#fb_emid').getValue();
	    						   }else {
	    							   handmanid=form.down('#'+info['ff_manidfield']).getValue();
	    							   me.currentManfield=info['ff_manidfield'];
	    							   Ext.getCmp(info['ff_manidfield']).on('change',function(){
	    								   var buttons=Ext.ComponentQuery.query('erpChangeHandlerButton');
	    								   if(buttons) buttons[0].setDisabled(false);
	    							   });
	    						   }
	    						   
	    						   
	    						   if(handmanid==emid){
	    							   Ext.Array.each(form.items.items,function(item){
	    								   if(item.groupName==info['ff_caption']){	    							   
	    									   if(item.name==info['ff_dealdatefield']){
	    										   item.setValue(new Date());	  									 
	    									   }else if(item.name=='fb_backman'){
	    										   item.setValue(em_name);
	    									   }else {
	    										   if(!hasfocused) {item.focus(false, 200); hasfocused=true;}  item.setReadOnly(false);							    				
	    										   item.setFieldStyle("background:#fffac0;color:#515151;");
	    										   if(Ext.Array.contains(me.otherfields,item.name)){
	    											   item.setValue(null);
	    										   }
	    									   }

	    								   }
	    							   });
	    						   }else {
	    							   form.down('erpConfirmButton').hide();
	    							   form.down('erpChangeHandlerButton').hide();
	    							   form.down('erpBackButton').hide();
	    							   var toolbar = Ext.getCmp('form_toolbar');
	    							   if(toolbar){
	    							   		toolbar.doLayout();
	    							   		form.autoSetBtnStyle(form);
	    							   }
	    						   }
	    					   }
	    					   //插入处理日志
	    					   if(fbid){
	    						   var data= me.loadNewStore(fbid);
	    						   var index=me.getInsertIndex();
	    						   form.insert(index,{
	    							   xtype: 'fieldset',
	    							   title: '<h2><img src="' + basePath + 'resource/images/icon/communicate.png" width=20/>处理明细</h2>',
	    							   collapsible: true,
	    							   columnWidth:1,
	    							   layout: 'anchor',
	    							   defaults: {
	    								   anchor: '100%',
	    								   labelStyle: 'padding-left:4px;'
	    							   },
	    							   items:[{
	    								   autoScroll: true,
	    								   xtype: 'dataview',
	    								   tpl:Ext.create('Ext.XTemplate',
	    										   '<tpl for=".">',
	    										   '<div class="search-item">',
	    										   '<h3><span>{fl_date}<br /> {fl_man}</span>',
	    										   '<font color="blue">{fl_position:this.formatTitle} &nbsp;&nbsp;</font></h3>',
	    										   '<p>处理方式 :<font color="green">{fl_kind:this.formatKind}</font> {fl_remark}</p>',
	    										   '</div></tpl>',
	    										   {formatKind: function(value){
	    										   if(value=='PLAN') return '回复处理';
												   else if(value=='CHANGEHANDLER') return '变更处理人';
												   else if(value=='REVIEW') return '确认处理';}
	    										   },{
	    											   formatTitle:function(value){
	    												   if(value=='TechnicalService') return '技术支持部处理信息';
	    												   else if(value=='R&DFirst') return '研发初步方案';
	    												   else if(value=='R&D') return '研发确认';
//	    												   else if(value=='ProductDeptFirst') return '产品初步方案';
	    												   else if(value=='ProductDept') return '产品方案确认';
	    												   else if(value=='Test') return '测试结果';
	    												   else if(value=='Standard') return '标准化确认';
	    												   else if(value=='End') return '回复处理';											   
	    												   else return value;
	    											   }
	    										   }),
	    										   store: Ext.create('Ext.data.Store', {
	    											   fields:[{name: 'fl_man' },
	    											           {name: 'fl_date'},
	    											           {name: 'fl_position'},
	    											           {name: 'fl_remark'},
	    											           {name: 'fl_kind'}
	    											           ],
	    											           data:data
	    										   })   
	    							   }]
	    						   });
	    					   }

	    				   },100);

	    			   }
	    		   },
	    		    'erpSaveButton': {
	    			   click: function(btn){		
	    				   var form = me.getForm(btn);
	    				   if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){								
	    					   me.BaseUtil.getRandomNumber();//自动添加编号
	    				   }		
	    				   this.FormUtil.beforeSave(this);
	    			   }
	    		   },
	    		   'erpDeleteButton' : {
	    			   click: function(btn){
	    			   	var form = this.getForm(btn);
	    			   	me.FormUtil.onDelete(Ext.getCmp(form.keyField).value);
	    			   }
	    		   },
	    		   'erpTurnProject' : {
	    			   click: function(btn){
	    				   me.FormUtil.onDelete(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpCanceltaskButton':{
	    			   click: function(btn){
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/canceltask.action',
	    					   params: {
	    						   id: Ext.getCmp('fb_id').value,
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   alert('修改成功！');
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'erpChangeHandlerButton':{
	    			   click:function(btn){
	    				   var newhandmanid=Ext.getCmp(me.currentManfield);
	    				   var oldemid=newhandmanid.originalValue;
	    				   var newemid=newhandmanid.value;
	    				   var data=me.getDirtyValues(),allowsave=false;

	    				   Ext.each(Ext.Object.getKeys(data), function(k){
	    					   if(me.FormUtil.contains(k, 'ext-', true)){
	    						   delete data[k];
	    					   }
	    					   if(!me.FormUtil.contains(k, 'fb_man', true) && !me.FormUtil.contains(k, 'fb_description', true)&& !me.FormUtil.contains(k, 'fb_testdescription', true)){
	    						   delete data[k];
	    					   }
	    				   });

	    				   if(oldemid==newemid){
	    					   showError('未做任何处理人修改!'); return;
	    				   } 
	    				   data.fb_id=Ext.getCmp('fb_id').getValue();
	    				   data.fb_position=Ext.getCmp('fb_position').getValue();
	    				   data.fb_kind=Ext.getCmp('fb_kind').getValue();
	    				   me.FormUtil.setLoading(true);
	    				   Ext.Ajax.request({
	    					   url: basePath + 'sys/feedback/changeHandler.action',
	    					   params:{
	    						   data:unescape(escape(Ext.JSON.encode(data)))
	    					   },
	    					   method: 'post',
	    					   callback: function(options,success,response){
	    						   me.FormUtil.setLoading(false);
	    						   var res=Ext.decode(response.responseText);
	    						   if(res.exceptionInfo){
	    							   showError(localJson.exceptionInfo);return;
	    						   }else if(res.success){
	    							   showMessage('提示','变更成功!',1000);
	    							   window.location.reload();
	    						   }    						
	    						   btn.setDisabled(true);
	    					   }
	    				   });
	    			   },
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('fb_statuscode').getValue(),position=Ext.getCmp('fb_position').getValue();
	    				   if(status!='AUDITED' || position=='End') btn.hide();
	    			   }
	    		   },
	    		   'erpTurnBuglist' : {
	    			   click: function(btn){
	    				   var form= Ext.getCmp('form');					
	    				   Ext.Ajax.request({
	    					   url : basePath + form.turnBuglistUrl,
	    					   params: {
	    						   id: Ext.getCmp('fb_id').value				   			
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{				   				
	    							   window.location.href = basePath + "jsps/plm/test/newchecklist.jsp?formCondition=cl_idIS" + localJson.id+"&gridCondition=cld_clidIS"+localJson.id;
	    						   }
	    					   }

	    				   });
	    			   }
	    		   },
	    		   'erpUpdateButton': {
	    			   click: function(btn){
	    				   this.FormUtil.onUpdate(this);
	    			   }
	    		   },
	    		   'erpAddButton': {
	    			   click: function(){
	    				   me.FormUtil.onAdd('addFeedback', '新增系统问题反馈', 'jsps/sys/Feedback.jsp?caller=Feedback');
	    			   }
	    		   },
	    		   'erpCloseButton': {
	    			   click: function(btn){
	    				   me.FormUtil.beforeClose(me);
	    			   }
	    		   },
	    		   'erpSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'ENTERING'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onSubmit(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpResSubmitButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResSubmit(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpReplyButton': {
	    			   afterrender: function(btn){				
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   var form= Ext.getCmp('form');					
	    				   Ext.Ajax.request({
	    					   url : basePath + form.replyUrl,
	    					   params: {
	    						   id: Ext.getCmp('fb_id').value,
	    						   comment:Ext.getCmp('fb_uasdetail').getValue(),
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   alert('回复成功');
	    							   window.location.href = basePath + "jsps/sys/Feedback.jsp?caller=UASFeedback&&formCondition=fb_idIS" + Ext.getCmp('ts_id').value;
	    						   }
	    					   }

	    				   });					   
	    			   }
	    		   },
	    		   'erpAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'COMMITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onAudit(Ext.getCmp('fb_id').value);
	    			   }
	    		   },
	    		   'erpBackButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('fb_statuscode').getValue(),currentposition=Ext.getCmp('fb_position').getValue(),relativefield=null,form=btn.ownerCt.ownerCt;  
	    				   if(status!='AUDITED' || currentposition=='End' ) btn.hide();
	    				   if(status=='AUDITED' && currentposition !='End'){
		    				   var _k=form.down('#fb_kind').getValue(),
	    					   _p=form.down('#fb_position').getValue();
		    				   me.info=me.getCurrentFlow(_k,_p);
	    					   relativefield=me.info['ff_plandatefield'];
	    					   if(!relativefield || (relativefield && Ext.getCmp(relativefield).value)){
	    						   btn.hide();
	    					   }
	    				   }
	    			   },
	    			   click:function(btn){
	    				   var data=me.getDirtyValues(),allowsave=false;
	    				   if(me.info){
	    					   if(data[me.info['ff_plandatefield']]) allowsave=true;
	    				   }
	    				   if(!allowsave){
	    					   showError('回复需设置预计完成时间!'); return;
	    				   } 
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/backPlan.action',
	    					   params: {
	    						   data:unescape(escape(Ext.JSON.encode(data)))
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   showMessage('提示','回复成功!',1000);
	    							   window.location.reload();
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'erpConfirmButton':{
	    			   afterrender:function(btn){
	    				   var status=Ext.getCmp('fb_statuscode').getValue();
	    				   if(status!='AUDITED') btn.hide();
	    			   },
	    			   click:function(btn){
	    				   var data=me.getDirtyValues();
	    				   Ext.each(Ext.Object.getKeys(data), function(k){
	    					   if(me.FormUtil.contains(k, 'ext-', true)){
	    						   delete r[k];
	    					   }
	    				   });
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/confirm.action',
	    					   params: {
	    						   data:unescape(escape(Ext.JSON.encode(data)))
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   showMessage('提示','确认成功!',1000);
	    							   window.location.reload();
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'erpProcessConfirmButton':{
	    			   click:function(btn){
	    				   var data=me.getDirtyValues(),form=btn.ownerCt.ownerCt,step=null;
	    				   
	    				   var dirtys=me.getEditValues(form);	
	    				   Ext.each(Ext.Object.getKeys(data), function(k){
	    					   if(me.FormUtil.contains(k, 'ext-', true)){
	    						   delete r[k];
	    					   }
	    				   });
	    				   Ext.each(Ext.Object.getKeys(dirtys), function(k){
	    					   var field=Ext.getCmp(k);
	    					   if(field && field.groupName) {
	    						   step=field.groupName;
	    						   return false;
	    					   }
	    				   });
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/processConfirm.action',
	    					   params: {
	    						   data:unescape(escape(Ext.JSON.encode(data))),
	    						   step:step
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   showMessage('提示','确认成功!',1000);
	    							   //window.location.reload();
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'dbfindtrigger[name=fb_module]': {
	    			   afterrender: function(f){
	    				   f.onTriggerClick = function(){
	    					   me.getModuleTree();
	    				   };
	    				   f.autoDbfind = false;
	    			   }
	    		   },
	    		   'treepanel': {
	    			   itemmousedown: function(selModel, record){
	    				   var tree = selModel.ownerCt;
	    				   me.loadTree(tree, record);
	    			   }
	    		   },
	    		   'erpChecktaskButton':{
	    			   click: function(btn){
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/changestatus.action',
	    					   params: {
	    						   id: Ext.getCmp('fb_id').value,
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   alert('修改成功！');
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'erpEndfeedbackButton':{
	    			   click: function(btn){
	    				   Ext.Ajax.request({
	    					   url : basePath + '/sys/feedback/Endfeedback.action',
	    					   params: {
	    						   id: Ext.getCmp('fb_id').value,
	    					   },
	    					   method : 'post',
	    					   callback : function(options,success,response){	
	    						   var localJson = new Ext.decode(response.responseText);
	    						   if(localJson.exceptionInfo){
	    							   var str = localJson.exceptionInfo;
	    							   showError(str);
	    						   }else{
	    							   alert('修改成功！');
	    						   }
	    					   }

	    				   });	
	    			   }
	    		   },
	    		   'erpResAuditButton': {
	    			   afterrender: function(btn){
	    				   var status = Ext.getCmp('fb_statuscode');
	    				   if(status && status.value != 'AUDITED'){
	    					   btn.hide();
	    				   }
	    			   },
	    			   click: function(btn){
	    				   me.FormUtil.onResAudit(Ext.getCmp('fb_id').value);
	    			   }
	    		   }
	    	   });
	       },
	       getForm: function(btn){
	    	   return btn.ownerCt.ownerCt;
	       },
	       getModuleTree: function(){
	    	   var w = Ext.create('Ext.Window',{
	    		   title: '查找模板',
	    		   height: "100%",
	    		   width: "80%",
	    		   maximizable : true,
	    		   buttonAlign : 'center',
	    		   layout : 'anchor',
	    		   items: [{
	    			   anchor: '100% 100%',
	    			   xtype: 'treepanel',
	    			   rootVisible: false,
	    			   useArrows: true,
	    			   store: Ext.create('Ext.data.TreeStore', {
	    				   root : {
	    					   text: 'root',
	    					   id: 'root',
	    					   expanded: true
	    				   }
	    			   })
	    		   }],
	    		   buttons : [{
	    			   text : '关  闭',
	    			   iconCls: 'x-button-icon-close',
	    			   cls: 'x-btn-gray',
	    			   handler : function(btn){
	    				   btn.ownerCt.ownerCt.close();
	    			   }
	    		   },{
	    			   text: '确定',
	    			   iconCls: 'x-button-icon-confirm',
	    			   cls: 'x-btn-gray',
	    			   handler: function(btn){
	    				   var t = btn.ownerCt.ownerCt.down('treepanel');
	    				   if(!Ext.isEmpty(t.title)) {
	    					   Ext.getCmp('fb_module').setValue(t.title);
	    				   }
	    				   btn.ownerCt.ownerCt.close();
	    			   }
	    		   }]
	    	   });
	    	   w.show();
	    	   this.loadTree(w.down('treepanel'), null);
	       },
	       loadTree: function(tree, record){
	    	   var uStore = this.getUstore();
	    	   var pid = 0;
	    	   var kind = Ext.getCmp('fb_remark2').value;
	    	   if(record) {
	    		   if (record.get('leaf')) {
	    			   return;
	    		   } else {
	    			   if(record.isExpanded() && record.childNodes.length > 0){
	    				   record.collapse(true, true);//收拢
	    				   return;
	    			   } else {
	    				   if(record.childNodes.length > 0){
	    					   record.expand(false,true);//展开
							   tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
	    					   return;
	    				   }
	    			   }
	    		   }
	    		   pid = record.get('id');
	    	   }
	    	   tree.setLoading(true);
	    	   
	    	   //加载UAS模块树
	    	   if (kind=='UAS' || kind=='B2B' || kind=='MES') {
		    	   Ext.Ajax.request({
		    		   url : basePath + 'common/lazyTree.action?_noc=1',
		    		   params: {
		    			   parentId: pid,
		    			   condition: 'sn_using=1'
		    		   },
		    		   callback : function(options,success,response){
		    			   tree.setLoading(false);
		    			   var res = new Ext.decode(response.responseText);
		    			   if(record==null){
		    				   //防止子节点加载优软商城
		    				   res.tree.push(uStore);
		    			   }
		    			   if(res.tree){
		    				   if(record) {
		    					   record.appendChild(res.tree);
		    					   record.expand(false,true);//展开
		    					   tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
		    				   } else {
		    					   tree.store.setRootNode({
		    						   text: 'root',
		    						   id: 'root',
		    						   expanded: true,
		    						   children: res.tree
		    					   });
		    				   }
		    			   } else if(res.exceptionInfo){
		    				   showError(res.exceptionInfo);
		    			   }
		    		   }
		    	   });	    	   	
	    	   //加载其他模块树	
	    	   }else if (kind=='APP'){
		    	   Ext.Ajax.request({
		    		   url : basePath + 'sys/feedback/getModules.action',
		    		   params: {
		    			   parentId: pid,
		    			   kind:kind,
		    			   condition: 'sn_using=1'
		    		   },
		    		   callback : function(options,success,response){
		    			   tree.setLoading(false);
		    			   var res = new Ext.decode(response.responseText);
		    			   if(res.tree){
		    				   if(record) {
		    					   record.appendChild(res.tree);
		    					   record.expand(false,true);//展开
		    					   tree.setTitle(record.getPath('text', '/').replace('root', '').replace('//', '/'));
		    				   } else {
		    					   tree.store.setRootNode({
		    						   text: 'root',
		    						   id: 'root',
		    						   expanded: true,
		    						   children: res.tree
		    					   });
		    				   }
		    			   } else if(res.exceptionInfo){
		    				   showError(res.exceptionInfo);
		    			   }
		    		   }
		    	   });	    	   
	    	   }
	       },
	       getCurrentFlow: function(kind,position) {
	    	   var result = false;
	    	   Ext.Ajax.request({
	    		   url : basePath + 'sys/feedback/getCurrentNode.action',
	    		   async: false,
	    		   params: {
	    			   kind:kind,
	    			   position:position
	    		   },
	    		   method : 'post',
	    		   callback : function(opt, s, res){
	    			   var r = new Ext.decode(res.responseText);
	    			   if(r.exceptionInfo){
	    				   showError(r.exceptionInfo);return;
	    			   } else if(r.data){
	    				   result = r.data;
	    			   }
	    		   }
	    	   });
	    	   return result;
	       },
	       getDirtyValues:function(){
	    	   var form=Ext.getCmp('form'),values=this.getEditValues(form);		
	    	   values.fb_id=Ext.getCmp('fb_id').getValue();
	    	   values.fb_position=Ext.getCmp('fb_position').getValue();
	    	   values.fb_kind=Ext.getCmp('fb_kind').getValue();
	    	   return values;
	       },
	       getEditValues:function(form){
	    	   var values=new Object(),formvalues=form.getForm().getValues();
	    	   Ext.Array.each(form.items.items,function(item){
	    		   if(!item.readOnly && item.name && item.value){
	    			   values[item.name]=formvalues[item.name];
	    		   }
	    	   });
	    	   return values;
	       },
	       loadNewStore: function(id){
	    	   var data=null;
	    	   Ext.Ajax.request({//拿到grid的columns
	    		   url : basePath + "common/loadNewGridStore.action",
	    		   params: {
	    			   caller:'Feedback',
	    			   condition:'fl_fbid='+id +' order by fl_id asc'
	    		   },
	    		   async:false,
	    		   method : 'post',
	    		   callback : function(options,success,response){
	    			   var res = new Ext.decode(response.responseText);
	    			   if(res.exceptionInfo){
	    				   showError(res.exceptionInfo);return;
	    			   }
	    			   data = res.data;

	    		   }
	    	   });
	    	   return data;
	       },
	       getInsertIndex:function(){
	    	   var form=Ext.getCmp('form'),i=0;
	    	   Ext.Array.each(form.items.items,function(item,index){
	    		   if(item.name=='fb_position'){
	    			   i=index;
	    		   }
	    	   });
	    	   return i+1;
	       },
	       getUstore:function(){
	    	   var json = {};
	    	   Ext.Ajax.request({
	    		   url:basePath+'resource/uucloud/sysnavigation.json',
	    		   async:false,
	    		   success : function(response){
	    			   var text = response.responseText;
	    			   json = new Ext.decode(text);
	    		   }
	    	   });
	    	   return json;
	       }

});