Ext.define('erp.view.oa.attention.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpAttentionFormPanel',
	id: 'form', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       focusCls: 'x-form-field-cir',//fieldCls
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
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
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? this.formCondition : formCondition.replace(/IS/g,"=");
    	var param = {caller: this.caller, condition: formCondition};
    	this.getItemsAndButtons(this, 'common/singleFormItems.action', this.params || param);//从后台拿到formpanel的items
		this.callParent(arguments);
	},
	 save:function(){
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
		Ext.Ajax.request({
	   		url : basePath + form.saveUrl,
	   		params : params,
	   		method : 'post',
	   		async: false,
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			 if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   					showError(str);
	   				} 
	   		}
		});
	}
	},
	update: function(){
		var params = new Object();
		var r=Ext.getCmp('form').getValues();
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
	   		url : basePath + form.updateUrl,
	   		params: params,
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   		var localJson = new Ext.decode(response.responseText);
    			 if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   					showError(str);
	   				} 
	   		}
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
	getItemsAndButtons: function(form, url, param){
		var me = this;
		Ext.Ajax.request({//拿到form的items
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		form.fo_id = res.fo_id;
        		form.fo_keyField = res.fo_keyField;
        		form.tablename = res.tablename;
        		if(res.keyField){
        			form.keyField = res.keyField;
        		}
        		if(res.statusField){
        			form.statusField = res.statusField;
        		}
        		if(res.statuscodeField){
        			form.statuscodeField = res.statuscodeField;
        		}
        		if(res.codeField){
        			form.codeField = res.codeField;
        		}
        		form.fo_detailMainKeyField = res.fo_detailMainKeyField;
        		var grids = Ext.ComponentQuery.query('gridpanel');
        		//如果该页面只有一个form，而且form字段少于8个，则布局改变
        		if(grids.length == 0 && res.items.length <= 8){
        			Ext.each(res.items, function(item){
        				item.columnWidth = 0.5;
        			});
        			form.layout = 'column';
    			}
        		if(res.data){
        			var data = Ext.decode(res.data);
        			if(form.statuscodeField && data[form.statuscodeField] != null && 
        					data[form.statuscodeField] != 'ENTERING' && data[form.statuscodeField] != 'COMMITED'){//非在录入和已提交均设置为只读
        				form.readOnly = true;
    					Ext.each(res.items, function(item){
                			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
                				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
                					item.columnWidth = 0.25;
                				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
                					item.columnWidth = 0.5;
                				}
                			}
                			item.value = data[item.name];
                			if(item.secondname){//针对合并型的字段
                				item.secondvalue = data[item.secondname];
                			}
                			if(item.name == form.statusField){//状态加特殊颜色
                				item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
                			} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
                				item.xtype = 'hidden';
                			}
                			item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
        					item.readOnly = true;
                		});
    				} else {
    					Ext.each(res.items, function(item){
                			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
                				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
                					item.columnWidth = 0.25;
                				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
                					item.columnWidth = 0.5;
                				}
                			}
                			item.value = data[item.name];
                			if(item.secondname){//针对合并型的字段
                				item.secondvalue = data[item.secondname];
                			}
                			if(item.name == form.statusField){//状态加特殊颜色
                				item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
                			}
                		});
    				}
        			//form.getForm().setValues(data);
//        			form.getForm().getFields().each(function (item,index,length){
//        				item.originalValue = item.getValue();
//        			});
        		} else {
        			Ext.each(res.items, function(item){
            			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
            				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
            					item.columnWidth = 0.25;
            				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
            					item.columnWidth = 0.5;
            				}
            			}
            			if(item.name == form.statusField){//状态加特殊颜色
            				item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
            			}
            		});
        		}
        		form.add(res.items);
        		if(res.title && res.title != ''){
        			form.setTitle(res.title);
        		}	        		
	        		var bool = true;
	        		Ext.each(form.items.items, function(item){
	        			if(bool && item.hidden == false && item.readOnly == false && item.editable == true){
	        				this.focus(false, 200);
	        				bool = false;
	        			}
	        		});
        	}
        });
	},
});