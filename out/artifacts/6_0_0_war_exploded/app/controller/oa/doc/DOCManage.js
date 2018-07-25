Ext.QuickTips.init();
Ext.define('erp.controller.oa.doc.DOCManage', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:[
	       'oa.doc.DOCManage','oa.doc.DocumentTreePanel',
	       'oa.doc.DocLog',
	       'common.datalist.GridPanel',
	       'common.datalist.Toolbar',
	       'oa.doc.Header','oa.doc.Bottom',
	       'oa.doc.Submit','oa.doc.Save','oa.doc.resSubmit','oa.doc.Close','core.button.Print',
	       'oa.doc.DocView','oa.doc.Update',
	       'core.button.Upload','oa.doc.Delete','oa.doc.resAudit',
	       'oa.doc.Audit','core.form.FileField','oa.doc.OrgTreePanel',
	       'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
	       'oa.doc.DocForm','core.form.YnField',
	       'oa.doc.DocPanel','oa.doc.DocTabPanel' ],
	       init:function(){
	    	   var me = this;
	    	   this.flag=true;
	    	   this.control({
	    		   'erpDocumentTreePanel[id=doctree]': { 
	    			   itemmousedown: function(selModel, record){	    				 
	    				   if(!this.flag){
	    					   return;
	    				   }
	    				   this.flag = false;
	    				   setTimeout(function(){
	    					   me.flag = true;
	    					   me.loadTab(selModel, record,true);
	    				   },20);
	    				   /*//控制顶部按钮
	    				   var docPanel = Ext.getCmp('docpanel'),
	    				   	   bar=docPanel.dockedItems.items[0];
	    				   docPanel.reSetButton(docPanel,true);*/
	    			   },
	    			   itemclick: function(selModel, record){
	    				   if(!this.flag){
	    					   return;
	    				   }
	    				   this.flag = false;
	    				   setTimeout(function(){
	    					   me.flag = true;
	    					   me.loadTab(selModel, record,true);
	    				   },40);
	    			   },
	    			   afterrender:function(tree){	    				   
	    				   tree.selModel.on('select', function(selModel, record){	    					
	    					   if(record.childNodes.length > 0){
	    						   selModel.isOnSelect = true;
	    						   selModel.isOnSelect = false;
	    						   //me.setVirtualpath(record);						
	    					   } else {
	    						   if(!selModel.isOnSelect){
	    							   var arr = selModel.getSelection();
	    							   arr.push(record);
	    							   selModel.isOnSelect = true;
	    							   selModel.select(arr);
	    							   selModel.isOnSelect = false;
	    							   //me.setVirtualpath(record);	
	    						   }
	    						   return;
	    					   }
	    				   });
	    			   }
	    		   },
	    		   'erpDocumentTreePanel[id=foldertree]': { 
	    			   itemmousedown: function(selModel, record){	    				 
	    				   if(!this.flag){
	    					   return;
	    				   }
	    				   this.flag = false;
	    				   setTimeout(function(){
	    					   me.flag = true;
	    					   me.loadTab(selModel, record,false);
	    				   },20);
	    			   },
	    			   itemclick: function(selModel, record){
	    				   if(!this.flag){
	    					   return;
	    				   }
	    				   this.flag = false;
	    				   setTimeout(function(){
	    					   me.flag = true;
	    					   me.loadTab(selModel, record,false);
	    				   },20);
	    			   }
	    		   },
	    		   'docgrid':{
	    			   select: function(selModel, record,item,index){
	    				   var docpanel=Ext.getCmp('docpanel');
	    				   docpanel.reSetButton(docpanel);
	    			   },
	    			   deselect: function(selModel, record,item,index){
	    				   var docpanel=Ext.getCmp('docpanel');
	    				   docpanel.reSetButton(docpanel);
	    			   }

	    		   },
	    		   'textareafield':{
	    			   beforerender:function(field){
	    				   field.labelAlign='left';
	    			   }
	    		   },'button[id=docIndex]':{
	    			   click:function(btn){
		    				me.doExit(btn);
	    			   }	
	    		   },'button[id=docExit]':{
	    			   click:function(btn){
		    				me.doExit(btn);
	    			   }	
	    		   },
	    		   'button[id=search]':{
	    			   click:function(btn){
	    				  console.log("click search");
	    			   }
	    		   },
	    		   'button[id=treeadd]':{
	    			   click:function(btn){
	    				   var hasPower = me.checkPower('dp_save');
	    				   if(hasPower == 1){
							  me.showAddWin(btn);
						   }else if(hasPower == 2){
							  showResult('提示','您不具有该文件夹的创建权限',me);
						   	  return;
						   }
	    			   }	  
	    		   },
	    		   'button[id=treeupdate]':{
	    			   click:function(btn){
	    				   var hasPower = me.checkPower('dp_save');
	    				   if(hasPower == 1){
	    					   me.showUpdateWin(btn);   
	    				   }else if(hasPower == 2){
	    					   showResult('提示','您不具有该文件夹的编辑权限',me);
    					   	   return;
	    				   }
	    				   
	    			   }
	    		   },
	    		   'button[id=treedelete]':{
	    			   click:function(btn){
	    				   me.deleteDir(btn);
	    			   }
	    		   },
	    		   'button[id=uploadDoc]':{
	    			   click:function(btn){
	    				   //校验权限
	    				   var hasPower = me.checkPower('dp_save');
	    				   if(hasPower == 1){
	    					   me.showUploadDoc(btn);
						   }else if(hasPower == 2){
							  showResult('提示','您不具有该文件夹的上传权限',me);
						   	  return;
						   }
	    			   }
	    		   },
	    		   'button[id=read]' : {
						click : function(btn) {
						var select = Ext.getCmp('docgrid').getSelectionModel().getSelection()[0];
						var path = unescape(select.data.dl_filepath);
						var type = path.substring(path.lastIndexOf('.') + 1);
						var folderId = select.data.dl_parentid;
						var folderId = select.data.dl_parentid;
						if (type == 'doc'|| type =='docx'|| type == 'xls'|| type == 'xlsx') {
							Ext.Ajax.request({
								url : basePath + 'oa/doc/getHtml.action',
								params: {
									folderId:folderId,
									path:path,
									type:type
								},
								method : 'post',
								async:false,
								callback : function(opt, s, res){
								var r = new Ext.decode(res.responseText);
								if(r.exceptionInfo){
									showError(r.exceptionInfo);
								} else if(r.success){
									path=r.newPath;
									var url=basePath+ 'jsps/oa/doc/readWordOrExcel.jsp?path='+basePath+path;
									window.open(url);
								} 
							}
							});	
						} else if (type == 'pdf') {
							var url = basePath+ 'jsps/oa/doc/read.jsp?path='+ path + '&folderId='+ folderId;
							window.open(url);
						} else {
							showResult('提示','当前文件类型不支持在线预览，请先下载!',btn);
						}
					}
					},
	    		   'button[id=downloadDoc]':{
/*	    			   click:function(btn){
	    				   var select =Ext.getCmp('docgrid').getSelectionModel().getSelection()[0];
	    				   var me = this;
	    				   if (!Ext.fly('ext-attach-download')) {  
	    					   var frm = document.createElement('form');  
	    					   frm.id = 'ext-attach-download';  
	    					   frm.name = id;  
	    					   frm.className = 'x-hidden';
	    					   document.body.appendChild(frm);  
	    				   }
	    				   Ext.Ajax.request({
	    					   url: basePath + 'doc/download.action?escape=1',
	    					   method: 'post',
	    					   form: Ext.fly('ext-attach-download'),
	    					   isUpload: true,
	    					   params: {
	    						   path : unescape(select.data.dl_filepath),
	    						   fileName:unescape(select.data.dl_name),
	    						   folderId:select.data.dl_parentid
	    					   },
	    					   callback : function(options, success, response){
	    							if (!response) return;
	    							var restext=response.responseText;	
	    							try{
	    								var res =new Ext.decode(restext);
	    								if(res.error) showResult('提示',res.error,btn);
	    							}catch (e){
	    								showResult('提示','您没有<下载>该文档的权限!',btn);
	    							}
	    					   }
	    				   });

	    			   }*/
	    			   click:function(btn){
	    				   var select =Ext.getCmp('docgrid').getSelectionModel().getSelection();   				   
	    				   var me = this;
	    				   if (!Ext.fly('ext-attach-download')) {  
	    					   var frm = document.createElement('form');  
	    					   frm.id = 'ext-attach-download';  
	    					   frm.name = id;  
	    					   frm.className = 'x-hidden';
	    					   document.body.appendChild(frm);  
	    				   }
	    				   if(select.length==1){
	    				       select = select[0];
		    				   Ext.Ajax.request({
		    					   url: basePath + 'doc/download.action?escape=1',
		    					   method: 'post',
		    					   form: Ext.fly('ext-attach-download'),
		    					   isUpload: true,
		    					   params: {
		    						   path : unescape(select.data.dl_filepath),
		    						   fileName:unescape(select.data.dl_name),
		    						   folderId:select.data.dl_parentid
		    					   },
		    					   callback : function(options, success, response){
		    							if (!response) return;
		    							var restext=response.responseText;	
		    							try{
		    								var res =new Ext.decode(restext);
		    								if(res.error) showResult('提示',res.error,btn);
		    							}catch (e){
		    								showResult('提示','您没有<下载>该文档的权限!',btn);
		    							}
		    					   }
		    				   });	    				   	
	    				   }else{
	    				   	   var ids = '';
	    				       Ext.Array.each(select,function(item){
	    				           ids += item.data.dl_fpid;
	    				       });
	    				       if(ids!=''){
	    				           ids = ids.substring(0,ids.length-1);
	    				           Ext.Ajax.request({
			    					   url: basePath + 'doc/downloadbyIds.action',
			    					   method: 'post',
			    					   form: Ext.fly('ext-attach-download'),
			    					   isUpload:true,
			    					   params: {
			    						   ids:ids,
			    						   folderId:select[0].data.dl_parentid
			    					   },
			    					   callback : function(options, success, response){
			    							if (!response) return;
			    							var restext=response.responseText;	
		    								var res =new Ext.decode(restext);
		    								if(res.error) showResult('提示',res.error,btn);		    							
			    					   }
			    				   });
	    				       }
	    				   	
	    				   }


	    			   }
	    		   },
	    		   'field[name=dl_virtualpath]':{
	    			   afterrender:function(field){
	    				   if(field.value==null){
	    					   //field.setValue(Ext.getCmp('virtualpath').getText());
	    				   }
	    			   }  
	    		   },
	    		   'field[name=dl_parentid]':{
	    			   afterrender:function(field){
	    				   if(field.value==null || field.value==""){	    				
	    					   field.setValue(CurrentFolderId);
	    				   }
	    			   }
	    		   },
	    		   'field[name=dlc_parentid]':{
	    			   afterrender:function(field){
	    				   if(field.value==null || field.value==""){	    				
	    					   field.setValue(CurrentFolderId);
	    				   }
	    			   }
	    		   },
	    		   'field[name=dlc_olddlid]':{
	    			   afterrender:function(field){
	    				   if(field.value==null || field.value==""){
	    					   field.setValue(Ext.getCmp('doctab').currentDoc.dl_id);
	    				   }
	    			   }
	    		   },
	    		   'field[name=dlc_oldversion]':{
	    			   afterrender:function(field){
	    				   if(field.value==null || field.value==""){	    				
	    					   field.setValue(Ext.getCmp('doctab').currentDoc.dl_version);
	    				   }
	    			   }
	    		   },
	    		   'field[name=dl_size]':{
	    			   afterrender:function(field){
	    				   if(field.value){	    				
	    					  var value = field.value;
	    				   }
	    			   }
	    		   },
	    		   'mfilefield[name=dlc_oldfpid]':{
	    			   beforerender:function(field){
	    				   if(field.value==null || field.value==""){	
	    					   field.value = Ext.getCmp('doctab').currentDoc.dl_fpid;
	    					   //field.setValue(Ext.getCmp('doctab').currentDoc.dl_fpid);
	    				   }
	    			   }
	    		   },
	    		   'button[id=switch]':{
	    			   click:function(btn){
	    				   if(btn.scanType=='list'){
	    					   var panel=Ext.getCmp('docpanel');
	    					   var grid=panel.items.items[0];
	    					   grid.hide();                    	
	    					   panel.add({
	    						   xtype:'docview',
	    						   gridData:grid.store.data	    						
	    					   });  
	    					   btn.scanType='figure';  
	    				   }else{
	    					   var view=Ext.getCmp('docview');
	    					   if(view){
	    						   view.hide();
	    					   }
	    					   var grid=Ext.getCmp('docgrid');
	    					   grid.show();
	    					   btn.scanType='list';  
	    				   }     
	    			   }
	    		   },
	    		   'button[id=updatedoc]':{
	    			   click:function(btn){
	    				   me.showUpdateDoc(btn);
	    			   }  
	    		   },
	    		   'button[id=editdoc]':{
	    			   click:function(btn){
	    				   me.showUpdateDoc(btn);
	    			   }  
	    		   },
	    		   'button[id=rename]':{
	    			   click:function(button){
	    				   var win = Ext.getCmp('rename_win');
	    				   if(!win){
	    					   var select =Ext.getCmp('docgrid').getSelectionModel().getSelection()[0];
	    					   var value = select.data.dl_name.substring(0,select.data.dl_name.lastIndexOf('.'));
	    					   win=Ext.create('Ext.window.Window',{
	    						   width: 350,
	    						   height:130,
	    						   closeAction: 'hide',
	    						   id:'rename_win',
	    						   title:'<div align="center" class="WindowTitle">重命名</div>', 
	    						   listeners:{
	    							   hide:function(win){
	    								   win.destroy();
	    								   Ext.getBody().unmask();
	    							   },show:function(){
	    				    			   Ext.getBody().mask();
	    		           		 	   }  
	    						   },
	    						   bodyStyle:'background:#F0F0F0;color:#515151;',
	    						   items:[{
	    							   xtype:'textfield',
	    							   fieldLabel:'名称',
	    							   name:'dl_name',
	    							   value:value,
	    							   allowBlank:false,
	    							   blankText:'文件名不能为空', 
	    							   labelWidth:'50',
	    							   labelAlign:'left',
	    							   msgTarget: 'side', 
	    							   cls:'form-field-allowBlank',
	    							   style:"margin-top:15px;margin-left:15px",
	    							   fieldStyle : "background:#FFFAFA;color:#515151;width: 240px;",
	    							   id:'dl_name'
	    						   }],
	    						   buttons:[{
	    							   cls:'x-btn-save',
	    							   xtype:'button',
	    							   text:'保存',
	    							  // style:'height: 20px; line-height: 20px;',
	    							   handler:function(btn){
	    								   var value=Ext.getCmp('dl_name').value;
	    								   if(value.trim() == ''){
	    									   Ext.Msg.alert("提示", "新文件名不能全部为空格");
	    									   return;
	    								   }
	    								   var re = /[\/:*"?<>| ]/gi;  
	    								   if(re.test(value)){
	    									   Ext.Msg.alert("提示", "新文件名不能包含特殊字符");
	    									   return;
	    								   }
	    								   if(value){
	    									   var params=new Object(),o={
	    										   dl_name:value+"."+select.data.dl_style,	    											
	    										   dl_id:select.data.dl_id,
	    										   dl_parentid:select.data.dl_parentid
	    									   };
	    									   params.formStore=unescape(Ext.JSON.encode(o).replace(/\\/g,"%"));
	    									   params.caller="DocRename";
	    									   me.UpdateByType(params);
	    									   showResult('提示','更新成功!',btn);
	    									   var docpanel=Ext.getCmp('docpanel');
	    									   docpanel.loadNewStore(CurrentFolderId,docpanel.currentItem);
	    							    	   docpanel.reSetButton(docpanel);
	    									   win.close();
	    								   }
	    							   }
	    						   }/*,{
	    						   	 	//text: $I18N.common.button.erpCloseButton,
										cls: 'x-btn-gray x-btn-close',
										text:'关闭',
										id:'close',
										//style:'height: 20px; line-height: 20px;',
										handler: function(btn) {
											btn.ownerCt.ownerCt.close();
										}
												
	    						   }*/],
	    						   buttonAlign:'center'	      
	    					   });
	    				   }
	    				   var el=button.getEl();
	    				   button.getEl().dom.disabled = true;
	    				   if (win.isVisible()) {
	    					   win.hide(el, function() {
    						   el.dom.disabled = false;
	    					   });
	    				   } else {
	    					   win.show(el, function() {
    						   el.dom.disabled = false;
    						   Ext.getBody().disabled=true;
	    					   });
	    				   }
	    			   }  

	    		   },
	    		   'button[id=delete]':{
	    			   click:function(btn){
	    				   me.deleteDoc(btn);
	    			   }
	    		   },
	    		   'button[id=lockdoc]':{
	    			   click:function(button){
	    				   var docpanel=Ext.getCmp('docpanel');
	    				   var select =docpanel.currentItem || Ext.getCmp('docgrid').getSelectionModel().getSelection()[0];
	    				   var value = select.data.dl_locked,msg='';
	    				   if(button.text=='锁定'){
	    					   msg = '确认锁定该文档?';
	    				   }else{
	    					   msg = '确认解锁该文档?';
	    				   }
	    				   warnMsg(msg, function(btn){
	    					   var changevalue=button.text=='锁定'?-1:0;
	    					   if(btn == 'yes'){
	    						   var params=new Object(),o={
	    							   dl_locked:changevalue,	    											
	    							   dl_id:select.data.dl_id,
                                       dl_parentid:select.data.dl_parentid
	    						   };
	    						   params.formStore=unescape(Ext.JSON.encode(o).replace(/\\/g,"%"));
	    						   params.caller="DocLocked";
	    						   params.type="LOCK";
	    						   me.UpdateByType(params);
	    						   //修改锁定状态的值： 
	    						   var lockstatus = Ext.getCmp('lockstatus');
	    						   lockstatus.setValue(changevalue);
	    						   showResult('提示','操作成功!',button);	
	    						   docpanel.loadNewStore(CurrentFolderId,docpanel.currentItem);
	    					   }
	    				   });
	    				   
	    			   }
	    		   
	    		   },
	    		   'button[id=lockbutton]':{
	    			   click:function(button){
	    				   var docpanel=Ext.getCmp('docpanel');
	    				   var select =docpanel.currentItem || Ext.getCmp('docgrid').getSelectionModel().getSelection()[0];
	    				   warnMsg('确认锁定该文档?', function(btn){
	    					   var changevalue=button.text=='锁定'?-1:0;
	    					   if(btn == 'yes'){
	    						   var params=new Object(),o={
	    							   dl_locked:changevalue,	    											
	    							   dl_id:select.data.dl_id,
                                       dl_parentid:select.data.dl_parentid
	    						   };
	    						   params.formStore=unescape(Ext.JSON.encode(o).replace(/\\/g,"%"));
	    						   params.caller="DocLocked";
	    						   params.type="LOCK";
	    						   me.UpdateByType(params);
	    						   showResult('提示','操作成功!',button);								   
	    						   docpanel.loadNewStore(CurrentFolderId,docpanel.currentItem);

	    					   }
	    				   });
	    			   }
	    		   },
	    		   'erpSaveButton':{
	    			   click:function(btn){
	    				   me.SaveDir(btn);
	    			   }
	    		   },
	    		   'erpUpdateButton':{
	    			   click:function(btn){
				   		    var form=btn.ownerCt.ownerCt;
			    		    form.update(btn,form.caller);
			    		    var docpanel=Ext.getCmp('docpanel');
			    		    docpanel.loadNewStore(CurrentFolderId);
    		    	   }
	    		   },
	    		   'erpDeleteButton':{
	    			   afterrender:function(btn){
	    				   btn.hide();
	    			   }
	    		   },
	    		   'erpSubmitButton':{
	    			   afterrender:function(btn){
	    				  btn.hide();
	    			   }
	    		   },
	    		   'erpResSubmitButton':{
	    			   afterrender:function(btn){
	    				  btn.hide();
	    			   }
	    		   },
	    		   'erpAuditButton':{
	    			   afterrender:function(btn){
	    				    btn.hide();
	    			   }

	    		   },
	    		   'erpResAuditButton':{
	    			   afterrender:function(btn){
	    				   btn.hide();
	    			   }
	    		   },
	    		   'button[id=setpower]':{
	    			   click:function(button){
	    				   var win = Ext.getCmp('powerwindow');
	    				   if(!win){
	    					   win= Ext.create('erp.view.oa.doc.PowerWindow');
	    				   }
	    				   var el=button.getEl();
	    				   button.getEl().dom.disabled = true;
	    				   if (win.isVisible()) {
	    					   win.hide(el, function() {
	    						   el.dom.disabled = false;
	    						   Ext.getBody().unmask();
	    					   });
	    				   } else {
	    					   win.show(el, function() {
	    						   el.dom.disabled = false;
	    						   Ext.getBody().disabled=true;
	    						   Ext.getBody().mask();
	    					   });
	    				   }
	    				   win.addListener('close',function(){
	    					   el.dom.disabled = false;
	    					   Ext.getBody().unmask();
	    				   });
	    			   }
	    		   },
	    		   'button[id=setdirpower]':{
	    			   click:function(button){
	    				   var me = this;
	    				   var selected = Ext.getCmp('doctree').getSelectionModel().selected;
	    				   if(selected.items < 1){
	    					   showResult('提示','请选择文件夹',me);
	    					   return;
	    				   }
	    				   var folderId = selected.items[0].data.id
	    				   Ext.Ajax.request({
	    					  url: basePath + 'oa/doc/checkPower.action',
	    					  params: {
	    						  folderId: folderId,
	    						  type:		"dp_control",
	    					  },
	    					  success: function(response){
	    						  var res = Ext.decode(response.responseText);
	    						  if(res){
	    							  var win = Ext.getCmp('powerwindow');
	    		    				   if(!win){
	    		    					   win= Ext.create('erp.view.oa.doc.PowerWindow');
	    		    				   }
	    		    				   var el=button.getEl();
	    		    				   el.dom.disabled = true;
	    		    				   if (win.isVisible()) {
	    		    					   win.hide(el, function() {
	    		    						   el.dom.disabled = false;
	    		    						   Ext.getBody().unmask();
	    		    					   });
	    		    				   } else {
	    		    					   win.show(el, function() {
	    		    						   el.dom.disabled = false;
	    		    						   Ext.getBody().disabled=true;
	    		    						   Ext.getBody().mask();
	    		    					   });
	    		    				   }
	    		    				   win.addListener('close',function(){
	    		    					   el.dom.disabled = false;
	    		    					   Ext.getBody().unmask(); 
	    		    				   });
	    						  }else{
	    							  showResult('提示','您不具有该文件夹的管理权限',me);
	   	    					   	  return;
	    						  }
	    					  }
	    				   });
	    				   
	    			   }
	    		   }
	    		   //CurrentFolderId
	    	   });
	       },doExit:function(btn){
	    	   warnMsg('确认退出?', function(btn){
				   if(btn == 'yes'){
					   var p = parent.Ext.getCmp('content-panel');
						if(p){
							p.getActiveTab().close();
						} else {
							window.close();
						}
				   }
			   });
	       },
	       deleteDir:function(btn){
	    	   var tree = Ext.getCmp('doctree');
	    	   var record=tree.getSelectionModel().lastFocused;
	    	   if(CurrentFolderId==5||CurrentFolderId==0||CurrentFolderId==1){
	    			 showResult('提示','默认目录不允许删除!',btn);
	    		 }else{
	    			 if(tree.selModel.getSelection().length<0) showResult('提示','请选择需要删除的目录!',btn);
		    	     warnMsg('确认删除该文档?', function(btn){
		    	    	 if(btn == 'yes'){
	    	    			 Ext.Ajax.request({
				    		   url : basePath + 'oa/documentlist/delete.action?caller=DocDeleteDir&_noc=1',
				    		   params : {
				    			   id:CurrentFolderId
				    		   },
				    		   method : 'post',
				    		   async: false,
				    		   callback : function(options,success,response){
				    			   var localJson = new Ext.decode(response.responseText);
				    			   if(localJson.exceptionInfo){
				    				   var str = localJson.exceptionInfo;
				    				   showError(str);
				    			   }else {
				    				   var win = parent.Ext.ComponentQuery.query('window');
				    				   if(win){
				    					   Ext.each(win, function(){
				    						   this.close();
				    					   });
				    				   } else {
				    					   window.close();
				    				   }
				    				   showResult('提示','删除成功!',btn);
				    				   record.remove();
				    				   //tree.refreshNodeByParentId(record.data.parentId,tree);
				    			   }
				    		   }
				    	   });
	    	    		 }
	    	    	 });
	    	    }
	       },
	       deleteDoc:function(btn){
	    	   var tree = Ext.getCmp('doctree');
	    	   var items = Ext.getCmp('docgrid').selModel.getSelection();
	    	   if(items.length<0) showResult('提示','请选择需要删除的文件!',btn);
	    	   warnMsg('确认删除该文档?', function(btn){
	    		   if(btn == 'yes'){
	    			   var data=new Array(),o;
	    			   Ext.Array.each(items,function(item){
	    				   o=new Object();
	    				   o.dl_id=item.data.dl_id;
	    				   o.dl_parentid=item.data.dl_parentid;
	    				   data.push(o);
	    			   });
	    			   Ext.Ajax.request({
	    				   url : basePath + 'oa/documentlist/deleteDoc.action?_noc=1',
	    				   params : {
	    					   data :unescape(Ext.JSON.encode(data).replace(/\\/g,"%"))
	    				   },
	    				   method : 'post',	    	
	    				   callback : function(options,success,response){
	    					   var localJson = new Ext.decode(response.responseText);
	    					   if(localJson.exceptionInfo){
	    						   var str = localJson.exceptionInfo;
	    						   showError(str);
	    					   }else {
	    						   var docpanel=Ext.getCmp('docpanel');
	    						   docpanel.loadNewStore(CurrentFolderId);
	    						   showResult('提示','删除成功!',btn);
	    						   //将按钮置为灰
	    						   var docpanel=Ext.getCmp('docpanel');
	    				    	   var barArray = docpanel.dockedItems.items[0].items.items;
	    				    	   Ext.Array.each(barArray,function(item){
	    				    		   Ext.Array.each(item.items.items, function(el){
	    				    			   if(el.xtype == 'button' && el.text != '上传')
	    				    				   el.setDisabled(true);
	    				    		   });
	    				    	   });
	    				    	   //改变全选按钮状态
	    				    	   var docGrid = Ext.getCmp('docgrid');
	    				    	   docGrid.selModel.deselectAll(true);
	    					   }
	    				   }
	    			   });
	    		   }
	    	   }); 

	       },
	       showAddWin:function(button){
	    	   var win = Ext.getCmp('add_win');
	    	   if(!win){
	    		   win=Ext.create('Ext.window.Window',{
	    			   width: 550,
	    			   height:350,
	    			   closeAction: 'hide',
	    			   id:'add_win',
	    			   layout:'fit',
	    			   title:'<div align="center" class="WindowTitle">创建目录</div>', 
	    			   listeners:{
	    				   hide:function(win){
	    					   Ext.getBody().unmask();
	    					   win.destroy();
	    				   },show:function(){
			    			   Ext.getBody().mask();
	           		 	   }  
	    			   },
	    			   items:[{
	    				   xtype: 'erpDocFormPanel', 	       	      
	    				   bodyPadding: 13,
	    				   autoScroll:true,
	    				   fixedlayout:true,
	    				   caller:'DocCreateDir',
	    				   enableTools:false,
	    				   frame:true,
	    				   bodyStyle: 'border: none;',
	    				   fieldDefaults: {
	    					   margin: '6 0 0 0',
	    					   labelWidth: 70
	    				   },
	    				   fieldDefaults: {
	    					   labelAlign: 'right',
	    					   labelWidth: 115,
	    					   msgTarget: 'side'
	    				   }
	    			   }],
	    			   buttonAlign:'center'	      
	    		   });
	    	   }
	    	   var el=button.getEl();
	    	   button.getEl().dom.disabled = true;
	    	   if (win.isVisible()) {
	    		   win.hide(el, function() {
	    			   el.dom.disabled = false;
	    		   });
	    	   } else {
	    		   win.show(el, function() {
	    			   el.dom.disabled = false;
	    			   Ext.getBody().disabled=true;
	    		   });
	    	   }
	       },
	       showUpdateWin:function(button){
	    	   if(CurrentFolderId==5||CurrentFolderId==0||CurrentFolderId==1){
	    			 showResult('提示','默认目录不允许编辑!',button);
	    	   }else{
	    		   var win = Ext.getCmp('update_win');
		    	   if(!win){
		    		   win=Ext.create('Ext.window.Window',{
		    			   width: 550,
		    			   height:350,
		    			   closeAction: 'hide',
		    			   id:'update_win',
		    			   layout:'fit',
		    			   listeners:{
		    				   hide: function(win){
		    					   Ext.getBody().unmask();
		    					   win.destroy();
		    				   },
		    				   show: function(){
				    			   Ext.getBody().mask();
		           		 	   }   
		    			   },
		    			   items:[{ xtype: 'erpDocFormPanel', 	       	      
		    				   bodyPadding: 13,
		    				   autoScroll:true,
		    				   fixedlayout:true,
		    				   caller:'DocCreateDir',
		    				   enableTools:false,
		    				   frame:true,
		    				   formCondition:"dl_id="+CurrentFolderId,
		    				   bodyStyle: 'border: none;',
		    				   fieldDefaults: {
		    					   margin: '6 0 0 0',
		    					   labelWidth: 70
		    				   },
		    				   fieldDefaults: {
		    					   labelAlign: 'right',
		    					   labelWidth: 115,
		    					   msgTarget: 'side'
		    				   }}],
		    				   buttonAlign:'center'	      
		    		   });
		    	   }
		    	   var el=button.getEl();
		    	   button.getEl().dom.disabled = true;
		    	   if (win.isVisible()) {
		    		   win.hide(el, function() {
		    			   el.dom.disabled = false;
		    		   });
		    	   } else {
		    		   win.show(el, function() {
		    			   el.dom.disabled = false;
		    			   Ext.getBody().disabled=true;
		    		   });
		    	   }
	    	   }
	    	   
	       },
	       showUploadDoc:function(button){
	    	   var win = Ext.getCmp('uploaddoc_win');
	    	   if(!win){
	    		   win=Ext.create('Ext.window.Window',{
	    			   width: 550,
	    			   height:350,
	    			   closeAction: 'hide',
	    			   id:'uploaddoc_win',
	    			   layout:'fit',
	    			   title:'<div align="center" class="WindowTitle">上传文件</div>', 
	    			   items:[{
	    				   xtype: 'erpDocFormPanel', 	       	      
	    				   bodyPadding: 13,
	    				   autoScroll:true,
	    				   fixedlayout:true,
	    				   caller:'UploadDoc',
	    				   enableTools:false,
	    				   frame:true,
	    				   bodyStyle: 'border: none;',
	    				   fieldDefaults: {
	    					   margin: '6 0 0 0',
	    					   labelWidth: 70
	    				   },
	    				   fieldDefaults: {
	    					   labelAlign: 'right',
	    					   labelWidth: 115,
	    					   msgTarget: 'side'
	    				   }
	    			   }],
	    			   buttonAlign:'center',
	    			   listeners:{
	    				   hide:function(win){
	    					   win.destroy();
	    					   Ext.getBody().unmask();//去除MASK
	    				   },show:function(){
			    			   Ext.getBody().mask();
	           		 	   }
	    			   }
	    		   }).show();
	    		   
	    	   }
	    	   var el=button.getEl();
	    	   button.getEl().dom.disabled = true;
	    	   if (win.isVisible()) {
    			   el.dom.disabled = false;
    			   Ext.getBody().disabled=true;
	    	   } else {
    			   el.dom.disabled = false;
	    	   }
	       },
	       showUpdateDoc:function(button){
	    	   var win = Ext.getCmp('updatedoc_win');
	    	   var docpanel=Ext.getCmp('doctab');
	    	   if(!win){
	    		   win=Ext.create('Ext.window.Window',{
	    			   width: 550,
	    			   height:350,
	    			   closeAction: 'hide',
	    			   id:'updatedoc_win',
	    			   layout:'fit',
	    			   title:'<div align="center" class="WindowTitle">修改文件</div>', 
	    			   listeners:{
	    				   hide:function(win){
	    					   win.destroy();
	    					   Ext.getBody().unmask();//去除MASK
	    				   },show:function(){
			    			   Ext.getBody().mask();
	           		 	   }
	    			   },
	    			   items:[{
	    				   xtype: 'erpDocFormPanel', 	       	      
	    				   bodyPadding: 13,
	    				   autoScroll:true,
	    				   fixedlayout:true,
	    				   caller:'DocumentListChange',
	    				   enableTools:false,
	    				   frame:true,
	    				   saveUrl:'oa/DocChange/save.action?_noc=1&caller=DocumentListChange',		/* 增加_noc=1,绕过修改版本时  拦截器的拦截 */
	    				   bodyStyle: 'border: none;',
	    				   fieldDefaults: {
	    					   margin: '6 0 0 0',
	    					   labelWidth: 70
	    				   },
	    				   fieldDefaults: {
	    					   labelAlign: 'right',
	    					   labelWidth: 115,
	    					   msgTarget: 'side'
	    				   }
	    			   }],
	    			   buttonAlign:'center'	      
	    		   }).show();
	    	   }
	    	   var el=button.getEl();
	    	   button.getEl().dom.disabled = true;
	    	   if (win.isVisible()) {
	    			   el.dom.disabled = false;
	    	   } else {
	    			   el.dom.disabled = false;
	    	   }
	       },
	       SaveDir: function(btn){
	    	   var tree= Ext.getCmp('doctree');
	    	   var closebool=true;
	    	   var form=btn.ownerCt.ownerCt;
	    	   if(form.caller=="DocumentListChange"){
	    		   if(!form.down('#dlc_newattach').items.items[0].value){
	    			   showResult('提示','请选择需要更新的文件!',btn);
	    			   closebool=false;
	    			   return;
	    		   }
	    	   }
	    	   if(closebool){
	    		   var form=Ext.getCmp('form');
	    		   form.save(btn,form.caller);
	    		   var docpanel=Ext.getCmp('docpanel');
	    		   docpanel.loadNewStore(CurrentFolderId);
	    	   }
	       },
	       UpdateByType:function(params){
	    	   Ext.Ajax.request({//拿到tree数据
	    		   url : basePath + 'oa/documentlist/DocUpdateByType.action',
	    		   params:params,
	    		   async:false,
	    		   callback : function(options,success,response){
	    		   	
	    		   }
	    	   });

	       },
	       setVirtualpath:function(record){
	    	   var data=record.raw!=undefined ? record.raw:record.data;
	    	   Ext.getCmp('virtualpath').setText(data.url);  
	       },
	       loadTab: function(selModel, record,bool){
	    	   var me = this;
	    	   if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
	    		   record.collapse(true,true);//收拢
	    		   me.flag = true;
	    	   } else {//未展开
	    		   //看是否加载了其children
	    		   if(record.childNodes.length == 0){
	    			   //从后台加载
	    			   var tree = selModel.ownerCt.ownerCt;
	    			   var condition = tree.baseCondition;
	    			   tree.setLoading(true, tree.body);
	    			   Ext.Ajax.request({//拿到tree数据
	    				   url : basePath + 'oa/documetlist/loadDir.action',
	    				   params: {
	    					   parentId: record.data['id'],
	    					   condition: condition
	    				   },
	    				   callback : function(options,success,response){
	    					   tree.setLoading(false);
	    					   var res = new Ext.decode(response.responseText);
	    					   if(res.tree){
	    						   if(!record.get('level')) {
	    							   record.set('level', 0);
	    						   }
	    						   Ext.each(res.tree, function(n){
	    							   if(n.showMode == 2){//openBlank
	    								   n.text = "<a href='" + basePath + me.parseUrl(n.url) + "' target='_blank'>" + n.text + "</a>";
	    							   }
	    							   if(!n.leaf) {
	    								   n.level = record.get('level') + 1;
	    								   n.iconCls = 'x-tree-icon-level-' + n.level;
	    							   }
	    						   });
	    						   record.appendChild(res.tree);
	    						   record.expand(false,true);//展开
	    						   if(res.tree.length==0){
	    							   if(record.get("expanded")){
	    								   record.collapse(true);//收拢
	    							   }
	    						   }
	    						   me.flag = true;
	    					   } else if(res.exceptionInfo){
	    						   showError(res.exceptionInfo);
	    						   me.flag = true;
	    					   }
	    				   }
	    			   });
	    			   //加载grid的数据

	    		   } else {
	    			   record.expand(false,true);//展开
	    			   me.flag = true;
	    		   }

	    	   }
	    	   if(bool){
		    	   if( CurrentFolderId !=record.data['id']){
		    		   var docpanel=Ext.getCmp('docpanel');
		    		   docpanel.loadNewStore(record.data['id']);
		    		   CurrentFolderId=record.data['id'];
			    	   Ext.getCmp('uploadDoc').setDisabled(CurrentFolderId<10);
			    	   me.changeTabs(record);
		    	   }
		    	   
	    	   }

	       }, 
	       onGridItemClick: function(selModel, record){//grid行选择
	    	   var me = this;

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
	       changeTabs:function (record){
	    	   var doctabs=Ext.getCmp('doctab');
	    	   //doctabs.fireEvent('tabItemChange',doctabs,record);
	    	   //main.setActiveTab(Ext.getCmp("HomePage"));
	    	   if(Ext.getCmp('4').disabled){
	    		   doctabs.setActiveTab('0'); 
	    	   }else if(Ext.getCmp('0'))doctabs.setActiveTab('4'); 
	    	   var docpanel=Ext.getCmp('docpanel');
	    	   docpanel.currentItem=null; 
	    	   docpanel.reSetButton(docpanel,true);
	    	   var docGrid = Ext.getCmp('docgrid');
	    	   docGrid.selModel.deselectAll(true);
	       },
	       checkPower: function(type){
	    	   var me = this, flag = 0;
			   var selected = Ext.getCmp('doctree').getSelectionModel().selected;
			   if(selected.items < 1){
				   showResult('提示','请选择文件夹',me);
				   return -1;
			   }
			   var folderId = selected.items[0].data.id;
			   Ext.Ajax.request({
					  url: basePath + 'oa/doc/checkPower.action',
					  params: {
						  folderId: folderId,
						  type:     type
					  },
					  async: false,
					  success: function(response){
						  var res = Ext.decode(response.responseText);
						  if(res){
							  flag = 1;
						  }else{
							  flag = 2;
						  }
						  
					  }
			   });
			   return flag;
	       }
	      
});