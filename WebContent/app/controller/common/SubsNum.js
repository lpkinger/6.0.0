Ext.QuickTips.init();
Ext.define('erp.controller.common.SubsNum', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.form.Panel','common.subs.SubsNum','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
      		'core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync','core.form.MultiHourField',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
      		'core.form.FileField','core.form.SplitTextField','core.button.End','core.button.ResEnd','core.picker.HighlightableDatePicker','core.form.TimeMinuteField',
      		'core.form.MonthDateField','core.form.SpecialContainField','core.form.HrOrgSelectField','core.trigger.FrequencyTrigger','erp.view.core.button.DYpreview',
      		'common.subs.SubsConditions','common.subs.SubsRelationConfig'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick   			
    		},
		    'SubsConditions': { 
				itemclick: this.onGridItemClick
			},
			'subsRelationConfig': { 
				itemclick: this.onGridItemClick
			},
    		'erpFormPanel':{
    			afterload:function(t,o){
    				var l=t.items.length;
    				t.insert(l,{
    					  margin:'7 0 0 0',
    		              id:'fileform',
    		              xtype:'form',
    		              columnWidth: 0.25,
    		              bodyStyle: 'background: #f2f2f2;border: none;',
    		              items:[{
    		              	  style:'background:#f2f2f2',
    		                  name:'img_',
    		                  id:'img_',
    		                  xtype: 'filefield',
    		                  name: 'file',
    		                  fieldLabel:'简图设置',
    		                  labelWidth:70,
    		                  buttonOnly:true,
    		                  createFileInput : function() {
    			                   var a = this;
    			                    a.fileInputEl = a.button.el.createChild({
    						           name : a.getName(),
    						           cls : Ext.baseCSSPrefix + "form-file-input",
    						           tag : "input",
    						           type : "file",
    						           size : 1,
    						           accept:"image/*"
    					           }).on("change", a.onFileChange, a);
    		                   },
    		                  buttonConfig:{
    		                      text:'上传',
    		                      margin:'2 0 0 2',
    		                      iconCls:'x-button-icon-pic'
    		                  },
    		                  listeners: {
    		                      change: function(field){
    		                          field.ownerCt.upload(field);
    		                      }
    		                  }
    		              },{
    		                  xtype: 'image',
    		                  width: 75,
    		                  height: 75,
    		                  id:'logo',
    		                  hidden:true,
    		                  margin:'2 0 0 75',
    		                  listeners:{
    		                  	 'afterrender':function(img,opts){
    		                  	 	if(formCondition){
    		                  	 		var id=formCondition.split("=")[1];
    		                  	 		var logo=Ext.getCmp('logo');
    		                  	 		logo.show();	
    		                            logo.setSrc(basePath+'common/charts/getImage.action?table=subsnum&id='+id);
    		                  	 	}
    		                  	 }
    		                  }
    		              },{
    		              	  style:{background:'#f2f2f2'},
    		                  xtype: 'displayfield',
    		                  fieldCls: 'sn_display',
    		                  value: '<div style="color:gray;background:#f2f2f2;" >建议尺寸75*75像素左右图片</div>',
    		                  margin:'0 0 0 10',
    		                  padding:'0'
    		              }],
    		               upload: function(field){
    		               	 var img_reg= /\.([jJ][pP][gG]){1}$|\.([jJ][pP][eE][gG]){1}$|\.([gG][iI][fF]){1}$|\.([pP][nN][gG]){1}$|\.([bB][mM][pP]){1}$/;
    						 if (img_reg.test(field.value)) {
    		                      field.ownerCt.getForm().submit({
    		                          url: basePath + 'common/charts/saveimage.action',
    		                          waitMsg:'正在上传',
    		                          params:{
    		                          		id:Ext.getCmp('id_').getValue(),
    		                          		table:'subsnum'
    		                          },
    		                          success: function(fp, o){
    		                          	 var logo=Ext.getCmp('logo');
    		                          	 logo.fireEvent('afterrender',logo);   
    		              				if(o.result.success){
    		                              Ext.Msg.alert('提示','上传成功');  
    		                              logo.show();
    		                              window.location.href = window.location.href;
    		                      			}else Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
    		                          },
    		                          failure:function(fp,o){
    		                              Ext.Msg.alert('提示','上传失败，请检查文件大小及格式!');
    		                          }
    		                      });  
    						 }else{
    						 	 Ext.Msg.alert('提示', '文件类型错误,请选择图片文件(jpg/jpeg/gif/png/bmp)');
    						 }
    		              }   		            
    				});
    				
    			}                 
    		},
			'HrOrgSelectfield':{
				beforerender:function(field){
					Ext.apply(field,{
						columnWidth:0.75
					});
				}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);    				    				
    				me.beforeSave('save');
    				//me.checkSql(me,'save');
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('id_').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				//me.checkSql(me,'update');
					me.beforeSave('update');
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSubsNum', '新增订阅号', 'jsps/common/subsnum.jsp');
    			}
    		},
    		'erpDYpreviewButton':{
    			click: function(btn){
    				var form = Ext.getCmp('form');
    				var grid = Ext.getCmp('grid');
    				this.showwindow(form,grid);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('id_').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('id_').value);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('id_').value);

				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('statuscode_');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('id_').value);
				}
			},
			'erpBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('statuscode_');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onBanned(Ext.getCmp('id_').value);
				}
			},
			'erpResBannedButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('statuscode_');
					if(status && status.value != 'DISABLE'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResBanned(Ext.getCmp('id_').value);
				}
			}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(type){
		var me = this;
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var bool = true, codes = {},e = '';	
		Ext.each(items, function(item){
			var v = item.get('formula_code_');
			if(v!=""){
				if(!codes[v]) {
					codes[v] = [item.get('detno_')];
				} else {
					e+='行'+item.get('detno_')+'的订阅项编号:'+v+'重复;';				
				}
			}			
		});
		if(e.length > 0) {
			bool=false;
			showError(e);
		}
		if(bool && type=='save')
			me.beforeSaveSubs(this);
		if(bool && type=='update')
			me.beforeUpdateSubs(this);
	},
	showwindow: function(form,grid){
		var store = grid.getStore();
		var id = Ext.getCmp('id_').value;
		var title = form.getValues().title_;
		var url = 'common/charts/mobilePreview.action?id='+id;
		if (Ext.getCmp('DYpreview')) {
			Ext.getCmp('DYpreview').setTitle(title);
			}
		else {
		var DYpreview = new Ext.window.Window({
		   id : 'DYpreview',
		   title: '订阅号详情',
		   height: "100%",
		   width: "40%",
		   resizable:false,
		   modal:true,
		   buttonAlign : 'center',
		   layout : 'anchor',
		   items: [{
			   tag : 'iframe',
			   frame : true,
			   anchor : '100% 100%',
			   layout : 'fit',
			   html : '<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>'
		   }],
		   buttons : [{
			   text : '关  闭',
			   iconCls: 'x-button-icon-close',
			   cls: 'x-btn-gray',
			   handler : function(){
				   Ext.getCmp('DYpreview').close();
			   }
		   }]
	   });
		DYpreview.show();}
	},
	beforeSaveSubs:function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');	
		var conDetail = Ext.getCmp('subsCondition');		
		var relDetail = Ext.getCmp('subsRelationConfig');
		/*Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});*/
		var param = me.GridUtil.getGridStore(detail);
		var param1 = new Array();
		var param2 = new Array();
		if(conDetail) {
			param1 = me.GridUtil.getGridStore(conDetail);
		}
		if(relDetail) {
			param2 = me.GridUtil.getGridStore(relDetail);
		}
		param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			me.save(r, param, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.formStore = unescape(Ext.JSON.encode(r));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param1 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition+'&gridCondition=num_iddIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=num_idIS'+value;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;

			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   		    	formCondition+'&gridCondition=num_idIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=num_idIS'+value;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	},
	beforeUpdateSubs:function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');	
		var conDetail = Ext.getCmp('subsCondition');
		var relDetail = Ext.getCmp('subsRelationConfig');
		/*Ext.each(detail.store.data.items, function(item){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -item.index;
			}
		});*/
		var param = me.GridUtil.getGridStore(detail);
		var param1 = new Array();
		var param2 = new Array();
		if(conDetail) {
			param1 = me.GridUtil.getGridStore(conDetail);
		}
		if(relDetail) {
			param2 = me.GridUtil.getGridStore(relDetail);
		}
		param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			me.update(r, param, param1, param2);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	update:function(){
		var params = new Object();
		var r = arguments[0];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param1 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[3].toString().replace(/\\/g,"%"));
		var me = this;
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.getActiveTab().setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(me.contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;
			   		    	if(me.contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   					formCondition;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   					formCondition;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
	   		
		});
	},
	contains: function(string,substr,isIgnoreCase){
	    if(isIgnoreCase){
	    	string=string.toLowerCase();
	    	substr=substr.toLowerCase();
	    }
	    var startChar=substr.substring(0,1);
	    var strLen=substr.length;
	    for(var j=0;j<string.length-strLen+1;j++){
	    	if(string.charAt(j)==startChar){//如果匹配起始字符,开始查找
	    		if(string.substring(j,j+strLen)==substr){//如果从j开始的字符与str匹配，那ok
	    			return true;
	    			}   
	    		}
	    	}
	    return false;
	},
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	checkSql: function(me,type){
		var bool = true;
		var grid = Ext.getCmp('subsCondition');
		var data = grid.store.data.items;
		if(data != '' && data != null){
			for(var i = 0; i < data.length; i++){
				if(data[i].data.type_ == 'sql'){
					bool = false;
					var value = data[i].data.value_;	//获取sql语句
					if(value.trim().lastIndexOf(";") != -1){	//末尾是否包含分号
						value = value.replace(";","");
						grid.store.data.items[i].data.value_=value;
						grid.getView().refresh();
					}
					//验证sql合法性
					Ext.Ajax.request({
						url:basePath + 'common/checkRuleSql.action',
						method:'post',
						params:{
							sql:value
						},
						callback:function(options,success,response){
							var res = Ext.decode(response.responseText);
							if(res.success){
								if(res.result){
//									Ext.Msg.alert('提示','检测通过!');
									me.beforeSave(type);
								}else{
									Ext.Msg.alert('提示','Sql检测失败，原因:' + res.errorInfo.substring(0,res.errorInfo.indexOf('\n')));
								}
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);
							}
						}
					});
				}
			}
			if(bool)
				me.beforeSave(type);
		}
	}
});