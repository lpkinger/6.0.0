Ext.QuickTips.init();
Ext.define('erp.controller.oa.custom.SingleForm', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.custom.SingleForm','core.form.Panel','core.form.CheckBoxGroup',
   		'core.button.UUListener','core.button.Sync','core.form.MultiField','core.form.FileField','core.button.FormBook',
   		'core.button.Add','core.button.Save','core.button.Close','core.button.Update','core.button.Delete','core.button.DeleteDetail',
   		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit','core.button.PrintByCondition',
  		'core.button.Scan','core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.ResConfirm','core.button.Sync',
   		'core.grid.TfColumn','core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField','core.form.ConDateHourMinuteField','core.form.DateHourMinuteField',
   		'core.grid.ItemGrid','core.button.PrintPDF','core.form.FtDateField','core.button.SubmitApproves','core.button.ResSubmitApproves','core.button.Check','core.form.ColorField',
   		'core.form.CheckBoxGroup','core.form.RadioGroup','core.button.CallProcedureByConfig','core.button.Modify','core.button.End','core.button.ResEnd'
   	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSubmitApprovesButton':{
    				afterrender:function(btn){
	    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
	    				var approvestatus=Ext.getCmp("ct_approvestatuscode");	    				    			
	    				if(!((status && status.value == 'AUDITED')&&(approvestatus&&approvestatus.value =='ENTERING'))){
	    					btn.hide();
	    				}
    				},
    				click:function(btn){   				
    					this.SubmitApproves(Ext.getCmp(me.getForm(btn).keyField).value);   					
    				}
    			
    			
    		},
    		'erpResSubmitApprovesButton':{
    				afterrender:function(btn){   				
    				var status=Ext.getCmp("ct_approvestatuscode");
	    				if(status && status.value != 'COMMITED'){
	    					btn.hide();
	    				}
    				},
    				click:function(btn){   					
    					this.ResSubmitApproves(Ext.getCmp(me.getForm(btn).keyField).value);
    					 
    				}
    			
    			
    		},
    		'erpModifyCommonButton':{
    			afterrender:function(btn){
    				var status=Ext.getCmp("CT_STATUSCODE");
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpCheckButton':{
    				afterrender:function(btn){   					
    					var status=Ext.getCmp("ct_approvestatuscode");
	    				if(status && status.value != 'COMMITED'){
	    					btn.hide();
	    				}
    				},
    				click:function(btn){
    					me.FormUtil.onCheck(Ext.getCmp(me.getForm(btn).keyField).value);
    				}    			
    		},
    		'textareafield':{
    			beforerender:function(field){   					
    				field.grow=true;
    				field.growMax=300;
				}
    		},
    		'dbfindtrigger[name=custlinkman]':{//客户服务单据使用
    			beforetrigger: function(field) {
			 	 	var custcode = Ext.getCmp('custcode');
				    if(custcode&&custcode.value!=''){
				    	field.dbBaseCondition = " ct_cucode = '" +custcode.value+"'";  
				    }else if(custcode){
				    	showError('请先选择客户');
				    	return false;
				    }
				}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(!Ext.isEmpty(form.codeField) && Ext.getCmp(form.codeField) && ( 
    						Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				var title = btn.ownerCt.ownerCt.title || ' ';
    				var url = window.location.href;
    				url = url.replace(basePath, '');
    				url = url.substring(0, url.lastIndexOf('formCondition')-1);
    				me.FormUtil.onAdd('add' + caller, title, url);
    			}
    		},
    		'erpCloseButton': {
    			click:function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'CANUSE'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onBanned(crid);
				}
			},
			'erpResBannedButton': {
				afterrender:function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				if(status && status.value != 'BANNED'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					var crid = Ext.getCmp(me.getForm(btn).keyField).value;
					me.FormUtil.onResBanned(crid);
				}
			},
    		'erpResAuditButton': {   		    		
    			afterrender: function(btn){
    				var status = Ext.getCmp(me.getForm(btn).statuscodeField);
    				var approvestatus=Ext.getCmp("ct_approvestatuscode");   	
    				if(approvestatus){
    					if((status && status.value == 'AUDITED')&&(approvestatus&&approvestatus.value=='ENTERING')){
    					
    					}else{
    						btn.hide();
    					}
    				}else{
    					if((status && status.value != 'AUDITED')||(Ext.getCmp('ct_confirmstatus')&&Ext.getCmp('ct_confirmstatus').value=='已确认')){
    						btn.hide();
    					}
    				}
    				
    				
    				
    				
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp(me.getForm(btn).keyField).value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				var condition = '{CUSTOMTABLE.CT_ID}=' + Ext.getCmp(me.getForm(btn).keyField).value + '';
    				me.FormUtil.onwindowsPrint(Ext.getCmp(me.getForm(btn).keyField).value,'',condition);
    			}
    		},
    		'dbfindtrigger[name=CT_VARCHAR500_1]': {
	    		afterrender: function(f){
	    			   	if(caller=='Testapply'){
	    			   	  f.onTriggerClick = function(){
	    					   me.getModuleTree();
	    				   };
	    				   f.autoDbfind = false;
	    			   	}
	    			   	if(caller=='FEEMX'){
	    			   		f.onTriggerClick = function(){
		    					   me.getModuleTree();
		    				   };
		    			    f.autoDbfind = false;
	    			   	}
	    			   }
	    	},
	    	'erpConfirmButton': {
		    	afterrender: function(btn){
					var status = Ext.getCmp(me.getForm(btn).statuscodeField);
					if((status && status.value != 'AUDITED')||(Ext.getCmp('ct_confirmstatus')&&Ext.getCmp('ct_confirmstatus').value=='已确认')){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var crid = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.FormUtil.onConfirm(crid);
    			}
    		},
    		'erpResConfirmButton': {
	    		afterrender: function(btn){
					var status = Ext.getCmp(me.getForm(btn).statuscodeField);
					if((status && status.value != 'AUDITED')||(Ext.getCmp('ct_confirmstatus')&&Ext.getCmp('ct_confirmstatus').value!='已确认')){
						btn.hide();
					}
				},
    			click: function(btn){  
    				var crid = Ext.getCmp(me.getForm(btn).keyField).value;
    				me.onResConfirm(crid);
    				
    			}
    		},
	      	'treepanel': {
	    		 itemmousedown: function(selModel, record){
	    				 var tree = selModel.ownerCt;
	    				 me.loadTree(tree, record);
	    			   }
	    		 },
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onChange: function(field, value){
		field.setValue(value);
		if(value == 'C'){
			
		}
	},
	print:function(btn){
		var form=Ext.getCmp('form');
		var oldstr=window.document.body.innerHTML;
		Ext.each(btn.ownerCt.items.items,function(b){
			b.hide();
		});
		document.getElementById('form').style.height='auto';
		document.getElementById('form-body').style.height='auto';
		var inputs=document.getElementsByTagName('input');
		var item_f=Ext.getCmp('form').items.items;
		var bdhtml=window.document.body.innerHTML;
		winname = window.open('', "_blank",'');
		winname.document.title = '项目确认书打印'+Ext.getCmp('CT_ID').value;
		winname.document.body.innerHTML='<link rel="stylesheet" ' +
				'href="'+basePath+'resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>' +
				'<link rel="stylesheet" href="'+basePath+'resource/css/main.css" type="text/css"></link>' +
				'<div>'+bdhtml+'</div>';
		Ext.each(item_f,function(item){
			if(item.xtype=='combo'||item.xtype=='erpYnField'){
				winname.document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[1].value=item.rawValue;
			}
			if(item.xtype=='textfield'||item.xtype=='numberfield'||item.xtype=='dbfindtrigger'){
				winname.document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[0].value=item.value;
			}
			if(item.xtype=='datefield'){
				winname.document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[0].value=item.rawValue;
			}
			if(item.xtype=='textareafield'){
				winname.document.getElementById(item.name+'-bodyEl').getElementsByTagName('textarea')[0].value=item.rawValue;
			}
			if(item.xtype=='multifield'&&item.logic!=''){
				winname.document.getElementById(item.name+'-bodyEl').getElementsByTagName('input')[0].value=item.value;
				winname.document.getElementById(item.secondname+'-bodyEl').getElementsByTagName('input')[0].value=item.secondvalue;
			
			}
		});
		winname.print(); 
		winname.close();
		location.reload();		
	},
	save: function(){
		//默认流程CAllER 和页面caller一致;
		var flowcaller=Ext.getCmp('fo_flowcaller');
		if(!flowcaller.value){
			flowcaller.setValue(Ext.getCmp('fo_caller').value);
		}
		var grid = Ext.getCmp('grid');
		var dd = grid.getChange();
		if(dd.added.length > 0) {
			var form = Ext.getCmp('form');
			this.FormUtil.getSeqId(form);
			this.FormUtil.save(form.getValues(), Ext.encode(dd.added));
		} else {
			showError('请至少配置一个有效字段!');
		}
		
	},
	update: function(){
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var field = Ext.getCmp('fo_table'), id = Ext.getCmp('fo_id').value;
		Ext.Array.each(items, function(item){
			if(item.data['fd_field'] != null && item.data['fd_field'] != ''){
				item.set('fd_table', field.value);
				item.set('fd_foid', id);
			}
		});
		var me = this;
		if(! me.FormUtil.checkForm()){
			return;
		}
		var dd = grid.getChange();
		me.FormUtil.update(Ext.getCmp('form').getValues(), Ext.encode(dd.added), 
				Ext.encode(dd.updated), Ext.encode(dd.deleted));
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
	    					 Ext.getCmp('CT_VARCHAR500_1').setValue(t.title);
	    			    }
	    				btn.ownerCt.ownerCt.close();
	    			   }
	    		   }]
	    	   });
	    	   w.show();
	    	   this.loadTree(w.down('treepanel'), null);
	 },
	 loadTree: function(tree, record){
	    	   var pid = 0;
	    	   if(record) {
	    		   if (record.get('leaf')) {
	    			   return;
	    		   } else {
	    			   if(record.isExpanded() && record.childNodes.length > 0){
	    				   record.collapse(true, true);//收拢
	    				   return;
	    			   } else {
	    				   if(record.childNodes.length != 0){
	    					   record.expand(false, true);//展开
	    					   return;
	    				   }
	    			   }
	    		   }
	    		   pid = record.get('id');
	    	   }
	    	   tree.setLoading(true);
	    	   Ext.Ajax.request({
	    		   url : basePath + 'common/lazyTree.action?_noc=1',
	    		   params: {
	    			   parentId: pid,
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
	       },
	  onResConfirm: function(id){
			var form = Ext.getCmp('form');	
			Ext.Ajax.request({
		   		url : basePath + form.resConfirmUrl,
		   		params: {
		   			id: id,
		   			caller:caller
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){    			
		   					window.location.reload();    				
		   			} 
		   			if (localJson.exceptionInfo) {
							showError(localJson.exceptionInfo);
					}
		   		}
			});
		},
		SubmitApproves:function(id){
			var me = this;
			var form = Ext.getCmp('form');			
			Ext.Ajax.request({
		   		url : basePath + form.submitApprovesUrl,
		   		params: {
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
					var localJson = new Ext.decode(response.responseText);
					if(localJson.success){
						 me.FormUtil.getMultiAssigns(id, caller+'!Confirm',form);
					} else {
						if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								 me.FormUtil.getMultiAssigns(id, caller+'!Confirm', form,me.FormUtil.showAssignWin);
							} 
							showMessage("提示", str);
						}
					}
				}
			});
		
			
		},
		ResSubmitApproves:function(id){
			var me = this;
			var form = Ext.getCmp('form');			
			Ext.Ajax.request({
		   		url : basePath + form.resSubmitApprovesUrl,
		   		params: {
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(localJson.success){    			
		   					window.location.reload();    				
		   			} 
		   			if (localJson.exceptionInfo) {
							showError(localJson.exceptionInfo);
					}
		   		}
			});
		
			
		}
		
		
});