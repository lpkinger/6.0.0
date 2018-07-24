Ext.QuickTips.init();
Ext.define('erp.controller.ma.MultiForm', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil'],
    views:[
   		'ma.MultiForm','ma.MyForm','ma.MyGrid','ma.MyDetail','core.button.DeleteDetail','core.toolbar.Toolbar',
   		'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.grid.TfColumn','core.grid.YnColumn',
   		'core.button.UUListener', 'core.button.DbfindButton','core.button.ComboButton', 'core.form.YnField',
   		'core.button.Sync','core.button.ReportFiles','core.trigger.AddDbfindTrigger'
   	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	this.control({ 
    		'dbfindtrigger[name=ds_likefield]':{
    			beforetrigger:function(field){
    				var table=Ext.getCmp('ds_whichdbfind').value;
    				if(table){
    					con=table.toUpperCase().split('LEFT JOIN')[0];
    					field.dbBaseCondition  = "table_name='"+con+"'";
    				}else{
	    				showError("请先选择查找表名!");
	    				return false;
    				}
    			}
    		},
    		'dbfindtrigger[name=linkkey]':{
    			beforetrigger:function(field){
    				var table=Ext.getCmp('dbtablename').value;
    				if(table){
    					con=table.toUpperCase().split('LEFT JOIN')[0].replace(/(^\s*)|(\s*$)/g, "");
    					field.dbBaseCondition  = "table_name='"+con+"'";
    				}
    			}
    		},
    		'erpSyncButton': {
    			afterrender: function(btn){
    				if(isSaas){btn.hide();};
    				btn.autoClearCache = true;
    			},
    			aftersync: function(btn, caller, datas, masters) {
    				if(caller == 'Form!Post') {
    					var grids = Ext.ComponentQuery.query('mydetail');
    					Ext.Array.each(grids, function(grid){
    						var dg = new Array();
    						grid.store.each(function(){
        						if(this.get('dg_id') > 0)
        							dg.push(this.get('dg_id'));
        					});
        					btn.syncdatas = dg.join(',');
        					btn.caller = 'DetailGrid!Post';
        					btn.sync();
    					});
    				} else {
    					btn.syncdatas = null;
    				}
    			}
    		},
    		'tabpanel': {
    			add: function(tab) {
    				var btn = Ext.ComponentQuery.query('erpSyncButton')[0];
    				if(btn&&tab.id!='newStyle_Tab') {
    					var ids = [];
    					Ext.Array.each(tab.items.items, function(p){
    						var id = p.dataId;
    						if(id > 0)
    							ids.push(id);
    					});
    					btn.syncdatas = ids.join(',');
    				}
    			}
    		},
    		'multidbfindtrigger': {
    			render: function(field){
    				if(field.name == 'fo_button4add' || field.name == 'fo_button4rw'){
    					var fields = Ext.Object.getKeys($I18N.common.button);
    					var values = Ext.Object.getValues($I18N.common.button);
    					var data = [];
    					Ext.each(fields, function(f, index){
    						var o = {};
    						o.value = fields[index];
    						o.display = values[index];
    						data.push(o);
    					});
    					field.multistore = {fields:['display', 'value'],data:data};
    				}
    			}
    		},
    		'mygrid': {
    			
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var grid=selModel.view.ownerCt.ownerCt;
    				if(record&& (record.data.fd_dbfind != 'F'||(record.data.fd_type=='MT'&&record.data.fd_logictype!=''))){
    					grid.down('erpDbfindButton').setDisabled(false); 
    					grid.down('erpComboButton').setDisabled(true);
    				}else if(record && record.data.fd_type == 'C'){
    					grid.down('erpDbfindButton').setDisabled(true);
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
    			}
    		},
    		'mydetail': {
    			select: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    				var grid = selModel.view.ownerCt.ownerCt;
    				if(record&&record.data.dg_dbbutton != '0') {
    					grid.down('erpDbfindButton').setDisabled(false); 
    					grid.down('erpComboButton').setDisabled(true);
    				}else if(record && (record.data.dg_type == 'combo' || record.data.dg_type=='editcombo')) {
    					grid.down('erpDbfindButton').setDisabled(true); 
    					grid.down('erpComboButton').setDisabled(false);
    				}else {
    					grid.down('erpComboButton').setDisabled(true);
    					grid.down('erpDbfindButton').setDisabled(true);
    				}
    			}
    		},
    		'button[name=save]': {
    			click: function(btn){
    				me.save();
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				btn.ownerCt.add({
    					xtype:'erpDbfindButton'
    				});
    				btn.ownerCt.add({
    					xtype:'erpComboButton'
    				});
    			}
    		},
    		/**
    		 * 下拉框设置
    		 */
    		'erpComboButton': {
    			click: function(btn){
    				var tab = btn.up('tabpanel'), 
						activeTab = tab.getActiveTab() || tab.items.items[0];
    				var record = activeTab.down('gridpanel').selModel.lastSelected;
    				if(record) {
    					if(record.data.fd_type == 'C') {   			
	    					btn.comboSet(activeTab.down('field[name=fo_caller]').value, record.data.fd_field);
	    				} else if(record.data.dg_type == 'combo' || record.data.dg_type =='editcombo') 
    						btn.comboSet((activeTab.whoami || record.data.dg_caller), record.data.dg_field);
    				}
    			}
    		},
    		/**
    		 * DBFind设置
    		 */
    		'erpDbfindButton': {
    			click: function(btn){
    				var tab = btn.up('tabpanel'), 
    					activeTab = tab.getActiveTab() || tab.items.items[0];
    				var grid = activeTab.down('gridpanel'), record = grid.selModel.lastSelected;
    				if(record) {
    					var isFormDetail = grid.xtype == 'mygrid', isDetail = grid.xtype == 'mydetail';
    					if(isFormDetail && (record.data.fd_dbfind != 'F'||(record.data.fd_type=='MT'&&record.data.fd_logictype!=''))) 
    						btn.dbfindSetUI(activeTab.down('field[name=fo_caller]').value, record.data.fd_field, grid);
    					else if(isDetail && record.data.dg_dbbutton != '0') 
        					btn.dbfindSetGrid((activeTab.whoami || record.data.dg_caller), activeTab.down('gridpanel'), record.data.dg_field);
    				}
    			}
    		},
    		'button[name=delete]': {
    			click: function(btn){
    				
    			}
    		},
    		'button[name=close]': {
    			click: function(btn){
    				//lidy  2017120375   解决 单据维护界面》选项》单据设置》界面设置   的关闭按钮不能关闭窗口。
    				var multiform = parent.Ext.getCmp('iframe_detail_multiform');
    				if(multiform!=null){
    					var settingWin = multiform.ownerCt.ownerCt.ownerCt;
    					if(settingWin.isVisible()){ 
	    					settingWin.close();
    					}else{
    						me.FormUtil.beforeClose(me);
    					}
    				}else{
    					me.FormUtil.beforeClose(me);
    				}
    			}
    		},
    		'button[name=ReportFiles]':{
    			click:function(btn){
    				var tab = btn.ownerCt.ownerCt.down('tabpanel'), 
    				activeTab = tab.getActiveTab() || tab.items.items[0];
    				var linkCaller='ReportFilesFG';    				
    				 var win = new Ext.window.Window(
 							{  
 								id : 'win',
 								height : '90%',
 								width : '95%',
 								maximizable : true,
 								buttonAlign : 'center',
 								layout : 'anchor',
 								items : [ {
 									tag : 'iframe',
 									frame : true,
 									anchor : '100% 100%',
 									layout : 'fit',
 									html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/fa/fp/ReportFilesFG.jsp?formCondition=fo_callerIS'+activeTab.down('field[name=fo_caller]').value+'&gridCondition=callerIS'+activeTab.down('field[name=fo_caller]').value+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
 								} ]
 							});
 					win.show(); 
    			}
    		},
    		'button[name=listSetting]':{    			
    			click:function(btn){
		    		var win = Ext.create('Ext.window.Window',{  
		    		  modal : true,
		        	  id : 'lsWin',
		        	  title:'列表设置',
		        	  height : '30%',
		        	  width : '38%',       	 
		        	  layout : 'anchor',   
		        	  bodyStyle: 'background: #f1f1f1;',
					  bodyPadding:10,					  
		        	  items : [{
		        	  	anchor: '100% 100%',
		                xtype: 'form',		              
			            layout: {
					        type: 'table',
					        columns: 2
					    },
					   	bodyStyle: 'background: #f1f1f1;border:none',	
					   	defaults:{
		        	  	  fieldStyle : "background:rgb(224, 224, 255);"    			 
		        	    },
	                    items : [{
							xtype : 'textfield',
							name : 'dl_caller',
							fieldLabel : '列表Caller',
							id : 'dl_caller',
							//value : d.fo_dlcaller || '',
							allowBlank : false,
                            labelWidth:105                               
						}, {
							xtype : 'button',
							text : '确定',
							id : 'confirmListBtn',
							cls : 'x-btn-gray',
							iconCls : 'x-button-icon-save',
							width: 80,
							style : {
								marginLeft : '15px'
							},
							listeners : {
								click : function(btn) {									
									me.setDatalist("dl");
								}
							}
						}, {
							xtype : 'textfield',
							name : 'dl_relative',
							fieldLabel : '关联列表Caller',
							id : 'dl_relative',
							//value : d.fo_dlrelativecaller || '',
							allowBlank : false,
							labelWidth:105   
						}, {
							xtype : 'button',
							text : '确定',
							id : 'confirmReBtn',
							cls : 'x-btn-gray',
							width: 80,
							iconCls : 'x-button-icon-save',
							style : {
								marginLeft : '15px'
							},
							listeners : {
								click : function(btn) {
									me.setDatalist("re");
								}
							}
						}],
					buttonAlign : 'center',
					buttons : [{
								text : '关闭',
								cls : 'x-btn-gray',
								iconCls : 'x-button-icon-close',
								id : 'closeBtn',
								handler : function(btn) {
									btn.up('window').close();
								}
							}]		 
		    	       }],
		    	      listeners:{
		    	       	 beforeshow:function(e){		    	       	 
		    	       	 	var fo_caller = me.getMainForm().getForm().getValues().fo_caller;
		    	       	 	me.FormUtil.getFieldsValue("Form", "fo_dlcaller,fo_dlrelativecaller", "fo_caller='"+fo_caller+"'", "dl_caller,dl_relative");
		    	       	 	Ext.getCmp("dl_caller").originalValue = Ext.getCmp("dl_caller").value; 
		    	       	 	Ext.getCmp("dl_relative").originalValue = Ext.getCmp("dl_relative").value; 
		    	       	 	if(Ext.getCmp('mytab').items.items.length == 1){//没有从表，不配置关联列表
			    	       	 	Ext.getCmp("dl_relative").hide(true);
			    	       	 	Ext.getCmp("confirmReBtn").hide(true);
		    	       	 	}		    	       	 	
		    	       	 }
		    	      }
		    		});
    			win.show(); 
    		   }
    		},
    		'dbfindtrigger[name=fo_keyfield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_codefield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_statusfield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_statuscodefield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_table";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailkeyfield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailmainkeyfield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatuscode]': {
    			afterrender: function(t){
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detailstatus]': {
    			afterrender: function(t){
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger[name=fo_detaildetnofield]': {
    			afterrender: function(t){
    				t.dbKey = "fo_detailtable";
    				t.mappingKey = "ddd_tablename";
    				t.dbMessage = "请先选择从表名!";
    			}
    		},
    		'dbfindtrigger': {
    			afterrender: function(t){
    				t.autoDbfind = false;
    			}
    		},
    		'mfilefield': {
    			afterrender: function(f) {
    				// 只能有一个rpt附件
    				f.multi = false;
    			}
    		},
    		'button[name=FormBook]': {
    			click: function(btn){
    				var forms = Ext.ComponentQuery.query('myform');
    				if(forms.length > 0)
    					me.showFormBookEditor(forms[0].down('field[name=fo_id]').value);
    			}
    		},
    		'field[name=dg_field]': {
    			blur: function(f) {
    				var value = f.getValue(), grid = me.getActiveGrid(), record = grid.selModel.lastSelected, gridCaller = record.get("dg_caller");
    				if(value && gridCaller) {
    					var form = me.getMainForm(), formCallerField = (form ? (form.down('field[name=fo_caller]')) : null),
    						formCaller = (formCallerField ? (formCallerField.getValue()) : null);
    					if(gridCaller == formCaller) {
    						me.checkColumn(form.down('field[name=fo_detailtable]').getValue(), value, record);
    					} else {
    						me.checkColumn(record.get('dg_table'), value, record);
    					}
    				}
    			}
    		},
    		'field[name=fd_field]': {
    			blur: function(f) {
    				var value = f.getValue().toLowerCase(), tabpanel = Ext.getCmp('mytab'), tab=tabpanel.getActiveTab() || tabpanel.items.items[0];
    				    grid = tab.down('gridpanel'), record = grid.selModel.lastSelected;
    				if(value && record) {
    					me.checkColumn(tab.down('form').down('field[name=fo_table]').getValue(), value, record);
    				}
    			}
    		},
    		'button[name=buttonGroupSet]':{
    			click: function(btn){
    				var forms = Ext.ComponentQuery.query('myform');
    				if(forms.length > 0)
    				{
    					var caller = forms[0].down('field[name=fo_caller]');
    					var button4rw = forms[0].down('field[name=fo_button4rw]');
    					if(caller.value&&button4rw.value){
    						me.showButtonGroupSetWin(caller.value,button4rw.value);
    					}
    				}
    			}
    		}
    	});
    },
    getMainForm: function() {
    	var forms = Ext.ComponentQuery.query('myform');
		if(forms.length > 0)
			return forms[0];
		return null;
    },
    
    getActiveGrid: function() {
    	return Ext.getCmp('mytab').getActiveTab().down('gridpanel');
    },
    checkColumn: function(table, field, record) {
    	Ext.Ajax.request({
    		url: basePath + 'ma/checkFields.action',
    		params: {
    			table: table,
    			field: field
    		},
    		callback: function(opts, success, response) {
				if(!success || response.responseText != 'true') {
					showError('字段'+field+'不存在！');
				}		
    		}
    	});
    },
    createPreForm: function(){
    	var form = Ext.create('Ext.form.Panel', {
    		
    	});
    },
    createPreGrid: function(){
    	
    },
    createFormItem: function(record){
    	
    },
    autoSetFormIgnore: function(item, dictionary, fields) {
    	if(item.get('deploy') && item.get('fd_field') && dictionary && !item.get('fd_logictype')){    		
    		var field = item.get('fd_field').toLowerCase(), d = Ext.Array.filter(dictionary, function(i){
    			return i.column_name == field;
    		});
    		if(d.length == 0){
    			item.set('fd_logictype', 'ignore');
    		}
    	}
    },
    autoSetGridIgnore: function(item, dictionary) {
    	if(item.get('deploy') && item.get('dg_field') && dictionary && !item.get('dg_logictype')) {
    		var field = item.get('dg_field').toLowerCase();
        	if (field.indexOf(" ")>0) {// column有取别名
    			var strs = field.split(" ");
    			field = strs[strs.length - 1];
    		}
    		var d = Ext.Array.filter(dictionary, function(i){
    			return i.column_name == field;
    		});
    		if(d.length == 0)
    			item.set('dg_logictype', 'ignore');
    	}
    },
    isUnique: function(grid, fieldName) {
    	var keys = {}, key = null;
    	grid.store.each(function(item){
    		key = item.get(fieldName);
    		if(item.get('deploy') && key) {
    			key = key.toLowerCase();
    			keys[key] = (keys[key] || 0) + 1;
    		}
    	});
    	var fields = Ext.Object.getKeys(keys);
    	var err = Ext.Array.filter(fields, function(field){
    		return keys[field] > 1;
    	});
    	return err.join(',');
    },
    save: function(){
    	var me = this, isErr = false, isDefault = false;
    	var forms = Ext.ComponentQuery.query('myform'), formData = [], added = [], updated = [], deleted = [];
    	Ext.Array.each(forms, function(form){
    		var grid = form.ownerCt.down('mygrid');
    		var field = form.down('field[name=fo_table]'), val = field.value.split(' ')[0],
    			id = form.dataId || form.down('field[name=fo_id]').value,
    			pagetype = form.down('field[name=fo_pagetype]').value,
    			button4add = form.down('field[name=fo_button4add]').value,
    			button4rw = form.down('field[name=fo_button4rw]').value;
    		var err = me.isUnique(grid, "fd_field");
    		if(err) {
    			showError("字段重复：" + err);
    			isErr = true;
    			return;
    		}
    		var commonChange=form.down('field[name=fo_caller]').value.indexOf("$")>0;//通用变更单不自动设忽略
    		// 普通页面需要自动设置ignore 
    		isDefault = (button4add != null && button4add.indexOf('erpSaveButton')>-1) || (button4rw != null  && (button4rw.indexOf('erpSaveButton')>-1 || button4rw.indexOf('erpUpdateButton')>-1) );   		
    		grid.store.each(function(item){
    			item.set('fd_foid', id);
    			if(item.get('deploy') && !Ext.isEmpty(item.get('fd_field'))){   				
    				if(Ext.isEmpty(item.get('fd_table'))){
    					item.set('fd_table', val);
    				}
    				if(isDefault && !commonChange){    					
    					me.autoSetFormIgnore(item, grid.dictionary[val]);
    				}
    			}
    		});
    		formData.push(form.getValues());
    		var dd = grid.getChange();
    		added = Ext.Array.merge(added, dd.added);
			updated = Ext.Array.merge(updated, dd.updated);
			deleted = Ext.Array.merge(deleted, dd.deleted);
    	});
		var details = Ext.ComponentQuery.query('mydetail'), gridAdded = [], gridUpdated = [], gridDeleted = [];
		if (details) {
			Ext.Array.each(details, function(detail){
				var err = me.isUnique(detail, "dg_field");
	    		if(err) {
	    			showError("字段重复：" + err);
	    			isErr = true;
	    			return;
	    		}
	    		if(isDefault){
	    			detail.store.each(function(item){
		    			me.autoSetGridIgnore(item, detail.dictionary);
		    		});
	    		}
				de = detail.getChange();
				gridAdded = Ext.Array.merge(gridAdded, de.added);
				gridUpdated = Ext.Array.merge(gridUpdated, de.updated);
				gridDeleted = Ext.Array.merge(gridDeleted, de.deleted);
			});
		}
		if (isErr)
			return;
		Ext.Array.each(formData, function(d){
			var keys = Ext.Object.getKeys(d);
			for(k in keys) {
				if (keys[k].indexOf('ext-') > -1)
					delete d[keys[k]];
			}
		});
		me.onSave(unescape(escape(Ext.encode(formData))), unescape(Ext.encode(added).toString()), 
				unescape(Ext.encode(updated).toString()), unescape(Ext.encode(deleted).toString()), 
				unescape(Ext.encode(gridAdded).toString()), unescape(Ext.encode(gridUpdated).toString()), unescape(Ext.encode(gridDeleted).toString()));
    },
    onSave: function(formData, formAdded, formUpdated, formDeleted, gridAdded, gridUpdated, gridDeleted){
		var me = this;
    	me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'ma/updateMultiForm.action',
			params: {
				formData: formData,
				formAdded: formAdded,
				formUpdated: formUpdated,
				formDeleted: formDeleted,
				gridAdded: gridAdded,
				gridUpdated: gridUpdated,
				gridDeleted: gridDeleted
			},
			method : 'post',
			callback : function(opt, s, res){
				me.FormUtil.setLoading(false);
				var rs = new Ext.decode(res.responseText);
				if(rs.success){
					showMessage('提示', '保存成功!', 1000);
					window.location.reload();
				} else if(rs.exceptionInfo){
					var str = rs.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
						str = str.replace('AFTERSUCCESS', '');
						window.location.reload();
					}
					showError(str);return;
				} else {
					showMessage('提示', '保存失败!');
				}
			}
		});
	},
	showFormBookEditor: function(fo_id) {
		Ext.Ajax.request({//拿到grid的columns
			url : basePath + "common/getFieldData.action",
			params: {
				caller: 'FormBook',
				field: 'fb_content',
				condition: "fb_foid='" + fo_id + "'"
			},
			method : 'post',
			async: false,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				if(res.success){
					var win = new Ext.window.Window({
						title: '责任书',
						height: "90%",
						width: "95%",
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						items: [{
							xtype : 'htmleditor',
							anchor : '100% 100%',
							value : res.data 
						}],
						buttons: [{
	   				    	text : $I18N.common.button.erpSaveButton,
	   				    	iconCls: 'x-button-icon-save',
	   				    	cls: 'x-btn-gray',
	   				    	listeners: {
	   				    		buffer: 500,
	   				    		click: function(btn) {
	   				    			Ext.Ajax.request({
	   				     			    url : basePath + 'ma/saveFormBook.action',
	   				     			    params: {
	   				     			    	foid: fo_id,
	   				     			    	text: btn.up('window').down('htmleditor').getValue()
	   				     			    },
	   				     			    method : 'post',
	   				     			    callback : function(options,success,response){
	   	   	    			   				var localJson = new Ext.decode(response.responseText);
	   	   	    			   				if(localJson.exceptionInfo){
	   	   	    			   					showError(localJson.exceptionInfo);
	   	   	    			   				}
	   	   	    			   				if(res.success){
	   	   	    			   					btn.up('window').close();
	   	   	    			   				}
	   	   	    			   			}
	   				    			});	 
	   				    		}
	   				    	}
						},{
							text : $I18N.common.button.erpCancelButton,
	   				    	iconCls: 'x-button-icon-close',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(btn){
	   				    		btn.up('window').close();
	   				    	}
						}]
					});
					win.show();
				}
			}
		});
	},
	setDatalist:function(type){		
		var me = this;
		var dlcaller,relativecaller;
		var fo_caller = me.getMainForm().getForm().getValues().fo_caller;
		var lockpage = parent.Ext.getCmp("content-panel").lockPage+"?whoami="+fo_caller;	
		if(type == 'dl'){//列表
			dlcaller = Ext.getCmp("dl_caller").value;
			if(Ext.isEmpty(dlcaller)){
				showError('列表Caller字段值不允许为空');
				return ;
			}
			//原来没有值，现在新增不需提示，后台自动生成
			//原来有值，值未发生改变，提示将会清空原有配置，自动生成，
			//原来有值，值发生了改变，不提示修改关联caller名称 datalist 表 dl_caller和 
			if(Ext.getCmp("dl_caller").originalValue != '' && Ext.getCmp("dl_caller").originalValue == dlcaller){ 
				warnMsg("确定重置原有列表配置，根据当前form主表配置生成?", function(btn){
					if(btn == 'yes'){
			    		me.savedatalist(fo_caller,dlcaller,lockpage);
			    	 } else if(btn == 'no'){
			    		 return;
			    	 } else {
			    		 return;
			    	 }
			    });
			}else{
				me.savedatalist(fo_caller,dlcaller,lockpage);
			}
		}else if(type == 're'){//关联列表
			relativecaller = Ext.getCmp("dl_relative").value;
			if(Ext.isEmpty(relativecaller)){
				showError('关联列表Caller字段值不允许为空');
			}
			if(Ext.getCmp("dl_relative").originalValue != '' && Ext.getCmp("dl_relative").originalValue == relativecaller){ 
				warnMsg("确定重置原有关联列表配置，根据当前form主从表配置生成?", function(btn){
					if(btn == 'yes'){
				    		me.saverelativelist(fo_caller,relativecaller,lockpage);
				    	 } else if(btn == 'no'){
				    		 return;
				    	 } else {
				    		 return;
				    	 }
				});
			}else{
				me.saverelativelist(fo_caller,relativecaller,lockpage);
			}
		}				
	},
	savedatalist :function(fo_caller,dlcaller,lockpage){
		var me = this;
		var win = Ext.getCmp('lsWin');
		win.setLoading(true,win);
		//当前页面的form配置对应的jsp
		Ext.Ajax.request({//设置列表caller
			url : basePath + "ma/setListCaller.action",
			params: {
				caller       : fo_caller,
				dl_caller    : dlcaller,
				lockpage     : lockpage
			},
			method : 'post',
			async: false,
			callback : function(options,success,response){
				win.setLoading(false,win);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				if(res.success){
					//提示修改成功	
					me.FormUtil.getFieldsValue("Form", "fo_dlcaller,fo_dlrelativecaller", "fo_caller='"+fo_caller+"'", "dl_caller,dl_relative");
					Ext.getCmp("dl_caller").originalValue = Ext.getCmp("dl_caller").value; 
	    	       	Ext.getCmp("dl_relative").originalValue = Ext.getCmp("dl_relative").value; 
	    	       	showMessage("提示", "重置列表成功");
				}
			}
	   });
	},
	saverelativelist:function(fo_caller,relativecaller,lockpage){
		var me = this;
		var win = Ext.getCmp('lsWin');
		win.setLoading(true,win);
		Ext.Ajax.request({//设置列表caller
			url : basePath + "ma/setRelativeCaller.action",
			params: {
				caller       : fo_caller,
				re_caller    : relativecaller,
				lockpage     : lockpage
			},
			method : 'post',
			async: false,
			callback : function(options,success,response){
				win.setLoading(false,win);
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
				if(res.success){
					me.FormUtil.getFieldsValue("Form", "fo_dlcaller,fo_dlrelativecaller", "fo_caller='"+fo_caller+"'", "dl_caller,dl_relative");
					Ext.getCmp("dl_caller").originalValue = Ext.getCmp("dl_caller").value; 
	    	       	Ext.getCmp("dl_relative").originalValue = Ext.getCmp("dl_relative").value; 
					showMessage("提示", "重置列表成功");
				}
			}
		});
	},
	showButtonGroupSetWin:function(caller,button4rw){
		var win =new Ext.window.Window({
			title: '<span style="color:#115fd8;">按钮分组设置</span>',
			draggable:true,
			height: '90%',
			width: '90%',
			constrain:true,
			resizable:false,
			id:'buttonGroupSetWin',
			iconCls:'x-button-icon-set',
	   		modal: true,
	   		layout: 'anchor',
	   		button4rw:button4rw,
		   	items: [{
				tag: 'iframe',
				frame: false,
				border:false,
				anchor: '100% 100%',
				layout: 'fit',
				html : '<iframe src="' + basePath + 'jsps/common/buttonGroupSet.jsp?caller='+caller+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
    		}]
		});
		win.show();	
	}
});