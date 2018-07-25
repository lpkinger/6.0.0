Ext.define('erp.view.oa.doc.DocForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpDocFormPanel',
	id: 'form', 
	region: 'north',
	frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	title:null,
	fieldDefaults : {
		margin : '2 2 2 2',
		fieldStyle : "background:#FFFAFA;color:#515151;",
		focusCls: 'x-form-field-cir',
		labelAlign : "right",
		msgTarget: 'side',
		blankText : $I18N.common.form.blankText
	},
	bodyBorder :true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	saveUrl: '',
	updateUrl: '',
	deleteUrl: '',
	getIdUrl: '',
	keyField: '',
	codeField: '',
	statusField: '',
	params: null,
	caller:null,
	formCondition:null,
	saveDirUrl:'oa/doc/saveDir.action?_noc=1&caller='+this.caller,
	updateDirUrl:'oa/doc/updateDir.action?_noc=1&caller='+this.caller,
	saveDocUrl:'oa/doc/saveDoc.action?_noc=1&caller='+this.caller,
	updateDocUrl:'oa/doc/updateDoc.action?_noc=1&caller='+this.caller,
	addButtons:['->',{
		xtype:'erpSaveButton'
	},{
		xtype:'erpCloseButton'
	},'->'],
	updateButtons:['->',{
		xtype:'erpUpdateButton'
	},{
		xtype:'erpDeleteButton'
	}/*,{		
	    xtype:'erpSubmitButton'
	},{
		xtype:'erpResSubmitButton'
	},{
		xtype:'erpAuditButton'
	},{
		xtype:'erpResAuditButton'
	}*/,'->'],
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? this.formCondition : formCondition.replace(/IS/g,"=");
		var param = {caller: this.caller, condition: formCondition};
		this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action?_noc=1', this.params || param);//从后台拿到formpanel的items
		this.callParent(arguments);
	},
	listeners:{
		afterload:function(form){
			var btn = form.down('erpCloseButton');
			btn.handler = function(btn){
				btn.ownerCt.ownerCt.ownerCt.close();
			};
		}
	},
	setTitle: function(){
		this.title='';
	},
	save:function(btn,caller){
		var me = this;
		var params = new Object();
		var form = Ext.getCmp('form');
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.getSeqId(form);
		}
		if(form.getForm().isValid()){
			//form里面数据
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					//number类型赋默认值，不然sql无法执行
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			var param=[];
			Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
				if(contains(k, 'ext-', true)){
					delete r[k];
				}
			});
			params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
			params.param = unescape(param.toString().replace(/\\/g,"%"));
			var url=this.caller=='DocCreateDir'?form.saveDirUrl:form.saveDocUrl;
			if(caller=='DocumentListChange') var url = form.saveUrl;
			Ext.Ajax.request({
				url : basePath +url,
				params : params,
				method : 'post',
				async: false,
				callback : function(options,success,response){
					//改变全选按钮状态
					var docGrid = Ext.getCmp('docgrid'),docpanel=Ext.getCmp('docpanel');
					docGrid.selModel.deselectAll(true);
					docpanel.reSetButton(docpanel);
					var localJson = new Ext.decode(response.responseText);
					if(localJson.exceptionInfo){
						var str = localJson.exceptionInfo;
						showError(str);
					}else {
						if(localJson.needflow){
							form.loadUpdateButtons(form,'ENTERING','在录入');
						}else {
							btn.ownerCt.ownerCt.ownerCt.close();
							showMessage('提示', '保存成功!', 1000);
							if(caller=='DocCreateDir'){
								var tree=Ext.getCmp('doctree');
								tree.refreshNodeByParentId(CurrentFolderId,tree);
							}
						}
				  
				    }
				}
			});
		}
	},
	update: function(btn){
		var params = new Object();
		var r=Ext.getCmp('form').getValues();
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		var form = Ext.getCmp('form');
		var url=this.caller=='DocCreateDir'?form.updateDirUrl:form.updateDocUrl;
		Ext.Ajax.request({
			url : basePath + url,
			params: params,
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					var str = localJson.exceptionInfo;
					showError(str);
				}else {
					btn.ownerCt.ownerCt.ownerCt.close();
					showMessage('提示', '修改成功!', 1000); 
					if(this.caller=='DocCreateDir'){
						var tree=Ext.getCmp('doctree');
						tree.refreshNodeByParentId(r.dl_parentid,tree,tree.getStore().getNodeById(r.dl_parentid));
					}
				}
			},
			scope:this
		});
	},
	ondelete: function(id){
		var me = this;
		warnMsg($I18N.common.msg.ask_del_main, function(btn){
			if(btn == 'yes'){
				var form = Ext.getCmp('form');
				Ext.Ajax.request({
					url : basePath + form.deleteUrl,
					params: {
						id: id
					},
					method : 'post',
					callback : function(options,success,response){
						me.getActiveTab().setLoading(false);
						var localJson = new Ext.decode(response.responseText);
						if(localJson.exceptionInfo){
							showError(localJson.exceptionInfo);return;
						}
					}
				});
			}
		});
	},
	getSeqId: function(form){
		if(!form){
			form = Ext.getCmp('form');
		}
		Ext.Ajax.request({
			url : basePath + 'common/getCommonId.action?caller=' +form.caller,
			method : 'get',
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					Ext.getCmp(form.keyField).setValue(rs.id);
				}
			}
		});
	},
	loadUpdateButtons:function(form,status,statuscode){
		if(!form){
			form = Ext.getCmp('form');
		}
		var toolbar=form.dockedItems.items[0];
		form.down("#dl_status").setValue(status);
		form.down("#dl_statuscode").setValue(statuscode);
		toolbar.removeAll();
		toolbar.add(form.updateButtons);
	},
	setButtonsOld: function(form, buttonString){
		if(buttonString != null && buttonString.trim() != ''){
			var buttons = new Array();
			buttons.push('->');//->使buttons放在toolbar中间
			Ext.each(buttonString.split('#'), function(btn, index){
				var o = {};
				if(btn.indexOf("erpCallProcedureByConfig")!=-1){
					o.xtype = 'erpCallProcedureByConfig';
					o.name = btn;
				}else if(btn.indexOf('erpCommonqueryButton!')!=-1){
					btn = btn.split('!');
					o.xtype = btn[0];
					o.id = btn[1];
				}else{
					o.xtype = btn;
				}
				o.height = 26;
				buttons.push(o);
				if((index + 1)%12 == 0){//每行显示12个button，超过12个就添加一个bbar
					buttons.push('->');
					form.addDocked({
						xtype: 'toolbar',
						dock: 'bottom',
						defaults: {
							style: {
								marginLeft: '10px'
							}
						},
						items: buttons//12个加进去
					});
					buttons = new Array();//清空
					buttons.push('->');
				}
			});
			buttons.push('->');
			form.addDocked({//未到12个的
				xtype: 'toolbar',
				dock: 'bottom',
				defaults: {
					style: {
						marginLeft: '10px'
					}
				},
				items: buttons
			});
		}
	}
});